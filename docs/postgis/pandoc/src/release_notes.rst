Appendix
========

Release Notes
Release 2.1.2
=============

Release date: 2014/03/31

This is a bug fix release, addressing issues that have been filed since
the 2.1.1 release.

#2666, Error out at configure time if no SQL preprocessor can be found

#2534, st\_distance returning incorrect results for large geographies

#2539, Check for json-c/json.h presence/usability before json/json.h

#2543, invalid join selectivity error from simple query

#2546, GeoJSON with string coordinates parses incorrectly

#2547, Fix ST\_Simplify(TopoGeometry) for hierarchical topogeoms

#2552, Fix NULL raster handling in ST\_AsPNG, ST\_AsTIFF and ST\_AsJPEG

#2555, Fix parsing issue of range arguments of ST\_Reclass

#2556, geography ST\_Intersects results depending on insert order

#2580, Do not allow installing postgis twice in the same database

#2589, Remove use of unnecessary void pointers

#2607, Cannot open more than 1024 out-db files in one process

#2610, Ensure face splitting algorithm uses the edge index

#2615, EstimatedExtent (and hence, underlying stats) gathering wrong
bbox

#2619, Empty rings array in GeoJSON polygon causes crash

#2634, regression in sphere distance code

#2638, Geography distance on M geometries sometimes wrong

#2648, #2653, Fix topology functions when "topology" is not in
search\_path

#2654, Drop deprecated calls from topology

#2655, Let users without topology privileges call
postgis\_full\_version()

#2674, Fix missing operator = and hash\_raster\_ops opclass on raster

#2675, #2534, #2636, #2634, #2638, Geography distance issues with tree
optimization

#2494, avoid memcopy in GiST index (hayamiz)

#2560, soft upgrade: avoid drop/recreate of aggregates that hadn't
changed

Release 2.1.1
=============

Release date: 2013/11/06

This is a bug fix release, addressing issues that have been filed since
the 2.1.0 release.

#2514, Change raster license from GPL v3+ to v2+, allowing distribution
of PostGIS Extension as GPLv2.

#2396, Make regression tests more endian-agnostic

#2434, Fix ST\_Intersection(geog,geog) regression in rare cases

#2454, Fix behavior of ST\_PixelAsXXX functions regarding
exclude\_nodata\_value parameter

#2489, Fix upgrades from 2.0 leaving stale function signatures

#2525, Fix handling of SRID in nested collections

#2449, Fix potential infinite loop in index building

#2493, Fix behavior of ST\_DumpValues when passed an empty raster

#2502, Fix postgis\_topology\_scripts\_installed() install schema

#2504, Fix segfault on bogus pgsql2shp call

#2512, Support for foreign tables and materialized views in
raster\_columns and raster\_overviews

#2478, support for tiger 2013

#2463, support for exact length calculations on arc geometries

Release 2.1.0
=============

Release date: 2013/08/17

This is a minor release addressing both bug fixes and performance and
functionality enhancements addressing issues since 2.0.3 release. If you
are upgrading from 2.0+, only a soft upgrade is required. If you are
upgrading from 1.5 or earlier, a hard upgrade is required.

#1653, Removed srid parameter from ST\_Resample(raster) and variants
with reference raster no longer apply reference raster's SRID.

#1962 ST\_Segmentize - As a result of the introduction of geography
support, The construct:
``SELECT ST_Segmentize('LINESTRING(1 2, 3 4)',0.5);`` will result in
ambiguous function error

#2026, ST\_Union(raster) now unions all bands of all rasters

#2089, liblwgeom: lwgeom\_set\_handlers replaces
lwgeom\_init\_allocators.

#2150, regular\_blocking is no longer a constraint. column of same name
in raster\_columns now checks for existance of spatially\_unique and
coverage\_tile constraints

ST\_Intersects(raster, geometry) behaves in the same manner as
ST\_Intersects(geometry, raster).

point variant of ST\_SetValue(raster) previously did not check SRID of
input geometry and raster.

ST\_Hillshade parameters azimuth and altitude are now in degrees instead
of radians.

ST\_Slope and ST\_Aspect return pixel values in degrees instead of
radians.

#2104, ST\_World2RasterCoord, ST\_World2RasterCoordX and
ST\_World2RasterCoordY renamed to ST\_WorldToRasterCoord,
ST\_WorldToRasterCoordX and ST\_WorldToRasterCoordY.
ST\_Raster2WorldCoord, ST\_Raster2WorldCoordX and ST\_Raster2WorldCoordY
renamed to ST\_RasterToWorldCoord, ST\_RasterToWorldCoordX and
ST\_RasterToWorldCoordY

ST\_Estimated\_Extent renamed to ST\_EstimatedExtent

ST\_Line\_Interpolate\_Point renamed to ST\_LineInterpolatePoint

ST\_Line\_Substring renamed to ST\_LineSubstring

ST\_Line\_Locate\_Point renamed to ST\_LineLocatePoint

ST\_Force\_XXX renamed to ST\_ForceXXX

ST\_MapAlgebraFctNgb and 1 and 2 raster variants of ST\_MapAlgebraFct.
Use ST\_MapAlgebra instead

1 and 2 raster variants of ST\_MapAlgebraExpr. Use expression variants
of ST\_MapAlgebra instead

- Refer to
http://postgis.net/docs/manual-2.1/PostGIS\_Special\_Functions\_Index.html#NewFunctions\_2\_1
for complete list of new functions

#310, ST\_DumpPoints converted to a C function (Nathan Wagner) and much
faster

#739, UpdateRasterSRID()

#945, improved join selectivity, N-D selectivity calculations, user
accessible selectivity and stats reader functions for testing (Paul
Ramsey / OpenGeo)

toTopoGeom with TopoGeometry sink (Sandro Santilli / Vizzuality)

clearTopoGeom (Sandro Santilli / Vizzuality)

ST\_Segmentize(geography) (Paul Ramsey / OpenGeo)

ST\_DelaunayTriangles (Sandro Santilli / Vizzuality)

ST\_NearestValue, ST\_Neighborhood (Bborie Park / UC Davis)

ST\_PixelAsPoint, ST\_PixelAsPoints (Bborie Park / UC Davis)

ST\_PixelAsCentroid, ST\_PixelAsCentroids (Bborie Park / UC Davis)

ST\_Raster2WorldCoord, ST\_World2RasterCoord (Bborie Park / UC Davis)

Additional raster/raster spatial relationship functions (ST\_Contains,
ST\_ContainsProperly, ST\_Covers, ST\_CoveredBy, ST\_Disjoint,
ST\_Overlaps, ST\_Touches, ST\_Within, ST\_DWithin, ST\_DFullyWithin)
(Bborie Park / UC Davis)

Added array variants of ST\_SetValues() to set many pixel values of a
band in one call (Bborie Park / UC Davis)

#1293, ST\_Resize(raster) to resize rasters based upon width/height

#1627, package tiger\_geocoder as a PostgreSQL extension

#1643, #2076, Upgrade tiger geocoder to support loading tiger 2011 and
2012 (Regina Obe / Paragon Corporation) Funded by Hunter Systems Group

GEOMETRYCOLLECTION support for ST\_MakeValid (Sandro Santilli /
Vizzuality)

#1709, ST\_NotSameAlignmentReason(raster, raster)

#1818, ST\_GeomFromGeoHash and friends (Jason Smith (darkpanda))

#1856, reverse geocoder rating setting for prefer numbered highway name

ST\_PixelOfValue (Bborie Park / UC Davis)

Casts to/from PostgreSQL geotypes (point/path/polygon).

Added geomval array variant of ST\_SetValues() to set many pixel values
of a band using a set of geometries and corresponding values in one call
(Bborie Park / UC Davis)

ST\_Tile(raster) to break up a raster into tiles (Bborie Park / UC
Davis)

#1895, new r-tree node splitting algorithm (Alex Korotkov)

#2011, ST\_DumpValues to output raster as array (Bborie Park / UC Davis)

#2018, ST\_Distance support for CircularString, CurvePolygon,
MultiCurve, MultiSurface, CompoundCurve

#2030, n-raster (and n-band) ST\_MapAlgebra (Bborie Park / UC Davis)

#2193, Utilize PAGC parser as drop in replacement for tiger normalizer
(Steve Woodbridge, Regina Obe)

#2210, ST\_MinConvexHull(raster)

lwgeom\_from\_geojson in liblwgeom (Sandro Santilli / Vizzuality)

#1687, ST\_Simplify for TopoGeometry (Sandro Santilli / Vizzuality)

#2228, TopoJSON output for TopoGeometry (Sandro Santilli / Vizzuality)

#2123, ST\_FromGDALRaster

#613, ST\_SetGeoReference with numerical parameters instead of text

#2276, ST\_AddBand(raster) variant for out-db bands

#2280, ST\_Summary(raster)

#2163, ST\_TPI for raster (Nathaniel Clay)

#2164, ST\_TRI for raster (Nathaniel Clay)

#2302, ST\_Roughness for raster (Nathaniel Clay)

#2290, ST\_ColorMap(raster) to generate RGBA bands

#2254, Add SFCGAL backend support. (Backend selection throught
postgis.backend var) Functions available both throught GEOS or SFCGAL:
ST\_Intersects, ST\_3DIntersects, ST\_Intersection, ST\_Area,
ST\_Distance, ST\_3DDistance New functions available only with SFCGAL
backend: ST\_3DIntersection, ST\_Tesselate, ST\_3DArea, ST\_Extrude,
ST\_ForceLHR ST\_Orientation, ST\_Minkowski, ST\_StraightSkeleton
postgis\_sfcgal\_version New function available in PostGIS: ST\_ForceSFS
(Olivier Courtin and Hugo Mercier / Oslandia)

For detail of new functions and function improvements, please refer to
?.

Much faster raster ST\_Union, ST\_Clip and many more function additions
operations

For geometry/geography better planner selectivity and a lot more
functions.

#823, tiger geocoder: Make loader\_generate\_script download portion
less greedy

#826, raster2pgsql no longer defaults to padding tiles. Flag -P can be
used to pad tiles

#1363, ST\_AddBand(raster, ...) array version rewritten in C

#1364, ST\_Union(raster, ...) aggregate function rewritten in C

#1655, Additional default values for parameters of ST\_Slope

#1661, Add aggregate variant of ST\_SameAlignment

#1719, Add support for Point and GeometryCollection ST\_MakeValid inputs

#1780, support ST\_GeoHash for geography

#1796, Big performance boost for distance calculations in geography

#1802, improved function interruptibility.

#1823, add parameter in ST\_AsGML to use id column for GML 3 output
(become mandatory since GML 3.2.1)

#1856, tiger geocoder: reverse geocoder rating setting for prefer
numbered highway name

#1938, Refactor basic ST\_AddBand to add multiple new bands in one call

#1978, wrong answer when calculating length of a closed circular arc
(circle)

#1989, Preprocess input geometry to just intersection with raster to be
clipped

#2021, Added multi-band support to ST\_Union(raster, ...) aggregate
function

#2006, better support of ST\_Area(geography) over poles and dateline

#2065, ST\_Clip(raster, ...) now a C function

#2069, Added parameters to ST\_Tile(raster) to control padding of tiles

#2078, New variants of ST\_Slope, ST\_Aspect and ST\_HillShade to
provide solution to handling tiles in a coverage

#2097, Added RANGE uniontype option for ST\_Union(raster)

#2105, Added ST\_Transform(raster) variant for aligning output to
reference raster

#2119, Rasters passed to ST\_Resample(), ST\_Rescale(), ST\_Reskew(),
and ST\_SnapToGrid() no longer require an SRID

#2141, More verbose output when constraints fail to be added to a raster
column

#2143, Changed blocksize constraint of raster to allow multiple values

#2148, Addition of coverage\_tile constraint for raster

#2149, Addition of spatially\_unique constraint for raster

TopologySummary output now includes unregistered layers and a count of
missing TopoGeometry objects from their natural layer.

ST\_HillShade(), ST\_Aspect() and ST\_Slope() have one new optional
parameter to interpolate NODATA pixels before running the operation.

Point variant of ST\_SetValue(raster) is now a wrapper around geomval
variant of ST\_SetValues(rast).

Proper support for raster band's isnodata flag in core API and loader.

Additional default values for parameters of ST\_Aspect and ST\_HillShade

#2178, ST\_Summary now advertises presence of known srid with an [S]
flag

#2202, Make libjson-c optional (--without-json configure switch)

#2213, Add support libjson-c 0.10+

#2231, raster2pgsql supports user naming of filename column with -n

#2200, ST\_Union(raster, uniontype) unions all bands of all rasters

#2264, postgis\_restore.pl support for restoring into databases with
postgis in a custom schema

#2244, emit warning when changing raster's georeference if raster has
out-db bands

#2222, add parameter OutAsIn to flag whether ST\_AsBinary should return
out-db bands as in-db bands

#1839, handling of subdatasets in GeoTIFF in raster2pgsql.

#1840, fix logic of when to compute # of tiles in raster2pgsql.

#1870, align the docs and actual behavior of raster's ST\_Intersects

#1872, fix ST\_ApproxSummarystats to prevent division by zero

#1875, ST\_SummaryStats returns NULL for all parameters except count
when count is zero

#1932, fix raster2pgsql of syntax for index tablespaces

#1936, ST\_GeomFromGML on CurvePolygon causes server crash

#1939, remove custom data types: summarystats, histogram, quantile,
valuecount

#1951, remove crash on zero-length linestrings

#1957, ST\_Distance to a one-point LineString returns NULL

#1976, Geography point-in-ring code overhauled for more reliability

#1981, cleanup of unused variables causing warnings with gcc 4.6+

#1996, support POINT EMPTY in GeoJSON output

#2062, improve performance of distance calculations

#2057, Fixed linking issue for raster2psql to libpq

#2077, Fixed incorrect values returning from ST\_Hillshade()

#2019, ST\_FlipCoordinates does not update bbox

#2100, ST\_AsRaster may not return raster with specified pixel type

#2126, Better handling of empty rasters from ST\_ConvexHull()

#2165, ST\_NumPoints regression failure with CircularString

#2168, ST\_Distance is not always commutative

#2182, Fix issue with outdb rasters with no SRID and ST\_Resize

#2188, Fix function parameter value overflow that caused problems when
copying data from a GDAL dataset

#2198, Fix incorrect dimensions used when generating bands of out-db
rasters in ST\_Tile()

#2201, ST\_GeoHash wrong on boundaries

#2203, Changed how rasters with unknown SRID and default geotransform
are handled when passing to GDAL Warp API

#2215, Fixed raster exclusion constraint for conflicting name of
implicit index

#2251, Fix bad dimensions when rescaling rasters with default
geotransform matrix

#2133, Fix performance regression in expression variant of
ST\_MapAlgebra

#2257, GBOX variables not initialized when testing with empty geometries

#2271, Prevent parallel make of raster

#2282, Fix call to undefined function nd\_stats\_to\_grid() in debug
mode

#2307, ST\_MakeValid outputs invalid geometries

#2309, Remove confusing INFO message when trying to get SRS info

#2336, FIPS 20 (KS) causes wildcard expansion to wget all files

#2348, Provide raster upgrade path for 2.0 to 2.1

#2351, st\_distance between geographies wrong

#2359, Fix handling of schema name when adding overview constraints

#2371, Support GEOS versions with more than 1 digit in micro

#2383, Remove unsafe use of \\' from raster warning message

#2384, Incorrect variable datatypes for ST\_Neighborhood

#2111, Raster bands can only reference the first 256 bands of out-db
rasters

Release 2.0.3
=============

Release date: 2013/03/01

This is a bug fix release, addressing issues that have been filed since
the 2.0.2 release. If you are using PostGIS 2.0+ a soft upgrade is
required. For users of PostGIS 1.5 or below, a hard upgrade is required.

#2126, Better handling of empty rasters from ST\_ConvexHull()

#2134, Make sure to process SRS before passing it off to GDAL functions

Fix various memory leaks in liblwgeom

#2173, Fix robustness issue in splitting a line with own vertex also
affecting topology building (#2172)

#2174, Fix usage of wrong function lwpoly\_free()

#2176, Fix robustness issue with ST\_ChangeEdgeGeom

#2184, Properly copy topologies with Z value

postgis\_restore.pl support for mixed case geometry column name in dumps

#2188, Fix function parameter value overflow that caused problems when
copying data from a GDAL dataset

#2216, More memory errors in MultiPolygon GeoJSON parsing (with holes)

Fix Memory leak in GeoJSON parser

#2141, More verbose output when constraints fail to be added to a raster
column

Speedup ST\_ChangeEdgeGeom

Release 2.0.2
=============

Release date: 2012/12/03

This is a bug fix release, addressing issues that have been filed since
the 2.0.1 release.

#1287, Drop of "gist\_geometry\_ops" broke a few clients package of
legacy\_gist.sql for these cases

#1391, Errors during upgrade from 1.5

#1828, Poor selectivity estimate on ST\_DWithin

#1838, error importing tiger/line data

#1869, ST\_AsBinary is not unique added to legacy\_minor/legacy.sql
scripts

#1885, Missing field from tabblock table in tiger2010 census\_loader.sql

#1891, Use LDFLAGS environment when building liblwgeom

#1900, Fix pgsql2shp for big-endian systems

#1932, Fix raster2pgsql for invalid syntax for setting index tablespace

#1936, ST\_GeomFromGML on CurvePolygon causes server crash

#1955, ST\_ModEdgeHeal and ST\_NewEdgeHeal for doubly connected edges

#1957, ST\_Distance to a one-point LineString returns NULL

#1976, Geography point-in-ring code overhauled for more reliability

#1978, wrong answer calculating length of closed circular arc (circle)

#1981, Remove unused but set variables as found with gcc 4.6+

#1987, Restore 1.5.x behaviour of ST\_Simplify

#1989, Preprocess input geometry to just intersection with raster to be
clipped

#1991, geocode really slow on PostgreSQL 9.2

#1996, support POINT EMPTY in GeoJSON output

#1998, Fix ST\_{Mod,New}EdgeHeal joining edges sharing both endpoints

#2001, ST\_CurveToLine has no effect if the geometry doesn't actually
contain an arc

#2015, ST\_IsEmpty('POLYGON(EMPTY)') returns False

#2019, ST\_FlipCoordinates does not update bbox

#2025, Fix side location conflict at TopoGeo\_AddLineString

#2026, improve performance of distance calculations

#2033, Fix adding a splitting point into a 2.5d topology

#2051, Fix excess of precision in ST\_AsGeoJSON output

#2052, Fix buffer overflow in lwgeom\_to\_geojson

#2056, Fixed lack of SRID check of raster and geometry in ST\_SetValue()

#2057, Fixed linking issue for raster2psql to libpq

#2060, Fix "dimension" check violation by GetTopoGeomElementArray

#2072, Removed outdated checks preventing ST\_Intersects(raster) from
working on out-db bands

#2077, Fixed incorrect answers from ST\_Hillshade(raster)

#2092, Namespace issue with ST\_GeomFromKML,ST\_GeomFromGML for libxml
2.8+

#2099, Fix double free on exception in ST\_OffsetCurve

#2100, ST\_AsRaster() may not return raster with specified pixel type

#2108, Ensure ST\_Line\_Interpolate\_Point always returns POINT

#2109, Ensure ST\_Centroid always returns POINT

#2117, Ensure ST\_PointOnSurface always returns POINT

#2129, Fix SRID in ST\_Homogenize output with collection input

#2130, Fix memory error in MultiPolygon GeoJson parsing

Update URL of Maven jar

#1581, ST\_Clip(raster, ...) no longer imposes NODATA on a band if the
corresponding band from the source raster did not have NODATA

#1928, Accept array properties in GML input multi-geom input (Kashif
Rasul and Shoaib Burq / SpacialDB)

#2082, Add indices on start\_node and end\_node of topology edge tables

#2087, Speedup topology.GetRingEdges using a recursive CTE

Release 2.0.1
=============

Release date: 2012/06/22

This is a bug fix release, addressing issues that have been filed since
the 2.0.0 release.

#1264, fix st\_dwithin(geog, geog, 0).

#1468 shp2pgsql-gui table column schema get shifted

#1694, fix building with clang. (vince)

#1708, improve restore of pre-PostGIS 2.0 backups.

#1714, more robust handling of high topology tolerance.

#1755, ST\_GeographyFromText support for higher dimensions.

#1759, loading transformed shapefiles in raster enabled db.

#1761, handling of subdatasets in NetCDF, HDF4 and HDF5 in raster2pgsql.

#1763, topology.toTopoGeom use with custom search\_path.

#1766, don't let ST\_RemEdge\* destroy peripheral TopoGeometry objects.

#1774, Clearer error on setting an edge geometry to an invalid one.

#1775, ST\_ChangeEdgeGeom collision detection with 2-vertex target.

#1776, fix ST\_SymDifference(empty, geom) to return geom.

#1779, install SQL comment files.

#1782, fix spatial reference string handling in raster.

#1789, fix false edge-node crossing report in ValidateTopology.

#1790, fix toTopoGeom handling of duplicated primitives.

#1791, fix ST\_Azimuth with very close but distinct points.

#1797, fix (ValidateTopology(xxx)).\* syntax calls.

#1805, put back the 900913 SRID entry.

#1813, Only show readable relations in metadata tables.

#1819, fix floating point issues with ST\_World2RasterCoord and
ST\_Raster2WorldCoord variants.

#1820 compilation on 9.2beta1.

#1822, topology load on PostgreSQL 9.2beta1.

#1825, fix prepared geometry cache lookup

#1829, fix uninitialized read in GeoJSON parser

#1834, revise postgis extension to only backup user specified
spatial\_ref\_sys

#1839, handling of subdatasets in GeoTIFF in raster2pgsql.

#1840, fix logic of when to compute # of tiles in raster2pgsql.

#1851, fix spatial\_ref\_system parameters for EPSG:3844

#1857, fix failure to detect endpoint mismatch in ST\_AddEdge\*Face\*

#1865, data loss in postgis\_restore.pl when data rows have leading
dashes.

#1867, catch invalid topology name passed to topogeo\_add\*

#1872, fix ST\_ApproxSummarystats to prevent division by zero

#1873, fix ptarray\_locate\_point to return interpolated Z/M values for
on-the-line case

#1875, ST\_SummaryStats returns NULL for all parameters except count
when count is zero

#1881, shp2pgsql-gui -- editing a field sometimes triggers removing row

#1883, Geocoder install fails trying to run
create\_census\_base\_tables() (Brian Panulla)

More detailed exception message from topology editing functions.

#1786, improved build dependencies

#1806, speedup of ST\_BuildArea, ST\_MakeValid and ST\_GetFaceGeometry.

#1812, Add lwgeom\_normalize in LIBLWGEOM for more stable testing.

Release 2.0.0
=============

Release date: 2012/04/03

This is a major release. A hard upgrade is required. Yes this means a
full dump reload and some special preparations if you are using obsolete
functions. Refer to ? for details on upgrading. Refer to ? for more
details and changed/new functions.

We are most indebted to the numerous members in the PostGIS community
who were brave enough to test out the new features in this release. No
major release can be successful without these folk.

Below are those who have been most valiant, provided very detailed and
thorough bug reports, and detailed analysis.

Andrea Peri - Lots of testing on topology, checking for correctness
Andreas Forø Tollefsen - raster testing
Chris English - topology stress testing loader functions
Salvatore Larosa - topology robustness testing
Brian Hamlin - Benchmarking (also experimental experimental branches
before they are folded into core) , general testing of various pieces
including Tiger and Topology. Testing on various server VMs
Mike Pease - Tiger geocoder testing - very detailed reports of issues
Tom van Tilburg - raster testing
#722, #302, Most deprecated functions removed (over 250 functions)
(Regina Obe, Paul Ramsey)

Unknown SRID changed from -1 to 0. (Paul Ramsey)

-- (most deprecated in 1.2) removed non-ST variants buffer, length,
intersects (and internal functions renamed) etc.

-- If you have been using deprecated functions CHANGE your apps or
suffer the consequences. If you don't see a function documented -- it
ain't supported or it is an internal function. Some constraints in older
tables were built with deprecated functions. If you restore you may need
to rebuild table constraints with populate\_geometry\_columns(). If you
have applications or tools that rely on deprecated functions, please
refer to ? for more details.

#944 geometry\_columns is now a view instead of a table (Paul Ramsey,
Regina Obe) for tables created the old way reads (srid, type, dims)
constraints for geometry columns created with type modifiers reads rom
column definition

#1081, #1082, #1084, #1088 - Mangement functions support typmod geometry
column creation functions now default to typmod creation (Regina Obe)

#1083 probe\_geometry\_columns(),
rename\_geometry\_table\_constraints(), fix\_geometry\_columns();
removed - now obsolete with geometry\_column view (Regina Obe)

#817 Renaming old 3D functions to the convention ST\_3D (Nicklas Avén)

#548 (sorta), ST\_NumGeometries,ST\_GeometryN now returns 1 (or the
geometry) instead of null for single geometries (Sandro Santilli, Maxime
van Noppen)

`KNN Gist index based centroid (<->) and box (<#>) distance operators
(Paul Ramsey / funded by
Vizzuality) <http://blog.opengeo.org/2011/09/28/indexed-nearest-neighbour-search-in-postgis/>`__

Support for TIN and PolyHedralSurface and enhancement of many functions
to support 3D (Olivier Courtin / Oslandia)

`Raster support integrated and
documented <http://trac.osgeo.org/postgis/wiki/WKTRaster/PlanningAndFunding>`__
(Pierre Racine, Jorge Arévalo, Mateusz Loskot, Sandro Santilli, David
Zwarg, Regina Obe, Bborie Park) (Company developer and funding:
University Laval, Deimos Space, CadCorp, Michigan Tech Research
Institute, Azavea, Paragon Corporation, UC Davis Center for Vectorborne
Diseases)

Making spatial indexes 3D aware - in progress (Paul Ramsey, Mark
Cave-Ayland)

Topology support improved (more functions), documented, testing (Sandro
Santilli / Faunalia for RT-SIGTA), Andrea Peri, Regina Obe, Jose Carlos
Martinez Llari

3D relationship and measurement support functions (Nicklas Avén)

ST\_3DDistance, ST\_3DClosestPoint, ST\_3DIntersects, ST\_3DShortestLine
and more...

N-Dimensional spatial indexes (Paul Ramsey / OpenGeo)

ST\_Split (Sandro Santilli / Faunalia for RT-SIGTA)

ST\_IsValidDetail (Sandro Santilli / Faunalia for RT-SIGTA)

ST\_MakeValid (Sandro Santilli / Faunalia for RT-SIGTA)

ST\_RemoveRepeatedPoints (Sandro Santilli / Faunalia for RT-SIGTA)

ST\_GeometryN and ST\_NumGeometries support for non-collections (Sandro
Santilli)

ST\_IsCollection (Sandro Santilli, Maxime van Noppen)

ST\_SharedPaths (Sandro Santilli / Faunalia for RT-SIGTA)

ST\_Snap (Sandro Santilli)

ST\_RelateMatch (Sandro Santilli / Faunalia for RT-SIGTA)

ST\_ConcaveHull (Regina Obe and Leo Hsu / Paragon Corporation)

ST\_UnaryUnion (Sandro Santilli / Faunalia for RT-SIGTA)

ST\_AsX3D (Regina Obe / Arrival 3D funding)

ST\_OffsetCurve (Sandro Santilli, Rafal Magda)

`ST\_GeomFromGeoJSON (Kashif Rasul, Paul Ramsey / Vizzuality
funding) <http://blog.opengeo.org/2011/11/21/st_geomfromgeojson/>`__

Made shape file loader tolerant of truncated multibyte values found in
some free worldwide shapefiles (Sandro Santilli)

Lots of bug fixes and enhancements to shp2pgsql Beefing up regression
tests for loaders Reproject support for both geometry and geography
during import (Jeff Adams / Azavea, Mark Cave-Ayland)

pgsql2shp conversion from predefined list (Loic Dachary / Mark
Cave-Ayland)

Shp-pgsql GUI loader - support loading multiple files at a time. (Mark
Leslie)

Extras - upgraded tiger\_geocoder from using old TIGER format to use new
TIGER shp and file structure format (Stephen Frost)

Extras - revised tiger\_geocoder to work with TIGER census 2010 data,
addition of reverse geocoder function, various bug fixes, accuracy
enhancements, limit max result return, speed improvements, loading
routines. (Regina Obe, Leo Hsu / Paragon Corporation / funding provided
by Hunter Systems Group)

Overall Documentation proofreading and corrections. (Kasif Rasul)

Cleanup PostGIS JDBC classes, revise to use Maven build. (Maria Arias de
Reyna, Sandro Santilli)

#1335 ST\_AddPoint returns incorrect result on Linux (Even Rouault)

We thank `U.S Department of State Human Information Unit
(HIU) <http://blog.opengeo.org/2012/02/01/it-goes-up-to-2-0/>`__ and
`Vizzuality <http://blog.cartodb.com/post/17318840209/postgis-core-committer-sandro-santilli-joins-cartodb>`__
for general monetary support to get PostGIS 2.0 out the door.

Release 1.5.4
=============

Release date: 2012/05/07

This is a bug fix release, addressing issues that have been filed since
the 1.5.3 release.

#547, ST\_Contains memory problems (Sandro Santilli)

#621, Problem finding intersections with geography (Paul Ramsey)

#627, PostGIS/PostgreSQL process die on invalid geometry (Paul Ramsey)

#810, Increase accuracy of area calculation (Paul Ramsey)

#852, improve spatial predicates robustness (Sandro Santilli, Nicklas
Avén)

#877, ST\_Estimated\_Extent returns NULL on empty tables (Sandro
Santilli)

#1028, ST\_AsSVG kills whole postgres server when fails (Paul Ramsey)

#1056, Fix boxes of arcs and circle stroking code (Paul Ramsey)

#1121, populate\_geometry\_columns using deprecated functions (Regin
Obe, Paul Ramsey)

#1135, improve testsuite predictability (Andreas 'ads' Scherbaum)

#1146, images generator crashes (bronaugh)

#1170, North Pole intersection fails (Paul Ramsey)

#1179, ST\_AsText crash with bad value (kjurka)

#1184, honour DESTDIR in documentation Makefile (Bryce L Nordgren)

#1227, server crash on invalid GML

#1252, SRID appearing in WKT (Paul Ramsey)

#1264, st\_dwithin(g, g, 0) doesn't work (Paul Ramsey)

#1344, allow exporting tables with invalid geometries (Sandro Santilli)

#1389, wrong proj4text for SRID 31300 and 31370 (Paul Ramsey)

#1406, shp2pgsql crashes when loading into geography (Sandro Santilli)

#1595, fixed SRID redundancy in ST\_Line\_SubString (Sandro Santilli)

#1596, check SRID in UpdateGeometrySRID (Mike Toews, Sandro Santilli)

#1602, fix ST\_Polygonize to retain Z (Sandro Santilli)

#1697, fix crash with EMPTY entries in GiST index (Paul Ramsey)

#1772, fix ST\_Line\_Locate\_Point with collapsed input (Sandro
Santilli)

#1799, Protect ST\_Segmentize from max\_length=0 (Sandro Santilli)

Alter parameter order in 900913 (Paul Ramsey)

Support builds with "gmake" (Greg Troxel)

Release 1.5.3
=============

Release date: 2011/06/25

This is a bug fix release, addressing issues that have been filed since
the 1.5.2 release. If you are running PostGIS 1.3+, a soft upgrade is
sufficient otherwise a hard upgrade is recommended.

#1056, produce correct bboxes for arc geometries, fixes index errors
(Paul Ramsey)

#1007, ST\_IsValid crash fix requires GEOS 3.3.0+ or 3.2.3+ (Sandro
Santilli, reported by Birgit Laggner)

#940, support for PostgreSQL 9.1 beta 1 (Regina Obe, Paul Ramsey, patch
submitted by stl)

#845, ST\_Intersects precision error (Sandro Santilli, Nicklas Avén)
Reported by cdestigter

#884, Unstable results with ST\_Within, ST\_Intersects (Chris Hodgson)

#779, shp2pgsql -S option seems to fail on points (Jeff Adams)

#666, ST\_DumpPoints is not null safe (Regina Obe)

#631, Update NZ projections for grid transformation support (jpalmer)

#630, Peculiar Null treatment in arrays in ST\_Collect (Chris Hodgson)
Reported by David Bitner

#624, Memory leak in ST\_GeogFromText (ryang, Paul Ramsey)

#609, Bad source code in manual section 5.2 Java Clients (simoc, Regina
Obe)

#604, shp2pgsql usage touchups (Mike Toews, Paul Ramsey)

#573 ST\_Union fails on a group of linestrings Not a PostGIS bug, fixed
in GEOS 3.3.0

#457 ST\_CollectionExtract returns non-requested type (Nicklas Avén,
Paul Ramsey)

#441 ST\_AsGeoJson Bbox on GeometryCollection error (Olivier Courtin)

#411 Ability to backup invalid geometries (Sando Santilli) Reported by
Regione Toscana

#409 ST\_AsSVG - degraded (Olivier Courtin) Reported by Sdikiy

#373 Documentation syntax error in hard upgrade (Paul Ramsey) Reported
by psvensso

Release 1.5.2
=============

Release date: 2010/09/27

This is a bug fix release, addressing issues that have been filed since
the 1.5.1 release. If you are running PostGIS 1.3+, a soft upgrade is
sufficient otherwise a hard upgrade is recommended.

Loader: fix handling of empty (0-verticed) geometries in shapefiles.
(Sandro Santilli)

#536, Geography ST\_Intersects, ST\_Covers, ST\_CoveredBy and Geometry
ST\_Equals not using spatial index (Regina Obe, Nicklas Aven)

#573, Improvement to ST\_Contains geography (Paul Ramsey)

Loader: Add support for command-q shutdown in Mac GTK build (Paul
Ramsey)

#393, Loader: Add temporary patch for large DBF files (Maxime Guillaud,
Paul Ramsey)

#507, Fix wrong OGC URN in GeoJSON and GML output (Olivier Courtin)

spatial\_ref\_sys.sql Add datum conversion for projection SRID 3021
(Paul Ramsey)

Geography - remove crash for case when all geographies are out of the
estimate (Paul Ramsey)

#469, Fix for array\_aggregation error (Greg Stark, Paul Ramsey)

#532, Temporary geography tables showing up in other user sessions (Paul
Ramsey)

#562, ST\_Dwithin errors for large geographies (Paul Ramsey)

#513, shape loading GUI tries to make spatial index when loading DBF
only mode (Paul Ramsey)

#527, shape loading GUI should always append log messages (Mark
Cave-Ayland)

#504, shp2pgsql should rename xmin/xmax fields (Sandro Santilli)

#458, postgis\_comments being installed in contrib instead of version
folder (Mark Cave-Ayland)

#474, Analyzing a table with geography column crashes server (Paul
Ramsey)

#581, LWGEOM-expand produces inconsistent results (Mark Cave-Ayland)

#513, Add dbf filter to shp2pgsql-gui and allow uploading dbf only (Paul
Ramsey)

Fix further build issues against PostgreSQL 9.0 (Mark Cave-Ayland)

#572, Password whitespace for Shape File (Mark Cave-Ayland)

#603, shp2pgsql: "-w" produces invalid WKT for MULTI\* objects. (Mark
Cave-Ayland)

Release 1.5.1
=============

Release date: 2010/03/11

This is a bug fix release, addressing issues that have been filed since
the 1.4.1 release. If you are running PostGIS 1.3+, a soft upgrade is
sufficient otherwise a hard upgrade is recommended.

#410, update embedded bbox when applying ST\_SetPoint, ST\_AddPoint
ST\_RemovePoint to a linestring (Paul Ramsey)

#411, allow dumping tables with invalid geometries (Sandro Santilli, for
Regione Toscana-SIGTA)

#414, include geography\_columns view when running upgrade scripts (Paul
Ramsey)

#419, allow support for multilinestring in ST\_Line\_Substring (Paul
Ramsey, for Lidwala Consulting Engineers)

#421, fix computed string length in ST\_AsGML() (Olivier Courtin)

#441, fix GML generation with heterogeneous collections (Olivier
Courtin)

#443, incorrect coordinate reversal in GML 3 generation (Olivier
Courtin)

#450, #451, wrong area calculation for geography features that cross the
date line (Paul Ramsey)

Ensure support for upcoming 9.0 PgSQL release (Paul Ramsey)

Release 1.5.0
=============

Release date: 2010/02/04

This release provides support for geographic coordinates (lat/lon) via a
new GEOGRAPHY type. Also performance enhancements, new input format
support (GML,KML) and general upkeep.

The public API of PostGIS will not change during minor (0.0.X) releases.

The definition of the =~ operator has changed from an exact geometric
equality check to a bounding box equality check.

GEOS, Proj4, and LibXML2 are now mandatory dependencies

The library versions below are the minimum requirements for PostGIS 1.5

PostgreSQL 8.3 and higher on all platforms

GEOS 3.1 and higher only (GEOS 3.2+ to take advantage of all features)

LibXML2 2.5+ related to new ST\_GeomFromGML/KML functionality

Proj4 4.5 and higher only

?

Added Hausdorff distance calculations (#209) (Vincent Picavet)

Added parameters argument to ST\_Buffer operation to support one-sided
buffering and other buffering styles (Sandro Santilli)

Addition of other Distance related visualization and analysis functions
(Nicklas Aven)

-  ST\_ClosestPoint

-  ST\_DFullyWithin

-  ST\_LongestLine

-  ST\_MaxDistance

-  ST\_ShortestLine

ST\_DumpPoints (Maxime van Noppen)

KML, GML input via ST\_GeomFromGML and ST\_GeomFromKML (Olivier Courtin)

Extract homogeneous collection with ST\_CollectionExtract (Paul Ramsey)

Add measure values to an existing linestring with ST\_AddMeasure (Paul
Ramsey)

History table implementation in utils (George Silva)

Geography type and supporting functions

-  Spherical algorithms (Dave Skea)

-  Object/index implementation (Paul Ramsey)

-  Selectivity implementation (Mark Cave-Ayland)

-  Serializations to KML, GML and JSON (Olivier Courtin)

-  ST\_Area, ST\_Distance, ST\_DWithin, ST\_GeogFromText,
   ST\_GeogFromWKB, ST\_Intersects, ST\_Covers, ST\_Buffer (Paul Ramsey)

Performance improvements to ST\_Distance (Nicklas Aven)

Documentation updates and improvements (Regina Obe, Kevin Neufeld)

Testing and quality control (Regina Obe)

PostGIS 1.5 support PostgreSQL 8.5 trunk (Guillaume Lelarge)

Win32 support and improvement of core shp2pgsql-gui (Mark Cave-Ayland)

In place 'make check' support (Paul Ramsey)

http://trac.osgeo.org/postgis/query?status=closed&milestone=PostGIS+1.5.0&order=priority

Release 1.4.0
=============

Release date: 2009/07/24

This release provides performance enhancements, improved internal
structures and testing, new features, and upgraded documentation. If you
are running PostGIS 1.1+, a soft upgrade is sufficient otherwise a hard
upgrade is recommended.

As of the 1.4 release series, the public API of PostGIS will not change
during minor releases.

The versions below are the \*minimum\* requirements for PostGIS 1.4

PostgreSQL 8.2 and higher on all platforms

GEOS 3.0 and higher only

PROJ4 4.5 and higher only

ST\_Union() uses high-speed cascaded union when compiled against GEOS
3.1+ (Paul Ramsey)

ST\_ContainsProperly() requires GEOS 3.1+

ST\_Intersects(), ST\_Contains(), ST\_Within() use high-speed cached
prepared geometry against GEOS 3.1+ (Paul Ramsey / funded by Zonar
Systems)

Vastly improved documentation and reference manual (Regina Obe & Kevin
Neufeld)

Figures and diagram examples in the reference manual (Kevin Neufeld)

ST\_IsValidReason() returns readable explanations for validity failures
(Paul Ramsey)

ST\_GeoHash() returns a geohash.org signature for geometries (Paul
Ramsey)

GTK+ multi-platform GUI for shape file loading (Paul Ramsey)

ST\_LineCrossingDirection() returns crossing directions (Paul Ramsey)

ST\_LocateBetweenElevations() returns sub-string based on Z-ordinate.
(Paul Ramsey)

Geometry parser returns explicit error message about location of syntax
errors (Mark Cave-Ayland)

ST\_AsGeoJSON() return JSON formatted geometry (Olivier Courtin)

Populate\_Geometry\_Columns() -- automatically add records to
geometry\_columns for TABLES and VIEWS (Kevin Neufeld)

ST\_MinimumBoundingCircle() -- returns the smallest circle polygon that
can encompass a geometry (Bruce Rindahl)

Core geometry system moved into independent library, liblwgeom. (Mark
Cave-Ayland)

New build system uses PostgreSQL "pgxs" build bootstrapper. (Mark
Cave-Ayland)

Debugging framework formalized and simplified. (Mark Cave-Ayland)

All build-time #defines generated at configure time and placed in
headers for easier cross-platform support (Mark Cave-Ayland)

Logging framework formalized and simplified (Mark Cave-Ayland)

Expanded and more stable support for CIRCULARSTRING, COMPOUNDCURVE and
CURVEPOLYGON, better parsing, wider support in functions (Mark Leslie &
Mark Cave-Ayland)

Improved support for OpenSolaris builds (Paul Ramsey)

Improved support for MSVC builds (Mateusz Loskot)

Updated KML support (Olivier Courtin)

Unit testing framework for liblwgeom (Paul Ramsey)

New testing framework to comprehensively exercise every PostGIS function
(Regine Obe)

Performance improvements to all geometry aggregate functions (Paul
Ramsey)

Support for the upcoming PostgreSQL 8.4 (Mark Cave-Ayland, Talha Bin
Rizwan)

Shp2pgsql and pgsql2shp re-worked to depend on the common
parsing/unparsing code in liblwgeom (Mark Cave-Ayland)

Use of PDF DbLatex to build PDF docs and preliminary instructions for
build (Jean David Techer)

Automated User documentation build (PDF and HTML) and Developer Doxygen
Documentation (Kevin Neufeld)

Automated build of document images using ImageMagick from WKT geometry
text files (Kevin Neufeld)

More attractive CSS for HTML documentation (Dane Springmeyer)

http://trac.osgeo.org/postgis/query?status=closed&milestone=PostGIS+1.4.0&order=priority

Release 1.3.6
=============

Release date: 2009/05/04

If you are running PostGIS 1.1+, a soft upgrade is sufficient otherwise
a hard upgrade is recommended. This release adds support for PostgreSQL
8.4, exporting prj files from the database with shape data, some crash
fixes for shp2pgsql, and several small bug fixes in the handling of
"curve" types, logical error importing dbf only files, improved error
handling of AddGeometryColumns.

Release 1.3.5
=============

Release date: 2008/12/15

If you are running PostGIS 1.1+, a soft upgrade is sufficient otherwise
a hard upgrade is recommended. This release is a bug fix release to
address a failure in ST\_Force\_Collection and related functions that
critically affects using MapServer with LINE layers.

Release 1.3.4
=============

Release date: 2008/11/24

This release adds support for GeoJSON output, building with PostgreSQL
8.4, improves documentation quality and output aesthetics, adds
function-level SQL documentation, and improves performance for some
spatial predicates (point-in-polygon tests).

Bug fixes include removal of crashers in handling circular strings for
many functions, some memory leaks removed, a linear referencing failure
for measures on vertices, and more. See the NEWS file for details.

Release 1.3.3
=============

Release date: 2008/04/12

This release fixes bugs shp2pgsql, adds enhancements to SVG and KML
support, adds a ST\_SimplifyPreserveTopology function, makes the build
more sensitive to GEOS versions, and fixes a handful of severe but rare
failure cases.

Release 1.3.2
=============

Release date: 2007/12/01

This release fixes bugs in ST\_EndPoint() and ST\_Envelope, improves
support for JDBC building and OS/X, and adds better support for GML
output with ST\_AsGML(), including GML3 output.

Release 1.3.1
=============

Release date: 2007/08/13

This release fixes some oversights in the previous release around
version numbering, documentation, and tagging.

Release 1.3.0
=============

Release date: 2007/08/09

This release provides performance enhancements to the relational
functions, adds new relational functions and begins the migration of our
function names to the SQL-MM convention, using the spatial type (SP)
prefix.

JDBC: Added Hibernate Dialect (thanks to Norman Barker)

Added ST\_Covers and ST\_CoveredBy relational functions. Description and
justification of these functions can be found at
http://lin-ear-th-inking.blogspot.com/2007/06/subtleties-of-ogc-covers-spatial.html

Added ST\_DWithin relational function.

Added cached and indexed point-in-polygon short-circuits for the
functions ST\_Contains, ST\_Intersects, ST\_Within and ST\_Disjoint

Added inline index support for relational functions (except
ST\_Disjoint)

Extended curved geometry support into the geometry accessor and some
processing functions

Began migration of functions to the SQL-MM naming convention; using a
spatial type (ST) prefix.

Added initial support for PostgreSQL 8.3

Release 1.2.1
=============

Release date: 2007/01/11

This release provides bug fixes in PostgreSQL 8.2 support and some small
performance enhancements.

Fixed point-in-polygon shortcut bug in Within().

Fixed PostgreSQL 8.2 NULL handling for indexes.

Updated RPM spec files.

Added short-circuit for Transform() in no-op case.

JDBC: Fixed JTS handling for multi-dimensional geometries (thanks to
Thomas Marti for hint and partial patch). Additionally, now JavaDoc is
compiled and packaged. Fixed classpath problems with GCJ. Fixed pgjdbc
8.2 compatibility, losing support for jdk 1.3 and older.

Release 1.2.0
=============

Release date: 2006/12/08

This release provides type definitions along with
serialization/deserialization capabilities for SQL-MM defined curved
geometries, as well as performance enhancements.

Added curved geometry type support for serialization/deserialization

Added point-in-polygon shortcircuit to the Contains and Within functions
to improve performance for these cases.

Release 1.1.6
=============

Release date: 2006/11/02

This is a bugfix release, in particular fixing a critical error with
GEOS interface in 64bit systems. Includes an updated of the SRS
parameters and an improvement in reprojections (take Z in
consideration). Upgrade is *encouraged*.

If you are upgrading from release 1.0.3 or later follow the `soft
upgrade <#soft_upgrade>`__ procedure.

If you are upgrading from a release *between 1.0.0RC6 and 1.0.2*
(inclusive) and really want a live upgrade read the `upgrade
section <#rel_1.0.3_upgrading>`__ of the 1.0.3 release notes chapter.

Upgrade from any release prior to 1.0.0RC6 requires an `hard
upgrade <#hard_upgrade>`__.

fixed CAPI change that broke 64-bit platforms

loader/dumper: fixed regression tests and usage output

Fixed setSRID() bug in JDBC, thanks to Thomas Marti

use Z ordinate in reprojections

spatial\_ref\_sys.sql updated to EPSG 6.11.1

Simplified Version.config infrastructure to use a single pack of version
variables for everything.

Include the Version.config in loader/dumper USAGE messages

Replace hand-made, fragile JDBC version parser with Properties

Release 1.1.5
=============

Release date: 2006/10/13

This is an bugfix release, including a critical segfault on win32.
Upgrade is *encouraged*.

If you are upgrading from release 1.0.3 or later follow the `soft
upgrade <#soft_upgrade>`__ procedure.

If you are upgrading from a release *between 1.0.0RC6 and 1.0.2*
(inclusive) and really want a live upgrade read the `upgrade
section <#rel_1.0.3_upgrading>`__ of the 1.0.3 release notes chapter.

Upgrade from any release prior to 1.0.0RC6 requires an `hard
upgrade <#hard_upgrade>`__.

Fixed MingW link error that was causing pgsql2shp to segfault on Win32
when compiled for PostgreSQL 8.2

fixed nullpointer Exception in Geometry.equals() method in Java

Added EJB3Spatial.odt to fulfill the GPL requirement of distributing the
"preferred form of modification"

Removed obsolete synchronization from JDBC Jts code.

Updated heavily outdated README files for shp2pgsql/pgsql2shp by merging
them with the manpages.

Fixed version tag in jdbc code that still said "1.1.3" in the "1.1.4"
release.

Added -S option for non-multi geometries to shp2pgsql

Release 1.1.4
=============

Release date: 2006/09/27

This is an bugfix release including some improvements in the Java
interface. Upgrade is *encouraged*.

If you are upgrading from release 1.0.3 or later follow the `soft
upgrade <#soft_upgrade>`__ procedure.

If you are upgrading from a release *between 1.0.0RC6 and 1.0.2*
(inclusive) and really want a live upgrade read the `upgrade
section <#rel_1.0.3_upgrading>`__ of the 1.0.3 release notes chapter.

Upgrade from any release prior to 1.0.0RC6 requires an `hard
upgrade <#hard_upgrade>`__.

Fixed support for PostgreSQL 8.2

Fixed bug in collect() function discarding SRID of input

Added SRID match check in MakeBox2d and MakeBox3d

Fixed regress tests to pass with GEOS-3.0.0

Improved pgsql2shp run concurrency.

reworked JTS support to reflect new upstream JTS developers' attitude to
SRID handling. Simplifies code and drops build depend on GNU trove.

Added EJB2 support generously donated by the "Geodetix s.r.l. Company"
http://www.geodetix.it/

Added EJB3 tutorial / examples donated by Norman Barker
<nbarker@ittvis.com>

Reorganized java directory layout a little.

Release 1.1.3
=============

Release date: 2006/06/30

This is an bugfix release including also some new functionalities (most
notably long transaction support) and portability enhancements. Upgrade
is *encouraged*.

If you are upgrading from release 1.0.3 or later follow the `soft
upgrade <#soft_upgrade>`__ procedure.

If you are upgrading from a release *between 1.0.0RC6 and 1.0.2*
(inclusive) and really want a live upgrade read the `upgrade
section <#rel_1.0.3_upgrading>`__ of the 1.0.3 release notes chapter.

Upgrade from any release prior to 1.0.0RC6 requires an `hard
upgrade <#hard_upgrade>`__.

BUGFIX in distance(poly,poly) giving wrong results.

BUGFIX in pgsql2shp successful return code.

BUGFIX in shp2pgsql handling of MultiLine WKT.

BUGFIX in affine() failing to update bounding box.

WKT parser: forbidden construction of multigeometries with EMPTY
elements (still supported for GEOMETRYCOLLECTION).

NEW Long Transactions support.

NEW DumpRings() function.

NEW AsHEXEWKB(geom, XDR\|NDR) function.

Improved regression tests: MultiPoint and scientific ordinates

Fixed some minor bugs in jdbc code

Added proper accessor functions for all fields in preparation of making
those fields private later

NEW regress test support for loader/dumper.

Added --with-proj-libdir and --with-geos-libdir configure switches.

Support for build Tru64 build.

Use Jade for generating documentation.

Don't link pgsql2shp to more libs then required.

Initial support for PostgreSQL 8.2.

Release 1.1.2
=============

Release date: 2006/03/30

This is an bugfix release including some new functions and portability
enhancements. Upgrade is *encouraged*.

If you are upgrading from release 1.0.3 or later follow the `soft
upgrade <#soft_upgrade>`__ procedure.

If you are upgrading from a release *between 1.0.0RC6 and 1.0.2*
(inclusive) and really want a live upgrade read the `upgrade
section <#rel_1.0.3_upgrading>`__ of the 1.0.3 release notes chapter.

Upgrade from any release prior to 1.0.0RC6 requires an `hard
upgrade <#hard_upgrade>`__.

BUGFIX in SnapToGrid() computation of output bounding box

BUGFIX in EnforceRHR()

jdbc2 SRID handling fixes in JTS code

Fixed support for 64bit archs

Regress tests can now be run \*before\* postgis installation

New affine() matrix transformation functions

New rotate{,X,Y,Z}() function

Old translating and scaling functions now use affine() internally

Embedded access control in estimated\_extent() for builds against pgsql
>= 8.0.0

More portable ./configure script

Changed ./run\_test script to have more sane default behaviour

Release 1.1.1
=============

Release date: 2006/01/23

This is an important Bugfix release, upgrade is *highly recommended*.
Previous version contained a bug in postgis\_restore.pl preventing `hard
upgrade <#hard_upgrade>`__ procedure to complete and a bug in GEOS-2.2+
connector preventing GeometryCollection objects to be used in
topological operations.

If you are upgrading from release 1.0.3 or later follow the `soft
upgrade <#soft_upgrade>`__ procedure.

If you are upgrading from a release *between 1.0.0RC6 and 1.0.2*
(inclusive) and really want a live upgrade read the `upgrade
section <#rel_1.0.3_upgrading>`__ of the 1.0.3 release notes chapter.

Upgrade from any release prior to 1.0.0RC6 requires an `hard
upgrade <#hard_upgrade>`__.

Fixed a premature exit in postgis\_restore.pl

BUGFIX in geometrycollection handling of GEOS-CAPI connector

Solaris 2.7 and MingW support improvements

BUGFIX in line\_locate\_point()

Fixed handling of postgresql paths

BUGFIX in line\_substring()

Added support for localized cluster in regress tester

New Z and M interpolation in line\_substring()

New Z and M interpolation in line\_interpolate\_point()

added NumInteriorRing() alias due to OpenGIS ambiguity

Release 1.1.0
=============

Release date: 2005/12/21

This is a Minor release, containing many improvements and new things.
Most notably: build procedure greatly simplified; transform()
performance drastically improved; more stable GEOS connectivity (CAPI
support); lots of new functions; draft topology support.

It is *highly recommended* that you upgrade to GEOS-2.2.x before
installing PostGIS, this will ensure future GEOS upgrades won't require
a rebuild of the PostGIS library.

This release includes code from Mark Cave Ayland for caching of proj4
objects. Markus Schaber added many improvements in his JDBC2 code. Alex
Bodnaru helped with PostgreSQL source dependency relief and provided
Debian specfiles. Michael Fuhr tested new things on Solaris arch. David
Techer and Gerald Fenoy helped testing GEOS C-API connector. Hartmut
Tschauner provided code for the azimuth() function. Devrim GUNDUZ
provided RPM specfiles. Carl Anderson helped with the new area building
functions. See the `credits <#credits_other_contributors>`__ section for
more names.

If you are upgrading from release 1.0.3 or later you *DO NOT* need a
dump/reload. Simply sourcing the new lwpostgis\_upgrade.sql script in
all your existing databases will work. See the `soft
upgrade <#soft_upgrade>`__ chapter for more information.

If you are upgrading from a release *between 1.0.0RC6 and 1.0.2*
(inclusive) and really want a live upgrade read the `upgrade
section <#rel_1.0.3_upgrading>`__ of the 1.0.3 release notes chapter.

Upgrade from any release prior to 1.0.0RC6 requires an `hard
upgrade <#hard_upgrade>`__.

scale() and transscale() companion methods to translate()

line\_substring()

line\_locate\_point()

M(point)

LineMerge(geometry)

shift\_longitude(geometry)

azimuth(geometry)

locate\_along\_measure(geometry, float8)

locate\_between\_measures(geometry, float8, float8)

SnapToGrid by point offset (up to 4d support)

BuildArea(any\_geometry)

OGC BdPolyFromText(linestring\_wkt, srid)

OGC BdMPolyFromText(linestring\_wkt, srid)

RemovePoint(linestring, offset)

ReplacePoint(linestring, offset, point)

Fixed memory leak in polygonize()

Fixed bug in lwgeom\_as\_anytype cast functions

Fixed USE\_GEOS, USE\_PROJ and USE\_STATS elements of postgis\_version()
output to always reflect library state.

SnapToGrid doesn't discard higher dimensions

Changed Z() function to return NULL if requested dimension is not
available

Much faster transform() function, caching proj4 objects

Removed automatic call to fix\_geometry\_columns() in
AddGeometryColumns() and update\_geometry\_stats()

Makefile improvements

JTS support improvements

Improved regression test system

Basic consistency check method for geometry collections

Support for (Hex)(E)wkb

Autoprobing DriverWrapper for HexWKB / EWKT switching

fix compile problems in ValueSetter for ancient jdk releases.

fix EWKT constructors to accept SRID=4711; representation

added preliminary read-only support for java2d geometries

Full autoconf-based configuration, with PostgreSQL source dependency
relief

GEOS C-API support (2.2.0 and higher)

Initial support for topology modelling

Debian and RPM specfiles

New lwpostgis\_upgrade.sql script

JTS support improvements

Stricter mapping between DBF and SQL integer and string attributes

Wider and cleaner regression test suite

old jdbc code removed from release

obsoleted direct use of postgis\_proc\_upgrade.pl

scripts version unified with release version

Release 1.0.6
=============

Release date: 2005/12/06

Contains a few bug fixes and improvements.

If you are upgrading from release 1.0.3 or later you *DO NOT* need a
dump/reload.

If you are upgrading from a release *between 1.0.0RC6 and 1.0.2*
(inclusive) and really want a live upgrade read the `upgrade
section <#rel_1.0.3_upgrading>`__ of the 1.0.3 release notes chapter.

Upgrade from any release prior to 1.0.0RC6 requires an `hard
upgrade <#hard_upgrade>`__.

Fixed palloc(0) call in collection deserializer (only gives problem with
--enable-cassert)

Fixed bbox cache handling bugs

Fixed geom\_accum(NULL, NULL) segfault

Fixed segfault in addPoint()

Fixed short-allocation in lwcollection\_clone()

Fixed bug in segmentize()

Fixed bbox computation of SnapToGrid output

Initial support for postgresql 8.2

Added missing SRID mismatch checks in GEOS ops

Release 1.0.5
=============

Release date: 2005/11/25

Contains memory-alignment fixes in the library, a segfault fix in
loader's handling of UTF8 attributes and a few improvements and
cleanups.

    **Note**

    Return code of shp2pgsql changed from previous releases to conform
    to unix standards (return 0 on success).

If you are upgrading from release 1.0.3 or later you *DO NOT* need a
dump/reload.

If you are upgrading from a release *between 1.0.0RC6 and 1.0.2*
(inclusive) and really want a live upgrade read the `upgrade
section <#rel_1.0.3_upgrading>`__ of the 1.0.3 release notes chapter.

Upgrade from any release prior to 1.0.0RC6 requires an `hard
upgrade <#hard_upgrade>`__.

Fixed memory alignment problems

Fixed computation of null values fraction in analyzer

Fixed a small bug in the getPoint4d\_p() low-level function

Speedup of serializer functions

Fixed a bug in force\_3dm(), force\_3dz() and force\_4d()

Fixed return code of shp2pgsql

Fixed back-compatibility issue in loader (load of null shapefiles)

Fixed handling of trailing dots in dbf numerical attributes

Segfault fix in shp2pgsql (utf8 encoding)

Schema aware postgis\_proc\_upgrade.pl, support for pgsql 7.2+

New "Reporting Bugs" chapter in manual

Release 1.0.4
=============

Release date: 2005/09/09

Contains important bug fixes and a few improvements. In particular, it
fixes a memory leak preventing successful build of GiST indexes for
large spatial tables.

If you are upgrading from release 1.0.3 you *DO NOT* need a dump/reload.

If you are upgrading from a release *between 1.0.0RC6 and 1.0.2*
(inclusive) and really want a live upgrade read the `upgrade
section <#rel_1.0.3_upgrading>`__ of the 1.0.3 release notes chapter.

Upgrade from any release prior to 1.0.0RC6 requires an `hard
upgrade <#hard_upgrade>`__.

Memory leak plugged in GiST indexing

Segfault fix in transform() handling of proj4 errors

Fixed some proj4 texts in spatial\_ref\_sys (missing +proj)

Loader: fixed string functions usage, reworked NULL objects check, fixed
segfault on MULTILINESTRING input.

Fixed bug in MakeLine dimension handling

Fixed bug in translate() corrupting output bounding box

Documentation improvements

More robust selectivity estimator

Minor speedup in distance()

Minor cleanups

GiST indexing cleanup

Looser syntax acceptance in box3d parser

Release 1.0.3
=============

Release date: 2005/08/08

Contains some bug fixes - *including a severe one affecting correctness
of stored geometries* - and a few improvements.

Due to a bug in a bounding box computation routine, the upgrade
procedure requires special attention, as bounding boxes cached in the
database could be incorrect.

An `hard upgrade <#hard_upgrade>`__ procedure (dump/reload) will force
recomputation of all bounding boxes (not included in dumps). This is
*required* if upgrading from releases prior to 1.0.0RC6.

If you are upgrading from versions 1.0.0RC6 or up, this release includes
a perl script (utils/rebuild\_bbox\_caches.pl) to force recomputation of
geometries' bounding boxes and invoke all operations required to
propagate eventual changes in them (geometry statistics update,
reindexing). Invoke the script after a make install (run with no args
for syntax help). Optionally run utils/postgis\_proc\_upgrade.pl to
refresh postgis procedures and functions signatures (see `Soft
upgrade <#soft_upgrade>`__).

Severe bugfix in lwgeom's 2d bounding box computation

Bugfix in WKT (-w) POINT handling in loader

Bugfix in dumper on 64bit machines

Bugfix in dumper handling of user-defined queries

Bugfix in create\_undef.pl script

Small performance improvement in canonical input function

Minor cleanups in loader

Support for multibyte field names in loader

Improvement in the postgis\_restore.pl script

New rebuild\_bbox\_caches.pl util script

Release 1.0.2
=============

Release date: 2005/07/04

Contains a few bug fixes and improvements.

If you are upgrading from release 1.0.0RC6 or up you *DO NOT* need a
dump/reload.

Upgrading from older releases requires a dump/reload. See the
`upgrading <#upgrading>`__ chapter for more informations.

Fault tolerant btree ops

Memory leak plugged in pg\_error

Rtree index fix

Cleaner build scripts (avoided mix of CFLAGS and CXXFLAGS)

New index creation capabilities in loader (-I switch)

Initial support for postgresql 8.1dev

Release 1.0.1
=============

Release date: 2005/05/24

Contains a few bug fixes and some improvements.

If you are upgrading from release 1.0.0RC6 or up you *DO NOT* need a
dump/reload.

Upgrading from older releases requires a dump/reload. See the
`upgrading <#upgrading>`__ chapter for more informations.

BUGFIX in 3d computation of length\_spheroid()

BUGFIX in join selectivity estimator

BUGFIX in shp2pgsql escape functions

better support for concurrent postgis in multiple schemas

documentation fixes

jdbc2: compile with "-target 1.2 -source 1.2" by default

NEW -k switch for pgsql2shp

NEW support for custom createdb options in postgis\_restore.pl

BUGFIX in pgsql2shp attribute names unicity enforcement

BUGFIX in Paris projections definitions

postgis\_restore.pl cleanups

Release 1.0.0
=============

Release date: 2005/04/19

Final 1.0.0 release. Contains a few bug fixes, some improvements in the
loader (most notably support for older postgis versions), and more docs.

If you are upgrading from release 1.0.0RC6 you *DO NOT* need a
dump/reload.

Upgrading from any other precedent release requires a dump/reload. See
the `upgrading <#upgrading>`__ chapter for more informations.

BUGFIX in transform() releasing random memory address

BUGFIX in force\_3dm() allocating less memory then required

BUGFIX in join selectivity estimator (defaults, leaks, tuplecount, sd)

BUGFIX in shp2pgsql escape of values starting with tab or single-quote

NEW manual pages for loader/dumper

NEW shp2pgsql support for old (HWGEOM) postgis versions

NEW -p (prepare) flag for shp2pgsql

NEW manual chapter about OGC compliancy enforcement

NEW autoconf support for JTS lib

BUGFIX in estimator testers (support for LWGEOM and schema parsing)

Release 1.0.0RC6
================

Release date: 2005/03/30

Sixth release candidate for 1.0.0. Contains a few bug fixes and
cleanups.

You need a dump/reload to upgrade from precedent releases. See the
`upgrading <#upgrading>`__ chapter for more informations.

BUGFIX in multi()

early return [when noop] from multi()

dropped {x,y}{min,max}(box2d) functions

BUGFIX in postgis\_restore.pl scrip

BUGFIX in dumper's 64bit support

Release 1.0.0RC5
================

Release date: 2005/03/25

Fifth release candidate for 1.0.0. Contains a few bug fixes and a
improvements.

If you are upgrading from release 1.0.0RC4 you *DO NOT* need a
dump/reload.

Upgrading from any other precedent release requires a dump/reload. See
the `upgrading <#upgrading>`__ chapter for more informations.

BUGFIX (segfaulting) in box3d computation (yes, another!).

BUGFIX (segfaulting) in estimated\_extent().

Small build scripts and utilities refinements.

Additional performance tips documented.

Release 1.0.0RC4
================

Release date: 2005/03/18

Fourth release candidate for 1.0.0. Contains bug fixes and a few
improvements.

You need a dump/reload to upgrade from precedent releases. See the
`upgrading <#upgrading>`__ chapter for more informations.

BUGFIX (segfaulting) in geom\_accum().

BUGFIX in 64bit architectures support.

BUGFIX in box3d computation function with collections.

NEW subselects support in selectivity estimator.

Early return from force\_collection.

Consistency check fix in SnapToGrid().

Box2d output changed back to 15 significant digits.

NEW distance\_sphere() function.

Changed get\_proj4\_from\_srid implementation to use PL/PGSQL instead of
SQL.

BUGFIX in loader and dumper handling of MultiLine shapes

BUGFIX in loader, skipping all but first hole of polygons.

jdbc2: code cleanups, Makefile improvements

FLEX and YACC variables set \*after\* pgsql Makefile.global is included
and only if the pgsql \*stripped\* version evaluates to the empty string

Added already generated parser in release

Build scripts refinements

improved version handling, central Version.config

improvements in postgis\_restore.pl

Release 1.0.0RC3
================

Release date: 2005/02/24

Third release candidate for 1.0.0. Contains many bug fixes and
improvements.

You need a dump/reload to upgrade from precedent releases. See the
`upgrading <#upgrading>`__ chapter for more informations.

BUGFIX in transform(): missing SRID, better error handling.

BUGFIX in memory alignment handling

BUGFIX in force\_collection() causing mapserver connector failures on
simple (single) geometry types.

BUGFIX in GeometryFromText() missing to add a bbox cache.

reduced precision of box2d output.

prefixed DEBUG macros with PGIS\_ to avoid clash with pgsql one

plugged a leak in GEOS2POSTGIS converter

Reduced memory usage by early releasing query-context palloced one.

BUGFIX in 72 index bindings.

BUGFIX in probe\_geometry\_columns() to work with PG72 and support
multiple geometry columns in a single table

NEW bool::text cast

Some functions made IMMUTABLE from STABLE, for performance improvement.

jdbc2: small patches, box2d/3d tests, revised docs and license.

jdbc2: bug fix and testcase in for pgjdbc 8.0 type autoregistration

jdbc2: Removed use of jdk1.4 only features to enable build with older
jdk releases.

jdbc2: Added support for building against pg72jdbc2.jar

jdbc2: updated and cleaned makefile

jdbc2: added BETA support for jts geometry classes

jdbc2: Skip known-to-fail tests against older PostGIS servers.

jdbc2: Fixed handling of measured geometries in EWKT.

new performance tips chapter in manual

documentation updates: pgsql72 requirement, lwpostgis.sql

few changes in autoconf

BUILDDATE extraction made more portable

fixed spatial\_ref\_sys.sql to avoid vacuuming the whole database.

spatial\_ref\_sys: changed Paris entries to match the ones distributed
with 0.x.

Release 1.0.0RC2
================

Release date: 2005/01/26

Second release candidate for 1.0.0 containing bug fixes and a few
improvements.

You need a dump/reload to upgrade from precedent releases. See the
`upgrading <#upgrading>`__ chapter for more informations.

BUGFIX in pointarray box3d computation

BUGFIX in distance\_spheroid definition

BUGFIX in transform() missing to update bbox cache

NEW jdbc driver (jdbc2)

GEOMETRYCOLLECTION(EMPTY) syntax support for backward compatibility

Faster binary outputs

Stricter OGC WKB/WKT constructors

More correct STABLE, IMMUTABLE, STRICT uses in lwpostgis.sql

stricter OGC WKB/WKT constructors

Faster and more robust loader (both i18n and not)

Initial autoconf script

Release 1.0.0RC1
================

Release date: 2005/01/13

This is the first candidate of a major postgis release, with internal
storage of postgis types redesigned to be smaller and faster on indexed
queries.

You need a dump/reload to upgrade from precedent releases. See the
`upgrading <#upgrading>`__ chapter for more informations.

Faster canonical input parsing.

Lossless canonical output.

EWKB Canonical binary IO with PG>73.

Support for up to 4d coordinates, providing lossless
shapefile->postgis->shapefile conversion.

New function: UpdateGeometrySRID(), AsGML(), SnapToGrid(), ForceRHR(),
estimated\_extent(), accum().

Vertical positioning indexed operators.

JOIN selectivity function.

More geometry constructors / editors.

PostGIS extension API.

UTF8 support in loader.
