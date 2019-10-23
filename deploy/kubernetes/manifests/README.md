# Static Stratus and related kubernetes manifests
A number of kubernetes manifests are provided for different environments and configurations, as well as some
helper deployments useful for testing. 
## Notes specific to different environments
Static kubernetes yamls are available in [stratus](stratus) for specific version releases (e.g., 1.0.3) and select profiles (e.g., lettuce-sentinel)

* [README-aws.md](README-aws.md) - AWS-specific configuration notes, including EFS config
* [README-gce.md](README-gce.md) - TODO
* [README-azure.md](README-azure.md) - TODO

* [README-autoscaling.md](README-autoscaling.md) - Understanding AWS Autoscaling in kubernetes

* [README-autoscaling-aws.md](README-autoscaling-aws.md) - AWS-specific autoscaling instructions
* [README-autoscaling-azure.md](README-autoscaling-azure.md) - TODO

## Helpful kubernetes deployments included here
* [bastion](bastion/README.md) 
* [postgis](postgis/README.md)
* [redis-cluster](redis-cluster/README.md)
* [redis-sentinel](redis-sentinel/README.md)

