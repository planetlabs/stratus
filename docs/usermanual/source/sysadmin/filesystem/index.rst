.. _sysadmin.filesystem:

Shared filesystems
==================

Stratus requires a shared file system for storing raster coverages and cached map tiles.  In order to make these work, you will need to configure a file system shared between the nodes of your cluster.

.. note::

   It's important that the path to a shared file system is consistent across all nodes.  If you are deploying nodes that differ in their configuration, they should all have the same file systems mounted at the same mount points.

Kubernetes volumes
------------------

If you are using Kubernetes, the standard way to provide a shared file system is with Volumes.  The details of setting up volumes depends on the form of storage and how you are deploying your pod, as outlined in the `Kubernetes documentation <https://kubernetes.io/docs/concepts/storage/volumes/>`_.

To use a volume, a Pod specifies what volumes to provide for the Pod (the .spec.volumes field) and where to mount those into Containers (the ``.spec.containers.volumeMounts field``) as seen in the example below.

.. literalinclude:: files/kubernetes.yml
   :language: yaml


This example Kubernetes configuration mounts the host directory ``/var/stratus/gwc`` as ``/var/gwc`` in the Stratus node.  A ``hostPath`` share like this is suitable for testing and examples.

.. note:: We recommend that when mounting shared file systems to use the same concluding directory name for the host and mount paths e.g. ``container.volumeMount.mountPath: /mnt/gwc`` and ``volumes.hostPath.path: /${PROJECT_NAME}/gwc``.

A different volume plugin used in Kubernetes is Persistent Volume (PV). PV is a piece of storage in the cluster that has been provisioned by an administrator. It is a resource in the cluster just like a node is a cluster resource. PVs are volume plugins like Volumes, but have a lifecycle independent of any individual pod that uses the PV.

A Persistent Volume Claim (PVC) is a request for storage by a user. It is similar to a pod. Pods consume node resources and PVCs consume PV resources.

.. note::

   For more information on PVs and PVCs refer to the `Kubernetes documentation for PVs <https://kubernetes.io/docs/concepts/storage/persistent-volumes/>`_.
