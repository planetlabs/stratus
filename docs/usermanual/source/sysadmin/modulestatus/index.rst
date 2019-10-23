.. _sysadmin.module_status:

Module Status
=============

The Module Status REST endpoint allows modules, plugins, and extensions to easily report their status information. This endpoint also reports the configuration environment of the running GeoServer instance. The Module Status information can be retrieved as HTML, XML, or JSON through the browser or the cURL utility. The default output format for the endpoint is HTML.

The information returned is:

.. list-table::
   :class: non-responsive
   :header-rows: 1
   :stub-columns: 1

   * - Variable
     - Description
   * - Module
     - Module identifier based on the artifact name
   * - Component (optional)
     - Identifier of component (ie. system-properties)
   * - Version (optional)
     - Version of the module
   * - Enabled
     - Boolean value indicating if the module is enabled
   * - Available
     - Boolean value indicating if module is avaiable
   * - Message (optional)
     - A status message describing the configuration of the module
   * - Documentation (optional)
     - Link to the module user manual

* Sample request:

  .. code-block:: bash

      curl -v -u admin:geoserver -XGET http://localhost:8080/geoserver/rest/about/status.xml

* HTML: http://localhost:8080/geoserver/rest/about/status

  .. figure:: img/html.png

* XML: http://localhost:8080/geoserver/rest/about/status.xml

  .. figure:: img/xml.png

* JSON: http://localhost:8080/geoserver/rest/about/status.json

  .. figure:: img/json.png
