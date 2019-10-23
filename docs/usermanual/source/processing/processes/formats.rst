.. _processing.processes.formats:


Process input and output formats
================================

GeoServer :term:`WPS` processes support a range of input and output formats, including 
geometry, feature collection, and raster.


.. _processing.processes.formats.geomin:

Geometry inputs
---------------

The following geometry inputs are supported:

Geographic markup (TEXT)
~~~~~~~~~~~~~~~~~~~~~~~~

Processes can support geographic markup, posted as part of the request, in one of the following formats:

* :term:`Well-Known Text` (WKT)
* :term:`Geographic Markup Language` (GML) 2.1.2
* :term:`Geographic Markup Language` (GML) 3.1.1

For example:

.. code-block:: xml

   <wps:Data>
     <wps:ComplexData mimeType="application/wkt"><![CDATA[POLYGON((0 0, 0 1 , 1 1, 1 0, 0 0))]]>
     </wps:ComplexData>
   </wps:Data>

In the :guilabel:`WPS request builder` in GeoServer, this is denoted as :guilabel:`TEXT`.


HTTP request (REFERENCE)
~~~~~~~~~~~~~~~~~~~~~~~~

Processes can also accept inputs from the result of an HTTP GET or POST request. This request (often a WFS GetFeature request or equivalent) should output GML or WKT.

For example::

   <wps:Reference mimeType="text/xml; subtype=gml/3.1.1"\
    xlink:href="http://example.com:8080/geoserver?myrequest" method="GET"/>

In the GeoServer :guilabel:`WPS request builder`, this is denoted as :guilabel:`REFERENCE`.


Subprocess (SUBPROCESS)
~~~~~~~~~~~~~~~~~~~~~~~

As processes can be chained, the output from one process can provide the input for another process. In this case, an entire process execute request is generated as the input parameter.

In the GeoServer :guilabel:`WPS request builder`, this is denoted as :guilabel:`SUBPROCESS`.

.. _processing.processes.formats.geomout:

Geometry outputs
----------------

For processes generating geometry as the output, the following output types are supported:

* :term:`Well-Known Text` (WKT)
* :term:`Geographic Markup Language` (GML) 2.1.2
* :term:`Geographic Markup Language` (GML) 3.1.1

.. _processing.processes.formats.fcin:

Feature collection inputs
-------------------------

When a process expects a feature collection as an input, the following input types are supported:


Geographic markup (TEXT)
~~~~~~~~~~~~~~~~~~~~~~~~

Processes that accept feature collections as input can support geographic markup, posted as part of the request, in one of the following formats:

* :term:`WFS` collection
* :term:`JSON`
* Shapefile archive (ZIP)

In the GeoServer :guilabel:`WPS request builder`, this is denoted as :guilabel:`TEXT`.


HTTP request (REFERENCE)
~~~~~~~~~~~~~~~~~~~~~~~~

Processes that accept feature collections as input can take the input from the result of an HTTP GET or POST request. This request (often a WFS GetFeature request or equivalent) should output either a WFS collection or JSON.

For example::

   <wps:Reference mimeType="text/xml; subtype=gml/3.1.1"\
    xlink:href="http://example.com:8080/geoserver?myrequest" method="GET"/>

In the GeoServer :guilabel:`WPS request builder`, this is denoted as :guilabel:`REFERENCE`.

GeoServer layer (VECTOR_LAYER)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

GeoServer processes that accept feature collections as input can also accept GeoServer layers. This is a special case of the above HTTP request, as the request will simply point to the local GeoServer HTTP endpoint. 

For example:

.. code-block:: xml

   <wps:Reference mimeType="text/xml; subtype=wfs-collection/1.0"
    xlink:href="http://geoserver/wfs" method="POST">
     <wps:Body>
       <wfs:GetFeature service="WFS" version="1.0.0" outputFormat="GML2"
        xmlns:usa="http://usa.opengeo.org">
         <wfs:Query typeName="usa:states"/>
       </wfs:GetFeature>
     </wps:Body>
   </wps:Reference>

In the GeoServer :guilabel:`WPS request builder`, this is denoted as :guilabel:`VECTOR_LAYER`.


Subprocess (SUBPROCESS)
~~~~~~~~~~~~~~~~~~~~~~~

As processes can be chained, the output from one process can provide the input for another process. In this case, an entire process execute request is generated as the input parameter.

In the GeoServer :guilabel:`WPS request builder`, this is denoted as :guilabel:`SUBPROCESS`.


.. _processing.processes.formats.fcout:

Feature collection outputs
--------------------------

For processes generating feature collections as the output, the following output types are supported:

* :term:`WFS` collection
* :term:`JSON`
* Shapefile archive (ZIP)


.. _processing.processes.formats.rasterin:


Raster inputs
-------------

When a process expects a raster (coverage) as an input, the following input types are supported:


Geographic markup (TEXT)
~~~~~~~~~~~~~~~~~~~~~~~~

Processes that accept raster data as input can support markup, posted as part of the request, in one of the following formats:

* TIFF
* ArcGrid

In the GeoServer :guilabel:`WPS request builder`, this is denoted as :guilabel:`TEXT`.


HTTP request (REFERENCE)
~~~~~~~~~~~~~~~~~~~~~~~~

Processes that accept rasters as input can take the input from the result of an HTTP GET or POST request. This request (often a :term:`WCS` GetCoverage request or equivalent) should output either TIFF or ArcGrid image data.

For example::

   <wps:Reference mimeType="text/xml; subtype=gml/3.1.1"\
    xlink:href="http://example.com:8080/geoserver?myrequest" method="GET"/>

In the GeoServer :guilabel:`WPS request builder`, this is denoted as :guilabel:`REFERENCE`.


GeoServer layer (RASTER_LAYER)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

GeoServer processes that accept feature collections as input can also accept GeoServer layers. This is a special case of the above HTTP request, as the request will simply point to the local GeoServer HTTP endpoint.

For example:

.. code-block:: xml

   <wps:Reference mimeType="image/tiff" xlink:href="http://geoserver/wcs" method="POST">
     <wps:Body>
       <wcs:GetCoverage service="WCS" version="1.1.1">
         <ows:Identifier>medford:elevation</ows:Identifier>
         <wcs:DomainSubset>
           <gml:BoundingBox crs="http://www.opengis.net/gml/srs/epsg.xml#4326">
             <ows:LowerCorner>-123.047 42.231</ows:LowerCorner>
             <ows:UpperCorner>-122.499 42.755</ows:UpperCorner>
           </gml:BoundingBox>
         </wcs:DomainSubset>
         <wcs:Output format="image/tiff"/>
       </wcs:GetCoverage>
     </wps:Body>
   </wps:Reference>

In the GeoServer :guilabel:`WPS request builder`, this is denoted as :guilabel:`RASTER_LAYER`.

Subprocess (SUBPROCESS)
~~~~~~~~~~~~~~~~~~~~~~~

As processes can be chained, the output from one process can provide the input for another process. In this case, an entire process execute request is generated as the input parameter.

In the GeoServer :guilabel:`WPS request builder`, this is denoted as :guilabel:`SUBPROCESS`.

.. _processing.processes.formats.rasterout:

Raster outputs
--------------

For processes generating rasters as the output, the following output types are supported:

* TIFF image
* ArcGrid image
