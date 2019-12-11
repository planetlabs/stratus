# Docker Base Images

Docker base images used by Stratus. Typically, you will only every need to build these once, the first time you build 
Stratus.

## amazonlinux-gdal-py-java11

Amazon Linux with GDAL 

Stratus uses `adoptopenjdk/openjdk11:alpine-slim` base image by default.  If GDAL support is desired, you may build a 
custom base image that contains the GDAL binaries. This image is necessary when using the `gdal` Maven profile while
building the Stratus docker image using the goal `docker:build` goal in the 
[stratus-application](../../src/stratus-application) module.

Build with `docker build -t amazonlinux-gdal-py-java11 .`

## redis-sentinal

Image with a Redis Sentinal master-slave configuration.

Build with `docker-compose build`

## redis-init-k8s

Redis cluster initialization image for use with the [redis-cluster kubernetes deployment](../../deploy/kubernetes/manifests/redis-cluster).

Build with: 

```
docker build -t redis-init-k8s .
docker tag redis-init-k8s:latest gsstratus/stratus-init-k8s:0.0.6

```

## redis-cluster-k8s

Redis cluster main image for use with the [redis-cluster kubernetes deployment](../../deploy/kubernetes/manifests/redis-cluster).

Build with: 

```
docker build -t redis-cluster-k8s .
docker tag redis-init-k8s:latest gsstratus/stratus-cluster-k8s:0.0.6

```