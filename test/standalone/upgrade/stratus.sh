#!/bin/sh

source env.sh

docker rm -f stratus-dev
echo "About to run Stratus from container: $DOCKER_REPO/gsstratus/stratus:$1"
docker run --rm --mount type=bind,source="$(pwd)"/$DATA_DIR,target=/data -e STRATUS_WPS_FILE_STORAGE="local" -e STRATUS_ADMIN_ENABLED=true -e STRATUS_CATALOG_REDIS_MANUAL_HOST=$REDIS_IP --name stratus-dev -p 8080:8080 gsstratus/stratus:$1 > $2 2>&1 &

#wait for Stratus to start
for ((n=0;n<24;n++)); do
sleep 5
printf "."
done

echo ""
