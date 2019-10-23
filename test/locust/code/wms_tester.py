"""Define a locust swarm for testing WMS on Stratus.
"""

from locust import HttpLocust, task
from wms_behavior import WMSBehavior
from utils import load_bbox_data, check_content
import os

class WMSTester(WMSBehavior):
    """Exercise Stratus WMS
    """


    def on_start(self):
        """Startup method called by locust once for each new simulated user.
        """        
        bbox_csv_file=os.environ.get("BBOX_FILE")
        if not bbox_csv_file:
            bbox_csv_file="data/wms_256_tiles.csv"
        self.bbox_iterator=load_bbox_data(bbox_csv_file)

        tmp=os.environ.get("LOCUST_LAYERS")
        if tmp:
            self.layers=tmp
        else:
            self.layers="osm:osm"

    @task(0)
    def wms_get_capabilities(self):
        """Exercise WMS GetCapabilities
        """
        response = self.get_capabilities("/geoserver/ows")
        check_content(response, "text/xml")

    @task(1)
    def wms_png_bbox(self):
        response = self.wms_get_map("image/png", self.layers)
        check_content(response, "image/png")

    # @task(1)
    def wms_png8_bbox(self):
        response = self.wms_get_map("image/png8", self.layers)
        check_content(response, "image/png")

    # @task(1)
    def wms_jpeg_bbox(self):
        response = self.wms_get_map("image/jpeg", self.layers)
        check_content(response, "image/jpeg")

    # @task(0)
    def wms_tiff_bbox(self):
        response = self.wms_get_map("image/tiff", self.layers)
        check_content(response, "image/tiff")

    def wms_get_map(self, image_format, layers):
        """Exercise WMS GetMap with the specified format
        """
        line = next(self.bbox_iterator)
        bbox = [line[1], line[0], line[3], line[2]]
        bbox_string = ",".join(bbox)
        name = "WMS_{0}_BBOX".format(image_format.split("/")[-1])
        return self.get_map(
            uri="/geoserver/wms",
            layers=layers,
            image_format=image_format,
            width=256,
            height=256,
            bbox=bbox_string,
            crs="EPSG:4326",
            name=name,
        )


class WMSUser(HttpLocust):
    """Specify how each simulated user will behave.
    """
    # Define the behavior of the user.
    task_set = WMSTester

    # Parameters for generating the random wait between tasks.
    min_wait = 0
    max_wait = 0
