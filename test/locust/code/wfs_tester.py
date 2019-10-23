from locust import HttpLocust, TaskSet, task
import csv



class EcWfsTester(TaskSet):

    with open('../bbox_data/roads-bbox-100k.csv', 'rb') as f:
            #fieldnames = ['width','height','bottom','left','top','right']
            reader = csv.reader(f)
            bbox_iterator = iter(list(reader))

    # @task(1)
    # def wms_get_capabilities(l):
    #     l.client.get("/geoserver/ows/service=WMS&request=GetCapabilities")
    @task(1)
    def wfs_gml2_bbox(self):
        response = self._doWFS("GML2","WFS_GML2_BBOX")
        if "text/xml" not in response.headers['Content-Type']:
            response.failure("Expected content-type to be text/xml but found"+response.headers['Content-Type'])
        response.success()

    @task(1)
    def wfs_gml3_bbox(self):
        response = self._doWFS("GML3", "WFS_GML3_BBOX")
        if "text/xml" not in response.headers['Content-Type']:
            response.failure("Expected content-type to be text/xml but found"+response.headers['Content-Type'])
        response.success()

    @task(1)
    def wfs_json_bbox(self):
        response = self._doWFS("application/json", "WFS_JSON_BBOX")
        if "text/xml" not in response.headers['Content-Type']:
            response.failure("Expected content-type to be application/json but found"+response.headers['Content-Type'])
        response.success()

    def _doWFS(self,fmt,name):
        url = "/geoserver/wfs?service=wfs&version=1.1.0&request=GetFeature&typeName=ws0030:ft0001&ouputFormat="+fmt
        line = self.bbox_iterator.next()
        url += "&bbox="+line[2]+","+line[3]+","+line[4]+","+line[5]
        return self.client.get(url, name= name,catch_response=True)



class WebsiteUser(HttpLocust):
    task_set = EcWfsTester
    min_wait = 0
    max_wait = 0
