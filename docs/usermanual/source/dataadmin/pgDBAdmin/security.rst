.. _dataadmin.pgDBAdmin.security:


Security with Amazon Aurora PostgreSQL
======================================

Aurora PostgreSQL has a flexible permissions system, with the ability to assign specific privileges to specific roles_, and assign users to one or more of those roles_. In addition, as the PostgreSQL server supports a number of methods for authenticating users, the database can use the same authentication infrastructure as other system components. This helps reduces the maintenance overhead by simplifying password management.


Users and roles
---------------

To control who can perform Amazon RDS management actions on Aurora DB clusters and DB instances, you use AWS Identity and Access Management (IAM). When you connect to AWS using IAM credentials, your IAM account must have IAM policies that grant the permissions required to perform Amazon RDS management operations. For more information, see `Authentication and Access Control for Amazon RDS <https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/UsingWithRDS.IAM.html>`_.

.. note:: If you are using an IAM account to access the Amazon RDS console, you must first log on to the AWS Management Console with your IAM account, and then go to the Amazon RDS console at


Encryption
----------

PostgreSQL provides a number of `encryption facilities <http://www.postgresql.org/docs/current/static/encryption-options.html>`_. Some of these facilities are enabled by default, while others are optional.

All passwords are MD5 encrypted by default. The client/server handshake double encrypts the MD5 password to prevent re-use of the hash by anyone who intercepts the password. `SSL connections <http://www.postgresql.org/docs/current/static/libpq-ssl.html>`_ (Secure Sockets Layer) are optionally available between the client and server, to encrypt all data and login information. SSL certificate authentication is also available when SSL connections are used.

Database columns can be encrypted using the pgcrypto_ module, which includes hashing algorithms, direct ciphers (blowfish, aes) and both public key and symmetric PGP encryption.

Using SSL with a PostgreSQL DB Instance
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Amazon RDS supports Secure Socket Layer (SSL) encryption for PostgreSQL DB instances. Using SSL, you can encrypt a PostgreSQL connection between your applications and your PostgreSQL DB instances. You can also force all connections to your PostgreSQL DB instance to use SSL.

SSL support is available in all AWS regions for PostgreSQL. Amazon RDS creates an SSL certificate for your PostgreSQL DB instance when the instance is created. If you enable SSL certificate verification, then the SSL certificate includes the DB instance endpoint as the Common Name (CN) for the SSL certificate to guard against spoofing attacks.

**To connect to a PostgreSQL DB instance over SSL**

  1. Download the certificate stored at https://s3.amazonaws.com/rds-downloads/rds-combined-ca-bundle.pem.

  2. Import the certificate into your operating system.

  3. Connect to your PostgreSQL DB instance over SSL by appending sslmode=verify-full to your connection string. When you use sslmode=verify-full, the SSL connection verifies the DB instance endpoint against the endpoint in the SSL certificate.

    Use the sslrootcert parameter to reference the certificate, for example, sslrootcert=rds-ssl-ca-cert.pem.

The following is an example of using the psql program to connect to a PostgreSQL DB instance :

  ::

    $ psql -h testpg.cdhmuqifdpib.us-east-1.rds.amazonaws.com -p 5432 \
      "dbname=testpg user=testuser sslrootcert=rds-ca-2015-root.pem sslmode=verify-full"

**Requiring an SSL Connection to a PostgreSQL DB Instance**

You can require that connections to your PostgreSQL DB instance use SSL by using the rds.force_ssl parameter. By default, the rds.force_ssl parameter is set to 0 (off). You can set the rds.force_ssl parameter to 1 (on) to require SSL for connections to your DB instance. Updating the rds.force_ssl parameter also sets the PostgreSQL ssl parameter to 1 (on) and modifies your DB instance’s pg_hba.conf file to support the new SSL configuration.

You can set the rds.force_ssl parameter value by updating the parameter group for your DB instance. If the parameter group for your DB instance isn't the default one, and the ssl parameter is already set to 1 when you set rds.force_ssl to 1, you don't need to reboot your DB instance. Otherwise, you must reboot your DB instance for the change to take effect. For more information on parameter groups, see Working with DB Parameter Groups.

When the rds.force_ssl parameter is set to 1 for a DB instance, you see output similar to the following when you connect, indicating that SSL is now required:

  ::

    $ psql postgres -h SOMEHOST.amazonaws.com -p 8192 -U someuser
    psql (9.3.12, server 9.4.4)
    WARNING: psql major version 9.3, server major version 9.4.
    Some psql features might not work.
    SSL connection (cipher: DHE-RSA-AES256-SHA, bits: 256)
    Type "help" for help.

    postgres=>

**Determining the SSL Connection Status**

The encrypted status of your connection is shown in the logon banner when you connect to the DB instance:

  ::

    Password for user master:
    psql (9.3.12)
    SSL connection (cipher: DHE-RSA-AES256-SHA, bits: 256)
    Type "help" for help.

    postgres=>

You can also load the sslinfo extension and then call the ssl_is_used() function to determine if SSL is being used. The function returns t if the connection is using SSL, otherwise it returns f.

  ::

    postgres=> create extension sslinfo;
    CREATE EXTENSION

    postgres=> select ssl_is_used();
    ssl_is_used
    ---------
    t
    (1 row)

You can use the select ssl_cipher() command to determine the SSL cipher:

  ::

    postgres=> select ssl_cipher();
    ssl_cipher
    --------------------
    DHE-RSA-AES256-SHA
    (1 row)

If you enable set rds.force_ssl and restart your instance, non-SSL connections are refused with the following message:

  ::

    $ export PGSSLMODE=disable
    $ psql postgres -h SOMEHOST.amazonaws.com -p 8192 -U someuser
    psql: FATAL: no pg_hba.conf entry for host "host.ip", user "someuser", database "postgres", SSL off
    $

Data encryption
~~~~~~~~~~~~~~~

.. ToDo:: couldn't find this file - consider removing topic - too brief to be of much use

There are many encryption options available with the pgcrypto_ module. One of the simplest examples is encrypting a column of data using a symmetric cipher. To set this up, complete the following steps:


 1. Enable pgcrypto by enabling the ``pgcrypto`` extenstion, either using pgAdmin or psql.

   ::

    postgres=> create extension pgcrypto;
    CREATE EXTENSION


 2. Test the encryption function.

   .. code-block:: sql

      -- encrypt a string using blowfish (bf)
      SELECT encrypt('this is a test phrase', 'mykey', 'bf');

 3. Ensure the encryption is reversible.

   .. code-block:: sql

      -- round-trip a string using blowfish (bf)
      SELECT decrypt(encrypt('this is a test phrase', 'mykey', 'bf'), 'mykey', 'bf');


Authentication
--------------

See `Authentication and Access Control for Amazon RDS  <https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/UsingWithRDS.IAM.html>`_ to see how to use authentication methods provided by AWS.


Links
-----

 * `PostgreSQL Authentication <http://www.postgresql.org/docs/current/static/auth-methods.html>`_
 * `PostgreSQL Encrpyption <http://www.postgresql.org/docs/current/static/encryption-options.html>`_
 * `PostgreSQL SSL Support <http://www.postgresql.org/docs/current/static/libpq-ssl.html>`_

.. _GSSAPI: <http://en.wikipedia.org/wiki/Generic_Security_Services_Application_Program_Interface>
.. _SSPI: http://msdn.microsoft.com/en-us/library/windows/desktop/aa380493(v=vs.85).aspx
.. _RADIUS: http://en.wikipedia.org/wiki/RADIUS
.. _LDAP: http://en.wikipedia.org/wiki/Lightweight_Directory_Access_Protocol
.. _Kerberos: http://en.wikipedia.org/wiki/Kerberos_(protocol)
.. _PAM: http://en.wikipedia.org/wiki/Pluggable_authentication_module
.. _pgcrypto: http://www.postgresql.org/docs/current/static/pgcrypto.html
.. _roles: http://www.postgresql.org/docs/current/static/user-manag.html
