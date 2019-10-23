PostGIS Raster Frequently Asked Questions
=========================================

**Q:** Where can I find out more about the PostGIS Raster Project?

**A:** Refer to the `PostGIS Raster home
page <http://trac.osgeo.org/postgis/wiki/WKTRaster>`__.

**Q:** Are there any books or tutorials to get me started with this
wonderful invention?

**A:** There is a full length beginner tutorial `Intersecting vector
buffers with large raster coverage using PostGIS
Raster <http://trac.osgeo.org/postgis/wiki/WKTRasterTutorial01>`__.
Jorge has a series of blog articles on PostGIS Raster that demonstrate
how to load raster data as well as cross compare to same tasks in Oracle
GeoRaster. Check out `Jorge's PostGIS Raster / Oracle GeoRaster
Series <http://gis4free.wordpress.com/category/postgis-raster/>`__.
There is a whole chapter (more than 35 pages of content) dedicated to
PostGIS Raster with free code and data downloads at `PostGIS in Action -
Raster chapter <http://www.postgis.us/chapter_13>`__. You can `buy
PostGIS in Action <http://www.postgis.us/page_buy_book>`__ now from
Manning in hard-copy (significant discounts for bulk purchases) or just
the E-book format. You can also buy from Amazon and various other book
distributors. All hard-copy books come with a free coupon to download
the E-book version.

Here is a review from a PostGIS Raster user `PostGIS raster applied to
land classification urban
forestry <http://fuzzytolerance.info/code/postgis-raster-ftw/>`__

**Q:** How do I install Raster support in my PostGIS database?

**A:** The easiest is to download binaries for PostGIS and Raster which
are currently available for windows and latest versions of Mac OSX.
First you need a working PostGIS 2.0.0 or above and be running
PostgreSQL 8.4, 9.0, or 9.1. Note in PostGIS 2.0 PostGIS Raster is fully
integrated, so it will be compiled when you compile PostGIS.

Instructions for installing and running under windows are available at
`How to Install and Configure PostGIS raster on
windows <http://gis4free.wordpress.com/2011/03/10/how-to-install-and-configure-postgis-raster-on-windows/>`__

If you are on windows, you can compile yourself, or use the
`pre-compiled PostGIS Raster windows
binaries <http://postgis.net/windows_downloads>`__. If you are on Mac
OSX Leopard or Snow Leopard, there are binaries available at `Kyng Chaos
Mac OSX PostgreSQL/GIS
binaries <http://www.kyngchaos.com/software/postgres>`__.

Then to enable raster support in your database, run the rtpostgis.sql
file in your database. To upgrade an existing install use
rtpostgis\_upgrade\_minor..sql instead of rtpostgis.sql

For other platforms, you generally need to compile yourself.
Dependencies are PostGIS and GDAL. For more details about compiling from
source, please refer to `Installing PostGIS Raster from source (in prior
versions of
PostGIS) <http://trac.osgeo.org/postgis/wiki/WKTRaster/Documentation01#a2.3-CompilingandInstallingfromSources>`__

**Q:** I get error could not load library "C:/Program
Files/PostgreSQL/8.4/lib/rtpostgis.dll": The specified module could not
be found. or could not load library on Linux when trying to run
rtpostgis.sql

**A:** rtpostgis.so/dll is built with dependency on libgdal.dll/so. Make
sure for Windows you have libgdal-1.dll in the bin folder of your
PostgreSQL install. For Linux libgdal has to be in your path or bin
folder.

You may also run into different errors if you don't have PostGIS
installed in your database. Make sure to install PostGIS first in your
database before trying to install the raster support.

**Q:** How do I load Raster data into PostGIS?

**A:** The latest version of PostGIS comes packaged with a
``raster2pgsql`` raster loader executable capable of loading many kinds
of rasters and also generating lower resolution overviews without any
additional software. Please refer to ? for more details. Pre-2.0
versions came with a ``raster2pgsql.py`` that required python with numpy
and GDAL. This is no longer needed.

**Q:** What kind of raster file formats can I load into my database?

**A:** Any that your GDAL library supports. GDAL supported formats are
documented `GDAL File
Formats <http://www.gdal.org/formats_list.html>`__.

Your particular GDAL install may not support all formats. To verify the
ones supported by your particular GDAL install, you can use

::

    raster2pgsql -G

**Q:** Can I export my PostGIS raster data to other raster formats?

**A:** Yes

GDAL 1.7+ has a PostGIS raster driver, but is only compiled in if you
choose to compile with PostgreSQL support.

The driver currently doesn't support irregularly blocked rasters,
although you can store irregularly blocked rasters in PostGIS raster
data type.

If you are compiling from source, you need to include in your configure

::

    --with-pg=path/to/pg_config

to enable the driver. Refer to `GDAL Build
Hints <http://trac.osgeo.org/gdal/wiki/BuildHints>`__ for tips on
building GDAL against in various OS platforms.

If your version of GDAL is compiled with the PostGIS Raster driver you
should see PostGIS Raster in list when you do

::

    gdalinfo --formats

To get a summary about your raster via GDAL use gdalinfo:

::

    gdalinfo  "PG:host=localhost port=5432 dbname='mygisdb' user='postgres' password='whatever' schema='someschema' table=sometable"

To export data to other raster formats, use gdal\_translate the below
will export all data from a table to a PNG file at 10% size.

Depending on your pixel band types, some translations may not work if
the export format does not support that Pixel type. For example floating
point band types and 32 bit unsigned ints will not translate easily to
JPG or some others.

Here is an example simple translation

::

    gdal_translate -of PNG -outsize 10% 10% "PG:host=localhost port=5432 dbname='mygisdb' user='postgres' password='whatever' schema='someschema' table=sometable" C:\somefile.png

You can also use SQL where clauses in your export using the where=... in
your driver connection string. Below are some using a where clause

::

    gdal_translate -of PNG -outsize 10% 10% "PG:host=localhost port=5432 dbname='mygisdb' user='postgres' password='whatever' schema='someschema' table=sometable where='filename=\'abcd.sid\''" " C:\somefile.png

::

    gdal_translate -of PNG -outsize 10% 10% "PG:host=localhost port=5432 dbname='mygisdb' user='postgres' password='whatever' schema='someschema' table=sometable where='ST_Intersects(rast, ST_SetSRID(ST_Point(-71.032,42.3793),4326) )' " C:\intersectregion.png

To see more examples and syntax refer to `Reading Raster Data of PostGIS
Raster
section <http://trac.osgeo.org/gdal/wiki/frmts_wtkraster.html#a3.2-Readingrasterdatafromthedatabase>`__

**Q:** Are their binaries of GDAL available already compiled with
PostGIS Raster suppport?

**A:** Yes. Check out the page `GDAL
Binaries <http://trac.osgeo.org/gdal/wiki/DownloadingGdalBinaries>`__
page. Any compiled with PostgreSQL support should have PostGIS Raster in
them.

PostGIS Raster is undergoing many changes. If you want to get the latest
nightly build for Windows -- then check out the Tamas Szekeres nightly
builds built with Visual Studio which contain GDAL trunk, Python
Bindings and MapServer executables and PostGIS Raster driver built-in.
Just click the SDK bat and run your commands from there.
http://vbkto.dyndns.org/sdk/. Also available are VS project files.

`FWTools latest stable version for Windows is compiled with Raster
support <http://fwtools.maptools.org/>`__.

**Q:** What tools can I use to view PostGIS raster data?

**A:** You can use MapServer compiled with GDAL 1.7+ and PostGIS Raster
driver support to view Raster data. QuantumGIS (QGIS) now supports
viewing of PostGIS Raster if you have PostGIS raster driver installed.

In theory any tool that renders data using GDAL can support PostGIS
raster data or support it with fairly minimal effort. Again for Windows,
Tamas' binaries http://vbkto.dyndns.org/sdk/ are a good choice if you
don't want the hassle of having to setup to compile your own.

**Q:** How can I add a PostGIS raster layer to my MapServer map?

**A:** First you need GDAL 1.7 or higher compiled with PostGIS raster
support. GDAL 1.8 or above is preferred since many issues have been
fixed in 1.8 and more PostGIS raster issues fixed in trunk version.

You can much like you can with any other raster. Refer to `MapServer
Raster processing options <http://mapserver.org/input/raster.html>`__
for list of various processing functions you can use with MapServer
raster layers.

What makes PostGIS raster data particularly interesting, is that since
each tile can have various standard database columns, you can segment it
in your data source

Below is an example of how you would define a PostGIS raster layer in
MapServer.

    **Note**

    The mode=2 is required for tiled rasters and was added in PostGIS
    2.0 and GDAL 1.8 drivers. This does not exist in GDAL 1.7 drivers.

::

    -- displaying raster with standard raster options
    LAYER
        NAME coolwktraster
        TYPE raster
        STATUS ON
        DATA "PG:host=localhost port=5432 dbname='somedb' user='someuser' password='whatever' 
            schema='someschema' table='cooltable' mode='2'" 
        PROCESSING "NODATA=0"
        PROCESSING "SCALE=AUTO"
        #... other standard raster processing functions here
        #... classes are optional but useful for 1 band data
        CLASS
            NAME "boring"
            EXPRESSION ([pixel] < 20)
            COLOR 250 250 250
        END
        CLASS
            NAME "mildly interesting"
            EXPRESSION ([pixel] > 20 AND [pixel] < 1000)
            COLOR 255 0 0
        END
        CLASS
            NAME "very interesting"
            EXPRESSION ([pixel] >= 1000)
            COLOR 0 255 0
        END
    END
            

::

    -- displaying raster with standard raster options and a where clause
    LAYER
        NAME soil_survey2009
        TYPE raster
        STATUS ON
        DATA "PG:host=localhost port=5432 dbname='somedb' user='someuser' password='whatever' 
            schema='someschema' table='cooltable' where='survey_year=2009' mode='2'"    
        PROCESSING "NODATA=0"
        #... other standard raster processing functions here
        #... classes are optional but useful for 1 band data
    END
            

**Q:** What functions can I currently use with my raster data?

**A:** Refer to the list of ?. There are more, but this is still a work
in progress.

Refer to the `PostGIS Raster roadmap
page <http://trac.osgeo.org/postgis/wiki/WKTRaster/PlanningAndFunding>`__
for details of what you can expect in the future.

**Q:** I am getting error ERROR: function st\_intersects(raster,
unknown) is not unique or st\_union(geometry,text) is not unique. How do
I fix?

**A:** The function is not unique error happens if one of your arguments
is a textual representation of a geometry instead of a geometry. In
these cases, PostgreSQL marks the textual representation as an unknown
type, which means it can fall into the st\_intersects(raster, geometry)
or st\_intersects(raster,raster) thus resulting in a non-unique case
since both functions can in theory support your request. To prevent
this, you need to cast the geometry to a geometry.

For example if your code looks like this:

::

    SELECT rast
     FROM my_raster
       WHERE ST_Intersects(rast, 'SRID=4326;POINT(-10 10)');

Cast the textual geometry representation to a geometry by changing your
code to this:

::

    SELECT rast
     FROM my_raster
       WHERE ST_Intersects(rast, 'SRID=4326;POINT(-10 10)'::geometry);

**Q:** How is PostGIS Raster different from Oracle GeoRaster
(SDO\_GEORASTER) and SDO\_RASTER types?

**A:** For a more extensive discussion on this topic, check out Jorge
Arévalo `Oracle GeoRaster and PostGIS Raster: First
impressions <http://gis4free.wordpress.com/2010/07/19/oracle-georaster-part-i/>`__

The major advantage of one-georeference-by-raster over
one-georeference-by-layer is to allow:

\* coverages to be not necessarily rectangular (which is often the case
of raster coverage covering large extents. See the possible raster
arrangements in the documentation)

\* rasters to overlaps (which is necessary to implement lossless vector
to raster conversion)

These arrangements are possible in Oracle as well, but they imply the
storage of multiple SDO\_GEORASTER objects linked to as many SDO\_RASTER
tables. A complex coverage can lead to hundreds of tables in the
database. With PostGIS Raster you can store a similar raster arrangement
into a unique table.

It's a bit like if PostGIS would force you to store only full
rectangular vector coverage without gaps or overlaps (a perfect
rectangular topological layer). This is very practical in some
applications but practice has shown that it is not realistic or
desirable for most geographical coverages. Vector structures needs the
flexibility to store discontinuous and non-rectangular coverages. We
think it is a big advantage that raster structure should benefit as
well.

**Q:** raster2pgsql load of large file fails with String of N bytes is
too long for encoding conversion?

**A:** raster2pgsql doesn't make any connections to your database when
generating the file to load. If your database has set an explicit client
encoding different from your database encoding, then when loading large
raster files (above 30 MB in size), you may run into a
``bytes is too long for encoding conversion``.

This generally happens if for example you have your database in UTF8,
but to support windows apps, you have the client encoding set to
``WIN1252``.

To work around this make sure the client encoding is the same as your
database encoding during load. You can do this by explicitly setting the
encoding in your load script. Example, if you are on windows:

::

    set PGCLIENTENCODING=UTF8

If you are on Unix/Linux

::

    export PGCLIENTENCODING=UTF8

Gory details of this issue are detailed in
http://trac.osgeo.org/postgis/ticket/2209
