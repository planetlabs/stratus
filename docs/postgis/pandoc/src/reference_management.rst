Management Functions
====================

.. toctree::
   :maxdepth: 2

   add_geometry_column
   drop_geometry_column
   drop_geometry_table
   postgis_full_version
   postgis_geos_version
   postgis_libxml_version
   postgis_lib_build_date
   postgis_lib_version
   postgis_proj_version
   postgis_scripts_build_date




PostGIS\_Scripts\_Build\_Date
Returns build date of the PostGIS scripts.
text
PostGIS\_Scripts\_Build\_Date
Description
-----------

Returns build date of the PostGIS scripts.

Availability: 1.0.0RC1

Examples
--------

::

    SELECT PostGIS_Scripts_Build_Date();
      postgis_scripts_build_date
    -------------------------
     2007-08-18 09:09:26
    (1 row)

See Also
--------

?, ?, ?, ?, ?

PostGIS\_Scripts\_Installed
Returns version of the postgis scripts installed in this database.
text
PostGIS\_Scripts\_Installed
Description
-----------

Returns version of the postgis scripts installed in this database.

    **Note**

    If the output of this function doesn't match the output of ? you
    probably missed to properly upgrade an existing database. See the
    `Upgrading <#upgrading>`__ section for more info.

Availability: 0.9.0

Examples
--------

::

    SELECT PostGIS_Scripts_Installed();
      postgis_scripts_installed
    -------------------------
     1.5.0SVN
    (1 row)

See Also
--------

?, ?, ?

PostGIS\_Scripts\_Released
Returns the version number of the postgis.sql script released with the
installed postgis lib.
text
PostGIS\_Scripts\_Released
Description
-----------

Returns the version number of the postgis.sql script released with the
installed postgis lib.

    **Note**

    Starting with version 1.1.0 this function returns the same value of
    ?. Kept for backward compatibility.

Availability: 0.9.0

Examples
--------

::

    SELECT PostGIS_Scripts_Released();
      postgis_scripts_released
    -------------------------
     1.3.4SVN
    (1 row)

See Also
--------

?, ?, ?

PostGIS\_Version
Returns PostGIS version number and compile-time options.
text
PostGIS\_Version
Description
-----------

Returns PostGIS version number and compile-time options.

Examples
--------

::

    SELECT PostGIS_Version();
                postgis_version
    ---------------------------------------
     1.3 USE_GEOS=1 USE_PROJ=1 USE_STATS=1
    (1 row)

See Also
--------

?, ?, ?, ?, ?

Populate\_Geometry\_Columns
Ensures geometry columns are defined with type modifiers or have
appropriate spatial constraints This ensures they will be registered
correctly in
geometry\_columns
view. By default will convert all geometry columns with no type modifier
to ones with type modifiers. To get old behavior set
use\_typmod=false
text
Populate\_Geometry\_Columns
boolean
use\_typmod=true
int
Populate\_Geometry\_Columns
oid
relation\_oid
boolean
use\_typmod=true
Description
-----------

Ensures geometry columns have appropriate type modifiers or spatial
constraints to ensure they are registered correctly in
``geometry_columns`` table.

For backwards compatibility and for spatial needs such as tble
inheritance where each child table may have different geometry type, the
old check constraint behavior is still supported. If you need the old
behavior, you need to pass in the new optional argument as false
``use_typmod=false``. When this is done geometry columns will be created
with no type modifiers but will have 3 constraints defined. In
particular, this means that every geometry column belonging to a table
has at least three constraints:

-  ``enforce_dims_the_geom`` - ensures every geometry has the same
   dimension (see ?)

-  ``enforce_geotype_the_geom`` - ensures every geometry is of the same
   type (see ?)

-  ``enforce_srid_the_geom`` - ensures every geometry is in the same
   projection (see ?)

If a table ``oid`` is provided, this function tries to determine the
srid, dimension, and geometry type of all geometry columns in the table,
adding constraints as necessary. If successful, an appropriate row is
inserted into the geometry\_columns table, otherwise, the exception is
caught and an error notice is raised describing the problem.

If the ``oid`` of a view is provided, as with a table oid, this function
tries to determine the srid, dimension, and type of all the geometries
in the view, inserting appropriate entries into the ``geometry_columns``
table, but nothing is done to enforce constraints.

The parameterless variant is a simple wrapper for the parameterized
variant that first truncates and repopulates the geometry\_columns table
for every spatial table and view in the database, adding spatial
constraints to tables where appropriate. It returns a summary of the
number of geometry columns detected in the database and the number that
were inserted into the ``geometry_columns`` table. The parameterized
version simply returns the number of rows inserted into the
``geometry_columns`` table.

Availability: 1.4.0

Changed: 2.0.0 By default, now uses type modifiers instead of check
constraints to constrain geometry types. You can still use check
constraint behavior instead by using the new ``use_typmod`` and setting
it to false.

Enhanced: 2.0.0 ``use_typmod`` optional argument was introduced that
allows controlling if columns are created with typmodifiers or with
check constraints.

Examples
--------

::

    CREATE TABLE public.myspatial_table(gid serial, geom geometry);
    INSERT INTO myspatial_table(geom) VALUES(ST_GeomFromText('LINESTRING(1 2, 3 4)',4326) );
    -- This will now use typ modifiers.  For this to work, there must exist data
    SELECT Populate_Geometry_Columns('public.myspatial_table'::regclass);

    populate_geometry_columns
    --------------------------
                            1


    \d myspatial_table

                                       Table "public.myspatial_table"
     Column |           Type            |                           Modifiers
    --------+---------------------------+---------------------------------------------------------------
     gid    | integer                   | not null default nextval('myspatial_table_gid_seq'::regclass)
     geom   | geometry(LineString,4326) |

::

    -- This will change the geometry columns to use constraints if they are not typmod or have constraints already.
    --For this to work, there must exist data
    CREATE TABLE public.myspatial_table_cs(gid serial, geom geometry);
    INSERT INTO myspatial_table_cs(geom) VALUES(ST_GeomFromText('LINESTRING(1 2, 3 4)',4326) );
    SELECT Populate_Geometry_Columns('public.myspatial_table_cs'::regclass, false);
    populate_geometry_columns
    --------------------------
                            1
    \d myspatial_table_cs

                              Table "public.myspatial_table_cs"
     Column |   Type   |                            Modifiers
    --------+----------+------------------------------------------------------------------
     gid    | integer  | not null default nextval('myspatial_table_cs_gid_seq'::regclass)
     geom   | geometry |
    Check constraints:
        "enforce_dims_geom" CHECK (st_ndims(geom) = 2)
        "enforce_geotype_geom" CHECK (geometrytype(geom) = 'LINESTRING'::text OR geom IS NULL)
        "enforce_srid_geom" CHECK (st_srid(geom) = 4326)

UpdateGeometrySRID
Updates the SRID of all features in a geometry column, geometry\_columns
metadata and srid. If it was enforced with constraints, the constraints
will be updated with new srid constraint. If the old was enforced by
type definition, the type definition will be changed.
text
UpdateGeometrySRID
varchar
table\_name
varchar
column\_name
integer
srid
text
UpdateGeometrySRID
varchar
schema\_name
varchar
table\_name
varchar
column\_name
integer
srid
text
UpdateGeometrySRID
varchar
catalog\_name
varchar
schema\_name
varchar
table\_name
varchar
column\_name
integer
srid
Description
-----------

Updates the SRID of all features in a geometry column, updating
constraints and reference in geometry\_columns. Note: uses
current\_schema() on schema-aware pgsql installations if schema is not
provided.

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

This will change the srid of the roads table to 4326 from whatever it
was before

::

    SELECT UpdateGeometrySRID('roads','geom',4326);

The prior example is equivalent to this DDL statement

::

    ALTER TABLE roads
      ALTER COLUMN geom TYPE geometry(MULTILINESTRING, 4326)
        USING ST_SetSRID(geom,4326);

If you got the projection wrong (or brought it in as unknown) in load
and you wanted to transform to web mercator all in one shot You can do
this with DDL but there is no equivalent PostGIS management function to
do so in one go.

::

    ALTER TABLE roads
     ALTER COLUMN geom TYPE geometry(MULTILINESTRING, 3857) USING ST_Transform(ST_SetSRID(geom,4326),3857) ;

See Also
--------

? , ?
