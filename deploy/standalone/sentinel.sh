echo 'Removing any existing redis docker images'
docker rm -f redis-sentinel

docker run -d --name redis-sentinel -p 6379:6379 -p 6380:6380 -p 6381:6381 -p 26379:26379 redis-sentinel
