# Unreleased

## Summary

## Changes

### New Features

### Removed Features

### Bugfixes

# 1.6.0

## Summary

* Upgrade to GeoServer 2.16.0
* Upgrade to GeoTools 22.0
* Upgrade to Spring Boot 2.2.1
* Upgrade to Spring Cloud Hoxton.RELEASE

## Changes

### New Features

* Added configuration properties for TransientCache constructor parameters

### Removed Features

* Removed redis discovery configuration

### Bugfixes

* Fixed NPE issues in catalog when using isolated workspaces
* Fixed REST issues with security endpoint controllers
* Move GeoServerInitializers from RedisGeoServerLoader to BSEInitializer so they get run each startup on all nodes
* Fixed issue where S3 blobstore settings were not properly being saved
* Certain properties not being honored in bootstrap.yml were moved to application.yml

# 1.5.0

## Summary

* Public and open-source release
* Rename from BSE to Stratus

## Changes

### New Features

* Stratus is now open-source, under the GPLv2 license
* The project has been renamed from BSE to Stratus. All package, class, and property names have changed accordingly.
* Stratus docs, integration tests, and deployment configurations have been merged into this repository (previously seperate)

### Removed Features

* Removed Boundless branding from Stratus
* Removed STAC support from WPS
* Removed Spatial Statistics from WPS
* Removed GSR
* Removed previously deprecated Auth, OAuth2 and Backup-Restore modules

### Bugfixes

* Fix WMSInfo SRS list persistance error
* Modified coverage dimension info to properly store the name only and not the name wrapped in the class name

# 1.4.0

## Summary

* Update to GeoServer 2.15.0 and Spring Boot 2
* Support Java 11, and use a Java 11 docker image

## Changes

### Version Updates

* Update to GeoTools 19.0 / GeoServer 2.15.0
* Update Spring Boot 2.1.3.RELEASE
* Update to spring-cloud 2.0.4 (Greenwich)
* Update jedis connector to 2.9.1
* Update lettuce connector to 5.1.4
* Update to GSR 0.6.0 (with Java 11 support)
* Update embedded web seerver to Tomcat 9

### New Features

* Add support for Java 11
* Use new docker base image with JDK 11 included
* GWC S3 Blobstores can now be added using the GeoServer BlobStore UI

### Removed Features

* Backup-Restore has been removed

### Bugfixes

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

# 1.3.1

## Summary

* Update GSR to support GeoTools 20 (Fixes `java.lang.NoClassDefFoundError: com/vividsolutions/jts/geom/Point`)
* Set WFS default MaxFeatures to 1000000 (was 0)
* Upgraded Spring Boot version to 1.5.18.RELEASE
* Exclude grib and netcdf imageio-ext deps from spatialstatistics, fixing startup failures with Stratus-1.3.0-ALL image

# 1.3.0

## Summary

* Update to GeoTools 20.1, GeoWebCache 1.14.1, GeoServer 2.14.1
* Added Spatial Statistics WPS processes, and general WPS enhancements.
* Upgrade GeoServer ServiceInfo configuration persistance, with migration code for Stratus 1.2.0 and older catalogs (enabled by default)

## Changes

### Version Updates

 * Upgraded Spring Cloud version to Edgware.SR5
 * Upgraded Spring Boot version to 1.5.17.RELEASE
 * Update to GeoTools 20.1, GeoWebCache 1.14.1, GeoServer 2.14.1

### New Features

* Added Spatial Statistics WPS processes
* Added WPS-Builder support
* Added STAC support for WPS

### Bugfixes

* Upgrade GeoServer ServiceInfo configuration persistance
  * Move from java-serialization to redis-data, with seperate implementation for each subtype.
  * Add serialization migration and upgrade code
  * Add 1.2.0 migration parameter (`stratus.catalog.redis.enable-stratus-1-2-upgrade`) to application properties
* Improved defaults to OWS Service config
* Fix global GeoServerInfo globalServices persistance
* Update default GWC vector tile formats to use application/vnd.mapbox-vector-tile
* Update GWC Demo Openlayers version to 5.3.0
* Fix Mapbox vector tiles "jitter" when zooming in on point layers
* stratus-gwc - support blobstore rollback on UnsuitableStorageException

# 1.2.0

## Summary

* Added support for Asyncronous WPS, with status backed by Redis, and supporting output to S3
* REST API changes and improvements
* Managed Stratus Auth module
* Various catalog stability improvements, especially around catalog caching and serialization.

## Changes

### Version Updates

 * Upgraded Spring Cloud version to Edgware.SR3
 * Upgraded Spring Boot version to 1.5.14.RELEASE
 * Update GSR to 0.5.1 - Feature CRUD, Form POSTs

### New Features

* Add support for Asyncronous WPS requests via redis
* Add Amazon S3 support for WPS file management
* Add REST endpoint to query WPS Status directly from Redis
* Added backup-restore module to Stratus + necessary changes in order to make it work
* Add an NDVI process to Stratus
* Add Auth module - JWT-based authorization for Managed Stratus
* Add redis-cli REST endpoint, merge keys and values endpoints into /redis/data
* Add /catalog/* REST endpoints for looking up Catalog Info directly
* Add /redis/exec REST endpoint

### Removed Features

* Remove PCF module
* Removed geofence and geonode modules

### Bugfixes

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

# 1.1.1

* Change to amazon linux base docker image (from centos)
* Remove MrSID support

# 1.1.0

## Summary

* Added support for integrated GeoWebCache to Stratus, with configuration backed by Redis
* Updated to GeoServer 2.13.0
* Added extensions:

  * GeoServices REST - ArcGIS REST API
  * S3 GeoTiff module
  * WPS (Only synchronous WPS is supported)

## Changes

### Version Updates

 * Upgraded Spring Cloud version to Edgware.SR1
 * Upgraded GeoServer version to 2.13.0, GeoWebCache version to 1.13.0 and GeoTools version to 19.0
 * Upgraded Lettuce version to 4.4.5.Final
 * Upgraded Spring Boot version to 1.5.13.RELEASE

### New Features

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

### Bugfixes

 * Improve catalog cache preloading for WMS virtual workspace.
 * Adjust OWS mappings to allow for operation at path component.
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
 * Added second redis template that does not enable transaction support for catalog/resource store. Modified caching transactional code to close/return pool connections.
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

# 1.0.0 

## Summary

Initial stable release

