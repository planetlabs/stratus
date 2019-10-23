This is a broad overview of [cluster-autoscaler](https://github.com/kubernetes/autoscaler/tree/master/cluster-autoscaler) use in Stratus. For instructions specific to cloudproviders, see:

- [AWS](README-autoscaling-aws.md)

# Overview
Stratus is a CPU-intensive application that will require scaling in response to excessive CPU activity. Cluster-autoscaler (CA) does not utilize CPU activity to schedule new pods, but rather does so in response to pods being unschedulable due to 
resource limitations. Thus, in order for CA to work, the Stratus deployment should be tuned such that pods cannot be 
scheduled when Stratus has maxed out the CPU. As such, we set the initial resource request high (e.g., 2.0 on a 4.0 CPU machine) and set `targetCPUUtilization` at 80% for the HorizontalPodAutoscaler.

# Kubernetes Cluster Autoscaling for Stratus
Stratus is a CPU-intensive application that will require scaling in response to excessive CPU activity. The main constraints for performance of Stratus on kubernetes are CPU constraints based on our tests. For more information on the tests, 
see [Test Results](LINK). 

Cluster-autoscaler (CA) adds nodes to a kubernetes cluster when pods are unschedulable due to resource limitations. Thus, 
if you have a horizontal pod autoscaler (HPA) that adds pods to a deployment based on CPUUtilization, and pods cannot
be scheduled due to resource limitations on the existing cluster, then a new kubernetes node will be brought up and tagged
appropriately. Kubernetes scheduler will eventually see the newly available node and add as many unschedulable pods as 
it can. CA may continue to add nodes until no pods are unschedulable.

works with an auto-scaling group, spinning up new kubernetes node instances, tagging them appropriately, and
adding them to the cluster. CA triggers a node scaling event based on the presence of unschedulable pods; that is, CPU, memory, 
or other triggers are used to spin up or down a kubernetes node. The kubernetes horizontal pod autoscaler (HPA) is
used to generate pod scaling events which will work up to the point of being unschedulable because either nodes are unschedulable or 
resources (CPU/memory) are not available to spin up the pod. For the instances we have worked with, the CPU limits are the
cause for HPA scaling event

To maximize throughput, the Stratus deployment should be configured to run one pod per kubernetes node and give Stratus as much CPU as possible, provided your other non-Stratus deployments
are given the CPU that they need as well. If your machines are m5.xlarge, for
example, then your max CPU should be set to 4.0. If your machines are m5.2xlarge, then your max CPU should be set to 8.0. And so on.

## Debugging
The best way to debug CA is to look at the logs on the cluster-autoscaler pod:
```
CA_POD=`kubectl get pods --namespace=kube-system | grep cluster-autoscaler | awk '{ print $1 }'`
kubectl logs -f --namespace=kube-system $CA_POD
```

Look specifically at the `scale_up.go` and `scale_down.go` log messages.
In this scenario, the cluster is stable:
```
I0316 22:40:32.108715       1 static_autoscaler.go:221] No schedulable pods
I0316 22:40:32.108725       1 static_autoscaler.go:227] No unschedulable pods
```
Additionally, look at `kubectl top pods` and `kubectl top nodes` 
```
kubectl top nodes
NAME                            CPU(cores)   CPU%      MEMORY(bytes)   MEMORY%
ip-172-20-43-145.ec2.internal   15043m       94%       13605Mi         21%
ip-172-20-56-46.ec2.internal    106m         10%       2470Mi          67%
kubectl top pods
NAME                                  CPU(cores)   MEMORY(bytes)
stratus-ogc-deployment-58d5b5bb75-9nfsq   14964m       8207Mi
stratus-ui-deployment-6f5f686c6b-s9gbh    1m           2364Mi
kube-scope-f48b894c6-zhw8g            0m           58Mi
```
In this case, the node has 1 m5.4xlarge (16 virtual CPUs) and an HPA for scaling the Stratus deployment triggered at 
`targetCPUUtilizationPercentage: 50`
which should trigger a new pod coming online. When it becomes `unschedulable` because there is not enough CPU to meet 
its initial CPU request, then it will sit in the queue and CA will note an unschedulable and attempt to scale up the 
kubernetes node group.


