.. _install.helm:

Install Stratus with Helm
=========================

The Helm package manager is the recommended method for deploying Stratus on Kubernetes. Helm uses a packaging format called charts. A chart is a Helm package that contains information sufficient for installing a set of Kubernetes resources into a Kubernetes cluster. Charts have been made for Stratus and Redis, and sample are included in the prerequisite sample charts below. For specific Stratus versions, Helm templates provided as ``values.yml`` files are given as a starting point.  Be sure to use up to date Helm charts based on your version of Stratus.

.. note:: For more in depth information on Helm charts, refer to the `Helm charts documentation <https://docs.helm.sh/developing_charts/#charts>`_.  If you need help finding appropriate Helm charts for your Stratus deployment reference the `deploy/kubernetes/helm` section of the Stratus GitHub repository.

Prerequisites
-------------

Helm is installed and initialized in your Kubernetes cluster. See `Using Helm <https://docs.helm.sh/using_helm/>`_.

``kubectl`` is installed. See `Install and Set Up kubectl <https://kubernetes.io/docs/tasks/tools/install-kubectl/>`_.

Environment configured for appropriate context. See :ref:`Application Configuration <sysadmin.config>`.

If you are using persistent volume (e.g., with coverages, GWC, or Redis-in-Kubernetes), the share is available for NFS-style mounting.

The sample charts and scripts. :download:`Download here <./files/helm_deploy.zip>`.

Identify resources and limits
-----------------------------

See :ref:`install.kubernetes_resources` for more details about identifying resource constraints. For a quick start, base your constraints on the CPU available for each node in the Kubernetes cluster. For example, if the cluster has 4 nodes and each node has 8 CPUs available, get starting points for memory and CPU with::

      ./resource_calc.sh  4 8

Setup persistent volume for Redis backup
----------------------------------------

Given a nfs-accessible file share such as an EFS resource::

      helm install Stratus/charts/persistentVolume \
            --set redis.persistentVolume.host=${NFS_HOST} \
            --set redis.persistentVolume.path=${NFS_PATH}

Helm Project Layout
-------------------

Under the hood, Helm deployment of Stratus is managed through three different Helm projects. The order listed below is the order in which they should be installed:

  * **stratus-service** is its own project and simply deploys the service. This is handled outside the regular Stratus deployment because it launches a load balancer, which results in a stable DNS name for the stratus-lb service. You may want to apply a DNS alias to this load-balancer through your cloud (i.e., AWS) console to make a persistent name such as dev.stratus.com.

  * **redis-manual|redis-sentinel|redis-cluster** - Redis is deployed as ``manual`` which is just a single Redis master, as ``sentinel``, which is deployed with a Redis and redis-sentinel service with 3 (configurable) replicas, or as ``cluster``, which isn't recommended but is retained as proof-of-concept.

  * **stratus-deployment** - Stratus is deployed as a deployment with a number of replicas.

Deploy Redis
------------

Redis can be run in Kubernetes or you can connect to a hosted Redis.

Hosted Redis (Option 1)
"""""""""""""""""""""""

Stratus needs to be told to use the manual profile and must be given the Redis service endpoint. This is done by configuring the following variables to be applied to the **Deploy Stratus** section below.::

      redis.type=manual
      redis.host.ro=$READ_ONLY_HOST
      redis.host.rw=$READ_WRITE_HOST

At this point, RO/RW should both be specified, even if they are the same host.

Deploy Redis Sentinel (Option 2):
"""""""""""""""""""""""""""""""""

Redis Sentinel is a system designed to help managing Redis instances. Capabilities of Sentinel include, monitoring, notification, automatic failover and configuration provider.

Kubernetes supports multiple virtual clusters backed by the same physical cluster, these virtual clusters are called namespaces. To run Redis within Kubernetes using Sentinel, deploy the provided redis-sentinel chart and substitute NAMESPACE as needed.::

      helm install Stratus/charts/redis-sentinel/ \
              --set redis.resources.maxCpu="2.0" \
              --set redis.persistentVolume="true" \
              --set redis.sentinel.minReplicas=3 \
              --set redis.sentinel.maxReplicas=3 \
              --namespace=${NAMESPACE}

Monitor to ensure that redis-master is up and running. Look for redis-master, redis-XXX, redis-sentinel in the pod list.::

      kubectl get po --namespace=${NAMESPACE}

.. note:: For more information on Redis Sentinel, refer to the `Redis Sentinel documentation <https://redis.io/topics/sentinel>`_. For more information on namespaces refer to the `Kubernetes documentation for namespaces <https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/>`_.

Deploy Stratus Services
-----------------------

This sets up the services for Stratus, including the loadbalancers for the Web Admin (UI) and the OGC REST services, but does not install the actual Stratus deployment::

      helm install Stratus/charts/stratus-service --namespace=${NAMESPACE}

Get your external IP for OGC services::

      kubectl describe svc stratus-ogc-lb --namespace=${NAMESPACE} | grep Ingress: | cut -f2 -d:

Note the $OGC_URL as the Ingress for the output above. Visit ``$OGC_URL/geoserver/rest`` to see the Stratus REST interface.

Get your external IP for the Web Admin (UI) interface::

      kubectl describe svc stratus-ui-lb --namespace=${NAMESPACE} | grep Ingress: | cut -f2 -d:

Note $UI_URL as the Ingress for the output above, visit ``$UI_URL/geoserver/web`` to see the Stratus web interface.

Deploy quay image pull secret
-----------------------------

Quay.io is a service that specializes in hosting private Docker repositories. If you have credentials to a Quay repository containing a Stratus image, you can deploy that image using::

      kubectl create secret docker-registry mydockerkey --docker-server=http://quay.io/ --docker-username="XXX" --docker-password="XXX" --docker-email="XXX"

Substituting your Quay credentials for "XXX" in each field.

Deploy Stratus
--------------

A node is a worker machine in Kubernetes, such as a VM or physical machine depending on the cluster. A pod is a group of one or more containers (such as Docker containers), with shared storage/network, and a specification for how to run the containers. A pod always runs on a node.  It is recommended to run no more than one Stratus instance per node and to size each pod to utilize as much CPU in the node as is allowed. 

Assume $REPLICAS is equal to or less than the number of nodes in the Kubernetes cluster and let $MIN_REPLICAS be 1. $MEM (minimum starting memory), $MAX_MEM (maximum memory), $CPU (minimum starting cpu), and $MAX_CPU (maximum cpu) are from the ``Identify Resources and Limits`` section above, and defined based on inputs for the ``resource_calc.sh`` script::

       helm install Stratus/charts/stratus-deploy \
          --namespace=test \
          --debug \
          --set debug=false \
          --set redis.implementation=jedis \
          --set redis.type=sentinel \
          --set redis.cache.enabled=true \
          --set stratus.container.repo=docker.io/gsstratus \
          --set stratus.container.image=stratus \
          --set stratus.container.tag=1.0.6-FE \
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


.. note:: The included zip file contains samples of different configurations and **should NOT** be used to deploy all charts included. Only one type of Redis should be installed, for example: redis-sentinel or redis-manual (redis-cluster is given for completeness but is not recommended).

Savvy Helm users are encouraged to work directly with Helm using the ``helm`` and ``kubectl`` tools to deploy, monitor, and debug deployments.

Additional Configuration and Debugging
--------------------------------------

Helm command for deploying Stratus specific values
""""""""""""""""""""""""""""""""""""""""""""""""""

This includes variables substituted into the template files. Note you can add ``--dry-run --debug`` to this command to preview the YAML deployment files without deploying to the Kubernetes cluster::

    cd Stratus/charts/stratus-deploy/profiles
    helm install -f v1.0.7-values.yml

Configuration & Resource limits
"""""""""""""""""""""""""""""""

Important variables:

.. list-table::
   :class: non-responsive
   :header-rows: 1
   :stub-columns: 1
   :widths: 30 70

   * - Variable name
     - Description
   * - KUBE_CONTEXT
     - The k8s context - you'll want to change this to your kube context
   * - KUBE_NAMESPACE
     - The k8s namespace
   * - REDIS_IMPLEMENTATION
     - jedis or lettuce
   * - REDIS_TYPE
     - manual, sentinel, or cluster (not recommended)
   * - DOCKER_REPO
     - quay.io
   * - DOCKER_TAG_NAME
     - Look in repo for appropriate release or snapshot version (e.g., 1.0.3 or 1.1.0-SNAPSHOT)
   * - MIN_STRATUS_PODS, MAX_STRATUS_PODS, MIN_REDIS_PODS, MAX_REDIS_PODS
     - The recommended configuration for Kubernetes (given Redis as a k8s service) is for a single redis-sentinel, Redis, and Stratus pod to be running on each node in the Kubernetes cluster. Thus, if the cluster has 5 nodes, then replicas should be set to 5 for both Redis and Stratus. Note that Redis can also be configured as an external service (e.g., via elasticache). At this point, autoscaling via Kubernetes is not part of the recommended configuration as we recommend a single Stratus pod per k8s node.
   * - MIN_CPU, MAX_CPU
     - CPU on Redis is typically very low. For Stratus, CPU is maximized on boot and can utilize up to 2.0 CPUs. If MAX_CPU is set below 2.0, startup will take longer. MIN_CPU is recommended as 0.5.
   * - MIN_MEM, MAX_MEM
     - Memory for Redis is negligible. MAX_MEM for Stratus needs to be at least 4.0. Recommended: MIN: 4.0, MAX: 4.0

Debugging resources
"""""""""""""""""""

  ::

    kubectl top nodes

and

  ::

    kubectl describe nodes

are good commands for debugging system information and resources for nodes like CPU requests, CPU limits, memory requests, and memory limits.

  ::

    kubectl --namespace=$NS get po

and

  ::

    kubectl --namespace=$NS describe <pod_id>
    kubectl --namespace=$NS logs -f <pod_id>

are good for debugging pods.

  ::

    kubectl --namespace=$NS port-forward <STRATUS_pod_id> 8180:8080
    kubectl --namespace=$NS port-forward <Redis_pod_id> 6379:6379

will port-forward the remote pods ports 8080 and 6379 to your local ports 8180 and 6379, respectively, so you can interact with Stratus as http://localhost:8180/geoserver and with remote Redis via redis-cli -p 6379.
