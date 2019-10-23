.. _sysadmin.security.masterpwd:

Managing the master password
============================

The master password for GeoServer provides administrator access. It is used as the password to the GeoServer ``root`` account, which is a built-in administrator account. The ``root`` account is different from other GeoServer administrator accounts such as the default ``admin`` account, in that it is not possible to change the user name or roles of of the ``root`` account. The ``root`` account always has the role of ``ROLE_ADMINISTRATOR`` which provides access to all configuration options and functionality.

The master password also provides access to the keystore, the collection of reversible passwords that GeoServer saves for access to resources such as PostGIS datastores and other database connections.

Read more about the `master password <../../geoserver/security/passwd.html>`_ in the GeoServer reference.

Why change the master password
------------------------------

The master password is initially set using `application.yml <../sysadmin/config/index.rst>`_ and defaults to ``geoserver``.  This means it has either been stored in plain text, or is a default known password. The former is a security risk, and the latter is an extreme security risk. In either case, changing the master password should be one of the first things done after installing Stratus.

Verifying the master password
-----------------------------

One should only need to log in to the GeoServer web admin interface as root for purposes of disaster recovery, such as when security configurations result in no administrative user being able to log in. An administrative user is defined as a user with the ``ROLE_ADMINISTRATOR`` role.

That said, one can log in to the GeoServer web admin interface as ``root`` to verify the master password. Use the following credentials:

* User name: ``root``
* Password: ``<master password>``

If the login is successful, the password is correct.

Changing the master password
----------------------------

#. Log in to the GeoServer web admin interface with an administrator account (a user that possesses the ``ROLE_ADMINISTRATOR`` role).

#. On the homepage, you should see a notification prompting you to change your master password. Click :guilabel:`Change it`. Proceed to step 5.

   .. figure:: img/masterpwd_changeit.png

      *Change the master password*

#. Alternatively, click :guilabel:`Passwords` in the :guilabel:`Security` section.

   .. figure:: img/masterpwd_menu_passwords.png

      *Passwords link in the Security menu*

#. At the very top of the screen next to :guilabel:`Active master password provider`, click :guilabel:`Change password` .

   .. figure:: img/masterpwd_page.png

      *Link to change the master password*

#. In the form that follows, enter the current master password, then the new master password, then again for confirmation. Click :guilabel:`Change password` when done.

   .. note:: The password will need to conform to the master password policy. By default, the policy states that the master password must be at least eight characters.

   .. figure:: img/masterpwd_change.png

      *Changing the master password*

#. Guard the new master password the same as any root or administrator account credentials.


.. todo::  re-instate this section once SUITE-1508 is resolved

   Changing the master password policy.

   By default, the master password policy states that the master password must be at least eight characters. It may be desired to change this policy to provide a different level of security.

   #. Log in to the GeoServer web admin interface with an administrator account (a user that possesses the ``ROLE_ADMINISTRATOR`` role).

   #. Once logged in, click :guilabel:`Passwords` in the :guilabel:`Security` section.

   .. figure:: img/masterpwd_menu_passwords.png

      *Passwords link in the Security menu*

   #. In the section titled :guilabel:`Password Policies`, click the :guilabel:`master` password policy.

   .. figure:: img/masterpwd_policy.png

      *Master password policy in the list of policies*

   #. In the form that follows, adjust the settings. There are settings for the type of characters allowed in the password, and the length of the password. Click :guilabel:`Save` when done.

   .. figure:: img/masterpwd_policychange.png

      *Changing the master password policy*

   The policy does not check to see if the current master password adheres to this new policy. After changing the policy, it is a good idea to go back and change the password to ensure that it adheres to this new policy.

