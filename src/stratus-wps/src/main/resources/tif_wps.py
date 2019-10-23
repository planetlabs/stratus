#!/usr/bin/env python3

import http.client

request_text = """<?xml version="1.0" encoding="UTF-8"?>
<wps:Execute xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns="http://www.opengis.net/wps/1.0.0" xmlns:gml="http://www.opengis.net/gml" xmlns:ogc="http://www.opengis.net/ogc" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:wcs="http://www.opengis.net/wcs/1.1.1" xmlns:wfs="http://www.opengis.net/wfs" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0" service="WPS" xsi:schemaLocation="http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsAll.xsd">
   <ows:Identifier>ras:StyleCoverage</ows:Identifier>
   <wps:DataInputs>
      <wps:Input>
         <ows:Identifier>coverage</ows:Identifier>
         <wps:Reference mimeType="image/tiff" xlink:href="http://geoserver/wps" method="POST">
            <wps:Body>
               <wps:Execute>
                  <ows:Identifier>ras:NDVI</ows:Identifier>
                  <wps:DataInputs>
                     <wps:Input>
                        <ows:Identifier>coverage</ows:Identifier>
                        <wps:Reference mimeType="image/tiff" xlink:href="http://geoserver/wps" method="POST">
                           <wps:Body>
                              <wps:Execute>
                                 <ows:Identifier>ras:BandMerge</ows:Identifier>
                                 <wps:DataInputs>
                                    <wps:Input>
                                       <ows:Identifier>coverages</ows:Identifier>
                                       <wps:Reference method="GET" mimeType="image/tiff" xlink:href="https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/031/LC08_L1TP_030031_20171018_20171019_01_RT/LC08_L1TP_030031_20171018_20171019_01_RT_B4.TIF" />
                                    </wps:Input>
                                    <wps:Input>
                                       <ows:Identifier>coverages</ows:Identifier>
                                       <wps:Reference method="GET" mimeType="image/tiff" xlink:href="https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/031/LC08_L1TP_030031_20171018_20171019_01_RT/LC08_L1TP_030031_20171018_20171019_01_RT_B5.TIF" />
                                    </wps:Input>
                                 </wps:DataInputs>
                                 <wps:ResponseForm>
                                    <wps:RawDataOutput mimeType="image/tiff">
                                       <ows:Identifier>result</ows:Identifier>
                                    </wps:RawDataOutput>
                                 </wps:ResponseForm>
                              </wps:Execute>
                           </wps:Body>
                        </wps:Reference>
                     </wps:Input>
                     <wps:Input>
                        <ows:Identifier>redBand</ows:Identifier>
                        <wps:Data>
                           <wps:LiteralData>0</wps:LiteralData>
                        </wps:Data>
                     </wps:Input>
                     <wps:Input>
                        <ows:Identifier>nirBand</ows:Identifier>
                        <wps:Data>
                           <wps:LiteralData>1</wps:LiteralData>
                        </wps:Data>
                     </wps:Input>
                  </wps:DataInputs>
                  <wps:ResponseForm>
                     <wps:RawDataOutput mimeType="image/tiff">
                        <ows:Identifier>result</ows:Identifier>
                     </wps:RawDataOutput>
                  </wps:ResponseForm>
               </wps:Execute>
            </wps:Body>
         </wps:Reference>
      </wps:Input>
      <wps:Input>
         <ows:Identifier>style</ows:Identifier>
         <wps:Data>
            <wps:ComplexData mimeType="text/xml; subtype=sld/1.0.0"><![CDATA[<?xml version="1.0" encoding="ISO-8859-1"?>   <StyledLayerDescriptor version="1.0.0"       xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd"       xmlns="http://www.opengis.net/sld"       xmlns:ogc="http://www.opengis.net/ogc"       xmlns:xlink="http://www.w3.org/1999/xlink"       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">     <NamedLayer>       <Name>NDVI</Name>       <UserStyle>         <Title>NDVI</Title>         <FeatureTypeStyle>          <Rule>            <RasterSymbolizer>            <ColorMap>              <ColorMapEntry color="#000000" quantity="-1" opacity="0" />              <ColorMapEntry color="#000000" quantity="-0.25" opacity="0"/>              <ColorMapEntry color="#0000ff" quantity="0"/>              <ColorMapEntry color="#00ff00" quantity="0.25"/>              <ColorMapEntry color="#ffff00" quantity="0.5"/>              <ColorMapEntry color="#ff0000" quantity="0.6"/>              <ColorMapEntry color="#ff0000" quantity="0.9"/>              <ColorMapEntry color="#ff00ff" quantity="1"/>              </ColorMap>            </RasterSymbolizer>           </Rule>         </FeatureTypeStyle>       </UserStyle>     </NamedLayer>    </StyledLayerDescriptor>]]></wps:ComplexData>
         </wps:Data>
      </wps:Input>
   </wps:DataInputs>
   <wps:ResponseForm>
      <wps:ResponseDocument storeExecuteResponse="true" status="true" lineage="false">
         <wps:Output asReference="true" mimeType="image/tif">
            <ows:Identifier>result</ows:Identifier>
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
