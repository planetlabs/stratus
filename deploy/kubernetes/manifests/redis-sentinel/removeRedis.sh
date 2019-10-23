echo -e "--> Removing autoscale...."
kubectl --context=$K_CONTEXT --namespace=$K_NS delete -f redis/hpaRedis.yml
echo -e "--> Done!"

echo "--> Deleting Redis Controller"
kubectl --context=$K_CONTEXT --namespace=$K_NS delete -f redis/redis-controller.yml
echo "--> Done!"

echo "--> Deleting Redis Proxy"
kubectl --context=$K_CONTEXT --namespace=$K_NS delete -f redis/redis-proxy.yml
echo "--> Done!"

echo "--> Deleting Sentinel"
kubectl --context=$K_CONTEXT --namespace=$K_NS delete -f redis/redis-sentinel-service.yml
echo "--> Done!"

echo "--> Deleting Sentinel Controller"
kubectl --context=$K_CONTEXT --namespace=$K_NS delete -f redis/redis-sentinel-controller.yml
echo "--> Done!"
