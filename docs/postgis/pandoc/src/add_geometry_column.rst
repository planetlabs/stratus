AddGeometryColumn
==================

    Adds a geometry column to an existing table of attributes. By default uses type modifier to define rather than constraints. Pass in false for use_typmod to get old check constraint based behavior

    *Synopsis*

      .. code::

        text AddGeometryColumn (varchar table_name, varchar column_name,
           integer srid, varchar type, integer dimension,
           boolean use_typmod=true);

        text AddGeometryColumn (varchar schema_name, varchar table_name,
           varchar column_name, integer srid, varchar type,
           integer dimension, boolean use_typmod=true);

        text AddGeometryColumn (varchar catalog_name, varchar schema_name,
           varchar table_name, varchar column_name, integer srid,
           varchar type, integer dimension,boolean use_typmod=true);

    *Description*

       Adds a geometry column to an existing table of attributes. The ``schema_name`` is the name of the table schema. The ``srid`` must be an integer value reference to an entry in the SPATIAL_REF_SYS table. The ``type`` must be a string corresponding to the geometry type, eg, 'POLYGON' or 'MULTILINESTRING' . An error is thrown if the schemaname doesn't exist (or not visible in the current search_path) or the specified SRID, geometry type, or dimension is invalid.

        .. note::

            **Changed: 2.0.0** This function no longer updates ``geometry_columns`` since ``geometry_columns`` is a view that reads from system catalogs. It by default also does not create constraints, but instead uses the built in type modifier behavior of PostgreSQL. So for example building a wgs84 POINT column with this function is now equivalent to: ``ALTER TABLE some_table ADD COLUMN geom geometry(Point,4326);``

            **Changed: 2.0.0** If you require the old behavior of constraints use the default ``use_typmod``, but set it to false.

        .. note::

            **Changed: 2.0.0** Views can no longer be manually registered in ``geometry_columns``, however views built against geometry typmod tables geometries and used without wrapper functions will register themselves correctly because they inherit the typmod behavior of their parent table column. Views that use geometry functions that output other geometries will need to be cast to typmod geometries for these view geometry columns to be registered correctly in ``geometry_columns``. Refer to :ref:`Manually Registering Geometry Columns in geometry_columns`.


        This method implements the `OpenGIS Simple Features Implementation Specification for SQL 1.1 <http://www.opengeospatial.org/standards/sfs>`_.

        This function supports 3d and will not drop the z-index.

        This method supports Circular Strings and Curves

        .. note::

            **Enhanced: 2.0.0**``use_typmod`` argument introduced. Defaults to creating typmod geometry column instead of constraint-based.

    *Examples*

    ::

        -- Create schema to hold data

        CREATE SCHEMA my_schema;
        -- Create a new simple PostgreSQL table

        CREATE TABLE my_schema.my_spatial_table (id serial);

        -- Describing the table shows a simple table with a single "id" column.
        postgis=# \d my_schema.my_spatial_table

        Table "my_schema.my_spatial_table"

         Column |  Type   |  Modifiers
        --------+---------+-------------------------------------------------------------------------
         id     | integer | not null default nextval('my_schema.my_spatial_table_id_seq'::regclass)

        -- Add a spatial column to the table
        SELECT AddGeometryColumn ('my_schema','my_spatial_table','geom',4326,'POINT',2);

        -- Add a point using the old constraint based behavior
        SELECT AddGeometryColumn ('my_schema','my_spatial_table','geom_c',4326,'POINT',2, false);

        --Add a curvepolygon using old constraint behavior
        SELECT AddGeometryColumn ('my_schema','my_spatial_table','geomcp_c',4326,'CURVEPOLYGON',2, false);

        -- Describe the table again reveals the addition of a new geometry columns.
        \d my_schema.my_spatial_table
                                    addgeometrycolumn
        -------------------------------------------------------------------------
         my_schema.my_spatial_table.geomcp_c SRID:4326 TYPE:CURVEPOLYGON DIMS:2
        (1 row)

                                            Table "my_schema.my_spatial_table"
          Column  |         Type         |                                Modifiers
        ----------+----------------------+-------------------------------------------------------------------------
         id       | integer              | not null default nextval('my_schema.my_spatial_table_id_seq'::regclass)
         geom     | geometry(Point,4326) |
         geom_c   | geometry             |
         geomcp_c | geometry             |
        Check constraints:
            "enforce_dims_geom_c" CHECK (st_ndims(geom_c) = 2)
            "enforce_dims_geomcp_c" CHECK (st_ndims(geomcp_c) = 2)
            "enforce_geotype_geom_c" CHECK (geometrytype(geom_c) = 'POINT'::text OR geom_c IS NULL)
            "enforce_geotype_geomcp_c" CHECK (geometrytype(geomcp_c) = 'CURVEPOLYGON'::text OR geomcp_c IS NULL)
            "enforce_srid_geom_c" CHECK (st_srid(geom_c) = 4326)
            "enforce_srid_geomcp_c" CHECK (st_srid(geomcp_c) = 4326)

        -- geometry_columns view also registers the new columns --
        SELECT f_geometry_column As col_name, type, srid, coord_dimension As ndims
            FROM geometry_columns
            WHERE f_table_name = 'my_spatial_table' AND f_table_schema = 'my_schema';

         col_name |     type     | srid | ndims
        ----------+--------------+------+-------
         geom     | Point        | 4326 |     2
         geom_c   | Point        | 4326 |     2
         geomcp_c | CurvePolygon | 4326 |     2


*See Also*

   :ref:`Drop Geometry Column`
   :ref:`Drop Geometry Table`
   :ref:`The GEOMETRY_COLUMNS VIEW`
   :ref:`Manually Registering Geometry Columns in geometry_columns`

