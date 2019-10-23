# Resources on Stratus on kubernetes

Resource tuning is the critical aspect to scaling Stratus, especially on kubernetes. The primary constraint is CPU, though Memory
is also key and scales linearly with CPU usage, though typically at usage levels much lower than common cloud infrastructure. An ideal 
deployment would reside on a compute-optimized VM with a CPU:RAM ratio of 1.0:1.6GB. Compute-optimized infrastructure on AWS and Azure
are provided at a 1:2.0 GB ratio.

A basic heuristic for a kubernetes cluster dedicated to Stratus:

Given a node CPU of `X` CPU units:
```
stratus.ogc.resources.cpu: X*0.4375
stratus.ogc.resources.maxCpu: X*0.875
stratus.ogc.resources.mem: 2.0GB
stratus.ogc.resources.maxMem: X*1.2GB
```
Redis will need approximately 3.5% of the CPU resources that Stratus will require:
```
redis.resources.maxCpu: X*0.015
```
Using a real example, supposing c5.xlarge nodes comprised the kubernetes nodes, then the available CPU is 4 and Mem is 8 GiB.
Then:
```
stratus.ogc.resources.cpu: 1.75
stratus.ogc.resources.maxCpu: 3.5
stratus.ogc.resources.mem: 2.0GB
stratus.ogc.resources.maxMem: 4.8GB
redis.resources.maxCpu: 0.06
```

## Autoscaling
The Stratus deployment utilizes a horizontal pod autoscaler (HPA) that can be tuned to increase capacity based on the average
CPU load across all pods in the deployment. The default setting is 80% but can be changed to be more or less aggressive 
with
```
stratus.ogc.hpa.cpu: 80
```

