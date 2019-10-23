PostGIS_PROJ_Version 
=====================

	Returns the version number of the PROJ4 library.

    *Synopsis*

      .. code::

         text PostGIS_PROJ_Version();



    *Description*

    	Returns the version number of the PROJ4 library.


    *Examples*

		::

		    SELECT PostGIS_PROJ_Version();
		      postgis_proj_version
		    -------------------------
		     Rel. 4.4.9, 29 Oct 2004
		    (1 row)


*See Also*
	
	:ref:`PostGIS_Full_Version`, :ref:`PostGIS_GEOS_Version`, :ref:`PostGIS_Lib_Version`, :ref:`PostGIS_LibXML_Version`, :ref:`PostGIS_Version`
