.. _sysadmin.startup:

How to add startup parameters for GeoServer
===========================================

This section shows how to set the Java options and system properties used during startup.

JVM Options
-----------

Java provides various options for configuring the JVM environment. Options can be supplied on the command line, and start with a dash (``-``).

System properties
~~~~~~~~~~~~~~~~~

GeoServer allows global configuration settings to be provided as Java system properties for use during the startup process. Java system properties can be supplied on the command line using ``-D`` and are of one of the following forms:

* ``-Dproperty=value``
* ``-D property=value``

.. note:: You can view existing Java options (:guilabel:`system-properties`) and environment variables (:guilabel:`system-environment`) on the GeoServer Detailed Status Page at http://localhost:8080/geoserver/rest/about/status.


Kubernetes
----------

If you are using Kubernetes, you can define java options using the ``command`` parameter of your container configuration. For example::

    containers:
      - name: stratus
        image: {{ .Values.stratus.container.repo }}/{{ .Values.stratus.container.image }}:{{ .Values.stratus.container.tag }}
        command: ["java",
              "-XX:+UnlockExperimentalVMOptions",
              "-XX:+UseCGroupMemoryLimitForHeap",
              "-XX:+UseG1GC",
              "-XX:MaxRAMFraction=1",
              "-Dorg.geotools.coverage.jaiext.enabled=true",
              "-Duser.timezone=GMT",
              "-jar",
              "stratus-application-1.1.0-exec.jar",
              "--spring.profiles.active=jedis-manual"
              ]

The command is supplied as a list of values. The first value is the name of the program being run: ``java``. This is followed by any number of Java options, including system properties. Next comes the ``-jar`` option, to specify which jar is being run. This is followed by the name of the jar: ``stratus-application-1.1.0-exec.jar`` (make sure to replace ``1.1.0`` with the version of Stratus that you are using). Finally, any number program arguments are listed; in this example there is just one, defining which spring profile to enable.

For more details on configuring your containers, refer to `Define a Command and Arguments for a Container <https://kubernetes.io/docs/tasks/inject-data-application/define-command-argument-container/>`_.

Other container orchestration systems can be configured similarily.
