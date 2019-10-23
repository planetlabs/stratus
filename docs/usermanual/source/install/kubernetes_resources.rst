.. _install.kubernetes_resources:

Stratus Resources on Kubernetes
===============================

Resource tuning is the critical aspect to scaling Stratus, especially on Kubernetes. The primary constraint is CPU, although memory is also key and scales linearly with CPU usage, though typically at usage levels much lower than common cloud infrastructure. An ideal deployment would reside on a compute-optimized VM with a CPU:RAM ratio of 1.0:1.6GB. Compute-optimized infrastructure on AWS are provided at a 1:2.0 GB ratio.

A basic heuristic for a Kubernetes cluster dedicated to Stratus, these can be set by installing a template such as a values.yml file or by setting the properties at deployment::

    helm install Stratus/charts/stratus-deploy

Given a node CPU of X CPU units:

  ::

    --set stratus.ogc.resources.cpu: X*0.4375
    --set stratus.ogc.resources.maxCpu: X*0.875
    --set stratus.ogc.resources.mem: 2.0GB
    --set stratus.ogc.resources.maxMem: X*1.2GB

Redis will need approximately 3.5% of the CPU resources that Stratus will require:

  ::

    --set redis.resources.maxCpu: X*0.015

Using a real example, supposing c5.xlarge nodes comprised the Kubernetes nodes, then the available CPU is 4 and Mem is 8 GiB. Then:

  ::

    --set stratus.ogc.resources.cpu: 1.75
    --set stratus.ogc.resources.maxCpu: 3.5
    --set stratus.ogc.resources.mem: 2.0GB
    --set stratus.ogc.resources.maxMem: 4.8GB
    --set redis.resources.maxCpu: 0.06

The resources in YAML will look like:

  .. code-block:: yaml

    redis:
      # lettuce | jedis
      implementation: "jedis"
      # sentinel | cluster | manual
      type: "manual"

      # only for sentinel
      sentinel:
        minReplicas: 1
        maxReplicas: 3
      cache:
        enabled: "true"
        parallel: "true"
      resources:
        maxCpu: "0.06"

    stratus:
      version: 1.0.7
      ogc:
        container:
          repo: "docker.io/gsstratus"
          image: "stratus"
          tag: "1.0.7"
        resources:
          minReplicas: 1
          maxReplicas: 3
          mem: "2.0G"
          maxMem: "4.8G"
          cpu: "1.75"
          maxCpu: "3.5"
      debug: "false"
      gwc:
        enabled: "false"

Autoscaling
-----------

The Stratus deployment on Kubernetes utilizes a horizontal pod autoscaler (HPA) that can be tuned to increase capacity based on the average CPU load across all pods in the deployment. The default setting is 80% but can be changed to be more or less aggressive with:

  ::

    --set stratus.ogc.hpa.cpu: 80
