.. _install:

Installation
============

Stratus is intended for a distributed cloud environment. This means that it is not installed on a single computer, but is instead split up across multiple virtual machines which are in turn managed by autoscaling and load-balancing software. This is referred to as a Stratus deployment.

.. todo: How to get Stratus. For now, provided through PS. Add this section when there is a defined way for customers to get Stratus.

Structure of a Stratus deployment
---------------------------------

A typical Stratus deployment consists of the following layers:

**Host Infrastructure**

The network of virtual machines which the containers run on. This is typically provided by a third-party cloud hosting provider, but you can use your own local cloud infrastructure if you desire. Many of the larger cloud hosting providers also provide provisioning and container management solutions.

Examples:

* `AWS (Amazon Web Services) <https://aws.amazon.com/>`_
* `Microsoft Azure <https://azure.microsoft.com/>`_
* `GCP (Google Cloud Platform) <https://cloud.google.com/>`_

**Provisioning and Container management** 

Provisioning an environment involves deploying all the containers and any other components that make up the environment to the host infrastructure. Typically, there is some configuration that describes the deployment environment, which is used by the provisioning software to construct the environment. 

The container management software manages the containers when they are running. It is responsible for load-balancing, autoscaling, and reprovisioning failed containers. It often (but not always) overlaps with the provisioning software.

Examples:

* `Helm <https://helm.sh/>`_ + `Kubernetes <https://kubernetes.io/>`_
* `Terraform <https://www.terraform.io/>`_ + `Kubernetes <https://kubernetes.io/>`_

**Containers** 

The virtual machines running the application software. This consists of Stratus, `Redis <https://hub.docker.com/_/redis/>`_, and `PostGIS <https://hub.docker.com/r/kartoza/postgis/>`_ in a minimal deployment, but can also include other databases such as MongoDB or Oracle, and other software including monitoring applications and single-sign-on providers. In a typical autoscaling setup, there are a variable number of containers running the same Docker image (this is managed by the container management layer)

The Stratus software consists of a single Docker image—all the other constituents of a Stratus deployment are available from third parties.

Deploying Stratus
-----------------

Given the the variety of options available, there are innumerable ways to go about deploying Stratus.

We recommend deploying Stratus in AWS, using the Helm package manager for Kubernetes. Helm charts helps you define deployments, install, and upgrade Stratus in Kubernetes with ease.

.. toctree::
   :hidden:

   kubernetes_aws
   helm
   kubernetes_resources
   standalone

Deploying Stratus on AWS
""""""""""""""""""""""""

For instructions on deploying Stratus using AWS, Helm and Kubernetes, first :ref:`install Kubernetes on AWS <install.kubernetes_aws>`, then :ref:`use Helm to deploy Stratus on Kubernetes <install.helm>`.

If you require assistance deploying Stratus on AWS using other provisioning and container management software, reference the ``deploy`` section of the Stratus GitHub repository.

.. todo: add more instructions as they get written.

Deploying Stratus on Microsoft Azure
""""""""""""""""""""""""""""""""""""

We do not have instructions for deploying Stratus on Microsoft Azure. Please reference the ``deploy`` section of the Stratus GitHub repository for general deployment details, and feel free to contribute improvements.

Deploying Stratus on GCP
""""""""""""""""""""""""

We do not have instructions for deploying Stratus on Google Cloud Platform. Please reference the ``deploy`` section of the Stratus GitHub repository for general deployment details, and feel free to contribute improvements.

Standalone Deployment
"""""""""""""""""""""

For instructions on deploying Stratus on a single machine without the use of any provisioning or container management, refer to :ref:`install.standalone`.

.. note:: This sort of deployment should **never** be used for production systems, and is instead intended for evaluation, demonstration, or testing purposes only.

