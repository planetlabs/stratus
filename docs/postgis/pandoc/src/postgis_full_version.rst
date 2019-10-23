PostGIS_Full_Version
=====================

	Reports full postgis version and build configuration infos. Also informs about synchronization between libraries and scripts suggesting upgrades as needed.

    *Synopsis*

      .. code::

         text PostGIS_Full_Version();



    *Description*

    	Reports full postgis version and build configuration infos. Also informs about synchronization between libraries and scripts suggesting upgrades as needed.



    *Examples*

		::

		    SELECT PostGIS_Full_Version();
		                                   postgis_full_version
		    ----------------------------------------------------------------------------------
		     POSTGIS="1.3.3" GEOS="3.1.0-CAPI-1.5.0" PROJ="Rel. 4.4.9, 29 Oct 2004" USE_STATS
		    (1 row)


*See Also*

	:ref:`Upgrading`, :ref:`PostGIS_GEOS_Version`, :ref:`PostGIS_Lib_Version`, :ref:`PostGIS_LibXML_Version`, :ref:`PostGIS_PROJ_Version`, :ref:`PostGIS_Version`