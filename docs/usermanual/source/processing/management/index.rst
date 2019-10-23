.. _processing.management:

Managing and preventing system overload from WPS processes
==========================================================

As WPS processes are both powerful and flexible, it is not difficult to conceive of using an existing process to perform a task that could overwhelm the server. (For examples, a contour map with bands at every integer level, or unioning every single feature into one giant multigeometry.)

Limits can be put in place either on the service level or the process level to prevent this sort of thing from occurring. 

Process-level limits
--------------------

Using the example of the contour map (using the process ``ras:Contour``, we can set the input that determines how closely spaced the band values can be. We would specifically want to set a minimum value for that input.

#. Navigate to :menuselection:`Security --> WPS security`.

   .. figure:: img/seclink.png

      Click to open the WPS security page.

#. This page shows the various process groups. Click the :guilabel:`Manage` link next to the ``ras`` group to see the processes in that group.

   .. note:: Processes can be :ref:`restricted or disabled <sysadmin.security.wps>` on these pages.

#. Scroll down to the ``ras:Contour`` process. Click the :guilabel:`Edit` link in the :guilabel:`Limits` column.

   .. figure:: img/processlimitslink.png

      Process list with Edit link

#. This page shows all of the input parameters of the process, as well that those parameters' types.

   .. note:: Descriptions of the parameters themselves can be found via a WPS Describe Process request, or more simply by navigating to the WPS Request Builder, and selecting the process.

#. In this case, we want to set the minimum on the ``interval`` parameter, which sets the repeating distance between each contour band. While the desired value here is data-specific (and unit-specific), even a local contour map of a hiking trail, it would be unlikely to want greater detail than 100 (meters or feet). Enter :kbd:`100` in the :guilabel:`Min` box next for the ``interval`` parameter.

   .. figure:: img/processlimitspage.png

      Setting the process input limits

#. Click :guilabel:`Apply` to close the process limits page.

#. Click :guilabel:`Apply` to close the process selection page.

#. Click :guilabel:`Submit`.

To verify these settings have taken effect, open the :guilabel:`WPS request builder` and execute the process with a value outside the allowed range.

Global limits
-------------

Input size
~~~~~~~~~~

While every process is slightly different, it is possible to set a global limit for input data. This is to prevent the server from ingesting too large a file to be handled properly.

#. Navigate back to :menuselection:`Security --> WPS security`.

#. Scroll down to the box titled :guilabel:`Maximum size for complex inputs` and enter a value (in MB). This will ensure that any process that takes as input a geometry or layer, will not accept content larger than this value.

   .. figure:: img/inputlimit.png

      Setting an input limit of 100 MB

#. Click :guilabel:`Submit`.

Timeout
~~~~~~~

You may also wish to set a timeout value, so that processes don't take too much time in execution. To do this:

#. Navigate to :menuselection:`Services --> WPS`.

   .. figure:: img/wpslink.png

      Click to go to the WPS service settings page

#. Scroll down to the section titled :guilabel:`Execution Settings` and enter a timeout value in the box titled :guilabel:`Connection Timeout`. You can also more granularly set the maximum execution time for synchronous processes and asynchronous processes.

   .. figure:: img/connectiontimeout.png

      Setting global connection timeout for WPS execution

   .. note:: There are also options in this section for restricting the number of concurrent processes that can run on the server.

#. Click :guilabel:`Submit`.
