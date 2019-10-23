PostGIS_Scripts_Build_Date
===========================

	Returns build date of the PostGIS scripts.

    *Synopsis*

      .. code::

         text PostGIS_Scripts_Build_Date();



    *Description*

    	Returns build date of the PostGIS scripts.


    *Examples*

		::

		    SELECT PostGIS_Scripts_Build_Date();
		      postgis_scripts_build_date
		    -------------------------
		     2007-08-18 09:09:26
		    (1 row)

*See Also*
	
	:ref:`PostGIS_Full_Version`, :ref:`PostGIS_GEOS_Version`, :ref:`PostGIS_Lib_Version`, :ref:`PostGIS_LibXML_Version`, :ref:`PostGIS_Version`
