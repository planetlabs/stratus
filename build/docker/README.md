# Docker Base Images

Docker base images used by Stratus. Typically, you will only every need to build these once, the first time you build Stratus.

## amazonlinux-gdal-py-java11

Base image for stratus-application

Build with `docker build -t amazonlinux-gdal-py-java11 .`


Amazon Linux with GDAL 

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