.. _sysadmin.security.adminpwd:

Managing the admin password
===========================

GeoServer provides a default ``admin`` account for setting up the server. The password to this account is ``geoserver``. Because this password defaults to the same value on all installations of GeoServer, changing the admin password should be one of the first things done after installing Stratus.

Changing the admin password
---------------------------

#. Log in to the GeoServer web admin interface with the ``admin`` account.

#. On the homepage, you should see a notification prompting you to change your administrator password. Click :guilabel:`Change it`.

   .. figure:: img/adminpwd_changeit.png

      *Change the administrator password*

#. Alternatively, click :guilabel:`Users, Groups, and Roles` in the :guilabel:`Security` section.

   .. figure:: img/adminpwd_menu_users.png

      *Users, Groups, and Roles link in the Security menu*

#. At the top of the screen, click the :guilabel:`Users/Groups` tab.

   .. figure:: img/adminpwd_tabs.png

      *Users, Groups, and Roles tabs*

#. In the user list, click the :guilabel:`admin` user.
  
   .. figure:: img/adminpwd_list.png

      *Admin user*

#. In the form that follows, enter the new admin password, then again for confirmation. Click :guilabel:`Save` when done.

   .. figure:: img/adminpwd_change.png

      *Changing the admin password*

#. Guard the new admin password the same as any root or administrator account credentials.
