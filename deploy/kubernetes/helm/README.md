# Install Stratus with helm
## Prerequisites
* helm installed
* kubectl installed
* environment configured for appropriate context
* if using persistent volume (e.g., with coverages, GWC, or redis-in-kubernetes), the share is available for NFS-style mounting


## Identify resources and  limits
See [../Performance-tuning.md](../Performance-tuning.md) for more details about identifying resource constraints. For a quick start, get some
starting points for your cluster based on the CPU available for each node in the kubernetes cluster. For example,
if the cluster has 4 nodes and each node has 8 vCPUs available, get starting points for Memory and CPU with:

```
. ../resource_calc.sh  4 8
```

## Setup persistent volume for redis backup
Given a nfs-accessible file share such as an EFS resource:
```
helm install Stratus/charts/persistentVolume \
        --set redis.persistentVolume.host=${NFS_HOST} \
        --set redis.persistentVolume.path=${NFS_PATH}
```       
## Redis
Redis can be run in kubernetes or you can connect to a hosted redis. 

### Hosted redis (Option 1)
Stratus needs to be told to use the `manual` profile and must be given the redis service endpoint. This is done by 
configuring the following variables to be applied to the Stratus Deployment below.

```
redis.type=manual
redis.host.ro=$READ_ONLY_HOST
redis.host.rw=$READ_WRITE_HOST
```
At this point, RO/RW should both be specified, even if they are the same host.

### Deploy redis-sentinel (Option 2):
If you would like to run `redis` within kubernetes:

Deploy `redis-sentinel`. Substitute NAMESPACE as needed:

```
helm install Stratus/charts/redis-sentinel/ \
        --set redis.resources.maxCpu="2.0" \
        --set redis.persistentVolume="true" \
        --set redis.sentinel.minReplicas=3 \
        --set redis.sentinel.maxReplicas=3 \
        --namespace=${NAMESPACE}
```

Monitor to ensure that redis-master is up and running. Look for `redis-master`, `redis-XXXX`, `redis-sentinel` in the pod list:
```
kubectl get po --namespace=${NAMESPACE}
```

## Deploy Stratus Services
This sets up the services for Stratus but does not install the actual deployment.
```
helm install Stratus/charts/stratus-service --namespace=${NAMESPACE}
```
Get your external IP for OGC services:
```
kubectl describe svc stratus-ogc-lb --namespace=${NAMESPACE} | grep Ingress: | cut -f2 -d:
```
Note the $OGC_URL as the `Ingress` for the output above.  visit `http://${OGC_URL}/geoserver/rest` to see the Stratus rest interface

Get your external IP for the Admin (UI) interface:
```
kubectl describe svc stratus-ui-lb --namespace=${NAMESPACE} | grep Ingress: | cut -f2 -d:
```
Given $UI_URL as the `Ingress` for the output above, visit `http://${UI_URL}/geoserver/web` to see the Stratus web interface

## Deploy docker image pull secret
```
kubectl create secret docker-registry mydockerkey --docker-server=http://docker.io/ --docker-username="XXX" --docker-password="XXX" --docker-email=XXX
```

## Deploy Stratus Deployment
It is recommended to run no more than one Stratus instance per node and to size each pod to utilize as much CPU in the node as is 
allowed. Assume `$REPLICAS` is equal to or less than the number of nodes in the kubernetes cluster and let $MIN_REPLICAS be 1.
`$MEM`,`$MAX_MEM`,`$CPU`, and `$MAX_CPU` are from the Resources section above
```
helm install Stratus/charts/stratus-deploy \
        --namespace=${NAMESPACE} \
        --debug \
        --set debug=false \
        --set redis.implementation=jedis \
        --set redis.type=sentinel \
        --set redis.cache.enabled=true \
        --set stratus.container.repo=docker.io/gsstratus \
        --set stratus.container.image=stratus \
        --set stratus.container.tag=1.0.7 \
        --set stratus.ogc.resources.minReplicas=$MIN_REPLICAS \
        --set stratus.ogc.resources.maxReplicas=$REPLICAS \
        --set stratus.ogc.resources.mem=$MEM \
        --set stratus.ogc.resources.maxMem=$MAX_MEM \
        --set stratus.ogc.resources.cpu=$CPU \
        --set stratus.ogc.resources.maxCpu=$MAX_CPU \
        --set stratus.ui.enabled=true \
        --set stratus.ui.resources.mem=1.0G \
        --set stratus.ui.resources.maxMem=3.0G \
        --set stratus.ui.resources.cpu=0.25 \
        --set stratus.ui.resources.maxCpu=0.5 \
        --set xmem="go"
```
