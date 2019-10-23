.. _sysadmin.config:

Application Configuration
=========================

Stratus uses various properties to configure much of its functionality.

Stratus is bundled with a properties file that defines the default configuration. There are two recommended methods for overriding this default configuration - setting environment variables, and specifying a different configuration file.

.. note:: For more details on all the different ways in which you can configure Spring Boot applications such as Stratus, refer to `Spring Boot Externalized Configuration <https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config>`_.

Environment Variables
---------------------

If you are only altering a few properties, setting environment variables is the simplest way of modifying your configuration.

.. note:: Most configuration properties in Stratus are delimited by periods, e.g. ``stratus.admin-enabled``. Operating systems typically disallow period-seperated key names for environement variables. To convert a property name into a valid environment variable, use the following rules:

   * Strictly use all uppercase
   * Replace all periods and hyphens in the property path with underscores
   * Separate camelCase variables with underscore where the case changes (For these properties, camelCase and hyphens are treated as equivalent, so stratus.admin-enabled and stratus.adminEnabled are equivalent)

   Following these rules, ``stratus.admin-enabled`` would become ``STRATUS_ADMIN_ENABLED``

You can specify environment variables for each of your containers using your container orchestration system. For example, if you are using Kubernetes, include the ``env`` or ``envFrom`` field in the configuration file::

    env:
      - name: STRATUS_ADMIN_ENABLED
        value: "false"
      - name: GEOWEBCACHE_CACHE_DIR
        value: "/data/gwc"

For more details on setting environment variables using Kubernetes, refer to `Define Environment Variables for a Container <https://kubernetes.io/docs/tasks/inject-data-application/define-environment-variable-container/>`_.

If you use both environment variables and a custom configuration file to specify configuration, any environment variables will take priority over equivalent properties in the configuration file.


Specifying a Configuration File
-------------------------------

If you wish to fully customize your Stratus configuration, specifying an alternate configuration file may be easier to manage than defining a large number of environment variables.

You can specify an alternate configuration file (or files) by setting the ``SPRING_CONFIG_LOCATION`` environment variable. The value of this property is a comma-seperated list of file or directory references, e.g. ``file:/path/to/config.yml``. The list is ordered by precedence (properties defined in locations higher in the list override those defined in lower locations).
Using ``SPRING_CONFIG_LOCATION`` will replace all properties defined in the default properties file bundled with Stratus. If instead wish to selectively override values from the default configuration with values from your custom configuration, instead use the environment variable ``SPRING_CONFIG_ADDITIONAL_LOCATION``.

For more information about specifying alternate configuration files, refer to `Application Property Files <https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-application-property-files>`_

Spring Profiles
---------------

Spring profiles provide a way to segregate parts of the application configuration and make it available only in certain environments. Certain configuration properties are only applicable under specific spring profiles. To set which spring profiles are active, you can use the ``spring.profiles.active`` configuration property.

By default, Stratus will start using the redis-manual profile which will attempt to connect to a single Redis host.  The following profiles are available:

.. list-table::
   :class: non-responsive
   :header-rows: 1
   :stub-columns: 1
   :widths: 30 70

   * - Profile
     - Description
   * - jedis-manual
     - Attempts to connect to a single Redis instance using the Jedis client
   * - lettuce-manual
     - Attempts to connect to a single Redis instance using the Lettuce client
   * - jedis-sentinel
     - Attempts to connect to a Redis Sentinel host using the Jedis client
   * - lettuce-sentinel
     - Attempts to connect to a Redis Sentinel host using the Lettuce client
   * - jedis-cluster
     - Attempts to connect to a Redis Cluster using the Jedis client
   * - lettuce-cluster
     - Attempts to connect to a Redis Cluster using the Lettuce client
   * - jedis-aws-tag-discovery
     - Attempts to connect to Redis using metadata provided by AWS tags using the Jedis client
   * - lettuce-aws-tag-discovery
     - Attempts to connect to Redis using metadata provided by AWS tags using the Lettuce client
   * - jedis-discovery
     - Attempts to use the Spring discovery client to obtain Redis endpoint information and connect using the Jedis client
   * - lettuce-discovery
     - Attempts to use the Spring discovery client to obtain Redis endpoint information and connect using the Lettuce client
   * - cloud
     - Used by PCF to connect to a Redis service


List of Application Properties
------------------------------

In addition to the properties specified below, Spring Boot provides a great number of other permitted properties. Refer to `Appendix A. Common application properties <https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html>`_ of the Spring Boot documentation for a list of common properties. Not all of these properties are necessarily applicable to Stratus.

Refer to the :download:`application.yaml included in Stratus <files/application.yml>` for recommended values for some of these additional properties.

Stratus Properties
~~~~~~~~~~~~~~~~~~

Stratus adds the following configuration properties:

.. list-table::
   :class: non-responsive
   :header-rows: 1
   :stub-columns: 1
   :widths: 25 50 25

   * - Property
     - Description
     - Default Value
   * - ``stratus.admin-enabled``
     - Controls whether the Stratus Web Administration GUI is enabled for this container.
     - false
   * - ``stratus.proxy-base-url``
     - The Stratus Proxy Base URL. Replaces the URL of individual instance in REST and OWS responses.
     -
   * - ``stratus.min-wait-for-initializer-check``
     - When Stratus starts up for the first time, one instance will perform initialization. This parameter controls the minimum time a Stratus instance will wait to check if initialization has been completed.
     - 1000
   * - ``stratus.max-wait-for-initializer-check``
     - When Stratus starts up for the first time, one instance will perform initialization. This parameter controls the maximum time a Stratus instance will wait to check if initialization has been completed.
     - 10000
   * - ``stratus.initializer-timeout``
     - When Stratus starts up for the first time, one instance will perform initialization. This parameter controls the time, in millisectonds, that an instance will wait before attempting to reacquire the initialization lock if initialization has not completed.
     - 240000
   * - ``stratus.gwc.default-file-blobstore``
     - This parameter controls whether Stratus should automatically create a default file blobstore upon initialization. **This is intended primarily for testing environments and should not be used in production**. For production systems, you should instead :ref:`manually create a file blobstore <sysadmin.caching.basics>` referencing a shared storage location.
     - false
   * - ``stratus.web.request-logging-filter-enabled``
     - Whether or not Stratus should log all requests it handles.
     - false
   * - ``stratus.web.request-logging-filter-log-request-bodies``
     - Whether or not Stratus should log the bodies of all requests it handles. Only applies if ``stratus.web.request-logging-filter-enabled`` is ``true``.
     - false
   * - ``stratus.web.theme``
     - **Experimental**. The graphical theme to use for the Web Administration GUI. Supported values are ``light`` or ``dark``.
     - light
   * - ``stratus.web.enable-redis-sessions``
     - Whether to enable redis sessions for each HTTP request to Stratus.
     - false
   * - ``stratus.catalog.redis.share-native-lettuce-connection``
     - Enables multiple lettuce connections to share a single native connection. If set to false, every redis operation will open and close a socket.
     - false
   * - ``stratus.catalog.redis.enable-connection-pooling``
     - Enables a shared connection pool for redis connections. The pool can be configured using the ``stratus.catalog.redis.pool`` properties.
     - true
   * - ``stratus.catalog.redis.enable-stratus-1-2-upgrade``
     - Enables automatic catalog upgrade from Stratus 1.2.0 (and earlier) to Stratus 1.3.0. If you are starting from a fresh catalog on Stratus 1.3.0, you can set this to ``false`` for a small performance increase.
     - true
   * - ``stratus.catalog.redis.caching.enable-rest-caching``
     - If set to ``true``, when a REST request is recieved by Stratus, it will scan the request and load data from redis into a temporary local catalog which will be used for the duration of the request. This reduces the total number of redis queries made over the lifetime of the Stratus request, and should result in shorter request times. Performance of parallel vs. batched depends upon your Stratus environment and redis configuration. Most basic configurations perform best with ``stratus.catalog.redis.caching.enable-rest-caching`` and ``stratus.catalog.redis.caching.enable-ows-caching`` set to ``true`` and ``stratus.catalog.redis.caching.use-parallel-queries`` set to ``false``.
     - true
   * - ``stratus.catalog.redis.caching.enable-ows-caching``
     - If set to ``true``, when an OWS request is recieved by Stratus, it will scan the request and load data from redis into a temporary local catalog which will be used for the duration of the request. This reduces the total number of redis queries made over the lifetime of the Stratus request, and should result in shorter request times. Performance of parallel vs. batched depends upon your Stratus environment and redis configuration. Most basic configurations perform best with ``stratus.catalog.redis.caching.enable-rest-caching`` and ``stratus.catalog.redis.caching.enable-ows-caching`` set to ``true`` and ``stratus.catalog.redis.caching.use-parallel-queries`` set to ``false``.
     - true
   * - ``stratus.catalog.redis.caching.use-parallel-queries``
     - If ``true``, Stratus will make individual parallel queries to redis when preloading the request cache from redis. Otherwise, it will use batched MULTI queries. Performance of parallel vs. batched depends upon your Stratus environment and redis configuration. Most basic configurations perform best with ``stratus.catalog.redis.caching.enable-rest-caching`` and ``stratus.catalog.redis.caching.enable-ows-caching`` set to ``true`` and ``stratus.catalog.redis.caching.use-parallel-queries`` set to ``false``.
     - false
   * - ``stratus.catalog.redis.pool.max-total``
     - The maximum number of objects that can be allocated by the pool (checked out to clients, or idle awaiting checkout) at a given time. When negative, there is no limit to the number of objects that can be managed by the pool at one time. Only applies if ``stratus.catalog.redis.enable-connection-pooling`` is ``true``.
     - 1000
   * - ``stratus.catalog.redis.pool.min-idle``
     - The target for the minimum number of idle objects to maintain in the pool. If the configured value of ``min-idle`` is greater than the configured value for ``max-idle`` then the value of ``max-idle`` will be used instead. Only applies if ``stratus.catalog.redis.enable-connection-pooling`` is ``true``.
     - 15
   * - ``stratus.catalog.redis.max-idle``
     -  The target for the maximum number of idle objects to maintain in the pool. If ``max-idle`` is set too low on heavily loaded systems it is possible you will see objects being destroyed and almost immediately new objects being created. This is a result of the active threads momentarily returning objects faster than they are requesting them, causing the number of idle objects to rise above ``max-idle``. The best value for ``max-idle`` for heavily loaded system will vary but the default is a good starting point. Only applies if ``stratus.catalog.redis.enable-connection-pooling`` is ``true``.
     - 20
   * - ``stratus.catalog.redis.manual.host``
     - The redis host, if redis manual configuration is enabled (spring profiles ``lettuce-manual`` or ``jedis-manual``).
     - localhost
   * - ``stratus.catalog.redis.manual.port``
     - The redis port, if redis manual configuration is enabled (spring profiles ``lettuce-manual`` or ``jedis-manual``).
     - 6379
   * - ``stratus.catalog.redis.manual.database``
     - The redis database, if redis manual configuration is enabled (spring profiles ``lettuce-manual`` or ``jedis-manual``).
     - 0
   * - ``stratus.catalog.redis.cluster.hosts``
     - A list of redis cluster nodes. Only used if redis cluster configuration is enabled (spring profiles ``lettuce-cluster`` or ``jedis-cluster``).
     - localhost:6379
   * - ``stratus.catalog.redis.sentinal.master``
     - The name of the sentinel master. Only used if redis sentinel configuration is enabled (spring profiles ``lettuce-sentinel`` or ``jedis-sentinel``).
     - mymaster
   * - ``stratus.catalog.redis.sentinal.hosts``
     - A list of sentinel hosts. Only used if redis sentinel configuration is enabled (spring profiles ``lettuce-sentinel`` or ``jedis-sentinel``).
     - localhost:2639
   * - ``stratus.store.overwrite-resources``
     - If ``true``, any existing resources in redis will be overwritten by the values specified under ``stratus.store.resource`` upon initialization. Initialization happens once, the first time Stratus starts up.
     - false
   * - ``stratus.jndi.sources``
     - A list of JNDI souces to register in Stratus. Sources should have a ``name``, and any number of key-value ``properties``. For example::

           -name: jdbc/roads
            properties:
              url: jdbc:postgresql://localhost:5432/na_roads
              username: docker
              password: docker
     -
   * - ``stratus.store.resource``
     - Specify the values of any number of GeoServer resources to load into redis when Stratus is initialized. This will happen once, the first time Stratus starts up. Resource values are specified by appending the relative path of the resource to ``stratus.store.resource``. For example, to set the default admin password, use ``stratus.store.resource.security/masterpw/default/passwd=geoserver``.
     -
   * - ``stratus.wps.file-storage``
     - Where to store WPS output. Anything other than S3 indicates local file storage
     - s3
   * - ``stratus.wps.s3-region``
     - s3 region for WPS output storage
     - US_EAST_1
   * - ``stratus.wps.s3-bucket``
     - The s3 bucket used for WPS output storage
     - stratus-wps
   * - ``stratus.wps.s3-url``
     - The S3 endpoint to use for S3 storage
     - https://s3.amazonaws.com/
   * - ``stratus.wps.access-key``
     - Optional S3 access key to use. Otherwise default credential chain is used
     -
   * - ``stratus.wps.secret-key``
     - Optional S3 secret key to use. Otherwise default credential chain is used
     -

Additional Properties
~~~~~~~~~~~~~~~~~~~~~

In addition to the properties added by Stratus (above), there are several additional properties supported by spring which are used to control important GeoServer configuration. The usual ways of altering this configuration in GeoServer (e.g. editing web.xml or changing the logging profile) are not applicable to Stratus, so they must be configured via these properties instead.

.. list-table::
   :class: non-responsive
   :header-rows: 1
   :stub-columns: 1
   :widths: 25 50 25

   * - Property
     - Description
     - Default Value
   * - ``logging.level``
     - Specifies the logging levels for each package. Values are specified using the period-delimited package name, for example: ``logging.level.stratus.redis.geoserver=INFO``.
     -
   * - ``server.servlet.context_parameters``
     - Defines any number of servlet context parameters.
     -
   * - ``server.servlet.context_parameters.serviceStrategy``
     - Sets the service strategy for Stratus. Valid values are ``SPEED``, ``BUFFER``, ``FILE``, and ``PARTIAL-BUFFER``. Refer to `Set a service strategy for more details <../../geoserver/production/config.html#set-a-service-strategy>`_ for more details on what each service strategy does.
     - "SPEED"
   * - ``server.servlet.context_parameters.PARTIAL_BUFFER_STRATEGY_SIZE``
     - The size of the partial buffer. Only applicable when ``server.servlet.context_parameters.serviceStrategy=PARTIAL-BUFFER``.
     - 50
