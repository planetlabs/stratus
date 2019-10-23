Drop Geometry Column
====================

    Removes a geometry column from a spatial table.

    *Synopsis*

      .. code::

        text DropGeometryColumn(varchar table_name, varchar column_name);

        text DropGeometryColumn(varchar schema_name, varchar table_name,
            varchar column_name)

        text DropGeometryColumn(varchar catalog_name, varchar schema_name,
            varchar table_name, varchar column_name);

    *Description*

        Removes a geometry column from a spatial table. Note that ``schema\_name`` will need to match the ``f\_table\_schema`` field of the table's row in the ``geometry\_columns`` table.

        This method implements the `OpenGIS Simple Features Implementation Specification for SQL 1.1 <http://www.opengeospatial.org/standards/sfs>`_.

        This function supports 3d and will not drop the z-index.

        This method supports Circular Strings and Curves

        .. note::

            Changed: 2.0.0 This function is provided for backward compatibility. Now that since geometry\_columns is now a view against the system catalogs, you can drop a geometry column like any other table column using ``ALTER TABLE``.

    *Examples*

        ::

            SELECT DropGeometryColumn ('my_schema','my_spatial_table','geom');

                        ----RESULT output ---

            dropgeometrycolumn
            ------------------------------------------------------
             my_schema.my_spatial_table.geom effectively removed.

            -- In PostGIS 2.0+ the above is also equivalent to the standard
            -- the standard alter table.  Both will deregister from geometry_columns
            ALTER TABLE my_schema.my_spatial_table DROP column geom;



*See Also*

    :ref:`Add Geometry Column`
    :ref:`Drop Geometry Table`
    :ref:`The GEOMETRY_COLUMNS VIEW`


