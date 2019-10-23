PostGIS Installation
====================

This chapter details the steps required to install PostGIS.

Short Version
------------------

To compile assuming you have all the dependencies in your search path:

::

    tar xvfz postgis-LAST_RELEASE_VERSION.tar.gz
    cd postgis-LAST_RELEASE_VERSION
    ./configure
    make
    make install

Once postgis is installed, it needs to be enabled in each individual
database you want to use it in.

    **Note**

    The raster support is currently optional, but installed by default.
    For enabling using the PostgreSQL 9.1+ extensions model raster is
    required. Using the extension enable process is preferred and more
    user-friendly. To spatially enable your database:

::

    psql -d yourdatabase -c "CREATE EXTENSION postgis;"
    psql -d yourdatabase -c "CREATE EXTENSION postgis_topology;"
    psql -d yourdatabase -c "CREATE EXTENSION postgis_tiger_geocoder;"

Please refer to ? for more details about querying installed/available
extensions and upgrading extensions, or switching from a non-extension
install to an extension install.

For those running PostgreSQL 9.0 or who decided for some reason not to
compile with raster support, or just are old-fashioned, here are longer
more painful instructions for you:

All the .sql files once installed will be installed in
share/contrib/postgis-LAST\_MINOR\_VERSION folder of your PostgreSQL
install

::

    createdb yourdatabase
    createlang plpgsql yourdatabase
    psql -d yourdatabase -f postgis.sql
    psql -d yourdatabase -f postgis_comments.sql
    psql -d yourdatabase -f spatial_ref_sys.sql
    psql -d yourdatabase -f rtpostgis.sql
    psql -d yourdatabase -f raster_comments.sql
    psql -d yourdatabase -f topology/topology.sql
    psql -d yourdatabase -f topology_comments.sql

The rest of this chapter goes into detail each of the above installation
steps.

Install Requirements
------------------------

PostGIS has the following requirements for building and usage:

**Required**

-  PostgreSQL MIN\_POSTGRES\_VERSION or higher. A complete installation
   of PostgreSQL (including server headers) is required. PostgreSQL is
   available from http://www.postgresql.org .

   For a full PostgreSQL / PostGIS support matrix and PostGIS/GEOS
   support matrix refer to
   http://trac.osgeo.org/postgis/wiki/UsersWikiPostgreSQLPostGIS

-  GNU C compiler (``gcc``). Some other ANSI C compilers can be used to
   compile PostGIS, but we find far fewer problems when compiling with
   ``gcc``.

-  GNU Make (``gmake`` or ``make``). For many systems, GNU ``make`` is
   the default version of make. Check the version by invoking
   ``make -v``. Other versions of ``make`` may not process the PostGIS
   ``Makefile`` properly.

-  Proj4 reprojection library, version 4.6.0 or greater. The Proj4
   library is used to provide coordinate reprojection support within
   PostGIS. Proj4 is available for download from
   http://trac.osgeo.org/proj/ .

-  GEOS geometry library, version 3.3 or greater, but GEOS 3.4+ is
   recommended to take full advantage of all the new functions and
   features. Without GEOS 3.4, you will be missing some major
   enhancements such as ST\_Triangles and long-running function
   interruption, and improvements to geometry validation and making
   geometries valid such as ST\_ValidDetail and ST\_MakeValid. GEOS
   3.3.2+ is also required for topology support. GEOS is available for
   download from http://trac.osgeo.org/geos/ and 3.4+ is
   backward-compatible with older versions so fairly safe to upgrade.

-  LibXML2, version 2.5.x or higher. LibXML2 is currently used in some
   imports functions (ST\_GeomFromGML and ST\_GeomFromKML). LibXML2 is
   available for download from http://xmlsoft.org/downloads.html.

-  JSON-C, version 0.9 or higher. JSON-C is currently used to import
   GeoJSON via the function ST\_GeomFromGeoJson. JSON-C is available for
   download from https://github.com/json-c/json-c/releases.

-  GDAL, version 1.8 or higher (1.9 or higher is strongly recommended
   since some things will not work well or behavior differently with
   lower versions). This is required for raster support and to be able
   to install with ``CREATE EXTENSION postgis`` so highly recommended
   for those running 9.1+.
   http://trac.osgeo.org/gdal/wiki/DownloadSource.

**Optional**

-  GDAL (pseudo optional) only if you don't want raster and don't care
   about installing with ``CREATE EXTENSION postgis`` can you leave it
   out. Keep in mind other extensions may have a requires postgis
   extension which will prevent you from installing them unless you
   install postgis as an extension. So it is highly recommended you
   compile with GDAL support.

-  GTK (requires GTK+2.0, 2.8+) to compile the shp2pgsql-gui shape file
   loader. http://www.gtk.org/ .

-  SFCGAL, version 0.2 (or higher) could be used to provide additional
   2D and 3D advanced analysis functions to PostGIS cf ?. And also allow
   to use SFCGAL rather than GEOS for some 2D functions provided by both
   backends (like ST\_Intersection or ST\_Area, for instance). A
   PostgreSQL configuration variable ``postgis.backend`` allow end user
   to control which backend he want to use if SFCGAL is installed (GEOS
   by default). Nota: SFCGAL 0.2 require at least CGAL 4.1.
   https://github.com/Oslandia/SFCGAL.

-  CUnit (``CUnit``). This is needed for regression testing.
   http://cunit.sourceforge.net/

-  Apache Ant (``ant``) is required for building any of the drivers
   under the ``java`` directory. Ant is available from
   http://ant.apache.org .

-  DocBook (``xsltproc``) is required for building the documentation.
   Docbook is available from http://www.docbook.org/ .

-  DBLatex (``dblatex``) is required for building the documentation in
   PDF format. DBLatex is available from http://dblatex.sourceforge.net/
   .

-  ImageMagick (``convert``) is required to generate the images used in
   the documentation. ImageMagick is available from
   http://www.imagemagick.org/ .

Getting the Source
--------------------

Retrieve the PostGIS source archive from the downloads website
`POSTGIS\_DOWNLOAD\_URL <&postgis_download_url;>`__

::

    wget POSTGIS_DOWNLOAD_URL
    tar -xvzf postgis-LAST_RELEASE_VERSION.tar.gz

This will create a directory called ``postgis-LAST_RELEASE_VERSION`` in
the current working directory.

Alternatively, checkout the source from the
`svn <http://subversion.apache.org/>`__ repository
http://svn.osgeo.org/postgis/trunk/ .

::

    svn checkout http://svn.osgeo.org/postgis/trunk/ postgis-LAST_RELEASE_VERSION

Change into the newly created ``postgis-LAST_RELEASE_VERSION`` directory
to continue the installation.



Compiling and Install from Source: Detailed
----------------------------------------------

    **Note**

    Many OS systems now include pre-built packages for
    PostgreSQL/PostGIS. In many cases compilation is only necessary if
    you want the most bleeding edge versions or you are a package
    maintainer.

    This section includes general compilation instructions, if you are
    compiling for Windows etc or another OS, you may find additional
    more detailed help at `PostGIS User contributed compile
    guides <http://trac.osgeo.org/postgis/wiki/UsersWikiInstall>`__ and
    `PostGIS Dev
    Wiki <http://trac.osgeo.org/postgis/wiki/DevWikiMain>`__.

    Pre-Built Packages for various OS are listed in `PostGIS Pre-built
    Packages <http://trac.osgeo.org/postgis/wiki/UsersWikiPackages>`__

    If you are a windows user, you can get stable builds via
    Stackbuilder or `PostGIS Windows download
    site <http://www.postgis.org/download/windows/>`__ We also have
    `very bleeding-edge windows experimental
    builds <http://www.postgis.org/download/windows/experimental.php>`__
    that are built usually once or twice a week or whenever anything
    exciting happens. You can use these to experiment with the in
    progress releases of PostGIS

The PostGIS module is an extension to the PostgreSQL backend server. As
such, PostGIS LAST\_RELEASE\_VERSION *requires* full PostgreSQL server
headers access in order to compile. It can be built against PostgreSQL
versions MIN\_POSTGRES\_VERSION or higher. Earlier versions of
PostgreSQL are *not* supported.

Refer to the PostgreSQL installation guides if you haven't already
installed PostgreSQL. http://www.postgresql.org .

    **Note**

    For GEOS functionality, when you install PostgresSQL you may need to
    explicitly link PostgreSQL against the standard C++ library:

    ::

        LDFLAGS=-lstdc++ ./configure [YOUR OPTIONS HERE]

    This is a workaround for bogus C++ exceptions interaction with older
    development tools. If you experience weird problems (backend
    unexpectedly closed or similar things) try this trick. This will
    require recompiling your PostgreSQL from scratch, of course.

The following steps outline the configuration and compilation of the
PostGIS source. They are written for Linux users and will not work on
Windows or Mac.

Configuration
~~~~~~~~~~~~~~~

As with most linux installations, the first step is to generate the
Makefile that will be used to build the source code. This is done by
running the shell script

``./configure``

With no additional parameters, this command will attempt to
automatically locate the required components and libraries needed to
build the PostGIS source code on your system. Although this is the most
common usage of ``./configure``, the script accepts several parameters
for those who have the required libraries and programs in non-standard
locations.

The following list shows only the most commonly used parameters. For a
complete list, use the ``--help`` or ``--help=short`` parameters.

``--prefix=PREFIX``
    This is the location the PostGIS libraries and SQL scripts will be
    installed to. By default, this location is the same as the detected
    PostgreSQL installation.

        **Caution**

        This parameter is currently broken, as the package will only
        install into the PostgreSQL installation directory. Visit
        http://trac.osgeo.org/postgis/ticket/635 to track this bug.

``--with-pgconfig=FILE``
    PostgreSQL provides a utility called ``pg_config`` to enable
    extensions like PostGIS to locate the PostgreSQL installation
    directory. Use this parameter
    (``--with-pgconfig=/path/to/pg_config``) to manually specify a
    particular PostgreSQL installation that PostGIS will build against.

``--with-gdalconfig=FILE``
    GDAL, a required library, provides functionality needed for raster
    support ``gdal-config`` to enable software installations to locate
    the GDAL installation directory. Use this parameter
    (``--with-gdalconfig=/path/to/gdal-config``) to manually specify a
    particular GDAL installation that PostGIS will build against.

``--with-geosconfig=FILE``
    GEOS, a required geometry library, provides a utility called
    ``geos-config`` to enable software installations to locate the GEOS
    installation directory. Use this parameter
    (``--with-geosconfig=/path/to/geos-config``) to manually specify a
    particular GEOS installation that PostGIS will build against.

``--with-xml2config=FILE``
    LibXML is the library required for doing GeomFromKML/GML processes.
    It normally is found if you have libxml installed, but if not or you
    want a specific version used, you'll need to point PostGIS at a
    specific ``xml2-config`` confi file to enable software installations
    to locate the LibXML installation directory. Use this parameter
    (``>--with-xml2config=/path/to/xml2-config``) to manually specify a
    particular LibXML installation that PostGIS will build against.

``--with-projdir=DIR``
    Proj4 is a reprojection library required by PostGIS. Use this
    parameter (``--with-projdir=/path/to/projdir``) to manually specify
    a particular Proj4 installation directory that PostGIS will build
    against.

``--with-libiconv=DIR``
    Directory where iconv is installed.

``--with-jsondir=DIR``
    `JSON-C <http://oss.metaparadigm.com/json-c/>`__ is an MIT-licensed
    JSON library required by PostGIS ST\_GeomFromJSON support. Use this
    parameter (``--with-jsondir=/path/to/jsondir``) to manually specify
    a particular JSON-C installation directory that PostGIS will build
    against.

``--with-gui``
    Compile the data import GUI (requires GTK+2.0). This will create
    shp2pgsql-gui graphical interface to shp2pgsql.

``--with-raster``
    Compile with raster support. This will build
    rtpostgis-LAST\_RELEASE\_VERSION library and rtpostgis.sql file.
    This may not be required in final release as plan is to build in
    raster support by default.

``--with-topology``
    Compile with topology support. This will build the topology.sql
    file. There is no corresponding library as all logic needed for
    topology is in postgis-LAST\_RELEASE\_VERSION library.

``--with-gettext=no``
    By default PostGIS will try to detect gettext support and compile
    with it, however if you run into incompatibility issues that cause
    breakage of loader, you can disable it entirely with this command.
    Refer to ticket http://trac.osgeo.org/postgis/ticket/748 for an
    example issue solved by configuring with this. NOTE: that you aren't
    missing much by turning this off. This is used for international
    help/label support for the GUI loader which is not yet documented
    and still experimental.

    **Note**

    If you obtained PostGIS from the SVN
    `repository <http://svn.osgeo.org/postgis/trunk/>`__ , the first
    step is really to run the script

    ``./autogen.sh``

    This script will generate the ``configure`` script that in turn is
    used to customize the installation of PostGIS.

    If you instead obtained PostGIS as a tarball, running
    ``./autogen.sh`` is not necessary as ``configure`` has already been
    generated.

Building
~~~~~~~~~~

Once the Makefile has been generated, building PostGIS is as simple as
running

``make``

The last line of the output should be "``PostGIS was built
        successfully. Ready to install.``\ "

As of PostGIS v1.4.0, all the functions have comments generated from the
documentation. If you wish to install these comments into your spatial
databases later, run the command which requires docbook. The
postgis\_comments.sql and other package comments files
raster\_comments.sql, topology\_comments.sql are also packaged in the
tar.gz distribution in the doc folder so no need to make comments if
installing from the tar ball.

``make comments``

Introduced in PostGIS 2.0. This generates html cheat sheets suitable for
quick reference or for student handouts. This requires xsltproc to build
and will generate 4 files in doc folder ``topology_cheatsheet.html``,
``tiger_geocoder_cheatsheet.html``, ``raster_cheatsheet.html``,
``postgis_cheatsheet.html``

You can download some pre-built ones available in html and pdf from
`PostGIS / PostgreSQL Study
Guides <http://www.postgis.us/study_guides>`__

``make cheatsheets``


Building PostGIS Extensions and Deploying them
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The PostGIS extensions are built and installed automatically if you are
using PostgreSQL 9.1+.

If you are building from source repository, you need to build the
function descriptions first. These get built if you have docbook
installed. You can also manually build with the statement:

``make comments``

Building the comments is not necessary if you are building from a
release tar ball since these are packaged pre-built with the tar ball
already.

If you are building against PostgreSQL 9.1, the extensions should
automatically build as part of the make install process. You can if
needed build from the extensions folders or copy files if you need them
on a different server.

::

    cd extensions
    cd postgis
    make clean
    make
    make install
    cd ..
    cd postgis_topology
    make clean
    make
    make install


The extension files will always be the same for the same version of
PostGIS regardless of OS, so it is fine to copy over the extension files
from one OS to another as long as you have the PostGIS binaries already
installed on your servers.

If you want to install the extensions manually on a separate server
different from your development, You need to copy the following files
from the extensions folder into the ``PostgreSQL / share / extension``
folder of your PostgreSQL install as well as the needed binaries for
regular PostGIS if you don't have them already on the server.

-  These are the control files that denote information such as the
   version of the extension to install if not specified.
   ``postgis.control, postgis_topology.control``.

-  All the files in the /sql folder of each extension. Note that these
   need to be copied to the root of the PostgreSQL share/extension
   folder ``extensions/postgis/sql/*.sql``,
   ``extensions/postgis_topology/sql/*.sql``

Once you do that, you should see ``postgis``, ``postgis_topology`` as
available extensions in PgAdmin -> extensions.

If you are using psql, you can verify that the extensions are installed
by running this query:

::

    SELECT name, default_version,installed_version
    FROM pg_available_extensions WHERE name LIKE 'postgis%' ;
          name       | default_version | installed_version
    -----------------+-----------------+-------------------
    postgis          | LAST_RELEASE_VERSION     | LAST_RELEASE_VERSION
    postgis_topology | LAST_RELEASE_VERSION      |

If you have the extension installed in the database you are querying,
you'll see mention in the ``installed_version`` column. If you get no
records back, it means you don't have postgis extensions installed on
the server at all. PgAdmin III 1.14+ will also provide this information
in the ``extensions`` section of the database browser tree and will even
allow upgrade or uninstall by right-clicking.

If you have the extensions available, you can install postgis extension
in your database of choice by either using pgAdmin extension interface
or running these sql commands:

::

    CREATE EXTENSION postgis;
    CREATE EXTENSION postgis_topology;
    CREATE EXTENSION postgis_tiger_geocoder;

In psql you can use to see what versions you have installed and also
what schema they are installed.

::

    \connect mygisdb
    \x
    \dx postgis*

::

    List of installed extensions
    -[ RECORD 1 ]-------------------------------------------------
    -
    Name        | postgis
    Version     | LAST_RELEASE_VERSION
    Schema      | public
    Description | PostGIS geometry, geography, and raster spat..
    -[ RECORD 2 ]-------------------------------------------------
    -
    Name        | postgis_tiger_geocoder
    Version     | LAST_RELEASE_VERSION
    Schema      | tiger
    Description | PostGIS tiger geocoder and reverse geocoder
    -[ RECORD 3 ]-------------------------------------------------
    -
    Name        | postgis_topology
    Version     | LAST_RELEASE_VERSION
    Schema      | topology
    Description | PostGIS topology spatial types and functions

    **Warning**

    Extension tables ``spatial_ref_sys``, ``layer``, ``topology`` can
    not be explicitly backed up. They can only be backed up when the
    respective ``postgis`` or ``postgis_topology`` extension is backed
    up, which only seems to happen when you backup the whole database.
    As of PostGIS 2.0.1, only srid records not packaged with PostGIS are
    backed up when the database is backed up so don't go around changing
    srids we package and expect your changes to be there. Put in a
    ticket if you find an issue. The structures of extension tables are
    never backed up since they are created with ``CREATE EXTENSION`` and
    assumed to be the same for a given version of an extension. These
    behaviors are built into the current PostgreSQL extension model, so
    nothing we can do about it.

If you installed LAST\_RELEASE\_VERSION, without using our wonderful
extension system, you can change it to be extension based by first
upgrading to the latest micro version running the upgrade scripts:
``postgis_upgrade_21_minor.sql``,\ ``raster_upgrade_21_minor.sql``,\ ``topology_upgrade_21_minor.sql``.

If you installed postgis without raster support, you'll need to install
raster support first (using the full ``rtpostgis.sql``

Then you can run the below commands to package the functions in their
respective extension.

::

    CREATE EXTENSION postgis FROM unpackaged;
    CREATE EXTENSION postgis_topology FROM unpackaged;
    CREATE EXTENSION postgis_tiger_geocoder FROM unpackaged;

Testing
~~~~~~~~~

If you wish to test the PostGIS build, run

``make check``

The above command will run through various checks and regression tests
using the generated library against an actual PostgreSQL database.

    **Note**

    If you configured PostGIS using non-standard PostgreSQL, GEOS, or
    Proj4 locations, you may need to add their library locations to the
    LD\_LIBRARY\_PATH environment variable.

    **Caution**

    Currently, the ``make check`` relies on the ``PATH`` and ``PGPORT``
    environment variables when performing the checks - it does *not* use
    the PostgreSQL version that may have been specified using the
    configuration parameter ``--with-pgconfig``. So make sure to modify
    your PATH to match the detected PostgreSQL installation during
    configuration or be prepared to deal with the impending headaches.

If successful, the output of the test should be similar to the
following:

::

         CUnit - A Unit testing framework for C - Version 2.1-0
         http://cunit.sourceforge.net/


    Suite: print_suite
      Test: test_lwprint_default_format ... passed
      Test: test_lwprint_format_orders ... passed
      Test: test_lwprint_optional_format ... passed
      Test: test_lwprint_oddball_formats ... passed
      Test: test_lwprint_bad_formats ... passed
    Suite: misc
      Test: test_misc_force_2d ... passed
      Test: test_misc_simplify ... passed
      Test: test_misc_count_vertices ... passed
      Test: test_misc_area ... passed
      Test: test_misc_wkb ... passed
    Suite: ptarray
      Test: test_ptarray_append_point ... passed
      Test: test_ptarray_append_ptarray ... passed
      Test: test_ptarray_locate_point ... passed
      Test: test_ptarray_isccw ... passed
      Test: test_ptarray_signed_area ... passed
      Test: test_ptarray_desegmentize ... passed
      Test: test_ptarray_insert_point ... passed
      Test: test_ptarray_contains_point ... passed
      Test: test_ptarrayarc_contains_point ... passed
    Suite: PostGIS Computational Geometry Suite
      Test: test_lw_segment_side ... passed
      Test: test_lw_segment_intersects ... passed
      Test: test_lwline_crossing_short_lines ... passed
      Test: test_lwline_crossing_long_lines ... passed
      Test: test_lwline_crossing_bugs ... passed
      Test: test_lwpoint_set_ordinate ... passed
      Test: test_lwpoint_get_ordinate ... passed
      Test: test_point_interpolate ... passed
      Test: test_lwline_clip ... passed
      Test: test_lwline_clip_big ... passed
      Test: test_lwmline_clip ... passed
      Test: test_geohash_point ... passed
      Test: test_geohash_precision ... passed
      Test: test_geohash ... passed
      Test: test_geohash_point_as_int ... passed
      Test: test_isclosed ... passed
    Suite: buildarea
      Test: buildarea1 ... passed
      Test: buildarea2 ... passed
      Test: buildarea3 ... passed
      Test: buildarea4 ... passed
      Test: buildarea4b ... passed
      Test: buildarea5 ... passed
      Test: buildarea6 ... passed
      Test: buildarea7 ... passed
    Suite: clean
      Test: test_lwgeom_make_valid ... passed
    Suite: PostGIS Measures Suite
      Test: test_mindistance2d_tolerance ... passed
      Test: test_rect_tree_contains_point ... passed
      Test: test_rect_tree_intersects_tree ... passed
      Test: test_lwgeom_segmentize2d ... passed
      Test: test_lwgeom_locate_along ... passed
      Test: test_lw_dist2d_pt_arc ... passed
      Test: test_lw_dist2d_seg_arc ... passed
      Test: test_lw_dist2d_arc_arc ... passed
      Test: test_lw_arc_length ... passed
      Test: test_lw_dist2d_pt_ptarrayarc ... passed
      Test: test_lw_dist2d_ptarray_ptarrayarc ... passed
    Suite: node
      Test: test_lwgeom_node ... passed
    Suite: WKT Out Suite
      Test: test_wkt_out_point ... passed
      Test: test_wkt_out_linestring ... passed
      Test: test_wkt_out_polygon ... passed
      Test: test_wkt_out_multipoint ... passed
      Test: test_wkt_out_multilinestring ... passed
      Test: test_wkt_out_multipolygon ... passed
      Test: test_wkt_out_collection ... passed
      Test: test_wkt_out_circularstring ... passed
      Test: test_wkt_out_compoundcurve ... passed
      Test: test_wkt_out_curvpolygon ... passed
      Test: test_wkt_out_multicurve ... passed
      Test: test_wkt_out_multisurface ... passed
    Suite: WKT In Suite
      Test: test_wkt_in_point ... passed
      Test: test_wkt_in_linestring ... passed
      Test: test_wkt_in_polygon ... passed
      Test: test_wkt_in_multipoint ... passed
      Test: test_wkt_in_multilinestring ... passed
      Test: test_wkt_in_multipolygon ... passed
      Test: test_wkt_in_collection ... passed
      Test: test_wkt_in_circularstring ... passed
      Test: test_wkt_in_compoundcurve ... passed
      Test: test_wkt_in_curvpolygon ... passed
      Test: test_wkt_in_multicurve ... passed
      Test: test_wkt_in_multisurface ... passed
      Test: test_wkt_in_tin ... passed
      Test: test_wkt_in_polyhedralsurface ... passed
      Test: test_wkt_in_errlocation ... passed
    Suite: WKB Out Suite
      Test: test_wkb_out_point ... passed
      Test: test_wkb_out_linestring ... passed
      Test: test_wkb_out_polygon ... passed
      Test: test_wkb_out_multipoint ... passed
      Test: test_wkb_out_multilinestring ... passed
      Test: test_wkb_out_multipolygon ... passed
      Test: test_wkb_out_collection ... passed
      Test: test_wkb_out_circularstring ... passed
      Test: test_wkb_out_compoundcurve ... passed
      Test: test_wkb_out_curvpolygon ... passed
      Test: test_wkb_out_multicurve ... passed
      Test: test_wkb_out_multisurface ... passed
      Test: test_wkb_out_polyhedralsurface ... passed
    :
    Suite: Geodetic Suite
      Test: test_sphere_direction ... passed
      Test: test_sphere_project ... passed
      Test: test_lwgeom_area_sphere ... passed
      Test: test_signum ... passed
      Test: test_gbox_from_spherical_coordinates ... passed
    :
      Test: test_geos_noop ... passed
    Suite: Internal Spatial Trees
      Test: test_tree_circ_create ... passed
      Test: test_tree_circ_pip ... passed
      Test: test_tree_circ_pip2 ... passed
      Test: test_tree_circ_distance ... passed
    Suite: triangulate
      Test: test_lwgeom_delaunay_triangulation ... passed
    Suite: stringbuffer
      Test: test_stringbuffer_append ... passed
      Test: test_stringbuffer_aprintf ... passed
    Suite: surface
      Test: triangle_parse ... passed
      Test: tin_parse ... passed
      Test: polyhedralsurface_parse ... passed
      Test: surface_dimension ... passed
    Suite: homogenize
      Test: test_coll_point ... passed
      Test: test_coll_line ... passed
      Test: test_coll_poly ... passed
      Test: test_coll_coll ... passed
      Test: test_geom ... passed
      Test: test_coll_curve ... passed
    Suite: force_sfs
      Test: test_sfs_11 ... passed
      Test: test_sfs_12 ... passed
      Test: test_sqlmm ... passed
    Suite: out_gml
      Test: out_gml_test_precision ... passed
      Test: out_gml_test_srid ... passed
      Test: out_gml_test_dims ... passed
      Test: out_gml_test_geodetic ... passed
      Test: out_gml_test_geoms ... passed
      Test: out_gml_test_geoms_prefix ... passed
      Test: out_gml_test_geoms_nodims ... passed
      Test: out_gml2_extent ... passed
      Test: out_gml3_extent ... passed
    Suite: KML Out Suite
      Test: out_kml_test_precision ... passed
      Test: out_kml_test_dims ... passed
      Test: out_kml_test_geoms ... passed
      Test: out_kml_test_prefix ... passed
    Suite: GeoJson Out Suite
      Test: out_geojson_test_precision ... passed
      Test: out_geojson_test_dims ... passed
      Test: out_geojson_test_srid ... passed
      Test: out_geojson_test_bbox ... passed
      Test: out_geojson_test_geoms ... passed
    Suite: SVG Out Suite
      Test: out_svg_test_precision ... passed
      Test: out_svg_test_dims ... passed
      Test: out_svg_test_relative ... passed
      Test: out_svg_test_geoms ... passed
      Test: out_svg_test_srid ... passed
    Suite: X3D Out Suite
      Test: out_x3d3_test_precision ... passed
      Test: out_x3d3_test_geoms ... passed

    --Run Summary: Type      Total     Ran  Passed  Failed
                   suites       27      27     n/a       0
                   tests       198     198     198       0
                   asserts    1728    1728    1728       0

    Creating database 'postgis_reg'
    Loading PostGIS into 'postgis_reg'
    PostgreSQL 9.3beta1 on x86_64-unknown-linux-gnu, compiled by gcc (Debian 4.4.5-8) 4.4.5, 64-bit
      Postgis 2.1.0SVN - r11415 - 2013-05-11 02:48:21
      GEOS: 3.4.0dev-CAPI-1.8.0 r3797
      PROJ: Rel. 4.7.1, 23 September 2009

    Running tests

     loader/Point .............. ok
     loader/PointM .............. ok
     loader/PointZ .............. ok
     loader/MultiPoint .............. ok
     loader/MultiPointM .............. ok
     loader/MultiPointZ .............. ok
     loader/Arc .............. ok
     loader/ArcM .............. ok
     loader/ArcZ .............. ok
     loader/Polygon .............. ok
     loader/PolygonM .............. ok
     loader/PolygonZ .............. ok
     loader/TSTPolygon ......... ok
     loader/TSIPolygon ......... ok
     loader/TSTIPolygon ......... ok
     loader/PointWithSchema ..... ok
     loader/NoTransPoint ......... ok
     loader/NotReallyMultiPoint ......... ok
     loader/MultiToSinglePoint ......... ok
     loader/ReprojectPts ........ ok
     loader/ReprojectPtsGeog ........ ok
     loader/Latin1 .... ok
     binary .. ok
     regress .. ok
     regress_index .. ok
     regress_index_nulls .. ok
     regress_selectivity .. ok
     lwgeom_regress .. ok
     regress_lrs .. ok
     removepoint .. ok
     setpoint .. ok
     simplify .. ok
     snaptogrid .. ok
     summary .. ok
     affine .. ok
     empty .. ok
     measures .. ok
     legacy .. ok
     long_xact .. ok
     ctors .. ok
     sql-mm-serialize .. ok
     sql-mm-circularstring .. ok
     sql-mm-compoundcurve .. ok
     sql-mm-curvepoly .. ok
     sql-mm-general .. ok
     sql-mm-multicurve .. ok
     sql-mm-multisurface .. ok
     polyhedralsurface .. ok
     polygonize .. ok
     postgis_type_name .. ok
     geography .. ok
     out_geometry .. ok
     out_geography .. ok
     in_geohash .. ok
     in_gml .. ok
     in_kml .. ok
     iscollection .. ok
     regress_ogc .. ok
     regress_ogc_cover .. ok
     regress_ogc_prep .. ok
     regress_bdpoly .. ok
     regress_proj .. ok
     regress_management .. ok
     dump .. ok
     dumppoints .. ok
     boundary .. ok
     wmsservers .. ok
     wkt .. ok
     wkb .. ok
     tickets .. ok
     typmod .. ok
     remove_repeated_points .. ok
     split .. ok
     relate .. ok
     bestsrid .. ok
     concave_hull .. ok
     hausdorff .. ok
     regress_buffer_params .. ok
     offsetcurve .. ok
     relatematch .. ok
     isvaliddetail .. ok
     sharedpaths .. ok
     snap .. ok
     node .. ok
     unaryunion .. ok
     clean .. ok
     relate_bnr .. ok
     delaunaytriangles .. ok
     in_geojson .. ok
     uninstall .. ok (4112)

    Run tests: 90

Installation
~~~~~~~~~~~~~

To install PostGIS, type

``make install``

This will copy the PostGIS installation files into their appropriate
subdirectory specified by the ``--prefix`` configuration parameter. In
particular:

-  The loader and dumper binaries are installed in ``[prefix]/bin``.

-  The SQL files, such as ``postgis.sql``, are installed in
   ``[prefix]/share/contrib``.

-  The PostGIS libraries are installed in ``[prefix]/lib``.

If you previously ran the ``make comments`` command to generate the
``postgis_comments.sql``, ``raster_comments.sql`` file, install the sql
file by running

``make comments-install``

    **Note**

    ``postgis_comments.sql``, ``raster_comments.sql``,
    ``topology_comments.sql`` was separated from the typical build and
    installation targets since with it comes the extra dependency of
    ``xsltproc``.



Create a spatially-enabled database on PostgreSQL lower than 9.1
-------------------------------------------------------------------

The first step in creating a PostGIS database is to create a simple
PostgreSQL database.

``createdb [yourdatabase]``

Many of the PostGIS functions are written in the PL/pgSQL procedural
language. As such, the next step to create a PostGIS database is to
enable the PL/pgSQL language in your new database. This is accomplish by
the command below command. For PostgreSQL 8.4+, this is generally
already installed

``createlang plpgsql [yourdatabase]``

Now load the PostGIS object and function definitions into your database
by loading the ``postgis.sql`` definitions file (located in
``[prefix]/share/contrib`` as specified during the configuration step).

``psql -d [yourdatabase] -f postgis.sql``

For a complete set of EPSG coordinate system definition identifiers, you
can also load the ``spatial_ref_sys.sql`` definitions file and populate
the ``spatial_ref_sys`` table. This will permit you to perform
ST\_Transform() operations on geometries.

``psql -d [yourdatabase] -f spatial_ref_sys.sql``

If you wish to add comments to the PostGIS functions, the final step is
to load the ``postgis_comments.sql`` into your spatial database. The
comments can be viewed by simply typing ``\dd

 [function_name]`` from a ``psql`` terminal window.

``psql -d [yourdatabase] -f postgis_comments.sql``

Install raster support

``psql -d [yourdatabase] -f rtpostgis.sql``

Install raster support comments. This will provide quick help info for
each raster function using psql or PgAdmin or any other PostgreSQL tool
that can show function comments

``psql -d [yourdatabase] -f raster_comments.sql``

Install topology support

``psql -d [yourdatabase] -f topology/topology.sql``

Install topology support comments. This will provide quick help info for
each topology function / type using psql or PgAdmin or any other
PostgreSQL tool that can show function comments

``psql -d [yourdatabase] -f topology/topology_comments.sql``

If you plan to restore an old backup from prior versions in this new db,
run:

``psql -d [yourdatabase] -f legacy.sql``

    **Note**

    There is an alternative ``legacy_minimal.sql`` you can run instead
    which will install barebones needed to recover tables and work with
    apps like MapServer and GeoServer. If you have views that use things
    like distance / length etc, you'll need the full blown
    ``legacy.sql``

You can later run ``uninstall_legacy.sql`` to get rid of the deprecated
functions after you are done with restoring and cleanup.



Creating a spatial database using EXTENSIONS
------------------------------------------------

If you are using PostgreSQL 9.1+ and have compiled and installed the
extensions/ postgis modules, you can create a spatial database the new
way.

``createdb [yourdatabase]``

The core postgis extension installs PostGIS geometry, geography, raster,
spatial\_ref\_sys and all the functions and comments with a simple:

::

    CREATE EXTENSION postgis;

command.

``psql -d [yourdatabase] -c "CREATE EXTENSION postgis;"``

Topology is packaged as a separate extension and installable with
command:

``psql -d [yourdatabase] -c "CREATE EXTENSION postgis_topology;"``

If you plan to restore an old backup from prior versions in this new db,
run:

``psql -d [yourdatabase] -f legacy.sql``

You can later run ``uninstall_legacy.sql`` to get rid of the deprecated
functions after you are done with restoring and cleanup.



Installing, Upgrading Tiger Geocoder and loading data
------------------------------------------------------

Extras like Tiger geocoder may not be packaged in your PostGIS
distribution, but will always be available in the
postgis-LAST\_RELEASE\_VERSION.tar.gz file. The instructions provided
here are also available in the
``extras/tiger_geocoder/tiger_2011/README``

If you are on Windows and you don't have tar installed, you can use
http://www.7-zip.org/ to unzip the PostGIS tarball.

Tiger Geocoder Enabling your PostGIS database: Using Extension
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you are using PostgreSQL 9.1+ and PostGIS 2.1.0+, you can take
advantage of the new extension model for installing tiger geocoder. To
do so:

1. First get binaries for PostGIS 2.1.0 or compile and install as usual.
   This should install the necessary extension files as well for tiger
   geocoder.

2. Connect to your database via psql or pgAdmin or some other tool and
   run the following SQL commands. Note that if you are installing in a
   database that already has postgis, you don't need to do the first
   step. If you have ``fuzzystrmatch`` extension already installed, you
   don't need to do the second step either.

   ::

       CREATE EXTENSION postgis;
       CREATE EXTENSION fuzzystrmatch;
       CREATE EXTENSION postgis_tiger_geocoder;

3. To confirm your install is working correctly, run this sql in your
   database:

   ::

       SELECT na.address, na.streetname,na.streettypeabbrev, na.zip
           FROM normalize_address('1 Devonshire Place, Boston, MA 02109') AS na;

   Which should output

   ::

        address | streetname | streettypeabbrev |  zip
       ---------+------------+------------------+-------
              1 | Devonshire | Pl               | 02109

4. Create a new record in ``tiger.loader_platform`` table with the paths
   of your executables and server.

   So for example to create a profile called debbie that follows ``sh``
   convention. You would do:

   ::

       INSERT INTO tiger.loader_platform(os, declare_sect, pgbin, wget, unzip_command, psql, path_sep,
                  loader, environ_set_command, county_process_command)
       SELECT 'debbie', declare_sect, pgbin, wget, unzip_command, psql, path_sep,
              loader, environ_set_command, county_process_command
         FROM tiger.loader_platform
         WHERE os = 'sh';

   And then edit the paths in the *declare\_sect* column to those that
   fit Debbie's pg, unzip,shp2pgsql, psql, etc path locations.

   If you don't edit this ``loader_platform`` table, it will just
   contain common case locations of items and you'll have to edit the
   generated script after the script is generated.

5. Then run the ? and ? SQL functions make sure to use the name of your
   custom profile. So for example to do the nation load using our new
   profile we would:

   ::

       SELECT Loader_Generate_Nation_Script('debbie');

Converting a Tiger Geocoder Regular Install to Extension Model
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you installed the tiger geocoder without using the extension model,
you can convert to the extension model as follows:

1. Follow instructions in ? for the non-extension model upgrade.

2. Connect to your database with psql or pgAdmin and run the following
   command:

   ::

       CREATE EXTENSION postgis_tiger_geocoder FROM unpackaged;

Using PAGC address standardizer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

One of the many complaints of folks is the address normalizer function ?
function that normalizes an address for prepping before geocoding. The
normalizer is far from perfect and trying to patch its imperfectness
takes a vast amount of resources. As such we have integrated with
another project that has a much better address standardizer engine. This
is currently a separate project, which is a subproject of PAGC. The
source code for this PostgreSQL standardizer extension can be downloaded
from `PAGC PostgreSQL Address
Standardizer <http://sourceforge.net/p/pagc/code/360/tree/branches/sew-refactor/postgresql>`__.
To use this new normalizer, you compile the pagc extension and install
as an extension in your database.

The PAGC project and standardizer portion in particular, relies on PCRE
which is usually already installed on most Nix systems, but you can
download the latest at: http://www.pcre.org. It also requires Perl with
the ``Regexp::Assemble`` installed

For Windows users, the PostGIS 2.1+ bundle will come packaged with the
address\_standardizer already so no need to compile and can move
straight to ``CREATE EXTENSION`` step.

Installing Regex::Assemble

::

    cpan Regexp::Assemble

or if you are on Ubuntu / Debian you might need to do

::

    sudo perl -MCPAN -e "install Regexp::Assemble"

Compiling

::

    svn co svn://svn.code.sf.net/p/pagc/code/branches/sew-refactor/postgresql address_standardizer
    cd address_standardizer
    make
    #if you have in non-standard location pcre try
    # make SHLIB_LINK="-L/path/pcre/lib -lpostgres -lpgport -lpcre" CPPFLAGS="-I.  -I/path/pcre/include"
    make install

Once you have installed, you can connect to your database and run the
SQL:

::

    CREATE EXTENSION address_standardizer;

Once you install this extension in the same database as you have
installed ``postgis_tiger_geocoder``, then the ? can be used instead of
?. The other nice thing about this extension is that its tiger agnostic,
so can be used with other data sources such as international addresses.

Tiger Geocoder Enabling your PostGIS database: Not Using Extensions
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

First install PostGIS using the prior instructions.

If you don't have an extras folder, download
`POSTGIS\_DOWNLOAD\_URL <&postgis_download_url;>`__

``tar xvfz postgis-LAST_RELEASE_VERSION.tar.gz``

``cd postgis-LAST_RELEASE_VERSION/extras/tiger_geocoder/tiger_2011``

Edit the ``tiger_loader_2012.sql`` to the paths of your executables
server etc or alternatively you can update the ``loader_platform`` table
once installed. If you don't edit this file or the ``loader_platform``
table, it will just contain common case locations of items and you'll
have to edit the generated script after the fact when you run the ? and
? SQL functions.

If you are installing Tiger geocoder for the first time edit either the
``create_geocode.bat`` script If you are on windows or the
``create_geocode.sh`` if you are on Linux/Unix/Mac OSX with your
PostgreSQL specific settings and run the corresponding script from the
commandline.

Verify that you now have a ``tiger`` schema in your database and that it
is part of your database search\_path. If it is not, add it with a
command something along the line of:

::

    ALTER DATABASE geocoder SET search_path=public, tiger;

The normalizing address functionality works more or less without any
data except for tricky addresses. Run this test and verify things look
like this:

::

    SELECT pprint_addy(normalize_address('202 East Fremont Street, Las Vegas, Nevada 89101')) As pretty_address;
    pretty_address
    ---------------------------------------
    202 E Fremont St, Las Vegas, NV 89101


Loading Tiger Data
~~~~~~~~~~~~~~~~~~~

The instructions for loading data are available in a more detailed form
in the ``extras/tiger_geocoder/tiger_2011/README``. This just includes
the general steps.

The load process downloads data from the census website for the
respective nation files, states requested, extracts the files, and then
loads each state into its own separate set of state tables. Each state
table inherits from the tables defined in ``tiger`` schema so that its
sufficient to just query those tables to access all the data and drop a
set of state tables at any time using the ? if you need to reload a
state or just don't need a state anymore.

In order to be able to load data you'll need the following tools:

-  A tool to unzip the zip files from census website.

   For Unix like systems: ``unzip`` executable which is usually already
   installed on most Unix like platforms.

   For Windows, 7-zip which is a free compress/uncompress tool you can
   download from http://www.7-zip.org/

-  ``shp2pgsql`` commandline which is installed by default when you
   install PostGIS.

-  ``wget`` which is a web grabber tool usually installed on most
   Unix/Linux systems.

   If you are on windows, you can get pre-compiled binaries from
   http://gnuwin32.sourceforge.net/packages/wget.htm

If you are upgrading from tiger\_2010, you'll need to first generate and
run ?. Before you load any state data, you need to load the nation wide
data which you do with ?. Which will generate a loader script for you. ?
is a one-time step that should be done for upgrading (from 2010) and for
new installs.

To load state data refer to ? to generate a data load script for your
platform for the states you desire. Note that you can install these
piecemeal. You don't have to load all the states you want all at once.
You can load them as you need them.

After the states you desire have been loaded, make sure to run the:

::

    SELECT install_missing_indexes();

as described in ?.

To test that things are working as they should, try to run a geocode on
an address in your state using ?

Upgrading your Tiger Geocoder Install
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you have Tiger Geocoder packaged with 2.0+ already installed, you can
upgrade the functions at any time even from an interim tar ball if there
are fixes you badly need. This will only work for Tiger geocoder not
installed with extensions.

If you don't have an extras folder, download
`POSTGIS\_DOWNLOAD\_URL <&postgis_download_url;>`__

``tar xvfz postgis-LAST_RELEASE_VERSION.tar.gz``

``cd postgis-LAST_RELEASE_VERSION/extras/tiger_geocoder/tiger_2011``

Locate the ``upgrade_geocoder.bat`` script If you are on windows or the
``upgrade_geocoder.sh`` if you are on Linux/Unix/Mac OSX. Edit the file
to have your postgis database credentials.

If you are upgrading from 2010 or 2011, make sure to unremark out the
loader script line so you get the latest script for loading 2012 data.

Then run th corresponding script from the commandline.

Next drop all nation tables and load up the new ones. Generate a drop
script with this SQL statement as detailed in ?

::

    SELECT drop_nation_tables_generate_script();

Run the generated drop SQL statements.

Generate a nation load script with this SELECT statement as detailed in
?

**For windows**

::

    SELECT loader_generate_nation_script('windows');

**For unix/linux**

::

    SELECT loader_generate_nation_script('sh');

Refer to ? for instructions on how to run the generate script. This only
needs to be done once.

    **Note**

    You can have a mix of 2010/2011 state tables and can upgrade each
    state separately. Before you upgrade a state to 2011, you first need
    to drop the 2010 tables for that state using ?.

Create a spatially-enabled database from a template
------------------------------------------------------

Some packaged distributions of PostGIS (in particular the Win32
installers for PostGIS >= 1.1.5) load the PostGIS functions into a
template database called ``template_postgis``. If the
``template_postgis`` database exists in your PostgreSQL installation
then it is possible for users and/or applications to create
spatially-enabled databases using a single command. Note that in both
cases, the database user must have been granted the privilege to create
new databases.

From the shell:

::

    # createdb -T template_postgis my_spatial_db

From SQL:

::

    postgres=# CREATE DATABASE my_spatial_db TEMPLATE=template_postgis

Upgrading
------------------

Upgrading existing spatial databases can be tricky as it requires
replacement or introduction of new PostGIS object definitions.

Unfortunately not all definitions can be easily replaced in a live
database, so sometimes your best bet is a dump/reload process.

PostGIS provides a SOFT UPGRADE procedure for minor or bugfix releases,
and a HARD UPGRADE procedure for major releases.

Before attempting to upgrade PostGIS, it is always worth to backup your
data. If you use the -Fc flag to pg\_dump you will always be able to
restore the dump with a HARD UPGRADE.

Soft upgrade
~~~~~~~~~~~~~~

If you installed your database using extensions, you'll need to upgrade
using the extension model as well. If you installed using the old sql
script way, then you should upgrade using the sql script way. Please
refer to the appropriate.

Soft Upgrade Pre 9.1+ or without extensions
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This section applies only to those who installed PostGIS not using
extensions. If you have extensions and try to upgrade with this approach
you'll get messages like:

::

    can't drop ... because postgis extension depends on it

After compiling you should find several ``postgis_upgrade*.sql`` files.
Install the one for your version of PostGIS. For example
``postgis_upgrade_20_to_21.sql`` should be used if you are upgrading
from PostGIS 2.0 to 2.1. If you are moving from PostGIS 1.\* to PostGIS
2.\* or from PostGIS 2.\* prior to r7409, you need to do a HARD UPGRADE.

::

    psql -f postgis_upgrade_21_minor.sql -d your_spatial_database

The same procedure applies to raster and topology extensions, with
upgrade files named ``rtpostgis_upgrade*.sql`` and
``topology_upgrade*.sql`` respectively. If you need them:

::

    psql -f rtpostgis_upgrade_21_minor.sql -d your_spatial_database

::

    psql -f topology_upgrade_21_minor.sql -d your_spatial_database

    **Note**

    If you can't find the ``postgis_upgrade*.sql`` specific for
    upgrading your version you are using a version too early for a soft
    upgrade and need to do a HARD UPGRADE.

The ? function should inform you about the need to run this kind of
upgrade using a "procs need upgrade" message.

Soft Upgrade 9.1+ using extensions
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you originally installed PostGIS with extensions, then you need to
upgrade using extensions as well. Doing a minor upgrade with extensions,
is fairly painless.

::

    ALTER EXTENSION postgis UPDATE TO "LAST_RELEASE_VERSION";
    ALTER EXTENSION postgis_topology UPDATE TO "LAST_RELEASE_VERSION";

If you get an error notice something like:

::

    No migration path defined for ... to LAST_RELEASE_VERSION

Then you'll need to backup your database, create a fresh one as
described in ? and then restore your backup ontop of this new database.

If you get a notice message like:

::

    Version "LAST_RELEASE_VERSION" of extension "postgis" is already installed

Then everything is already up to date and you can safely ignore it.
**UNLESS** you're attempting to upgrade from an SVN version to the next
(which doesn't get a new version number); in that case you can append
"next" to the version string, and next time you'll need to drop the
"next" suffix again:

::

    ALTER EXTENSION postgis UPDATE TO "LAST_RELEASE_VERSIONnext";
    ALTER EXTENSION postgis_topology UPDATE TO "LAST_RELEASE_VERSIONnext";

    **Note**

    If you installed PostGIS originally without a version specified, you
    can often skip the reinstallation of postgis extension before
    restoring since the backup just has ``CREATE EXTENSION postgis`` and
    thus picks up the newest latest version during restore.

Hard upgrade
~~~~~~~~~~~~~

By HARD UPGRADE we mean full dump/reload of postgis-enabled databases.
You need a HARD UPGRADE when PostGIS objects' internal storage changes
or when SOFT UPGRADE is not possible. The `Release
Notes <#release_notes>`__ appendix reports for each version whether you
need a dump/reload (HARD UPGRADE) to upgrade.

The dump/reload process is assisted by the postgis\_restore.pl script
which takes care of skipping from the dump all definitions which belong
to PostGIS (including old ones), allowing you to restore your schemas
and data into a database with PostGIS installed without getting
duplicate symbol errors or bringing forward deprecated objects.

Supplementary instructions for windows users are available at `Windows
Hard
upgrade <http://trac.osgeo.org/postgis/wiki/UsersWikiWinUpgrade>`__.

The Procedure is as follows:

1. Create a "custom-format" dump of the database you want to upgrade
   (let's call it ``olddb``) include binary blobs (-b) and verbose (-v)
   output. The user can be the owner of the db, need not be postgres
   super account.

   ::

       pg_dump -h localhost -p 5432 -U postgres -Fc -b -v -f "/somepath/olddb.backup" olddb

2. Do a fresh install of PostGIS in a new database -- we'll refer to
   this database as ``newdb``. Please refer to ? and ? for instructions
   on how to do this.

   The spatial\_ref\_sys entries found in your dump will be restored,
   but they will not override existing ones in spatial\_ref\_sys. This
   is to ensure that fixes in the official set will be properly
   propagated to restored databases. If for any reason you really want
   your own overrides of standard entries just don't load the
   spatial\_ref\_sys.sql file when creating the new db.

   If your database is really old or you know you've been using long
   deprecated functions in your views and functions, you might need to
   load ``legacy.sql`` for all your functions and views etc. to properly
   come back. Only do this if \_really\_ needed. Consider upgrading your
   views and functions before dumping instead, if possible. The
   deprecated functions can be later removed by loading
   ``uninstall_legacy.sql``.

3. Restore your backup into your fresh ``newdb`` database using
   postgis\_restore.pl. Unexpected errors, if any, will be printed to
   the standard error stream by psql. Keep a log of those.

   ::

       perl utils/postgis_restore.pl "/somepath/olddb.backup" | psql -h localhost -p 5432 -U postgres newdb 2> errors.txt

Errors may arise in the following cases:

1. Some of your views or functions make use of deprecated PostGIS
   objects. In order to fix this you may try loading ``legacy.sql``
   script prior to restore or you'll have to restore to a version of
   PostGIS which still contains those objects and try a migration again
   after porting your code. If the ``legacy.sql`` way works for you,
   don't forget to fix your code to stop using deprecated functions and
   drop them loading ``uninstall_legacy.sql``.

2. Some custom records of spatial\_ref\_sys in dump file have an invalid
   SRID value. Valid SRID values are bigger than 0 and smaller than
   999000. Values in the 999000.999999 range are reserved for internal
   use while values > 999999 can't be used at all. All your custom
   records with invalid SRIDs will be retained, with those > 999999
   moved into the reserved range, but the spatial\_ref\_sys table would
   loose a check constraint guarding for that invariant to hold and
   possibly also its primary key ( when multiple invalid SRIDS get
   converted to the same reserved SRID value ).

   In order to fix this you should copy your custom SRS to a SRID with a
   valid value (maybe in the 910000..910999 range), convert all your
   tables to the new srid (see ?), delete the invalid entry from
   spatial\_ref\_sys and re-construct the check(s) with:

   ::

       ALTER TABLE spatial_ref_sys ADD CONSTRAINT spatial_ref_sys_srid_check check (srid > 0 AND srid < 999000 );

   ::

       ALTER TABLE spatial_ref_sys ADD PRIMARY KEY(srid));

Common Problems during installation
------------------------------------

There are several things to check when your installation or upgrade
doesn't go as you expected.

1. Check that you have installed PostgreSQL MIN\_POSTGRES\_VERSION or
   newer, and that you are compiling against the same version of the
   PostgreSQL source as the version of PostgreSQL that is running.
   Mix-ups can occur when your (Linux) distribution has already
   installed PostgreSQL, or you have otherwise installed PostgreSQL
   before and forgotten about it. PostGIS will only work with PostgreSQL
   MIN\_POSTGRES\_VERSION or newer, and strange, unexpected error
   messages will result if you use an older version. To check the
   version of PostgreSQL which is running, connect to the database using
   psql and run this query:

   ::

       SELECT version();

   If you are running an RPM based distribution, you can check for the
   existence of pre-installed packages using the ``rpm`` command as
   follows: ``rpm -qa | grep postgresql``

2. If your upgrade fails, make sure you are restoring into a database
   that already has PostGIS installed.

   ::

       SELECT postgis_full_version();

Also check that configure has correctly detected the location and
version of PostgreSQL, the Proj4 library and the GEOS library.

1. The output from configure is used to generate the
   ``postgis_config.h`` file. Check that the ``POSTGIS_PGSQL_VERSION``,
   ``POSTGIS_PROJ_VERSION`` and ``POSTGIS_GEOS_VERSION`` variables have
   been set correctly.

JDBC
------

The JDBC extensions provide Java objects corresponding to the internal
PostGIS types. These objects can be used to write Java clients which
query the PostGIS database and draw or do calculations on the GIS data
in PostGIS.

1. Enter the ``java/jdbc`` sub-directory of the PostGIS distribution.

2. Run the ``ant`` command. Copy the ``postgis.jar`` file to wherever
   you keep your java libraries.

The JDBC extensions require a PostgreSQL JDBC driver to be present in
the current CLASSPATH during the build process. If the PostgreSQL JDBC
driver is located elsewhere, you may pass the location of the JDBC
driver JAR separately using the -D parameter like this:

::

    # ant -Dclasspath=/path/to/postgresql-jdbc.jar

PostgreSQL JDBC drivers can be downloaded from
http://jdbc.postgresql.org .

Loader/Dumper
------------------

The data loader and dumper are built and installed automatically as part
of the PostGIS build. To build and install them manually:

::

    # cd postgis-LAST_RELEASE_VERSION/loader
    # make
    # make install

The loader is called ``shp2pgsql`` and converts ESRI Shape files into
SQL suitable for loading in PostGIS/PostgreSQL. The dumper is called
``pgsql2shp`` and converts PostGIS tables (or queries) into ESRI Shape
files. For more verbose documentation, see the online help, and the
manual pages.

 .. toctree::

    template
