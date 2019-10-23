# Stratus Standalone Deployment.

Standalone Stratus deployment scripts for testing and local development.

**Prerequisites:**
* Docker
* Local docker image of Stratus

## Minimum Deployment 

Run the script `single-host-deploy.sh` to get a minimum containerized redis + stratus environment running.

**Starting up:** Run Stratus in one of two modes locally: 1) from jar, 2) from container.
Before running Stratus, you need to have redis running. The `redis.sh` script is provided to start a single redis instance running with exposed port 6379 for local connections. 
* 1) Run `redis.sh`

* 2a) If testing container: 
```
./stratus-docker.sh
```

* 2b) If testing jar: 
```
cd ${workspace}/stratus
mvn clean package
java -jar ~/workspace/gsstratus/stratus/target/stratus-1.1.0-SNAPSHOT-exec.jar (where version will vary)
```
See http://github.com/gsstratus/stratus/README.md for command-line configuration for environment variables. 

* 3) A postgis container is provided if you need a data source:
```
./postgis.sh
```

## Testing

Some test data is provided in (../../test/)

To import and publish a simple vector layer:

```
cd ../../test/standalone
./na_roads_postgis.sh
./loadPgData.sh
```

## Shutting down / Restarting

* `./kill-env.sh` to stop all docker containers

* `docker service rm` to remove the stratus service

   Aggressive mode: `docker service rm $(docker service ls -q)`

* `docker kill`

  Aggressive mode: `docker kill $(docker ps -q)`

* `docker rm`

  Aggressive mode: `docker rm $(docker ps -a -q)`

## Redis Sentinel

To run Stratus locally using [Redis Sentinal](https://redis.io/topics/sentinel):

Build the redis-sentinel docker image, if you have not already done so:

```
cd ../../build/docker/redis-sentinel
docker-compose build
```

Run `sentinel.sh`

```
cd ../../deploy/standalone
./sentinel.sh
```

Run Stratus with the `lettuce-sentinel` (or `jedis-sentinel`) profile enabled:

```
cd ../../src/stratus-application
mvn spring-boot:run -Dspring-boot.run.profiles=lettuce-sentinel
```
