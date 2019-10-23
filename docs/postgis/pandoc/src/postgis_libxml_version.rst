PostGIS_LibXML_Version
=======================

	 Returns the version number of the LibXML2 library.

    *Synopsis*

      .. code::

         text PostGIS_GEOS_Version();



    *Description*

    	Returns the version number of the LibXML2 library. Availability: 1.5



    *Examples*

		::

		    SELECT PostGIS_LibXML_Version();
		     postgis_libxml_version
		    ----------------------
		     2.7.6
		    (1 row)

*See Also*

	:ref:`PostGIS_Full_Version`, :ref:`PostGIS_Lib_Version`, :ref:`PostGIS_PROJ_Version`, :ref:`Post_GEOS_Version`, :ref:`PostGIS_Version`, 