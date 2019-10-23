.. _install.standalone:

Standalone Stratus Deployment
=============================

It is possible to deploy Stratus locally on a single computer.

This sort of deployment should **never** be used for production systems, and is instead intended for evaluation, demonstration, or testing purposes only.

.. note:: These instructions are largely identical regardless of what OS you are running on, except that on Windows you should use the ``set`` command to set environment variables such as ``REDIS_IP``.

For a simple minimal deployment: 

**Prerequisites**:

1. `Docker <https://docs.docker.com/install/>`_ is installed. Verify using:: 

    docker --version

2. The Stratus image is downloaded into Docker (Fill in ``$DOCKER_REPO`` and ``$DOCKER_TAG`` as appropriate)::

    docker pull $DOCKER_REPO/gsstratus/stratus:$DOCKER_TAG

**Deployment**:

1. Start a Redis instance::

    docker run -d --name redis -p 6379:6379 -e "SERVICE_NAME=redis" redis

2. Get the IP of the Redis container::

    REDIS_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' redis)

3. Start a Stratus instance, passing in the Redis IP (additional environment variables or :ref:`configuration properties <sysadmin.config>` can be passed using the ``-e`` option)::

    docker run --rm -e STRATUS_ADMIN_ENABLED=true -e STRATUS_CATALOG_REDIS_MANUAL_HOST=$REDIS_IP --name stratus-dev -p 8080:8080 gsstratus/stratus

4. Wait a few minutes for Stratus to startup. You should now be able to visit http://localhost:8080/geoserver/web and see the Stratus homepage.

5. (Optional) Start a PostGIS instance, for storing spatial data. Note the IP of the PostGIS instance (similar to how you got the Redis IP, above) for when you add it as a store to Stratus::

    docker run --name postgis -p 5432:5432 -d -t kartoza/postgis
    docker exec -d postgis service postgresql start

More advanced standalone deployments can be configured using Kubernetes or other container management software.