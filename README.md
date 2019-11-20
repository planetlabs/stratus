# Stratus
Stratus is a Spring Boot packaging of Geoserver (currently targeting 2.16) that is designed for Enterprise/Cloud deployments. Due to the magic of Spring and Maven, Stratus doesn't actually change any of the core code of the GeoServer community project. It instead declares (some) of those modules as dependencies and rewires parts of the runtime configuration to use custom pieces. 

Stratus has the following main differences from community GeoServer:

 * The web administration interface has been removed from all Stratus instances in the cluster.  A single instance with the web UI will be deployed for times when the administrative tasks need to be performed with the UI.
 * The standard XML file based catalog and resource stores are replaced with a Redis based ones.  They intentionally do not cache any resources but instead fetch configuration from the catalog on each request.  Ideally this renders Stratus stateless and scalable.
 * Stratus uses an embedded Tomcat 8 rather than building a WAR file (however it is still possible to build one)

## Contents:

* [Building Stratus](#building-stratus)
* [Running Stratus locally](#running-stratus-locally)
* [Releasing Stratus](#releasing-stratus)
* [Testing](#testing)
* [Deployment](#deployment)
* [Upgrade Procedures](#upgrade-procedures)

## Building Stratus:

### Prerequisites:

* Maven 3
* Java 8+
* Docker

### Base image

Before building Stratus for the first time, build the docker base image:

    cd build/docker/amazonlinux-gdal-py-java11
    docker build -t amazonlinux-gdal-py-java11 .

Build Stratus using:

    cd src
    mvn clean install

For more details on building and developing Stratus, refer to the [stratus-application submodule](./src/stratus-application/README.md).

### Eclipse:

Stratus makes extensive use of [Lombok](https://projectlombok.org/), download from https://projectlombok.org/download and run `java -jar lombok.jar` to launch the installer. Restart eclipse and run Project/clean/clean all projects.

## Running Stratus locally:

**Requires**: Java 8, Local Redis instance

The Stratus jar can be executed with the command `java -jar stratus.jar`.

Alternatively, you can run Stratus from source from the `stratus-application` subfolder using `mvn spring-boot:run`.

Redis is the only dependency required for Stratus to run. By default, it will attempt to connect to a Redis server at localhost:6379.

The [deploy/standalone](deploy/standalone) directory contains several useful scripts to start various supporting services, including [Redis](deploy/standalone/redis.sh), [PostGIS](deploy/standalone/postgis.sh), and [Stratus itself](deploy/standalone/single-host-deploy.sh).

By default, Stratus will start with a configuration that excludes all web components and backup/restore dependencies. To enable the web admin UI, be sure to set the property `stratus.admin-enabled` or environment variable `STRATUS_ADMIN_ENABLED` to `true`.

### Profiles

By default, Stratus will start using the redis-manual profile which will attempt to connect to a single Redis host.  The following profiles are available:

| Profile | Description |
|---------|-------------|
| jedis-manual | Attempts to connect to a single Redis instance using the Jedis client |
| lettuce-manual | Attempts to connect to a single Redis instance using the Lettuce client |
| jedis-sentinel | Attempts to connect to a Redis Sentinel host using the Jedis client |
| lettuce-sentinel | Attempts to connect to a Redis Sentinel host using the Lettuce client |
| jedis-cluster | Attempts to connect to a Redis Cluster using the Jedis client |
| lettuce-cluster | Attempts to connect to a Redis Cluster using the Lettuce client |
| jedis-aws-tag-discovery | Attempts to connect to Redis using metadata provided by AWS tags using the Jedis client |
| lettuce-aws-tag-discovery | Attempts to connect to Redis using metadata provided by AWS tags using the Lettuce client |
| jedis-discovery | Attempts to use the Spring discovery client to obtain Redis endpoint information and connect using the Jedis client |
| lettuce-discovery | Attempts to use the Spring discovery client to obtain Redis endpoint information and connect using the Lettuce client |
| cloud | Used by PCF to connect to a Redis service |

### Startup Properties/Parameters

By default, running Stratus with no parameters will attempt to connect to a single Redis host on <code>localhost:6379</code>.  The following configuration properties can be specified as command line parameters or environment variables.

| Property | Shortcut | Profile | Default Value | Description |
|----------|----------|---------|---------------|-------------|
| stratus.catalog.redis.manual.host | redis-host | {jedis/lettuce}-manual | localhost| The Redis host to connect to. |
| stratus.catalog.redis.manual.port | redis-port | {jedis/lettuce}-manual | 6379 | The Redis port to connect to. |
| stratus.catalog.redis.manual.database | redis-database | {jedis/lettuce}-manual | 0 | The Redis database to connect to. |
| stratus.catalog.redis.sentinel.master | sentinel-master | {jedis/lettuce}-sentinel | mymaster | The name of the Sentinel master. |
| stratus.catalog.redis.sentinel.hosts | sentinel-hosts | {jedis/lettuce}-sentinel | localhost:26379 | A list of Sentinel hosts. |
| stratus.catalog.redis.cluster.hosts | cluster-hosts | {jedis/letuce}-cluster | localhost:6379 | A list of Redis cluster nodes. |

Passing in custom properties depends on how you are running Stratus.  Below are examples using java and maven from the command line:

Set the active profile:
 * java: <code>java -jar -Dspring.profiles.active=redis-sentinel stratus.jar</code>
 * maven: <code>mvn spring-boot:run -Dspring-boot.run.profiles=lettuce-sentinel</code>
 
Set the server port:
 * java: <code>java -jar -Dserver.port=8081 stratus.jar</code>
 * maven: <code>mvn spring-boot:run -Dserver.port=8081</code>
 
In addition, properties can be set via environment variables.  The variable names should follow the following rules:
 * Strictly use all uppercase
 * Replace all periods in the property path with underscores
 * Separate camelcase variables with underscore where the case changes
 
Example:

| Stratus Property Name | Environment Variable Name |
|------------------|---------------------------|
| test | TEST |
| spring.profiles.active | SPRING_PROFILES_ACTIVE |
| sentinel-master | SENTINEL_MASTER |
| stratus.catalog.redis.caching.useParallelQueries | STRATUS_CATALOG_REDIS_CACHING_USE_PARALLEL_QUERIES |

## Releasing Stratus

### Update the Changelog

Before performing a release, update the [CHANGELOG](./CHANGELOG.md) and [whatsnew.rst](./docs/usermanual/source/whatsnew.rst) with details of the release.

1. Add a new heading for the version being released.

2. If you are performing a major or minor release: 

    i. Add a **Summary** subsection with the highlights of the release. Any migration notes should also be included in this section.
    ii. Add a **Changes** subsections to list individual changes (`git log --oneline $PREVIOUS_RELEASE_TAG..HEAD` should provide a good basis for the list). Further seperate this list into **Version Updates**, **New Features**, and **Bugfixes**.

3. If you are performing a patch release, instead just list the changes, without any subsections. This should be pretty short, or else you probably shouldn't be doing a patch release.

4. Update [build.properties](./docs/build.properties) and [banner.txt](./src/stratus-application/src/main/resources/banner.txt) with the version of the new release.

5. Push the updated files to master.

### Jenkins Release (Recommended)

Stratus is released using Jenkins, via the stratus-release job.
When run, this job will:
 * Run all tests.
 * Remove -SNAPSHOT from the project version
 * Commit and Tag in git with v${version}.
 * Increment the patch version by one, and add -SNAPSHOT back onto the version.
 * Commit.
 * Push commits and tag to the gsstratus/stratus repository.
 * Trigger a new run of stratus-deploy-all against the released tag, which will create Docker images and push them to Docker Hub.

Jenkins configuration is managed in [deploy/jenkins](deploy/jenkins).

### Local Release (Not Recommended)

**Requires**:

* Git
* Maven
* Docker
* Commit access to https://github.com/gsstratus/stratus/releases
* A Docker Hub account, with access to https://docker.io/repository/gsstratus/stratus
* You must be logged into Docker Hub locally in docker

To run the release process locally, you can use the command:

    mvn release:prepare --batch-mode -Dtag=$GIT_TAG -DreleaseVersion=$RELEASE_VERSION -DdevelopmentVersion=$DEVELOPMENT_VERSION

Where:

* `$GIT_TAG` - The tag for the release, to be pushed to git. Usually of the form `v1.2.0`
* `$RELEASE_VERSION` - The maven version for the release, usualy of the form `1.2.0`
* `$DEVELOPMENT_VERSION` - The maven version to update master to, usually of the form `1.2.1-SNAPSHOT`.

(For more information on tagging and version guidlines, refer to [tagging.md](./tagging.md))

This will do everything the `stratus-release` job does except for building and deploying the docker images. To deploy the release, checkout the newly created release tag, cd to the `stratus-application` submodule and follow the instructions for [deploying with docker](./src/stratus-application/README.md#deploying-with-docker).

### Verify the Release

After running the release job:

1. Verify the new git tag is listed at https://github.com/planetlabs/stratus/releases.

2. Still on GitHub, click `Draft a new release`, selecting the tag you just pushed, and fill in details for this release (Only do this for final releases, not RCs).

3. Verify the new docker tag is listed at https://docker.io/repository/gsstratus/stratus?tab=tags

You can test the release artifacts locally using [single-host-deploy.sh](deploy/standalone/single-host-deploy.sh) in stratus:

1. Update `DOCKER_TAG` to the tag you just pushed to docker.io, and ensure that `DOCKER_REPO=docker.io`

2. Run `single-host-deploy.sh`.

3. Navigate to http://localhost:8080/geoserver/web and verify the UI shows up.

4. Navigate to http://localhost:8080/geoserver/rest/manage/info and verify that the git commit id matches that of the release tag in git.

## Testing

For integration testing, refer to the [test/](./test/README.md) directory.

This includes a number of integration and performance tests, and an [upgrade test](./test/standalone/upgrade/README.md).

## Deployment

Stratus deployments can be found under [deploy/](./deploy/README.md).

This includes:

* Jenkins pipelines for CI.
* Scripts for standalone deployments for demos and testing.
* Kubernetes deployments, both manual and using helm.
* Terraform Deployments.

Any custom Docker images used by these deployments can be found in [build/docker](build/docker).

## Upgrade Procedures

- Ensure the methods in [CommunityRestConfiguration](./src/stratus-application/src/main/java/com/gsstratus/stratus/config/CommunityRestConfiguration.java)
are up to date with the current [RestConfiguration](https://github.com/geoserver/geoserver/blob/master/src/rest/src/main/java/org/geoserver/rest/RestConfiguration.java) class
found in community GeoServer (note: community extends WebMvcConfigurationSupport and Stratus should extend WebMvcConfigurerAdapter)

- Ensure all of the context XML files in the [stratus-gwc](./src/stratus-gwc/src/main/resources) are updated with any 
changes made in the corresponding [GeoServer GWC](https://github.com/geoserver/geoserver/blob/master/src/gwc/src/main/resources)
context files.  Note there are exceptions for context files that contain important custom configurations.  These  
files are:
    * [applicationContext.xml](./src/stratus-gwc/src/main/resources/applicationContext.xml) 
    * [geowebcache-rest-context.xml](./src/stratus-gwc/src/main/resources/geowebcache-rest-context.xml)
    * [geowebcache-wmtsservice-context.xml](./src/stratus-gwc/src/main/resources/geowebcache-wmtsservice-context.xml)
