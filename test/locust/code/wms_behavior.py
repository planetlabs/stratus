"""Implement WMS requests without specific test logic.
"""
from urllib.parse import urlencode
from locust import TaskSet
import datetime
import time


class WMSBehavior(TaskSet):
    """Utility methods for exercising WMS requests.

    WMSTester uses this to implement the specific test logic.
    """

    def get_capabilities(self, uri, namespace=None, fmt=None):
        """Make a GetCapabilities request to get data about a WMS service.
        """
        parameters = {
            # "version": "1.3.0",
            "service": "WMS",
            "request": "GetCapabilities"
        }
        if namespace is not None:
            parameters["namespace"] = namespace
        if fmt is not None:
            parameters["format"] = fmt
        return self.client.get(
            uri,
            params=parameters,
            name="GetCapabilities",
            catch_response=True,
        )

    def describe_layer(self, path, service, version, request):
        """Get the WFS or WCS to retrieve additional info about a WMS layer.
        """
        # TODO
        version = "1.1.1"
        service = "WMS"
        request = "DescribeLayer"
        exception_format = self.exception_format
        output_format = self.output_format
        parameters = {
            "service": service,
            "version": version,
            "request": request,
            "layers": "whee",
            "exceptions": exception_format,
            "output_format": output_format,
        }
        return self.client.get(path, params=parameters)

    def get_legend(self, uri):
        """Get a generated legend for a WMS map.
        """
        # TODO
        request = "GetLegendGraphic"
        version = "1.0.0"
        fmt = "image/png"
        width = 20
        height = 20
        layer = "topp:states"
        parameters = {
            "VERSION": version,
            "REQUEST": request,
            "LAYER": layer,
            "FORMAT": fmt,
            "EXCEPTIONS": self.exception_format,
        }
        if width:
            parameters["WIDTH"] = width
        if height:
            parameters["HEIGHT"] = height
        return self.client.get(uri, params=parameters)

    def get_feature_info(self, uri):
        """Get data for a pixel location on a WMS map
        """
        # TODO
        parameters = {
            "service": "wms",
            "request": "GetFeatureInfo",
        }
        return self.client.get(uri, params=parameters)

    def get_map(self, uri, layers, image_format, width, height, bbox, crs,
                name=None):
        """Make a WMS GetMap request.

        :arg uri:
            e.g. "/geoserver/wms"
        :arg layers:
            e.g. "osm:osm"
        :arg image_format:
            e.g. "png"
        :arg width:
            e.g. 256
        :arg height:
            e.g. 256
        :arg bbox:
            passed through as-is
        :arg crs:
            e.g. "EPSG:4326"
        :arg name:
            e.g. "WMS_png_BBOX"
        """
        parameters = {
            # Things we don't have any reason to vary
            "service": "wms",
            "version": "1.3.0",
            "request": "GetMap",

            # Variables
            "layers": layers,
            "format": image_format,
            "width": "{}".format(width),
            "height": "{}".format(height),
            "crs": crs,
            # "bbox": bbox,
        }
        # Build URL ourselves since requests insists on encoding commas
        # in querystrings, grumble grumble
        url = "{}?{}&bbox={}".format(uri, urlencode(parameters), bbox)
        map=self.client.get(
            url,
            name=name,
            # mark everything failed unless it's explicitly marked successful,
            # since 200 doesn't mean anything in this postmodern era
            catch_response=True,
        )

        content_type="None"
        if map.headers is not None:
            content_type=map.headers.get("content-type")
        if not content_type.startswith("image/png"):
            map.failure("Got wrong content-type")
            map.status_code=469
        st = datetime.datetime.fromtimestamp(time.time()).strftime('%Y-%m-%d %H:%M:%S')
        print(st+"\t"+url+"\t"+content_type+"\t"+str(map.status_code))
        return map
