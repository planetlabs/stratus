"""Assumes Python 3.6+
"""
import argparse
import statistics
import time
import logging
from textwrap import indent, dedent
from urllib.parse import urlencode
from datetime import datetime as Datetime
import asyncio
import aiohttp
from async_timeout import timeout

LOG = logging.getLogger("post_latency")

def argument_parser():
    parser = argparse.ArgumentParser(
        description="Test some latency thing",
    )
    parser.add_argument(
        dest="post_node_name",
    )
    parser.add_argument(
        dest="get_node_names",
        nargs="*",
    )
    parser.add_argument(
        "--database-host",
        default="localhost",
    )
    parser.add_argument(
        "--database-port",
        default="5432",
    )
    parser.add_argument(
        "--database-name",
        default="osm",
    )
    parser.add_argument(
        "--database-user",
        default="geoserver",
    )
    parser.add_argument(
        "--database-password",
        default="geoserver",
    )
    parser.add_argument(
        "--username",
        default="admin",
    )
    parser.add_argument(
        "--password",
        default="geoserver",
    )
    parser.add_argument(
        "--workspace",
        default="osm",
    )
    parser.add_argument(
        "--datastore",
        default="openstreetmap",
    )
    parser.add_argument(
        "--layer",
        default="water",
    )
    parser.add_argument(
        "--test-count",
        type=int,
        default=1,
    )
    parser.add_argument(
        "--delay",
        type=float,
        default=0.1,
    )
    parser.add_argument(
        "--max-fails",
        type=int,
        default=0.1,
    )
    return parser


class RequestResult:
    """Store data on one request.
    """
    def __init__(self):
        self.content_type = None
        self.success = None
        self.status = None
        self.body = None
        self.start = None
        self.connected = None
        self.end = None

    @property
    def duration(self):
        if self.end is None or self.start is None:
            return None
        return self.end - self.start


class TestResult:
    """Store data on one node test.
    """
    def __init__(self, post_node_name, get_node_names):
        """
        :arg post_node_name:
            One string specifying the hostname or hostname:port of the host to
            issue POST requests to, setting up the test.
        :arg get_node_names:
            List of strings specifying hostname or hostname:port for each host
            to issue polling GET requests to.
        """
        self.post_node_name = post_node_name
        self.get_node_names = get_node_names

        # A list of RequestResult instances representing retries of the POST
        self.workspace_post_results = None
        # A dict mapping hostname strings to retries of the GET
        self.workspace_get_results = None

        self.datastore_post_results = None
        self.datastore_get_results = None

        self.featuretype_post_results = None
        self.featuretype_get_results = None

        # How many times did we fail?
        self.failures = 0

        # Was the test successful overall?
        self.success = None

        # time.monotonic() timestamps for timing the whole test.
        self.start = None
        self.end = None


async def retry(label, async_function, args=None, kwargs=None, max_fails=1,
                delay=0.1):
    # default values for args/kwargs which will unpack successfully
    args = args or []
    kwargs = kwargs or {}

    # Get a child logger which prefixes the given label,
    # e.g. so we have context like node names when reporting failures
    log = LOG.getChild(label)

    # List of results representing different retries.
    # If the last one is successful, the whole process succeeded.
    # Otherwise, the whole process failed.
    results = []

    # Use a finite number of retries
    for fails in range(0, max_fails):

        result = RequestResult()
        results.append(result)
        now = Datetime.now()
        result.start = time.monotonic()

        log.debug(f"attempt #{fails + 1} at {now}")

        # e.g. "async with session.post(url) as response:"
        async with async_function(*args, **kwargs) as response:
            result.connected = time.monotonic()

            log.debug(f"response {response}")

            # Record properties of original response for debug
            result.status = response.status
            result.content_type = response.headers.get("content-type")
            log.debug(f"status: {result.status}")

            # Record an indicator of whether this was a success
            # Default success to whether it is a 2xx response.
            result.success = (200 <= response.status <= 299)

            # As a special case, GS may emit a 500 to mean resource already
            # exists. e.g. body = b":Workspace named 'osm' already exists."
            # This is an error for us, e.g. workspace wasn't deleted yet,
            # so we need to just retry until we're done retrying.

            # Read the body so we can report on its length and the total
            # request handling time.
            body = await response.read()
            result.end = time.monotonic()
            # GS won't give out Content-Length, so just do it all in RAM
            result.length = len(body)

            if result.length:
                log.debug(f"length: {result.length}")
                log.debug(f"content-type: {result.content_type}")

                # Log some body in case something went wrong
                partial_body = body[:100]
                log.debug(f"body: {partial_body}")

            # Stop retrying once successful.
            if result.success:
                break

            log.error(f"failure: {result.status}")

            await asyncio.sleep(delay)

    now = Datetime.now()
    log.debug(f"retry returning at {now}")

    return results

async def delete_workspace(session, options):
    """
    """
    node_name = options.post_node_name
    url = (
        f"http://{node_name}/geoserver/rest"
        f"/workspaces/{options.workspace}"
        f"?recurse=true"
    )
    future = retry(
        f"workspace.DELETE.{node_name}", session.delete, args=[url],
        delay=options.delay,
    )
    results = await future
    return results

async def post_workspace(session, options, node_name):
    """Issue a POST to the master write node to create a workspace.

    :returns:
        A list of RequestResult instances, each representing one attempt to
        perform the request. If the last one was not successful, then the POST
        was not successful.
    """

    # Specifically for POST workspace, we need to retry because there is a
    # chance that previous workspace deletes have not yet propagated by the
    # time we start trying to POST a new workspace.
    max_fails = 1

    url = f"http://{node_name}/geoserver/rest/workspaces"
    headers = {
        "Content-Type": "text/xml",
    }
    data = f"""
        <workspace>
            <name>{options.workspace}</name>
        </workspace>
        """.strip()

    future = retry(
        f"workspace.POST.{node_name}", session.post, args=[url], kwargs={
            "headers": headers,
            "data": data,
        },
        max_fails=max_fails,
        delay=options.delay,
    )
    results = await future
    return results


async def get_workspace(options, session, node_name):
    """Issue polling GETs to a node to determine when workspace is available.
    """

    # Poll for a while before faliing.

    url = f"http://{node_name}/geoserver/rest/workspaces/{options.workspace}"

    # I don't know why the empty string needs a content-type
    headers = {
        "Content-Type": "text/xml",
    }

    future = retry(
        f"workspace.GET.{node_name}", session.get, args=[url], kwargs={
            "headers": headers,
        },
        max_fails=options.max_fails,
        delay=options.delay,
    )
    results = await future
    return results


async def delete_datastore(session, options):
    """
    """
    node_name = options.post_node_name
    url = (
        f"http://{node_name}/geoserver/rest"
        f"/workspaces/{options.workspace}"
        f"/datastores/{options.datastore}"
    )
    future = retry(
        f"workspace.DELETE.{node_name}", session.delete, args=[url],
        delay=options.delay,
    )
    results = await future
    return results


async def post_datastore(options, session, node_name):
    """Issue a POST to the master write node to create a datastore.

    :returns:
        A list of RequestResult instances, each representing one attempt to
        perform the request. If the last one was not successful, then the POST
        was not successful.
    """

    # If POST datastore fails at all, something is already wrong
    max_fails = 1

    url = (
        f"http://{node_name}/geoserver/rest"
        f"/workspaces/{options.workspace}"
        f"/datastores"
    )
    headers = {
        "Content-Type": "text/xml",
    }
    data = f"""
        <dataStore>
        <name>openstreetmap</name>
        <connectionParameters>
            <host>{options.database_host}</host>
            <port>{options.database_port}</port>
            <database>{options.database_name}</database>
            <user>{options.database_user}</user>
            <passwd>{options.database_password}</passwd>
            <dbtype>postgis</dbtype>
        </connectionParameters>
        </dataStore>
        """.strip()

    print("DATASTORE: "+url)
    print(headers)
    print(data) 
    future = retry(
        f"datastore.POST.{node_name}", session.post, args=[url], kwargs={
            "headers": headers,
            "data": data,
        },
        max_fails=max_fails,
        delay=options.delay,
    )
    results = await future
    return results


async def get_datastore(options, session, node_name):
    """Issue polling GETs to a node to determine when datastore is available.
    """

    # Poll for a while before faliing.

    url = (
        f"http://{node_name}/geoserver/rest"
        f"/workspaces/{options.workspace}"
        f"/datastores/{options.datastore}"
    )

    # I don't know why the empty string needs a content-type
    headers = {
        "Content-Type": "text/xml",
    }

    future = retry(
        f"datastore.GET.{node_name}", session.get, args=[url], kwargs={
            "headers": headers,
        },
        max_fails=options.max_fails,
        delay=options.delay,
    )
    results = await future
    return results


async def delete_featuretype(session, options):
    """
    """
    node_name = options.post_node_name
    url = (
        f"http://{node_name}/geoserver/rest"
        f"/workspaces/{options.workspace}"
        f"/datastores/{options.datastore}"
        f"/featuretypes/{options.layer}"
    )
    future = retry(
        f"workspace.DELETE.{node_name}", session.delete, args=[url],
        delay=options.delay,
    )
    results = await future
    return results



async def post_featuretype(options, session, node_name):
    """Issue a POST to the master write node to create a featuretype.

    :returns:
        A list of RequestResult instances, each representing one attempt to
        perform the request. If the last one was not successful, then the POST
        was not successful.
    """

    # If POST featuretype fails at all, something is already wrong
    max_fails = 1

    url = (
        f"http://{node_name}/geoserver/rest"
        f"/workspaces/{options.workspace}"
        f"/datastores/{options.datastore}"
        f"/featuretypes?recalculate=nativebbox,latlonbbox"
    )
    headers = {
        "Content-Type": "text/xml",
    }
    data = f"""
        <featureType>
            <name>{options.layer}</name>
        </featureType>
        """.strip()

    future = retry(
        f"featuretype.POST.{node_name}", session.post, args=[url], kwargs={
            "headers": headers,
            "data": data,
        },
        max_fails=max_fails,
        delay=options.delay,
    )
    results = await future
    return results


async def get_featuretype(options, session, node_name):
    """Issue polling GETs to a node to determine when featuretype is available.
    """

    # Poll for a while before faliing.

    url = (
        f"http://{node_name}/geoserver/rest"
        f"/workspaces/{options.workspace}"
        f"/datastores/{options.datastore}"
        f"/featuretypes/{options.layer}"
    )

    # I don't know why the empty string needs a content-type
    headers = {
        "Content-Type": "text/xml",
    }

    future = retry(
        f"featuretype.GET.{node_name}", session.get, args=[url], kwargs={
            "headers": headers,
        },
        max_fails=options.max_fails,
        delay=options.delay,
    )
    results = await future
    return results


async def test(options, label):

    # Create object used for HTTP basic authentication using command line args
    auth = aiohttp.BasicAuth(options.username, options.password)

    # Create object to store all results for test
    test_result = TestResult(options.post_node_name, options.get_node_names)

    # Timing for the whole test
    # For most reporting we'll prefer to use actual request timings, so ew
    # don't include client setup time
    test_result.start = time.monotonic()

    # TODO: fix conn_timeout
    async with aiohttp.ClientSession(
        auth=auth, conn_timeout=5, read_timeout=5,
    ) as session:

        # Preparatory cleanup

        results = await delete_featuretype(session, options)
        LOG.debug(f"DELETE featuretype results {results}")

        results = await delete_datastore(session, options)
        LOG.debug(f"DELETE datastore results {results}")

        results = await delete_workspace(session, options)
        LOG.debug(f"DELETE workspace results {results}")

        await asyncio.sleep(10)

        # Create a bunch of futures. Coroutine execution doesn't start yet.

        node_names = [options.post_node_name] + options.get_node_names

        workspace_get_futures = [
            get_workspace(options, session, node_name)
            for node_name in node_names
        ]
        workspace_post_future = post_workspace(
            session, options, options.post_node_name
        )
        futures = [workspace_post_future] + workspace_get_futures

        # Start all the futures at the same time.
        #
        # await actually schedules the futures, then we go to sleep,
        # and once they've all ripened, the scheduler wakes us up again.
        #
        # Since we used gather, results will be in the same order.

        results = await asyncio.gather(*futures)

        post_results, *all_get_results = results
        get_results = {
            node_names[index]: all_get_results[index]
            for index in range(0, len(all_get_results))
        }

        test_result.workspace_post_results = post_results
        test_result.workspace_get_results = get_results

        # Log how many tries we had
        LOG.debug(f"{len(post_results)} POST workspace results")
        LOG.debug(f"{len(get_results)} GET workspace results:")
        for get_node_name, results in sorted(get_results.items()):
            LOG.debug(f"    {get_node_name}: {len(results)} results")

        # Rinse, repeat for datastore
        datastore_get_futures = [
            get_datastore(options, session, node_name)
            for node_name in node_names
        ]
        datastore_post_future = post_datastore(
            options, session, options.post_node_name
        )
        futures = [datastore_post_future] + datastore_get_futures
        results = await asyncio.gather(*futures)
        post_results, *all_get_results = results
        get_results = {
            node_names[index]: all_get_results[index]
            for index in range(0, len(all_get_results))
        }
        test_result.datastore_post_results = post_results
        test_result.datastore_get_results = get_results
        LOG.debug(f"{len(post_results)} POST datastore results")
        LOG.debug(f"{len(get_results)} GET datastore results:")
        for get_node_name, results in sorted(get_results.items()):
            LOG.debug(f"    {get_node_name}: {len(results)} results")

        # Rinse, repeat for featuretype
        featuretype_get_futures = [
            get_featuretype(options, session, node_name)
            for node_name in node_names
        ]
        featuretype_post_future = post_featuretype(
            options, session, options.post_node_name
        )
        futures = [featuretype_post_future] + featuretype_get_futures
        results = await asyncio.gather(*futures)
        post_results, *all_get_results = results
        get_results = {
            node_names[index]: all_get_results[index]
            for index in range(0, len(all_get_results))
        }
        test_result.featuretype_post_results = post_results
        test_result.featuretype_get_results = get_results
        LOG.debug(f"{len(post_results)} POST featuretype results")
        LOG.debug(f"{len(get_results)} GET featuretype results:")
        for get_node_name, results in sorted(get_results.items()):
            LOG.debug(f"    {get_node_name}: {len(results)} results")

    test_result.end = time.monotonic()

    # Debug report on the completion and timing of the whole process
    duration = round(test_result.end - test_result.start, 4)
    LOG.debug(f"Test for {options.post_node_name} finished in {duration}s.")

    return test_result


def summary_text(values):
    # drop None values to avoid mucking up the statistics
    values = [value for value in values if value is not None]
    if not values:
        return "no data to report"
    text = dedent(f"""
        values: {values}
        count : {len(values)}
        min   : {min(values)}
        mean  : {statistics.mean(values)}
        median: {statistics.median(values)}
        max   : {max(values)}
    """).strip()
    return text


LATENCY_NAMES = [
    "end-end",
    "end-start",
    "end-conn",
    "start-end",
    "start-start",
    "start-conn",
    "conn-end",
    "conn-start",
    "conn-conn",
]


def latency_dict(last_get, last_post):
    latencies = {}
    if last_get.end is not None:
        if last_post.end is not None:
            latencies["end-end"] = last_get.end - last_post.end
        if last_post.start is not None:
            latencies["end-start"] = last_get.end - last_post.start
        if last_post.connected is not None:
            latencies["end-conn"] = last_get.end - last_post.connected
    if last_get.start is not None:
        if last_post.end is not None:
            latencies["start-end"] = last_get.start - last_post.end
        if last_post.start is not None:
            latencies["start-start"] = last_get.start - last_post.start
        if last_post.connected is not None:
            latencies["start-conn"] = last_get.start - last_post.connected
    if last_get.connected is not None:
        if last_post.end is not None:
            latencies["conn-end"] = last_get.connected - last_post.end
        if last_post.start is not None:
            latencies["conn-start"] = last_get.connected - last_post.start
        if last_post.connected is not None:
            latencies["conn-conn"] = last_get.connected - last_post.connected
    return latencies


def report(results):
    tab = " " * 4

    for result in results:
        if isinstance(result, Exception):
            logging.exception(result)

    results = [
        result for result in results
        if not isinstance(result, Exception)
    ]

    if not results:
        print("No results to display.")
        return

    all_latencies = {}

    for index, result in enumerate(results, 1):
        test_duration = round(result.end - result.start, 3)

        paragraph = dedent(f"""

            Test #{index}
            Test Host: {result.post_node_name}
            Test Duration: {test_duration}s
            Test Results:
            """.rstrip())
        print(paragraph)

        tups = [
            (
                "Workspace",
                result.workspace_post_results,
                result.workspace_get_results,
            ),
            (
                "Datastore",
                result.datastore_post_results,
                result.datastore_get_results,
            ),
            (
                "Featuretype",
                result.featuretype_post_results,
                result.featuretype_get_results,
            ),
        ]
        for label, post_results, get_results in tups:

            # e.g. "Datastore:"
            line = indent(f"{label}:", tab)
            print(line)

            last_post = post_results[-1] if post_results else None
            last_gets = {
                key: (results[-1] if results else None)
                for key, results in get_results.items()
            }

            # If there are no results for e.g. datastore, it's a failure
            if not post_results:
                line = indent("POST: failure - no results", tab * 2)
                print(line)

            # Otherwise, if the last POST succeeded, test succeeded overall.
            # The fail count is the number of preceding POST retry results
            else:
                post_success = "success" if last_post.success else "failure"
                post_fail_count = sum(
                    (1 if result and not result.success else 0)
                    for result in post_results
                )
                post_missing_count = sum(
                    (1 if not result else 0)
                    for result in post_results
                )
                line = indent(
                    f"POST: "
                    f"{post_success} "
                    f"fails={post_fail_count} ",
                    tab * 2
                )
                print(line)

                print(indent("Durations:", tab * 3))
                post_durations = [result.duration for result in post_results]
                if len(post_durations) == 1:
                    print(indent(f"{post_durations[-1]}", tab * 4))
                else:
                    print(indent(summary_text(post_durations), tab * 4))


            if not get_results:
                line = indent("GET: failure - no results", tab * 2)
                print(line)
            else:
                get_success = {
                    key: value.success if value else False
                    for key, value in last_gets.items()
                }
                get_success_count = sum(
                    (1 if value else 0)
                    for value in get_success.values()
                )
                line = indent(
                    f"GET: {get_success_count} successful host(s)",
                    tab * 2
                )
                print(line)

                for name, values in sorted(get_results.items()):
                    if not values:
                        print(indent(f"{name}: no results", tab * 3))
                        continue

                    last_get = values[-1]
                    success = "success" if last_get.success else "failure"
                    fails = sum(
                        (0 if result and result.success else 1)
                        for result in values
                    )
                    durations = [result.duration for result in values]
                    paragraph = indent(dedent(f"""
                        {name}: {success} fails={fails}
                        """).strip(), tab * 3)
                    print(paragraph)

                    print(indent("Durations:", tab * 4))
                    if len(durations) == 1:
                        print(indent(f"{durations[-1]}", tab * 4))
                    else:
                        paragraph = indent(summary_text(durations), tab * 5)
                        print(paragraph)

                    if last_get and last_get.success and last_post and last_post.success:

                        print(indent("Latencies:", tab * 4))
                        latencies = latency_dict(last_get, last_post)
                        for latency_name, value in sorted(latencies.items()):
                            print(indent(f"{latency_name} = {value}", tab * 5))

                        existing = all_latencies.setdefault((name, label), {})
                        for key, value in latencies.items():
                            values = existing.setdefault(key, [])
                            values.append(value)

    print("Combined Latencies")
    tuples = list(all_latencies.keys())
    names = sorted(set([tup[0] for tup in tuples]))
    labels = sorted(set([tup[1] for tup in tuples]))
    for name in names:
        print("=" * len(name))
        print(f"{name}")
        print("=" * len(name))
        for label in labels:
            print(indent(f"{label}", tab * 1))
            print(indent("-" * len(label), tab * 1))
            key = (name, label)
            latencies = all_latencies.get(key)
            for latency_name in LATENCY_NAMES:
                print(indent(f"{latency_name}", tab * 2))
                values = latencies.get(latency_name) or []
                if values:
                    print(indent(summary_text(values), tab * 3))


def main():
    # Configure logging: we want debug messages from almost everything,
    # but we only want important information from asyncio itself
    logging.basicConfig(level=logging.DEBUG)
    logging.getLogger("asyncio").setLevel(logging.INFO)

    # Parse command line arguments
    parser = argument_parser()
    options = parser.parse_args()
    LOG.debug(f"options: {options}")

    # Run coroutines
    loop = asyncio.get_event_loop()

    test_results = []
    try:
        for index in range(0, options.test_count):
            test_result = loop.run_until_complete(test(options, index + 1))
            test_results.append(test_result)
    except KeyboardInterrupt:
        print(f"collected {len(test_results)} test results before interruption")

    # Output formatted summarized results
    report(test_results)


main()
