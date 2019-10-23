echo 'Removing any existing redis docker image'
docker rm -f redis

docker run -d --name redis -p 6379:6379 -e "SERVICE_NAME=redis" redis
