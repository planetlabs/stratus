.. _sysadmin.jndi:

Working with JNDI
-----------------

Applications such as GeoServer are in position to work with database connection pools set up by the application server. The Java Naming and Directory Interface (JNDI) can be used to create a connection pool for a JDBC data source.

In Stratus, JNDI can be configured using an application.yml resource file. Please refer to :ref:`sysadmin.config`.

An example JNDI resource file for application-jndi.yml might look like this:

   .. code-block:: yaml

    stratus:
      jndi:
        sources:
          -
            name: jdbc/postgres
            properties:
              url: jdbc:postgresql://localhost:5432/na_roads
              username: docker
              password: docker
          -
            name: jdbc/postgres
            properties:
              url: jdbc:postgresql://localhost:5432/na_roads
              username: docker
              password: docker

   .. note:: For more information about the possible parameters and their values refer to the `DBCP documentation <http://commons.apache.org/dbcp/configuration.html>`_.

  When adding a store in GeoServer, select the :guilabel:`JNDI` option. Enter the following information:

    * The :guilabel:`jndiReferenceName` used by the application server.

    * The :guilabel:`schema` used by the database.

    Using the configuration example above, the :guilabel:`jndiReferenceName` would be ``jdbc/postgres``.
