.. _sysadmin.rest:

REST Interface
==============

Stratus provides a REST-based interface for monitoring the application status and interacting with your configuration and data without using the web interface. 

All REST endpoints are available under ``/geoserver/rest/``, except for the embedded GeoWebCache REST endpoints which are available under ``/geoserver/gwc/rest/``.

The :api:`index endpoint <index.yaml>` provides a listing of all available endpoints, including the actuator and redis endpoints.


GeoServer REST Endpoints
------------------------

GeoServer, and the embeded GeoWebCache, contribute many endpoints to the Stratus REST interface. These endpoints are documented in the REST section of the `GeoServer Component Manual <../../geoserver/rest/index.html>`_.

Spring Boot Actuator
--------------------

The Spring Boot Actuator provides REST endpoints for monitoring Stratus. Each endpoint can be controlled using the application properties – for more details refer to the `Spring reference documentation <https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html>`_.

The folowing actuator endpoints are available in Stratus:

* :api:`manage/auditevents <manage-auditevents.yaml>`
* :api:`manage/beans <manage-beans.yaml>`
* :api:`manage/configprops <manage-configprops.yaml>`
* :api:`manage/env <manage-env.yaml>`
* :api:`manage/health <manage-health.yaml>`
* :api:`manage/heapdump <manage-heapdump.yaml>`
* :api:`manage/httptrace <manage-trace.yaml>`
* :api:`manage/info <manage-info.yaml>`
* :api:`manage/loggers <manage-loggers.yaml>`
* :api:`manage/mappings <manage-mappings.yaml>`
* :api:`manage/metrics <manage-metrics.yaml>`
* :api:`manage/threaddump <manage-dump.yaml>`

Redis Endpoints
---------------

Stratus also provides REST endpoints for interacting with the Redis catalog backend. These are intended primarily for troubleshooting purposes.

.. warning:: Some of these endpoints (``redis/exec`` and ``redis/terminal``) allow you to change the contents of redis. This may corrupt the Stratus catalog or configuration. Use these endpoints with **extreme** caution.

* :api:`redis/connection <redis-connection.yaml>`
* :api:`redis/data <redis-data.yaml>`
* :api:`redis/exec <redis-exec.yaml>`
* :api:`redis/info <redis-info.yaml>`
* :api:`redis/reinitialize <redis-reinitialize.yaml>`
* :api:`redis/terminal <redis-terminal.yaml>`

WPS Enpoints
------------

Stratus provides a WPS endpoint for querying WPS request status:

* :api:`wps <wps.yaml>`

.. TODO: Add other endpoints as necessary