.. _whatsnew:

What's new in Stratus
#####################

What's new in 1.5.0
===================

Stratus 1.5.0 is a major release, and the first public, open-source release of the project. It includes significant refactoring of package and class names, but is similar to 1.4.0 in actual functionality.

Prior to this release, **Stratus** was known as **Boundless Server Enterprise** or **BSE**.

Highlights:
-----------

Name Change
^^^^^^^^^^^

Stratus has been renamed from **Boundless Server Enterprise** to **Stratus**. All package, class, and property names have changed accordingly.

* ``com.boundlessgeo.bse.*`` packages have been refactored to ``stratus.*``
* ``BSE`` in class and module names has been replaced with ``Stratus``
* ``boundless.bse`` and ``bse`` properties have been consolidated under ``stratus``. For more details, refer to :ref:`sysadmin.config`.

Open-source
^^^^^^^^^^^

Stratus is now released as a public, open-source project, under the `GPLv2 license <https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html>`_.

Removed Features
^^^^^^^^^^^^^^^^

Some private and/or deprecated modules have been removed from Stratus:

* Removed Spatial Statistics and STAC support from WPS
* Removed GSR
* Removed Auth, OAuth2 and Backup-Restore modules

Changelog
---------

New Features
^^^^^^^^^^^^

* Stratus is now open-source, under the GPLv2 license
* The project has been renamed from BSE to Stratus. All package, class, and property names have changed accordingly.
* Stratus docs, integration tests, and deployment configurations have been merged into this repository (previously seperate)

Removed Features
^^^^^^^^^^^^^^^^

* Removed Boundless branding from Stratus
* Removed STAC support from WPS
* Removed Spatial Statistics from WPS
* Removed GSR
* Removed previously deprecated Auth, OAuth2 and Backup-Restore modules

Bugfixes
^^^^^^^^

* Fix WMSInfo SRS list persistance error
* Modified coverage dimension info to properly store the name only and not the name wrapped in the class name


What's new in 1.4.0
===================

Stratus 1.4.0 is a major release, including version updates GeoServer and Spring Boot, and adding support for Java 11.

Highlights:
-----------

GeoServer 2.15
^^^^^^^^^^^^^^

Stratus 1.4.0 includes GeoTools 21.0, GeoWebCache 1.15.0, and GeoServer 2.15.0, each of which include various improvements and bugfixes.

Notable changes include:
 
 * Support for Java 11
 * Layer Service Settings
 * Style Editor SLD Auto-Complete
 * WPS GetExecutionStatus and Dismiss Opterations
 * JAI-EXT enabled by default

For more details, refer to the `GeoServer 2.15.0 release blog post <http://blog.geoserver.org/2019/03/02/geoserver-2-15-0-released/>`_.

Spring Boot 2
^^^^^^^^^^^^^

Stratus 1.4.0 includes Spring Boot 2.1.3 and Spring Framework 5.1.5, which include substantial changes and many bugfixes.

Notable changes include

* Support for Java 11
* Spring Boot Actuator improvements
* Spring security changes and improvements

As part of the Spring Boot 2 upgrade a number of application properties changed. If you are using any of these properities for custom Stratus configuraitons, you should update them:

* ``server.context-path`` changed to ``server.servlet.context-path``
* ``server.context_parameters.*`` changed to ``server.servlet.context_parameters.*``
* ``server.servlet-path`` changed to ``spring.mvc.servlet.path``
* ``server.session.*`` changed to ``servlet.session.*``

For more details on application properties in Stratus, refer to the :ref:`sysadmin.config` section.

Java 11
^^^^^^^

With Spring Boot 2 and GeoServer 2.15 both now supporting Java 11, the Stratus 1.4.0 docker image now runs on Java 11 by default.

In partuclar, Stratus is using `Amazon Corretto 11 <https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/what-is-corretto-11.html>`_, which is an OpenJDK 11 build, with some special enhancements for running on Amazon Linux (while still supporting all other OSes).

Previous versions versions of Stratus included custom configuration to enable the Marlin Renderer. The Marlin renderer is included by default in Java 11, so this custom configuration has been removed, as it is no longer necessary.

Changelog
---------

Version Updates
^^^^^^^^^^^^^^^

* Update to GeoTools 19.0 / GeoServer 2.15.0
* Update Spring Boot 2.1.3.RELEASE
* Update to spring-cloud 2.0.4 (Greenwich)
* Update jedis connector to 2.9.1
* Update lettuce connector to 5.1.4
* Update to GSR 0.6.0 (with Java 11 support)
* Update embedded web seerver to Tomcat 9

New Features
^^^^^^^^^^^^

* Add support for Java 11
* Use new docker base image with JDK 11 included
* GWC S3 Blobstores can now be added using the GeoServer BlobStore UI

Removed Features
^^^^^^^^^^^^^^^^

* Backup-Restore has been removed

Bugfixes
^^^^^^^^

* Fixed incompatibilies between spatialstatistics and grib/netcdf
* Fix issues with AttributeTypeInfo (de)serialization
* Update bootstrap and default application properties to use Spring Boot 2 names
* Disable spring-cloud auto refresh to fix geoserver startup issues
* Add programatic Stratus Rest security configuration
* Exclude spring-jcl in favor of commons-logging for API compatibility with GeoServer
* Update jedis / lettuce connection factories for Spring Boot 2
* Add new dispatcher registration bean config to work around servlet path info issue.
* Remove spring profile from pom.xml (overriding actual profile in bootstrap.yml)
* Disable Spring Aop Autoconfiguration
* Move authentication manager config to WebSecurityConfigurer Disable basic and anonymous Spring filters
* Remove Spring's CSRF protection to get web login working
* Fixed rest content negotiation for Spring Boot 2 compatibility.
* Support virtual services in WMTS
* Fix conflict between Spring Boot and GWC contexts
* Fix jackson-databind serialization issues
* Fix style initialization NPE
* Fix JDK 11 internal API usage violations
* Removed deprecated and duplicate redis code
* Update RestConfiguration and fix circular dependency errors
* Fix GS JSON rest endpoint message conversion
* Resolve (most) split packages between Stratus and other projects.
* Resolve Stratus-internal split packages.
* Add custom redis converter for NetCDFSettingsContainer, fixing startup failure when using netcdfout
* WPSStorageCleaner should log to its own package
* Add X-Frame-Options: SAMEORIGIN so OL GetFeatureInfo works
* Set default timezone for Stratus to GMT to fix oracle connection issues
* Fix error where OWS Cache preloading will crash and swallow exceptions if there is an error parsing the opening tag of and XML Post request
* Fix error where JAITools processes did not get registered with JAI resulting in a "java.lang.IllegalArgumentException: The input argument(s) may not be null." response when running certain WPS processes, such as ras:Contour


What's new in 1.3.1
===================

Stratus 1.3.1 is a patch release, containing the following bugfixes:

Changelog
---------

* Update GSR to support GeoTools 20 (Fixes `java.lang.NoClassDefFoundError: com/vividsolutions/jts/geom/Point`)
* Set WFS default MaxFeatures to 1000000 (was 0)
* Upgraded Spring Boot version to 1.5.18.RELEASE
* Exclude grib and netcdf imageio-ext deps from spatialstatistics, fixing startup failures with Stratus-1.3.0-ALL image

What's new in 1.3.0
===================

Stratus 1.3.0 includes updates to most components, as well as some enhancements and numerous bugfixes.

Hightlights:
------------

GeoServer 2.14
^^^^^^^^^^^^^^

Stratus 1.3.0 includes GeoTools 20.1, GeoWebCache 1.14.1, and GeoServer 2.14.1, each of which include various improvements and bugfixes.

Notable changes include:
 
 * WMS “nearest match” support for time dimension
 * SLD Channel selection name allow expressions
 * SLD Map algebra
 * PostGIS store improvements and measured geometries support
 * Image mosaic improvements
 * Style editor improvements
 * JTS Upgrade

For more details, refer to the `GeoServer 2.14.0 release blog post <http://blog.geoserver.org/2018/09/24/geoserver-2-14-0-released/>`_.

Spatial Statistics WPS
^^^^^^^^^^^^^^^^^^^^^^

Stratus 1.3.0 includes a great many new WPS processes from the Spatial Statistics project.

GeoServer Configuration Serialization
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In order to handle some changes introduced in GeoServer 2.14.0, the GeoServer ServiceInfo configuration persistance has been modified. Migration code for Stratus 1.2.0 and older catalogs is included, and enabled by default - if you have an existing catalog you should be able to upgrade to Stratus 1.3.0 without issue.

If you are starting fresh with Stratus 1.3.0, you do not need to have this migration code enabled, and can disable it by :ref:`setting <sysadmin.config>` the ``stratus.catalog.redis.enable-stratus-1-2-upgrade`` application property to ``false``.

Changelog
---------

Version Updates
^^^^^^^^^^^^^^^

 * Upgraded Spring Cloud version to Edgware.SR5
 * Upgraded Spring Boot version to 1.5.17.RELEASE
 * Update to GeoTools 20.1, GeoWebCache 1.14.1, GeoServer 2.14.1

New Features
^^^^^^^^^^^^

* Added Spatial Statistics WPS processes
* Added WPS-Builder support
* Added STAC support for WPS

Bugfixes
^^^^^^^^

* Upgrade GeoServer ServiceInfo configuration persistance

  * Move from java-serialization to redis-data, with seperate implementation for each subtype.
  * Add serialization migration and upgrade code
  * Add 1.2.0 migration parameter (``stratus.catalog.redis.enable-stratus-1-2-upgrade``) to application properties

* Improved defaults to OWS Service config
* Fix global GeoServerInfo globalServices persistance
* Update default GWC vector tile formats to use application/vnd.mapbox-vector-tile
* Update GWC Demo Openlayers version to 5.3.0
* Fix Mapbox vector tiles "jitter" when zooming in on point layers
* stratus-gwc - support blobstore rollback on UnsuitableStorageException

What's new in 1.2.0
===================

Stratus 1.2.0 includes an assortment of improvements, bugfixes, and new features.

Hightlights:
------------

Asynchronous WPS
^^^^^^^^^^^^^^^^

Stratus 1.2.0 adds suport for Asyncronous WPS requests to Stratus. Request status is stored in redis, and is consistent across Stratus nodes. Request results can be stored locally, or in Amazon S3.

New REST endpoints
^^^^^^^^^^^^^^^^^^

Several new REST endpoints have been added for managing Stratus and Redis. Refer to the :ref:`REST Interface <sysadmin.rest>` section for more details.

GSR Improvements
^^^^^^^^^^^^^^^^

The ArcGIS REST API now supports more endpoints, most notably feature creation, updates, and deletion.

Backup-Restore improvements
^^^^^^^^^^^^^^^^^^^^^^^^^^^

The Backup-Restore module is now more tightly integrated with Stratus, and should be more reliable.

Changelog
---------

Version Updates
^^^^^^^^^^^^^^^

 * Upgraded Spring Cloud version to Edgware.SR3
 * Upgraded Spring Boot version to 1.5.14.RELEASE
 * Update GSR to 0.5.1 - Feature CRUD, Form POSTs

New Features
^^^^^^^^^^^^

* Add support for Asyncronous WPS requests via redis
* Add Amazon S3 support for WPS file management
* Add REST endpoint to query WPS Status directly from Redis
* Added backup-restore module to Stratus + necessary changes in order to make it work
* Add an NDVI process to Stratus
* Add Auth module - JWT-based authorization for Managed Stratus
* Add redis-cli REST endpoint, merge keys and values endpoints into /redis/data
* Add /catalog/* REST endpoints for looking up Catalog Info directly
* Add /redis/exec REST endpoint

Removed Features
^^^^^^^^^^^^^^^^

* Remove PCF module
* Removed geofence and geonode modules

Bugfixes
^^^^^^^^

* CachePreloading: Delegating keys should return null if delegate query fails
* Fix NPE in nested layer groups when using cache preloading
* Fix layer not found errors for global layer groups
* Added more detailed logging for s3 read error.
* Add DEBUG loging for RedisMultiQueryCachingEngine queries Fix bug in CachingEngine with empty-result query responses Fix caching error when there is no default workspace
* Use 10 retries for WPS S3 file retrieval
* Fix stackoverflow for ExecutionType clash with ExecutionStatus
* Handle GWC services in OWSVirtualServiceCallback (fixes 404 errors)
* Managed Stratus Jenkins build
* WPS S3 should use local storage by default
* Move wps s3 configuration to seperate yaml file (off by default)
* Log failed WPS S3 connection attemps as error; cancel S3 storage cleaner and de-escelate to info after 5 failed attemps.
* Suport Amazon S3 for WPS file management
* Catalog anti-corruption - roll back failed catalog changes on add and modify
* Ensure linearizationTolerance property of FeatureTypeInfo serializes and deserializes from Redis
* OWS requests should return 404 if an invalid virtual service is provided
* Support serving static web resources (logos, icons, etc.)
* Support cache preloading for unprefixed layer names when using virtual workspaces
* Fixed prefix issue with jndi store configuration
* getCatalogInfoByName REST endpoint returns layer instead of resource
* Remove unused autoconfigured beans
* Managed Stratus Auth - Handle Anonymous user
* Stricter auth restrictions on REST endpoints by default

What's new in 1.1.0
===================

Stratus 1.1.0 is the first major update since the initial 1.0.0 release, and includes a number of new features and bugfixes.

Highlights:
-----------

Integrated GeoWebCache
^^^^^^^^^^^^^^^^^^^^^^
Stratus 1.1.0 adds support for integrated GeoWebCache to Stratus. This means that Stratus will automatically create (and update) tile layer caches for layers you add to Stratus.

* As with the GeoServer catalog and configuration, GeoWebCache configuration is stored in Redis.
* New `REST endpoints <../../geoserver/rest/index.html>`_ have been added to allow for modifying GWC configuration.


For more details, refer to :ref:`sysadmin.caching`.

New Components
^^^^^^^^^^^^^^
Stratus 1.1.0 includes several new components, refer to the respective documentation for more details.
  * GeoServices REST - ArcGIS REST API
  * `S3 GeoTiff module <../../geoserver/community/s3-geotiff/index.html>`_.
  * :ref:`WPS <processing.intro.wps>` (Only synchronous WPS is supported)

GeoServer 2.13
^^^^^^^^^^^^^^
Stratus 1.1.0 includes GeoServer 2.13.0, which adds a number of features:

* Isolated workspaces
* UI Improvements
* GeoPackage performance improvements
* Support for more PostGIS data types
* Beter label position control in map rendering
* Coverage views from heterogeneous bands

For more details, see the `GeoServer 2.13.0 release anouncement <http://blog.geoserver.org/2018/03/20/geoserver-2-13-0-released/>`_.

Backup-Restore improvements
^^^^^^^^^^^^^^^^^^^^^^^^^^^

The Backup and Restore module also saw some improvements:

* Add the ability to skip deleting some items when restoring, particularly existing worksapces.
* Add the ability to parameterize outgoing passwords when doing a backup, and replace them upon restore.

For more details on Backup and Restore, refer to `Backing up <https://docs.boundlessgeo.com/bse/1.3.1/sysadmin/backup.html>`_.

Changelog
---------

Version Updates
^^^^^^^^^^^^^^^

 * Upgraded Spring Cloud version to Edgware.SR1
 * Upgraded GeoServer version to 2.13.0, GeoWebCache version to 1.13.0 and GeoTools version to 19.0
 * Upgraded Lettuce version to 4.4.5.Final
 * Upgraded Spring Boot version to 1.5.13.RELEASE

New Features
^^^^^^^^^^^^

 * Stratus-GWC Integration, with GWC config backed by redis
 * Add s3-geotiff dependency
 * Add WPS extension to default build
 * Add GSR extension to stratus-application
 * Added redis manager controller reinitialize method
 * Support OWS Virtual Services endpoints
 * Support Isolated Workspaces
 * Added WMTSLayerInfo support to catalog facade
 * Added config pool config for sentinel configurations
 * Added unsupported response for /rest/redis endpoints in cluster/sentinel configurations
 * Added redis info controller endpoint
 * Refactored pcf-sso into separate oauth2 and pcf modules; added externalized sessions for pcf
 * Added pcf-sso module
 * Added lettuce config for pcf

Bugfixes
^^^^^^^^

 * Improve catalog cache preloading for WMS virtual workspace
 * Adjust OWS mappings to allow for operation at path component
 * Update base docker image - fixes various security vulnerabilities
 * Fix GWC parameter filter (de)serialization
 * Add Stratus branding to GWC homepage
 * Fix GWC rest links so they include '/gwc'
 * Reload global geoserver info across nodes on modification
 * Don't register redis converters as beans, only use CustomConversions (Fixes DimensionInfoImpl conversion errors)
 * Fix error clearing initialization lock before the instance has finished initializing
 * Handle unresolvable resources in RedisCatalogImpl
 * Fixed issues persisting GeoServerTileLayer info objects to redis
 * Added custom tomcat config for specifying relative redirect URIs
 * Fix integrated GWC automatic tile truncation for style changes
 * Added code to prevent servlet containers from appending jsessionid parameters to url
 * Bug fix for using the redis manager controller to view the raw contents of catalog info object hashes in redis
 * Make GWC ServerConfiguration initialization occur only once, on the master node
 * Fix GeoGig module
 * Fix caching configuration and cache deletes
 * Refactored locking provider to use functional interface
 * Added converter for DimensionInfoImpl
 * Added second redis template that does not enable transaction support for catalog/resource store. Modified caching transactional code to close/return pool connections
 * JNDI should support both tomcat jdbc and dbcp
 * Moved redis session config to stratus-application and made it configurable via properties
 * Excluded bruteForceListener from bean instantiation
 * Added VirtualTable converter class to handle VirtualTable metadata objects
 * Added more info to the redis clients controller endpoint
 * Added all available pool config properties to RedisConfigProps
 * Set default theme to light
 * Added single shared connection for scan operations; updated union/intersection set operations
 * Fixed redis controller to create authenticated connections to redis if necessary
 * Update Stratus logo
