# Stratus with kubernetes

These Stratus deployment artifacts are meant to be deployed with kubernetes 1.8.

_Recommendation: Use [helm](helm) instructions._
<p/>
It is recommended you use the helm configuration because it incorporates redis, EC, and all the permutations of redis implementation and resource constraints that the kubernetes YAMLs available in this directory do not. Legacy configurations are provided here for helpful information for deployment configuration notes specific to those cloud providers.

For static manifests, see [manifests](manifests).

For running kubernetes locally via minikube, see [Install-minikube.md](./Install-minikube.md).

For details about identifying resource constraints, see [Performance-tuning.md](./Performance-tuning.md). 

