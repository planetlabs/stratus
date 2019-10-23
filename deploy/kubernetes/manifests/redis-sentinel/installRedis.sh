echo -e "Installing Redis"

echo -e "\r --> Installing Redis Master"
kubectl --context=$K_CONTEXT --namespace=$K_NS apply -f redis/redis-master.yml
echo -en "\r --> Done!"

echo -e "\r --> Installing Sentinel"
kubectl --context=$K_CONTEXT --namespace=$K_NS apply -f redis/redis-sentinel-service.yml
echo -e "\r --> Done!"

echo -e "\e[0K\r --> Installing Redis Controller"
kubectl --context=$K_CONTEXT --namespace=$K_NS apply -f redis/redis-controller.yml
echo -e "\r --> Done!"

# echo -e "\r --> Installing Redis Proxy"
# kubectl --context=$K_CONTEXT --namespace=$K_NS create -f redis/redis-proxy.yml
# echo -e "\r --> Done!"


echo -e "\r --> Installing Sentinel Controller"
kubectl --context=$K_CONTEXT --namespace=$K_NS apply -f redis/redis-sentinel-controller.yml
echo -e "\r --> Done!"

echo -e "\r --> Scaling up nodes..."
kubectl --context=$K_CONTEXT --namespace=$K_NS scale rc redis --replicas=3
kubectl --context=$K_CONTEXT --namespace=$K_NS scale rc redis-sentinel --replicas=3

# echo -e "\\r --> Setting autoscale...."
# kubectl --context=$K_CONTEXT --namespace=$K_NS apply -f redis/hpaRedis.yml
# echo -e "\r --> Done!"

# echo -e "\r --> Removing un-needed Redis Master"
# kubectl --context=$K_CONTEXT --namespace=$K_NS delete -f redis/redis-master.yml
# echo -en "\r --> Done!"

echo -e "\r Redis installed"
