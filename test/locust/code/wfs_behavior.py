"""Implement WMS requests without specific test logic.
"""
from urllib.parse import urlencode
from locust import TaskSet


class WFSBehavior(TaskSet):
    """Utility methods for exercising WFS requests.

    WFSTester uses this to implement the specific test logic.
    """

    def get_feature(self, uri, output_format, bbox=None, name=None):
        """Make a GetFeature request to a WFS service.
        """
        parameters = {
            "service": "wfs",
            "version": "1.1.0",
            "request": "GetFeature",
            "typeName": "ws0030:ft0001",
            "outputFormat": output_format,
        }
        # Build URL ourselves since requests insists on encoding commas
        # in querystrings, grumble grumble
        uri = "{}?{}&bbox={}".format(uri, urlencode(parameters), bbox)
        return self.client.get(
            uri=uri,
            name=name,
            catch_response=True,
        )
