.. _sysadmin.stratus:

Administration on Stratus
=========================

This document contains information about various tasks specific to Stratus. For more details, please see the :ref:`sysadmin` section.

Controlling the PostgreSQL service
----------------------------------

PostgreSQL should be set up using AWS Aurora. See :ref:`dataadmin.pgGettingStarted` to learn more about working PostgreSQL.

Working with GeoServer
----------------------

Redis and the GeoServer Data Directory
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

**Redis** is an in-memory data structure store, used as a database, cache, and message broker. Stratus uses Redis for Geoserver to maintain all of its configurations and Geoserver resources. Stratus does not cache any resources but instead fetches configuration from the Redis on each request. This renders Stratus stateless and scalable.

The Resource Store within Redis stores the binary contents of everything that would be normally in the Data Directory, and still uses the relative folder structure of the data directory. The contents of the Resource Store can be viewed via REST at ``geoserver/rest/resource``.

Geoserver instances in a Stratus deployment also have a Geoserver Data Directory, which is a unique folder on each instance within Stratus. It is still the default lookup path when adding a store using the Web UI, but is otherwise largely unused by Stratus. Each Geoserver Data Directory is inaccessible to all other instances within Stratus. The Geoserver Data Directory still has a potential application if it is configured to as shared folder, refer to :ref:`creating a shared folder reference <sysadmin.filesystem>` for more information.

.. _sysadmin.stratus.resource:

Modify resources with REST
^^^^^^^^^^^^^^^^^^^^^^^^^^

Within Stratus, **resources** are any item that does not represent catalog or configuration data. Stratus uses a Redis resource store to handle all resources. Typical resources include styles and icons.

To see a list of available resources direct your request to ``geoserver/rest/resource``. Listed resources can be accessed and modified by appending ``/{PATH_TO_RESOURCE}``.

For example the following creates a new style resource in the ``/styles`` directory:

.. code-block:: bash

      curl -u admin:geoserver -X PUT --data "@new_point.xml" http://geoserver/rest/resource/styles/new_point.xml

Where new_point.xml contains the following content:

.. code-block:: xml

      <style>
        <id>StyleInfoImpl-f49d2a5:1638a37ae87:-7fdb</id>
        <name>point</name>
        <format>sld</format>
        <languageVersion>
          <version>1.0.0</version>
        </languageVersion>
        <filename>new_point.sld</filename>
      </style>

.. note:: Using REST differentiates requests made against metadata with ``operation=metadata``. See the REST documentation below for more information.

For a full list of REST resource operations refer to the :api:`Geoserver Resource REST API documentation <resource.yaml>`.

How to add startup parameters for GeoServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Stratus configuration is done so using Spring Configuration and Environment Variables. To learn about ways to configure parameters for Geoserver refer to the instructions here :ref:`sysadmin.config`

.. note:: You can view existing Java options (:guilabel:`system-properties`) and environment variables (:guilabel:`system-environment`) on the :api:`GeoServer Detailed Status Page  <manifests.yaml>` at ``geoserver/rest/about/status``.
