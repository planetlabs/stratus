#!/usr/bin/env python3

import http.client

request_text = """<?xml version="1.0" encoding="UTF-8"?><wps:Execute version="1.0.0" service="WPS" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://www.opengis.net/wps/1.0.0" xmlns:wfs="http://www.opengis.net/wfs" xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:gml="http://www.opengis.net/gml" xmlns:ogc="http://www.opengis.net/ogc" xmlns:wcs="http://www.opengis.net/wcs/1.1.1" xmlns:xlink="http://www.w3.org/1999/xlink" xsi:schemaLocation="http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsAll.xsd">
  <ows:Identifier>vec:Count</ows:Identifier>
  <wps:DataInputs>
    <wps:Input>
      <ows:Identifier>features</ows:Identifier>
      <wps:Reference mimeType="text/xml" xlink:href="http://geoserver/wfs" method="POST">
        <wps:Body>
          <wfs:GetFeature service="WFS" version="1.0.0" outputFormat="GML2" xmlns:topp="http://www.openplans.org/topp">
            <wfs:Query typeName="test:tasmania_cities"/>
          </wfs:GetFeature>
        </wps:Body>
      </wps:Reference>
    </wps:Input>
  </wps:DataInputs>
  <wps:ResponseForm>
     <wps:ResponseDocument storeExecuteResponse="true" status="true" >
        <wps:Output>
            <ows:Identifier>result</ows:Identifier>
            <ows:Title>result</ows:Title>
            <ows:Abstract>result</ows:Abstract>
        </wps:Output>
    </wps:ResponseDocument>
</wps:ResponseForm>
</wps:Execute>"""

print(request_text)

conn = http.client.HTTPConnection("localhost", 8080)
conn.request("POST", "/geoserver/ows?service=wps&version=1.0.0&request=Execute", request_text)
result = conn.getresponse()
print(result.status, result.reason)

print(result.read())
# file = open('test.tiff', 'wb')
# file.write(result.read())
# file.close()
