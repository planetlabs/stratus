.. _intro.extensions:

Stratus components
==================

Stratus comes with a number of components that add functionality. These are equivalent to the extensions in GeoServer, except that they are included by default in Stratus—you do not need to do anything to install them.

.. _intro.extensions.gdal:

GDAL Image Formats
------------------

The GDAL Image Formats component adds the ability for GeoServer to publish data from extra raster data sources, through the use of `GDAL <http://www.gdal.org/>`_. These formats include, but are not limited to DTED, EHdr, AIG, and ENVIHdr.

.. _intro.extensions.geopackage:

GeoPackage
----------

The GeoPackage component adds the ability for GeoServer to publish data from `GeoPackage <http://www.geopackage.org/>`_ sources (a data format based on `SQLite <http://www.sqlite.org/>`_).


.. _intro.extensions.jp2k:

JPEG 2000
---------

The JPEG 2000 component adds the ability for GeoServer to publish data from `JPEG 2000 <https://jpeg.org/jpeg2000/index.html>`_ sources. This image format utilizes wavelet compression for more efficient storage. 

.. _intro.extensions.mbstyle:

Mapbox Styles
-------------

The Mapbox Styles component adds `Mapbox styles <https://www.mapbox.com/mapbox-gl-js/style-spec/>`_ as a supported styling language for GeoServer.

For more information, see the :ref:`cartography.mbstyle.tutorial`.

.. _intro.extensions.vectortiles:

Vector Tiles
------------

The Vector Tiles component adds a number of output formats to GeoServer that deliver geographic data to a browser or other client application in tiles which using a vector representation of the features in the tile. Vector tiles improve the performance of maps fast while offering full client-side design flexibility.

For more information on Vector Tiles, please see the :ref:`dataadmin.vectortiles` section.

.. _intro.extensions.wps:

Web Processing Service
----------------------

The Web Processing Service (WPS) provides a service where users or client applications can execute spatial processes on arbitrary spatial datasets, including but not limited to any data published by Stratus.

For more information, see the :ref:`processing.intro.wps` section.

