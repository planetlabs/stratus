.. _sysadmin.wfst:

Allowing read-write access to vector data through transactions
==============================================================

Stratus contains a full Web Feature Service (WFS) implementation. This also includes support for WFS Transactions (WFS-T).

However, as unrestricted write access to data is a security risk, the WFS in Stratus is set to "Basic" by default. This means that the only requests that will be accepted are:

* GetCapabilites
* DescribeFeatureType
* GetFeature

**To enable WFS-T, you will need to change the WFS Service Level in GeoServer to Transactional** (or Complete).

#. Open the GeoServer admin interface.

#. Under the :guilabel:`Services` section, click :guilabel:`WFS`.

   .. figure:: img/wfslink.png

      WFS in the Services menu

#. Scroll down to :guilabel:`Service Level`. Click the box next to :guilabel:`Transactional`.

   .. figure:: img/wfst-basic.png

      Default setting: WFS-T not allowed

   .. figure:: img/wfst-transactional.png

      WFS-T allowed

#. Click :guilabel:`Save`.


Testing WFS transactions
------------------------   
   
To test WFS transactions you will need an editable vector data set such as a PostGIS layer.

The simplest option is to test using whatever client you plan to use in production such as QGIS. If that is not possible, you can use the following instructions to run an insert transaction manually.

This example uses a point feature type ``boundless_offices`` in the workspace ``example`` with a namespace of ``http://www.boundlessgeo.com/example``.  You can alter it to fit other feature types.

.. literalinclude:: files/offices.sql
   :language: sql
   :caption: PostGIS schema for ``boundless_offices``

Create an XML file for the insert transaction. 
     
.. literalinclude:: files/insert.xml
   :language: xml

Make adjustments to fit your data
      
.. literalinclude:: files/insert.xml
   :language: xml
   :lines: 5

Replace the ``example`` namespace with that for your workspace
   
.. literalinclude:: files/insert.xml
   :language: xml
   :lines: 14-21
   :emphasize-lines: 1-2,6-8

Replace ``example`` with the namespace identifier you specified above.  Replace ``boundless_offices`` with the name of your layer, and replace the attributes ``geom`` and ``city`` with the attributes in your data.  You may need more or fewer attributes.

.. literalinclude:: files/insert.xml
   :language: xml
   :lines: 14-21
   :emphasize-lines: 3-5

Replace the GML geometry with one appropriate for your layer. For more details, see the `GML 3.2 specification <http://www.opengeospatial.org/standards/gml>`_.

When you have set up your transaction, you can execute it via curl or a similar HTTP client.

.. code-block:: bash

  curl -u admin:geoserver -XPOST -H "Content-type: text/xml" -d @insert.xml http://localhost:8080/geoserver/ows

Replace ``admin:geoserver`` with your username and password, ``insert.xml`` with the path to your transaction XML, and ``localhost:8080`` with the host where you are running Stratus. The result you get back should look like the following if you were successful.  

.. literalinclude:: files/output.xml
   :language: xml
   :emphasize-lines: 14,19-23

Notice that the ``TransactionSummary`` shows 1 insert, and the ``InsertResults`` shows one ``Feature`` indicating that the insertion was successful.
