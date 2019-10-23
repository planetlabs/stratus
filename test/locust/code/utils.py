import csv
from itertools import cycle
from pathlib import Path


def load_bbox_data(filename):
    """Load a data file containing example bboxes.
    """
    path = Path(filename)
    with open(path, newline="") as stream:
        reader = csv.reader(stream)
        iterator = cycle(list(reader))
    return iterator


def check_content(response, expected):
    """Fail the response if content-type doesn't start with given prefix.

    Invoked inside locust task functions to mark success/failure
    (we can't consistently trust that 200 means everything is okay,
    so we use catch_response=True).
    """
    content_type = response.headers.get("Content-Type")
    if not content_type:
        response.failure("No Content-Type in response")
    elif not content_type.startswith(expected):
        message = "Expected content-type {0!r} but got {1!r}".format(
            expected, content_type,
        )
        response.failure(message)
    elif not response.content:
        response.failure("Empty response")
    else:
        response.success()
