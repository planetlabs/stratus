
export DEPLOY_DIR="../../../deploy/standalone"
export DATA_DIR="../../data"

export REDIS_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' redis)
export POSTGIS_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' postgis)