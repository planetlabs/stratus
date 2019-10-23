DOCKER_TAG=latest

echo About to run Stratus from container: gsstratus/stratus:$DOCKER_TAG 

docker run -d --name stratus -p 8080:8080 gsstratus/stratus:$DOCKER_TAG
