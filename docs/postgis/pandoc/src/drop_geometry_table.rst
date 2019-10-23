Drop Geometry Table
====================

  Drops a table and all its references in geometry\_columns.

  *Synopsis*

     .. code::

        boolean DropGeometryTable(varchar table_name);

        boolean DropGeometryTable(varchar schema_name, varchar table_name);

        boolean DropGeometryTable(varchar catalog_name, varchar schema_name,
            varchar table_name);

  *Description*

    Drops a table and all its references in geometry\_columns. Note: uses ``current_schema()`` on schema-aware pgsql installations if schema is not provided.

    .. note::

      Changed: 2.0.0 This function is provided for backward compatibility. Now that since geometry\_columns is now a view against the system catalogs, you can drop a table with geometry columns like any other table using ``DROP TABLE``.

  *Examples*

    ::

        SELECT DropGeometryTable ('my_schema','my_spatial_table');

        ----RESULT output ---
        my_schema.my_spatial_table dropped.

        -- The above is now equivalent to --
        DROP TABLE my_schema.my_spatial_table;

*See Also*

   :ref:`Add Geometry Column`, :ref:`Drop Geometry Column`, :ref:`The GEOMETRY_COLUMNS VIEW`
