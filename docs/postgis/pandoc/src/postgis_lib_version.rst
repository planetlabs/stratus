PostGIS_Lib_Version 
====================

	Returns the version number of the PostGIS library.

    *Synopsis*

      .. code::

         text PostGIS_Lib_Version();



    *Description*

    	Returns the version number of the PostGIS library.


    *Examples*

		::

		    SELECT PostGIS_Lib_Version();
		     postgis_lib_version
		    ---------------------
		     1.3.3
		    (1 row)


*See Also*
	
	:ref:`PostGIS_Full_Version`, :ref:`PostGIS_GEOS_Version`, :ref:`PostGIS_LibXML_Version`, :ref:`PostGIS_PROJ_Version`, :ref:`PostGIS_Version`
