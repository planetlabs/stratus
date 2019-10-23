.. _sysadmin.security.wps:

Restricting access to WPS processes
===================================

The Web Processing Service (WPS) is a very powerful geospatial analysis platform. That said, by default it is open to receiving requests from any user (anonymous access). As processes can add, edit, and delete data on the server, this is generally inadvisable.

This section will show how to restrict WPS for optimal usage in most environments.

Restricting processes that can add layers
-----------------------------------------

Two built-in processes, ``gs:Import`` and ``gs:StoreCoverage``, add the ability to save new vector and raster data (respectively) to the GeoServer catalog. These processes can be very useful, especially when chained with other processes, in that one can save the output of one process to the server as a new layer, as opposed to just sending the output back to the client.
; 
That said, there is great potential for misuse if left unrestricted.

Processes are restricted based on roles, much in the same way as layers and services. So in the tutorial, we will change the allowed role for these processes to be ``ADMIN`` only.

.. note:: See the GeoServer documentation on the `security subsystem <../../geoserver/security>`_.

#. Navigate to :menuselection:`Security --> WPS security`.

   .. figure:: img/wps_seclink.png

      Click to access WPS security settings

#. Processes are not specifically listed on this page, but instead are listed by group (prefix). Click the :guilabel:`Manage` link next to the ``gs`` group to see the processes in that group.

   .. figure:: img/wps_secgroups.png

      WPS security groups

#. Now the individual processes are displayed. In the boxes titled :guilabel:`Roles`, type :kbd:`ADMIN;` for both ``gs:Import`` and ``gs:StoreCoverage``.

   .. figure:: img/wps_secprocessroles.png

      Restricting the processes to be accessed only by the ADMIN role.

#. Click :guilabel:`Apply`.

#. Click :guilabel:`Submit`. (Both :guilabel:`Apply` and :guilabel:`Submit` are required for changes to persist.)

It is easy to test that these restrictions have gone into effect:

#. Log out of the admin account, or log in as a different user.

#. Navigate to :menuselection:`Demos --> WPS request builder`

#. In the :guilabel:`Choose process` dialog, you should not see the restricted processes in the list.

   .. figure:: img/wps_processhidden.png

      The gs:Import process is hidden for a non-ADMIN user

.. note:: You can also view the WPS capabilities document to see if the processes are displayed there.

.. note::

   The processes will only be hidden if the :guilabel:`Process Access Mode` is set to :guilabel:`HIDE` or :guilabel:`MIXED`. If set to :guilabel:`CHALLENGE`, the process will be displayed, but just won't be able to be executed.

   .. figure:: img/wps_accessmode.png

      Process Access Mode

   For more about the Process Access Mode, please see the GeoServer documentation on `WPS security <../../geoserver/extensions/wps/security.html>`_.


Disabling processes
-------------------

If these processes (or any others) aren't planned to be used on the server at all, it would be better to disable them outright. To do this:

#. Navigate back to :menuselection:`Security --> WPS security`.

#. Click the :guilabel:`Manage` link next to the ``gs`` group.

#. Uncheck the :guilabel:`Enabled` box for all the processes to be disabled.

   .. figure:: img/wps_processdisable.png

      Disabling processes

#. Click :guilabel:`Apply`.

#. Click :guilabel:`Submit`.

Repeat for any process or process group that you would like to disable.
