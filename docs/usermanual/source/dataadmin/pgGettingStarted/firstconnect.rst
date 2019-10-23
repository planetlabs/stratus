.. _dataadmin.pgGettingStarted.firstconnect:

Connecting to PostgreSQL on Amazon Aurora for the first time
============================================================

You can connect to a DB instance in your Amazon Aurora PostgreSQL DB cluster using the same tools that you use to connect to a PostgreSQL database. 

To connect, you use the same public key for Secure Sockets Layer (SSL) connections as you would for any other connection to that Amazon instance. You can use the endpoint and port information from the primary DB instance or use Aurora Replicas in your Aurora PostgreSQL DB cluster in the connection string of any script, utility, or application that connects to a PostgreSQL DB instance. In the connection string, specify the DNS address from the primary instance or Aurora Replica endpoint as the host parameter. Specify the port number from the endpoint as the port parameter.

For more details, refer to the `applicable Amazon RDS documentation <https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Aurora.Connecting.html#Aurora.Connecting.AuroraPostgreSQL>`_.

.. note:: This section uses the command line utility ``psql`` and optionally the graphical utility ``pgAdmin`` and assumes you have security priveleges to write to the Aurora database cluster.

The default PostgreSQL configuration has connections turned off for the ``postgres`` user.

In the details view for your Aurora PostgreSQL DB cluster you can find the cluster endpoint. You use this endpoint in your PostgreSQL connection string. The endpoint is made up of the domain name and port for your DB cluster.

Connect with the psql command line utility
------------------------------------------

You can use a local instance of the psql command line utility to connect to a PostgreSQL DB instance. You need either PostgreSQL or the psql client installed on your client computer. To connect to your PostgreSQL DB instance using psql, you need to provide host information and access credentials.

Use one of the following formats to connect to a PostgreSQL DB instance on Amazon RDS. When you connect, you're prompted for a password. For batch jobs or scripts, use the --no-password option.

For Unix, use the following format::

  psql \
   --host=<DB instance endpoint> \
   --port=<port> \
   --username=<master user name> \
   --password \
   --dbname=<database name>

For Windows, use the following format::

  psql ^
   --host=<DB instance endpoint> ^
   --port=<port> ^
   --username=<master user name> ^
   --password ^
   --dbname=<database name>

For example, the following command connects to a database called mypgdb on a PostgreSQL DB instance called mypostgresql using fictitious credentials::

  psql --host=mypostgresql.c6c8mwvfdgv0.us-west-2.rds.amazonaws.com --port=5432 --username=awsuser --password --dbname=mypgdb

First time access problems
--------------------------

If you try to connect to PostgreSQL via the :command:`psql` command-line utility or through :command:`pgAdmin`, you may get connection errors. By default it's assumed that you have access to a VPC (Amazon Virtual Protected Cloud).

If you want to access a DB instance that is not in a VPC, you must set access rules for a DB security group to allow access from specific EC2 security groups or CIDR IP ranges. You then must associate that DB instance with that DB security group. This process is called ingress. Once ingress is configured for a DB security group, the same ingress rules apply to all DB instances associated with that DB security group.

For more information, and setting up security groups to allow access to the database cluster please see the `RDS Aurora Connectivity <https://s3-us-west-2.amazonaws.com/jsmiley-share/Aurora/RDS+Aurora+Connectivity+Guide+-+v4.pdf>`_.
