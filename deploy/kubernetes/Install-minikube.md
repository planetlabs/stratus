This doc provides a simple, all-in-one alternative presentation of information from the upstream [Running Kubernetes Locally via Minikube](https://kubernetes.io/docs/getting-started-guides/minikube/) and the various documents it links to. If you are comfortable with that doc, feel free to use it instead of this.

# Prerequisites
You should have a 64-bit computer with hardware virtualization support. This feature should also be enabled in the computer’s BIOS or UEFI setup that is accessible at the computer’s power-on. You will also want to be running a 64-bit OS. To run the VMs you will need plenty of memory.

# Installing Minikube
First you’ll want to install Minikube ([upstream doc](https://kubernetes.io/docs/tasks/tools/install-minikube/)), which takes a few steps:

* Install a hypervisor that Minikube can use to run a virtual machine: i.e., VirtualBox, KVM (on Linux), or HyperKit (on OS X). If you have no preference, VirtualBox is a safe option.
* If you are using a hypervisor other than VirtualBox (e.g. for speed) you will also need to install a Minikube “driver” for that hypervisor. 
The drivers are available on the [Minikube Releases](https://github.com/kubernetes/minikube/releases) page. 
Get the driver appropriate to your OS, then follow its installation instructions:
** [Instructions for installing the kvm2 driver for Minikube](https://github.com/kubernetes/minikube/blob/master/docs/drivers.md#kvm2-driver)
** [Instructions for installing the hyperkit driver Minikube](https://github.com/kubernetes/minikube/blob/master/docs/drivers.md#hyperkit-driver)
* Grab and install kubectl (you can get it from Google Cloud SDK, curl, homebrew, snap)
* Grab and install Minikube itself

# Starting Minikube
If you’re not using one of the special hypervisor drivers, then you can start Minikube with the default VirtualBox driver via
```
minikube start
```
Memory and CPU limits need to be set on Minikube start
```
minikube --memory 8192 --cpus 3 start
```
If you’re using kvm2 that is
```
minikube start --vm-driver kvm2
```

## Caveat: Hardware Sharing
A caveat you may run into if you use multiple hypervisors on the same computer is that multiple hypervisors don’t share the virtualization hardware while running VMs.
For example, if you are trying to run with kvm2 and VirtualBox is already running a VM (in this example we suppose it’s the one created by minikube called “minikube”), it might not start properly. You could debug and correct that situation by as follows:
```
VBoxManage list runningvms
VBoxManage controlvm minikube poweroff
```
# Dashboard
Kubernetes has a web-based dashboard that you can use in your browser to look at the state of the cluster, logs, etc. To view the Kubernetes dashboard in your browser after starting minikube, use: 
```
minikube dashboard
```
# Context Switching
“minikube start” sets the current context to minikube, so your kubectl commands will manipulate the minikube cluster… unless the context got switched. 
You should usually verify which context you’re in before messing with things using this command:
```
kubectl config current-context
```
You can switch to another context (e.g. an actual kubernetes cluster on the internet somewhere - in this example it’s called whatever) using
```
kubectl config use-context whatever
```
If you switch to another context and then need to switch back to minikube later, use:
```
kubectl config use-context minikube
```
# Building Containers, including Stratus
It’s a pain to make kubernetes run containers you built locally with docker unless you first do this:
```
eval $(minikube docker-env)
```
Then you can build containers as normal:
```
cd $STRATUS_GIT_DIR
mvn clean -DskipTests -Pweb-admin package
cd stratus-application
mvn docker:build
```
which builds `gsstratus/stratus:latest` locally.
 
If you rebuild the same tag, kubernetes should recognize this happened and restart the stuff which used that image.

Whenever you’re done working with docker stuff in minikube, use to switch back to regular docker:
```
eval $(minikube docker-env -u)
```
# Deploying Stratus
Deploying Stratus to minikube is no different than deploying to a real kubernetes cluster. Since you are already running minikube and have your `kubectl` context set to minikube, any `helm` or `kubectl` commands will apply to the local minikube cluster.

Stratus will be deployed based on a container image, either pulled from a remote repo (e.g., docker.io/gsstratus) or your local docker images. As you edit the values in the helm configuration, keep this in mind.

## Helm
Helm is a wrapper that provides additional parameterization of deployments that `kubectl` by itself does not provide. Its configuration is provided by yaml files in the same format as the manifests that `kubectl apply -f` would use, but with `go` templating support. Thus, variable substitution and conditional inclusion/exclusion of manifest sections are supported.

### Install helm
[https://docs.helm.sh/using_helm/#installing-helm](https://docs.helm.sh/using_helm/#installing-helm)

### Helm errors
Tiller does not connect to localhost:8080 / Error: no available release name found
```
kubectl -n kube-system patch deployment tiller-deploy -p '{"spec": {"template": {"spec": {"automountServiceAccountToken": true}}}}'
``` 

## Running Stratus in minikube from local image
From this directory, this will run the latest build (`gsstratus:stratus/latest`) as built from https://github.com/planetlabs/stratus. Update resources as necessary if mem, maxMem, cpu, and maxCpu are too greedy (though note that mem requirements may approach 4GB regardless).
```
cd helm/Stratus/charts
helm init
```
This may take a few minutes
```
helm install redis-manual
helm install stratus-service
helm install stratus-deploy -f stratus-deploy/profiles/minikube-local-values.yaml 
```
To check status:
```kubectl get po```
To inspect a particular pod:
```kubectl describe po <POD_NAME>``` where <POD_NAME> is the `NAME` from `kubectl get po`

The error `ErrImageNeverPull` is typical when the local docker container was not compiled within the current minikube environment. In this case, re-compile the docker image with the minikube docker-env; `eval $(minikube docker-env)`. Once the docker image has been copmpiled in the minikube environment, simply delete the running pods and minikube will attempt to re-deploy the containers from the image. This time around, the docker image should be found.
```kubectl delete po stratus-ui-deployment-6fd559fb8c-7msqd stratus-ogc-deployment-6cb776b8f5-xbcvl``` where the pod `NAME`s come from `kubectl get po`

## Running Stratus in minikube from docker image
From this directory, this will run v1.0.7, pulling the image from dockerhub. Update resources as necessary if mem, maxMem, cpu, and maxCpu are too greedy (though note that mem requirements may approach 4GB regardless). See HELM ERRORS for some issues we've seen.
```
cd helm/Stratus/charts
helm init
```
This may take a few minutes. If you get `Error: could not find a ready tiller pod` just wait a few seconds and try again
```
helm install redis-manual
helm install stratus-service
# You should substitute your docker.io credentials below:
kubectl create secret docker-registry mydockerkey --docker-server=http://docker.io/ --docker-username=$DOCKER_USER --docker-password="$DOCKER_PASSWORD" --docker-email=$DOCKER_EMAIL
helm install stratus-deploy -f stratus-deploy/profiles/minikube-repo-values.yaml
```
## Check status/troubleshooting/view logs
To see quick status of the pods:
```kubectl get po``` 

Look for a `Running` STATUS. Some possible errors include `ErrImageNeverPull` (typical for local installs where the docker container was not built in the minikube env).

A smattering of helpful commands:
```kubectl describe po <POD_NAME>
kubectl logs -f <POD NAME>
kubectl get nodes
kubectl describe node <NODE_NAME>
kubectl top po # must run heapster for this to work: https://github.com/kubernetes/heapster
kubectl top nodes # to see resource use on pods (requires heapster)
kubectl exec -it <REDIS_POD_NAME> redis-cli # to invoke the redis repl and look at keys (see redis-cli commands).
kubectl port-forward <POD_NAME> 8081:8080 # to use localhost:8080 to interact with a specific Stratus pod's port 8080
```
## Get the service endpoints (different for minkube than a real cluster)
This brings up the web-admin service endpoint in your browser. You will need to add `/geoserver/rest` to the url as the initial connection will be refused. Note also that two browser windows will pop up. The second is for https, which is not currently supported.
```
minikube service stratus-ui-lb
```
This brings up the non-UI endpoint; add `/geoserver/rest` for a working endpoint.
```
minikube service stratus-ogc-lb
```

## Examine Stratus logs
First, list the running pods:
```
kubectl get po
```
To tail the logs
```
kubectl logs stratus-ogc-deployment-698d56c88d-5q8sp
kubectl logs -f stratus-ogc-deployment-698d56c88d-5q8sp
```
The `-f` streams the logs without exiting.

## Run more than one pod (i.e., distributed)
There are two ways to run Stratus with more than one pod. First, some background. Stratus is deployed as a kubernetes `deployment` which is backed be a `HorizontalPodAutoscaler` that can be set to scale from a `min` to `max` number of pods depending on CPU usage. By default, the minikube deployment is designed to be small in order to fit on development machines easily. The two ways are 
### Scale by changing the hpa yaml
Modify `stratus.ogc.resources.minReplicas` to change the initial number of pods in this deployment, either by editing the values.yaml file you are using (e.g., `stratus-deploy/profiles/minikube-local-values.yaml`) or by passing it in the command line. To avoid changing files that could be committed back to this repo, the latter is illustrated here:
```
helm list
# Read ths list and find the helm deployment name (e.g., wilting-giraffe) for StratusDeployment
helm upgrade <HELM NAME> stratus-deploy/ -f stratus-deploy/profiles/minikube-local-values.yaml --set stratus.ogc.resources.minReplicas=2
```
This will tear down the running pods and start up two more. Obviously, if you are using the `minikube-repo-values.yaml` deployment, change the file provided by `-f` above.

### Scale with kubectl
Use kubectl to scale:
```
kubectl scale deploy stratus-ogc-deploy --replicas=2
```
This has the benefit of being easy, spinning up just more pods (not tearing any down), but also lacks the persistence that a file edit would provide.

## Connect directly to a pod (bypass the service)
```
kubectl port-forward stratus-ogc-deployment-698d56c88d-5q8sp 8081:8080
kubectl port-forward stratus-ogc-deployment-6cb776b8f5-w4qmd 8082:8080
```
Then connect to http://localhost:8081/geoserver/rest and http://localhost:8082/geoserver/rest top interact directly with two different Stratus "OGC" pods and perform deterministic testing of the distributed system.

# Shutting Down Minikube
When you’re done playing with the cluster, you can use: 
```
minikube stop
```
This preserves state. If you restart the cluster, it will have the same state.
If you want to wipe out state, use 
```
minikube delete
```
# Persistent State
Ideally we want to avoid using state when we can, but if you need persistent data under Minikube, here’s what you can do. Put it in a directory under /data, and Minikube will make its state persistent. 
Here's an example config for a scratch volume under /data/scratch that can only be used by one node:
 ```
 	apiVersion: v1
 	kind: PersistentVolume
 	metadata:
   	  name: scratch
 	spec:
   	  accessModes:
     	    - ReadWriteOnce
   	  capacity:
     	    storage: 5Gi
   	  hostPath:
     	    path: /data/scratch/
```
# What Minikube Creates For You
You probably don’t have to worry about this unless you are into the Kubernetes details. In Kubernetes terms, Minikube creates all of the following, which means you don’t have to worry about them (and they should not be in any kubernetes config files you write if you want them to work with minikube):
* A cert and key in ~/.minikube/client.crt & ~/.minikube/client.key
* a user named minikube set up with that cert and key
* a cluster named minikube pointing to the VM’s IP as its server, accessible by user minikube
* a context named minikube that ties all this together so you can easily switch to it.

Anything other than this is in the domain of the application and has to be specified by its Kubernetes config files.







