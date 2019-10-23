.. _dataadmin.oracle:

Working with Oracle data
========================

Stratus can read and publish data from an Oracle Spatial database. Any table that has a valid geometry column can be published as a layer in GeoServer.

Oracle support
--------------

Oracle isn't included in the default Stratus image. To get an image that includes the Oracle datastore, build Stratus with the ``oracle`` profile or use the "ALL" image. 

Because connecting to the Oracle datastore requires use of the Oracle JDBC driver, you must have an Oracle License to use the Oracle datastore.

Verifying the Oracle datastore
------------------------------

To verify that the Oracle datastore is included in your Stratus image:

#. Log in to the GeoServer web interface.

#. Click :guilabel:`Stores` then :guilabel:`Add new store`.

#. In the list of :guilabel:`Vector Data Stores`, you should see **three entries** that mention Oracle:

   .. figure:: oracle_storelink.png

      Oracle in the list of vector stores

   If you don't see any entries, then Oracle is not included in your Stratus image.

For more information on adding a store and publishing layers, please see the `GeoServer documentation for Oracle <../../geoserver/data/database/oracle.html>`_.

.. note:: If you encounter the error::

       Error creating data store, check the parameters. Error message: Unable to obtain 
       connection: Cannot create PoolableConnectionFactory (ORA-00604: error occurred at 
       recursive SQL level 1 ORA-01882: timezone region not found )

   Add the :ref:`startup parameter <sysadmin.startup>` ``-Duser.timezone=GMT``. This should resolve the issue.

Caveats
-------

Oracle data will be assumed to be point geometries, so new layers will be styled accordingly. Data can be rendered as intended by changing the styling of the layer to use the correct geometry.
