.. _intro.whatis:

What is Stratus?
================

Stratus is a complete web-based geospatial software stack optimized for the cloud, based on `GeoServer <http://geoserver.org/>`_. 

How does it differ from GeoServer?
----------------------------------

The primary difference between Stratus and GeoServer is substantial changes to the data catalog:

 * It is no longer stored on disk; it is instead stored in an in-memory data store called `Redis <https://redis.io/>`_.
 * It is no longer XML-based, and is instead stored in a serialization format optimized for Redis.
 * It is no longer stateful; instead of loading the catalog into GeoServer at startup, all data is fetched directly from Redis when it is needed.

This allows Stratus to be be scaled across multiple nodes with no local system storage dependencies. Refer to :ref:`sysadmin.stratus` for more details.

Stratus includes a number of :ref:`extensions <intro.extensions>` by default, and does not support all extensions that are available to GeoServer.

Stratus also includes :ref:`additional REST endpoints <sysadmin.rest>` intended for managing some of its unique features.