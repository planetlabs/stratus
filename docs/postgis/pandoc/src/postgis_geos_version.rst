PostGIS_GEOS_Version
=====================

	 Returns the version number of the GEOS library.

    *Synopsis*

      .. code::

         text PostGIS_GEOS_Version();



    *Description*

    	Returns the version number of the GEOS library, or NULL if GEOS support is not enabled.



    *Examples*

		::

		    SELECT PostGIS_GEOS_Version();
		     postgis_geos_version
		    ----------------------
		     3.1.0-CAPI-1.5.0
		    (1 row)


*See Also*

	:ref:`PostGIS_Full_Version`, :ref:`PostGIS_Lib_Version`, :ref:`PostGIS_LibXML_Version`, :ref:`PostGIS_PROJ_Version`, :ref:`PostGIS_Version`