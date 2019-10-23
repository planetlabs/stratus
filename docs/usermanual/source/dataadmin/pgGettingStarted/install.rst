.. _dataadmin.pgGettingStarted.install:


Installing PostgreSQL and PostGIS
=================================

This section describes how to get and install PostgreSQL and PostGIS database for use in the subsequent sections.

Amazon Aurora with PostgresSQL
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

When working with Stratus, we recommend using Amazon Aurora with PostgreSQL which supports PostgreSQL 9.3, 9.4, 9.5, 9.6, and 10; all these versions support the PostGIS extension. Information about the supported minor versions is available in the `Amazon RDS User Guide <https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_PostgreSQL.html#PostgreSQL.Concepts.General.DBVersions>`_.

PostgreSQL and PostGIS
~~~~~~~~~~~~~~~~~~~~~~

Installing PostgreSQL locally allows you to work with command line utilities to connect and interact with your cloud database clusters. Determine which version is most applicable to your environment and install it locally with the PostGIS extension, following instructions for your platform laid out in these `PostGIS installation instructions <https://postgis.net/install/>`_.

pgAdmin
~~~~~~~

Some sections inclue instructions for using ``pgAdmin`` an optional GUI for PostgreSQL with the same functionality as the command line utility ``psql``.  You can install ``pgAdmin`` with instructions found at `pgAdmin's website <https://www.pgadmin.org/>`_.

Links
-----
 * `PostGIS <https://postgis.net>`_
 * `PostgreSQL <https://www.postgresql.org/>`_
 * `Amazon Aurora PostgreSQL <https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Aurora.AuroraPostgreSQL.html>`_
