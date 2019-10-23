# Stratus Tagging and Versioning Guidelines

Stratus Nightly builds and releases can be found on [docker.io](https://docker.io/repository/gsstratus/stratus?tab=tags)

## Profiles

The Stratus build is configured using Maven profiles. These profiles determine what compents are included (and what aren't) in any Stratus build.

All the profiles are configured in [stratus-application/pom.xml](./stratus-application/pom.xml).

A number of profiles are enabled by default. They can be disabled by including the `-Dminimal` property in your maven build.

When adding a new profile, the profile name must not contain a dash (`-`), as that is used as a delimiter for tagging (below).

The full list of profiles are:

| Profile Id  | Default? | Description                                                                                            |
|-------------|----------|--------------------------------------------------------------------------------------------------------|
| appschema   | false    | GeoServer [Application Schema Support Extension](http://docs.geoserver.org/latest/en/user/data/app-schema/index.html). |
| colormap    | false    | GeoServer [Dynamic Colormap Generation Community Module](http://docs.geoserver.org/latest/en/user/community/colormap/index.html). |
| csw         | false    | GeoServer [Catalog Services for the Web Extension](http://docs.geoserver.org/latest/en/user/services/csw/index.html). |
| elasticgeo  | false    | NGA GeoInt [ElasticGeo plugin](https://github.com/ngageoint/elasticgeo). |
| gdal        | **true** | GeoServer [GDAL Image Formats Extension](http://docs.geoserver.org/latest/en/user/data/raster/gdal.html). |
| gdalwcs     | false    | GeoServer [GDAL based WCS Output Format Community Module](http://docs.geoserver.org/latest/en/user/community/gdal/index.html). |
| gdalwps     | false    | GeoServer GDAL based WPS Output Format Community Module. |
| geogig      | false    | GeoServer [GeoGig Community Module](http://geogig.org/docs/interaction/geoserver_ui.html). |
| geopkg      | **true** | GeoServer [GeoPackage Data Store Extension](http://docs.geoserver.org/latest/en/user/community/geopkg/). |
| grib        | false    | GeoServer [GRIB Data Store Extension](http://docs.geoserver.org/latest/en/user/extensions/grib/grib.html). |
| gwc         | **true** | Stratus Embedded GeoWebCache support. |
| jp2k        | **true** | GeoServer [JP2K Data Store Extension](http://docs.geoserver.org/latest/en/user/extensions/jp2k/index.html). |
| libjpegturbo | false   | GeoServer [libjpeg-turbo Map Encoder Extension](http://docs.geoserver.org/latest/en/user/extensions/libjpeg-turbo/index.html). |
| mbstyle     | **true** | GeoServer [MapBox Styling Community Module](http://docs.geoserver.org/latest/en/user/styling/mbstyle/index.html). |
| mongo       | false    | GeoServer [MongoDB Data Store Extension](http://docs.geoserver.org/latest/en/user/extensions/mongodb/index.html). |
| monitor     | false    | GeoServer [Monitoring Extension](http://docs.geoserver.org/latest/en/user/extensions/monitoring/index.html). |
| netcdf      | false    | GeoServer [NetCDF Data Store Extension](http://docs.geoserver.org/latest/en/user/extensions/netcdf/netcdf.html). |
| netcdfout   | false    | GeoServer [NetCDF WCS Output Format Extension](http://docs.geoserver.org/latest/en/user/extensions/netcdf-out/index.html). |
| oauth2      | false    | Stratus OAuth2 plugin. **EXPERIMENTAL**. |
| ogrwfs      | false    | GeoServer [OGR based WFS Output Format Extension](http://docs.geoserver.org/latest/en/user/extensions/ogr.html). |
| ogrwps      | false    | GeoServer [OGR based WPS Output Format Extension](http://docs.geoserver.org/latest/en/user/extensions/ogr.html#ogr-based-wps-output-format). |
| oracle      | false    | GeoServer [Oracle Data Store Extension](http://docs.geoserver.org/latest/en/user/data/database/oracle.html). |
| printing    | false    | GeoServer [Printing Extension](http://docs.geoserver.org/latest/en/user/extensions/printing/index.html). |
| secured     | false    | Secure Stratus Application Profile. Used for customer deployments with a valid Stratus Support Key. |
| sldservice  | false    | GeoServer [SLD REST Service Community Module](http://docs.geoserver.org/latest/en/user/community/sldservice/index.html). |
| sqlserver   | false    | GeoServer [Microsoft SQL Server Data Store Extension](http://docs.geoserver.org/latest/en/user/data/database/sqlserver.html). |
| vectortiles | **true** | GeoServer [Vector Tiles WMS Output Format Extension](http://docs.geoserver.org/latest/en/user/extensions/vectortiles/index.html). |
| webadmin    | false    | GeoServer Web GUI. |
| wps         | **true** | GeoServer [Web Processing Service Extension](http://docs.geoserver.org/latest/en/user/services/wps/). |
| xauth       | false    | Monsanto Header Authentication **EXPERIMENTAL**. |
| ysld        | false    | GeoServer [YSLD Styling Extension](http://docs.geoserver.org/latest/en/user/styling/ysld/index.html). |


## Tags

When a Stratus docker image is built, it is given a unique tag and pushed to [docker.io](https://docker.io/repository/gsstratus/stratus?tab=tags).

Tags should conform to the following naming scheme:

`${VERSION}-${DESCRIPTION}`

Where:

`${VERSION}` is the Stratus version, e.g. `1.1.0`. In the case of a nightly or snapshot build, the version should end with `-SNAPSHOT`.

`${DESCRIPTION}` is one of:

* `BASIC` - Includes the default profiles, plus `webadmin`.

* `ALL` - Includes everything in the `BASIC` build, plus every data format extension: `grib`, `mongo`, `netcdf`, `oracle`, `sqlserver`.

* Customer name - In some cases, a unique customer build will be created, and tagged with the customer name. Such a build will typically include the `secured` profile. The person supporting the customer is responsible for tracking, building and managing any custom images built for that customer. This includes maintaining the list of profiles that should be included in such a build.

* The full list of profile names included in the build - **Not Reccomended**. For cases where a build that doesn't conform to one of the three main tags (above) is required. Names should be case-sensitive, dash-delimited, and in alphabetical order. Default profiles are not included in the list of names. For example: `mongo-oracle-sqlserver-webadmin`. If such a build starts to become commonly used, a new representative name should be added to the three above, and that should be used instead.

Historical data on what profiles are included in a specific release (including any custom customer tags) is tracked in [this spreadsheet](https://docs.google.com/spreadsheets/d/1ONTcDKsmtxi4HTDc81-lq4jEEnq268aPTCLqJDY9_GU/edit). It should be updated whenever necessary.

### Latest

The `latest` tag is the most recent build of the master branch, with `BASIC` profiles.

### Development builds

In some cases, builds of a development (i.e. non-master) branch may be required. Such builds should just use the branch name as a tag. `BASIC` profiles are assumed; if this is not the case append the appropriate tag.


## Versions

The Stratus version number is formatted `${MAJOR}.${MINOR}.${PATCH}`, where:

* `MAJOR` - The major version. Currently `1`.
* `MINOR` - The minor version. Incremented for any backwards-incompatible API or implementation change (including any change in Redis serialization format), or when any significant new features are added.
* `PATCH` - The patch version. Incremented for any release with only minor improvements and bug fixes. In the future, Stratus is expected to publish such patch releases on a monthly or weekly basis.

On the Stratus `master` branch in git, the version is expected to always be `${VERSION}-SNAPSHOT`, where `${VERSION}` is the next (unreleased) version. If any commits are merged to master which merit updating the minor version, this must be done manually. Otherwise, the release process in Jenkins will handle updating the patch version.
