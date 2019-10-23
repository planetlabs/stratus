#!/bin/sh
# Script to deploy a bare minimum Stratus+Redis environment with docker
DOCKER_TAG=latest
DOCKER_REPO=docker.io

echo "Starting Redis..."

docker run -d --name redis -p 6379:6379 -e "SERVICE_NAME=redis" redis
REDIS_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' redis)

echo About to run Stratus from container: $DOCKER_REPO/gsstratus/stratus:$DOCKER_TAG
mkdir -p "$(pwd)"/data 
docker pull $DOCKER_REPO/gsstratus/stratus:$DOCKER_TAG
docker run --rm --mount type=bind,source="$(pwd)"/data,target=/data -e STRATUS_WPS_FILE_STORAGE="local" -e STRATUS_ADMIN_ENABLED=true -e STRATUS_CATALOG_REDIS_MANUAL_HOST=$REDIS_IP --name stratus-dev -p 8080:8080 $DOCKER_REPO/gsstratus/stratus:$DOCKER_TAG

