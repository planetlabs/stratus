.. _processing.intro:

Introduction to Spatial Processing
==================================

What is Spatial Processing?
---------------------------

There are many aspects to a complete GIS. There is **map composition**; the layering of data, the placement of map artifacts, and so forth. Intertwined with this is **map cartography**, the actual visualization of the data, and the manner in which it is conveyed.

The other aspect of GIS, which is arguably its most basic, is that of **processing** (also known as **analysis**). Processing refers to the management and operations applied in order to ask questions and derive insight from spatial data.

Under the heading of "asking questions", one might wish to know:

 | "How far is the dam from the town?"
 | "Where are the peaks of the tallest mountains?"
 | "What is the viewshed of this particular tower?"

And when it comes to "deriving insight", one might wish to accomplish the following:

 | "Create the watershed area of this particular river"
 | "Combine census blocks into counties."
 | "Find the areas not covered by a viewshed of any of these towers."

These questions and many more like them can be asked and answered via spatial processing.

In the past, web mapping software has focused primarily on composition and display, leaving processing to desktop clients. But web-based tools today, such as Stratus, have a wide range of processing capabilities, all but equaling their desktop equivalents.


Spatial Processing in Stratus
-------------------------------------------------

Spatial processing is available through a variety of tools in Stratus.

.. _processing.intro.wps:

Web Processing Service
~~~~~~~~~~~~~~~~~~~~~~

One powerful method of performing spatial processing is through the :term:`Web Processing Service`, or **WPS**. This OGC-based protocol, analogous to other protocols such as Web Map Service (WMS) and Web Feature Service (WFS), allows for client-server interaction with server-hosted "processes". A server can provide WPS processes, which can then be executed by clients on data they supply or applied to existing server-side datasets.

Processes fall into three categories:  vector, raster, and geometry, referring to the type of geospatial content used as the process's input. These categories are broad, as processes can take multiple types of input.

WPS is a `service published by GeoServer <../geoserver/extensions/wps/>`_ and so is an integral part of Stratus. It provides a collection of many standard processes along with the ability to add additional ones.

For example, one can run the ``geo:union`` process on a collection of geometries to output a single geometry that is the union of them. Processes can be chained, so one can run the ``vec:Reproject`` process to reproject a raster image to a different SRS, then take the output of that and run ``ras:CropCoverage`` to crop the raster down to a certain bounds. The result can be fed into the ``gs:Import`` process to save the resulting coverage as a new layer in GeoServer, for use by other clients.

.. _processing.intro.wps.enable:

Enabling WPS
^^^^^^^^^^^^

 WPS is included in GeoServer by default, but disabled. To enable WPS:

 #. In the GeoServer web interface, click :guilabel:`WPS` under :guilabel:`Services`.

 #. Check :guilabel:`Enable WPS`, then click :guilabel:`Submit`.

SQL Views
~~~~~~~~~

Another way to perform spatial processing is by using the `SQL Views <../geoserver/data/database/sqlview.html>`_ functionality in GeoServer. When backed by the powerful capabilities of a database such as PostGIS, SQL Views provide an effective way to transform and analyze spatial datasets. Using a SQL View, a layer can be defined as the result of a spatial query on a PostGIS dataset. This layer can then be visualized by WMS, queried via WFS, and even used in further processing operations. Spatial queries in PostGIS support a wide variety of spatial processing, including data transformation, spatial analysis, and even raster processing.

WPS S3 Storage Configurations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

S3 storage is available for the results of WPS requests. Below is an example of the configuration needed to enable S3 storage assuming you have the correct access to a bucket. For more information on how to set up various Stratus application properties see the `Application Configuration section <../sysadmin/config/index.html>`_. ::

  wps:
    file-storage: s3 #anything other than "s3" here defaults to local file storage, which may not work well for multi-instance deployments
    s3-region: US_EAST_1 #All caps with underscores should be used as the separator here.  See https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/regions/Regions.html
    s3-url: https://s3.amazonaws.com/
    s3-bucket: <AWS S3 Bucket Name> #This bucket will be created if it doesn't already exist
    access-key: <Access Key> #optional if there is an Amazon credential chain external to Stratus
    secret-key: <Secret Key> #optional if there is an Amazon credential chain external to Stratus
