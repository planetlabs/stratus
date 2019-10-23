#stop all docker containers
# NB!! This won't discriminate -- will stop everything
docker stop $(docker ps -a -q)
docker ps -a| awk '{print $1}'|tail -n +2|xargs docker rm -f -v
rm -rf /tmp/geoserver/*
