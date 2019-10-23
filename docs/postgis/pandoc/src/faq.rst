PostGIS Frequently Asked Questions
==================================

**Q:** Where can I find tutorials, guides and workshops on working with PostGIS?
---------------------------------------------------------------------------------

**A:** OpenGeo has a step by step tutorial guide workshop `Introduction
to PostGIS <http://workshops.opengeo.org/postgis-intro/>`__. It includes
packaged data as well as intro to working with OpenGeo Suite. It is
probably the best tutorial on PostGIS.

BostonGIS also has a `PostGIS almost idiot's guide on getting
started <http://www.bostongis.com/PrinterFriendly.aspx?content_name=postgis_tut01>`__.
That one is more focused on the windows user.

**Q:** My applications and desktop tools worked with PostGIS 1.5,but they don't work with PostGIS 2.0. How do I fix this?
--------------------------------------------------------------------------------------------------------------------------------

**A:** A lot of deprecated functions were removed from the PostGIS code
base in PostGIS 2.0. This has affected applications in addition to
third-party tools such as Geoserver, MapServer, QuantumGIS, and OpenJump
to name a few. There are a couple of ways to resolve this. For the
third-party apps, you can try to upgrade to the latest versions of these
which have many of these issues fixed. For your own code, you can change
your code to not use the functions removed. Most of these functions are
non ST\_ aliases of ST\_Union, ST\_Length etc. and as a last resort,
install the whole of ``legacy.sql`` or just the portions of
``legacy.sql`` you need.

The ``legacy.sql`` file is located in the same folder as postgis.sql.
You can install this file after you have installed postgis.sql and
spatial\_ref\_sys.sql to get back all the 200 some-odd old functions we
removed.

**Q:** When I load OpenStreetMap data with osm2pgsql, I'm getting an error failed: ERROR: operator class "gist\_geometry\_ops" does not exist for access method "gist" Error occurred. This worked fine in PostGIS 1.5.
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

**A:** In PostGIS 2, the default geometry operator class
gist\_geometry\_ops was changed to gist\_geometry\_ops\_2d and the
gist\_geometry\_ops was completely removed. This was done because
PostGIS 2 also introduced Nd spatial indexes for 3D support and the old
name was deemed confusing and a misnomer.

Some older applications that as part of the process create tables and
indexes, explicitly referenced the operator class name. This was
unnecessary if you want the default 2D index. So if you manage said
good, change index creation from:

BAD:

::

    CREATE INDEX idx_my_table_geom ON my_table USING gist(geom gist_geometry_ops);

To GOOD:

::

    CREATE INDEX idx_my_table_geom ON my_table USING gist(geom);

The only case where you WILL need to specify the operator class is if
you want a 3D spatial index as follows:

::

    CREATE INDEX idx_my_super3d_geom ON my_super3d USING gist(geom gist_geometry_ops_nd);

If you are unfortunate to be stuck with compiled code you can't change
that has the old gist\_geometry\_ops hard-coded, then you can create the
old class using the ``legacy_gist.sql`` packaged in PostGIS 2.0.2+.
However if you use this fix, you are advised to at a later point drop
the index and recreate it without the operator class. This will save you
grief in the future when you need to upgrade again.

**Q:** I'm running PostgreSQL 9.0 and I can no longer read/view geometries in OpenJump, Safe FME, and some other tools?
-------------------------------------------------------------------------------------------------------------------------

**A:** In PostgreSQL 9.0+, the default encoding for bytea data has been
changed to hex and older JDBC drivers still assume escape format. This
has affected some applications such as Java applications using older
JDBC drivers or .NET applications that use the older npgsql driver that
expect the old behavior of ST\_AsBinary. There are two approaches to
getting this to work again.

You can upgrade your JDBC driver to the latest PostgreSQL 9.0 version
which you can get from http://jdbc.postgresql.org/download.html

If you are running a .NET app, you can use Npgsql 2.0.11 or higher which
you can download from http://pgfoundry.org/frs/?group_id=1000140 and as
described on `Francisco Figueiredo's NpgSQL 2.0.11 released blog
entry <http://fxjr.blogspot.com/2010/11/npgsql-2011-released.html>`__

If upgrading your PostgreSQL driver is not an option, then you can set
the default back to the old behavior with the following change:

::

    ALTER DATABASE mypostgisdb SET bytea_output='escape';


**Q:** I tried to use PgAdmin to view my geometry column and it is blank, what gives?
-------------------------------------------------------------------------------------------

**A:** PgAdmin doesn't show anything for large geometries. The best ways
to verify you do have data in your geometry columns are?

::

    -- this should return no records if all your geom fields are filled in
    SELECT somefield FROM mytable WHERE geom IS NULL;

::

    -- To tell just how large your geometry is do a query of the form
    --which will tell you the most number of points you have in any of your geometry columns
    SELECT MAX(ST_NPoints(geom)) FROM sometable;


**Q:** What kind of geometric objects can I store?
-----------------------------------------------------

**A:** You can store Points, Linestrings, Polygons, CircularStrings,
CompoundCurves, CurvePolygons, Triangles, PolyhedralSurfaces, TINs,
Rasters, and collections of all the above. The most commonly used types
used are Points, Linestrings and Polygons, and their collections.

Points, Linestrings and Polygons can be stored either as "geometry" or
"geography". "Geometry" are cartesian representations of features in a
2D space. The shortest distance between two "geometry" points is a
straight line. "Geography" are representations of objects on a spherical
surface. The shortest distance between two "geography" points is a great
circle.

The "raster" type has a distinct set of functions for manipulation and
analysis. Refer to ? and ? for more details.

**Q:** I'm all confused. Which data store should I use geometry or geography?
---------------------------------------------------------------------------------

**A:** Short Answer: geography is a new data type that supports long
range distances measurements, but most computations on it are currently
slower than they are on geometry. If you use geography -- you don't need
to learn much about planar coordinate systems. Geography is generally
best if all you care about is measuring distances and lengths and you
have data from all over the world. Geometry data type is an older data
type that has many more functions supporting it, enjoys greater support
from third party tools, and operations on it are generally faster --
sometimes as much as 10 fold faster for larger geometries. Geometry is
best if you are pretty comfortable with spatial reference systems or you
are dealing with localized data where all your data fits in a single
`spatial reference system (SRID) <#spatial_ref_sys>`__, or you need to
do a lot of spatial processing. Note: It is fairly easy to do one-off
conversions between the two types to gain the benefits of each. Refer to
? to see what is currently supported and what is not.

Long Answer: Refer to our more lengthy discussion in the ? and `function
type matrix <#PostGIS_TypeFunctionMatrix>`__.

**Q:** I have more intense questions about geography, such as how big of a geographic region can I stuff in a geography column and still get reasonable answers. Are there limitations such as poles, everything in the field must fit in a hemisphere (like SQL Server 2008 has), speed etc?
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


**A:** Your questions are too deep and complex to be adequately answered
in this section. Please refer to our ?.


**Q:** How do I insert a GIS object into the database?
----------------------------------------------------------

**A:** First, you need to create a table with a column of type
"geometry" or "geography" to hold your GIS data. Storing geography type
data is a little different than storing geometry. Refer to ? for details
on storing geography.

For geometry: Connect to your database with ``psql`` and try the
following SQL:

::

    CREATE TABLE gtest ( gid serial primary key, name varchar(20)
            , geom geometry(LINESTRING) );

If the geometry column definition fails, you probably have not loaded
the PostGIS functions and objects into this database or are using a
pre-2.0 version of PostGIS. See the ?.

Then, you can insert a geometry into the table using a SQL insert
statement. The GIS object itself is formatted using the OpenGIS
Consortium "well-known text" format:

::

    INSERT INTO gtest (ID, NAME, GEOM)
    VALUES (
      1,
      'First Geometry',
      ST_GeomFromText('LINESTRING(2 3,4 5,6 5,7 8)')
    );

For more information about other GIS objects, see the `object
reference <#RefObject>`__.

To view your GIS data in the table:

::

    SELECT id, name, ST_AsText(geom) AS geom FROM gtest;

The return value should look something like this:

::

     id | name           | geom
    ----+----------------+-----------------------------
      1 | First Geometry | LINESTRING(2 3,4 5,6 5,7 8)
    (1 row)


**Q:** How do I construct a spatial query?
------------------------------------------------

**A:** The same way you construct any other database query, as an SQL
combination of return values, functions, and boolean tests.

For spatial queries, there are two issues that are important to keep in
mind while constructing your query: is there a spatial index you can
make use of; and, are you doing expensive calculations on a large number
of geometries.

In general, you will want to use the "intersects operator" (&&) which
tests whether the bounding boxes of features intersect. The reason the
&& operator is useful is because if a spatial index is available to
speed up the test, the && operator will make use of this. This can make
queries much much faster.

You will also make use of spatial functions, such as Distance(),
ST\_Intersects(), ST\_Contains() and ST\_Within(), among others, to
narrow down the results of your search. Most spatial queries include
both an indexed test and a spatial function test. The index test serves
to limit the number of return tuples to only tuples that *might* meet
the condition of interest. The spatial functions are then use to test
the condition exactly.

::

    SELECT id, the_geom
    FROM thetable
    WHERE
      ST_Contains(the_geom,'POLYGON((0 0, 0 10, 10 10, 10 0, 0 0))');


**Q:** How do I speed up spatial queries on large tables?
-----------------------------------------------------------

**A:** Fast queries on large tables is the *raison d'etre* of spatial
databases (along with transaction support) so having a good index is
important.

To build a spatial index on a table with a ``geometry`` column, use the
"CREATE INDEX" function as follows:

::

    CREATE INDEX [indexname] ON [tablename] USING GIST ( [geometrycolumn] );

The "USING GIST" option tells the server to use a GiST (Generalized
Search Tree) index.

    **Note**

    GiST indexes are assumed to be lossy. Lossy indexes uses a proxy
    object (in the spatial case, a bounding box) for building the index.

You should also ensure that the PostgreSQL query planner has enough
information about your index to make rational decisions about when to
use it. To do this, you have to "gather statistics" on your geometry
tables.

For PostgreSQL 8.0.x and greater, just run the ``VACUUM
        ANALYZE`` command.

For PostgreSQL 7.4.x and below, run the ``SELECT
        UPDATE_GEOMETRY_STATS()`` command.


**Q:** Why aren't PostgreSQL R-Tree indexes supported?
---------------------------------------------------------

**A:** Early versions of PostGIS used the PostgreSQL R-Tree indexes.
However, PostgreSQL R-Trees have been completely discarded since version
0.6, and spatial indexing is provided with an R-Tree-over-GiST scheme.

Our tests have shown search speed for native R-Tree and GiST to be
comparable. Native PostgreSQL R-Trees have two limitations which make
them undesirable for use with GIS features (note that these limitations
are due to the current PostgreSQL native R-Tree implementation, not the
R-Tree concept in general):

-  R-Tree indexes in PostgreSQL cannot handle features which are larger
   than 8K in size. GiST indexes can, using the "lossy" trick of
   substituting the bounding box for the feature itself.

-  R-Tree indexes in PostgreSQL are not "null safe", so building an
   index on a geometry column which contains null geometries will fail.


**Q:** Why should I use the ``AddGeometryColumn()`` function and all the other OpenGIS stuff?
----------------------------------------------------------------------------------------------

**A:** If you do not want to use the OpenGIS support functions, you do
not have to. Simply create tables as in older versions, defining your
geometry columns in the CREATE statement. All your geometries will have
SRIDs of -1, and the OpenGIS meta-data tables will *not* be filled in
properly. However, this will cause most applications based on PostGIS to
fail, and it is generally suggested that you do use
``AddGeometryColumn()`` to create geometry tables.

MapServer is one application which makes use of the ``geometry_columns``
meta-data. Specifically, MapServer can use the SRID of the geometry
column to do on-the-fly reprojection of features into the correct map
projection.


**Q:** What is the best way to find all objects within a radius of another object?
------------------------------------------------------------------------------------

**A:** To use the database most efficiently, it is best to do radius
queries which combine the radius test with a bounding box test: the
bounding box test uses the spatial index, giving fast access to a subset
of data which the radius test is then applied to.

The ``ST_DWithin(geometry, geometry, distance)`` function is a handy way
of performing an indexed distance search. It works by creating a search
rectangle large enough to enclose the distance radius, then performing
an exact distance search on the indexed subset of results.

For example, to find all objects with 100 meters of POINT(1000 1000) the
following query would work well:

::

    SELECT * FROM geotable
    WHERE ST_DWithin(geocolumn, 'POINT(1000 1000)', 100.0);

**Q:** How do I perform a coordinate reprojection as part of a query?
-------------------------------------------------------------------------

**A:** To perform a reprojection, both the source and destination
coordinate systems must be defined in the SPATIAL\_REF\_SYS table, and
the geometries being reprojected must already have an SRID set on them.
Once that is done, a reprojection is as simple as referring to the
desired destination SRID. The below projects a geometry to NAD 83 long
lat. The below will only work if the srid of the\_geom is not -1 (not
undefined spatial ref)

::

    SELECT ST_Transform(the_geom,4269) FROM geotable;


**Q:** I did an ST\_AsEWKT and ST\_AsText on my rather large geometry and it returned blank field. What gives?
------------------------------------------------------------------------------------------------------------------


**A:** You are probably using PgAdmin or some other tool that doesn't
output large text. If your geometry is big enough, it will appear blank
in these tools. Use PSQL if you really need to see it or output it in
WKT.

::

                    --To check number of geometries are really blank
                    SELECT count(gid) FROM geotable WHERE the_geom IS NULL;

**Q:** When I do an ST\_Intersects, it says my two geometries don't intersect when I KNOW THEY DO. What gives?
------------------------------------------------------------------------------------------------------------------

**A:** This generally happens in two common cases. Your geometry is
invalid -- check ? or you are assuming they intersect because ST\_AsText
truncates the numbers and you have lots of decimals after it is not
showing you.

**Q:** I am releasing software that uses PostGIS, does that mean my software has to be licensed using the GPL like PostGIS? Will I have to publish all my code if I use PostGIS?
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


**A:** Almost certainly not. As an example, consider Oracle database
running on Linux. Linux is GPL, Oracle is not, does Oracle running on
Linux have to be distributed using the GPL? No. So your software can use
a PostgreSQL/PostGIS database as much as it wants and be under any
license you like.

The only exception would be if you made changes to the PostGIS source
code, and distributed your changed version of PostGIS. In that case you
would have to share the code of your changed PostGIS (but not the code
of applications running on top of it). Even in this limited case, you
would still only have to distribute source code to people you
distributed binaries to. The GPL does not require that you *publish*
your source code, only that you share it with people you give binaries
to.
