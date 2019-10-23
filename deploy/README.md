# Stratus Deployment Artifacts

## Directory

* [docker](./docker) - Docker support files
* [kubernetes](./kubernetes) - Kubernetes (and helm) deployment
* [standalone](./standalone) - Scripts for standalone/local deployment
* [terraform](./terraform) - Terraform deployment


## Standalone

The standalone directory contains several useful scripts for running Redis, PostGIS, and Stratus itself in an local environment. 

| Script | Description |
|--------|-------------|
| redis.sh | Starts a docker container with a single redis instance on port 6379 |
| sentinel.sh | Starts a redis sentinel docker container.  The sentinel service runs on port 26379.  The redis master/slave nodes run on ports 6379, 6380, and 6381 |
| postgis.sh | Starts a postgis container on port 5432 |
| multi-stratus.sh | Starts 3 Stratus instances on ports 8080, 8081, and 8082 |
| kill-env.sh | Indiscriminantly kill all docker containers |

Note: If you are running docker as a VM (As in certain OS X docker installs), be sure to forward the relevant ports from the VM:
* postgis: 5432
* redis: 6379
