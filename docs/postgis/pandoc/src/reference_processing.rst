Geometry Processing
===================

ST\_Buffer (T) For geometry: Returns a geometry that represents all
points whose distance from this Geometry is less than or equal to
distance. Calculations are in the Spatial Reference System of this
Geometry. For geography: Uses a planar transform wrapper. Introduced in
1.5 support for different end cap and mitre settings to control shape.
buffer\_style options:
quad\_segs=#,endcap=round\|flat\|square,join=round\|mitre\|bevel,mitre\_limit=#.#
geometry ST\_Buffer geometry g1 float radius\_of\_buffer geometry
ST\_Buffer geometry g1 float radius\_of\_buffer integer
num\_seg\_quarter\_circle geometry ST\_Buffer geometry g1 float
radius\_of\_buffer text buffer\_style\_parameters geography ST\_Buffer
geography g1 float radius\_of\_buffer\_in\_meters Description
-----------

Returns a geometry/geography that represents all points whose distance
from this Geometry/geography is less than or equal to distance.

Geometry: Calculations are in the Spatial Reference System of the
geometry. Introduced in 1.5 support for different end cap and mitre
settings to control shape.

    **Note**

    Negative radii: For polygons, a negative radius can be used, which
    will shrink the polygon rather than expanding it.

    **Note**

    Geography: For geography this is really a thin wrapper around the
    geometry implementation. It first determines the best SRID that fits
    the bounding box of the geography object (favoring UTM, Lambert
    Azimuthal Equal Area (LAEA) north/south pole, and falling back on
    mercator in worst case scenario) and then buffers in that planar
    spatial ref and retransforms back to WGS84 geography.

For geography this may not behave as expected if object is sufficiently
large that it falls between two UTM zones or crosses the dateline

Availability: 1.5 - ST\_Buffer was enhanced to support different endcaps
and join types. These are useful for example to convert road linestrings
into polygon roads with flat or square edges instead of rounded edges.
Thin wrapper for geography was added. - requires GEOS >= 3.2 to take
advantage of advanced geometry functionality.

The optional third parameter (currently only applies to geometry) can
either specify number of segments used to approximate a quarter circle
(integer case, defaults to 8) or a list of blank-separated key=value
pairs (string case) to tweak operations as follows:

-  'quad\_segs=#' : number of segments used to approximate a quarter
   circle (defaults to 8).

-  'endcap=round\|flat\|square' : endcap style (defaults to "round",
   needs GEOS-3.2 or higher for a different value). 'butt' is also
   accepted as a synonym for 'flat'.

-  'join=round\|mitre\|bevel' : join style (defaults to "round", needs
   GEOS-3.2 or higher for a different value). 'miter' is also accepted
   as a synonym for 'mitre'.

-  'mitre\_limit=#.#' : mitre ratio limit (only affects mitered join
   style). 'miter\_limit' is also accepted as a synonym for
   'mitre\_limit'.

Units of radius are measured in units of the spatial reference system.

The inputs can be POINTS, MULTIPOINTS, LINESTRINGS, MULTILINESTRINGS,
POLYGONS, MULTIPOLYGONS, and GeometryCollections.

    **Note**

    This function ignores the third dimension (z) and will always give a
    2-d buffer even when presented with a 3d-geometry.

Performed by the GEOS module.

SFS\_COMPLIANT s2.1.1.3

SQLMM\_COMPLIANT SQL-MM 3: 5.1.17

    **Note**

    People often make the mistake of using this function to try to do
    radius searches. Creating a buffer to to a radius search is slow and
    pointless. Use ? instead.

Examples
--------

+-----------------------+----------------------------+------------------------+
| quad\_segs=8          | quad\_segs=2 (lame)        |                        |
| (default)             |                            |                        |
+-----------------------+----------------------------+------------------------+
| SELECT ST\_Buffer(    | SELECT ST\_Buffer(         |                        |
| ST\_GeomFromText('POI | ST\_GeomFromText('POINT(10 |                        |
| NT(100                | 0                          |                        |
| 90)'), 50,            | 90)'), 50,                 |                        |
| 'quad\_segs=8');      | 'quad\_segs=2');           |                        |
+-----------------------+----------------------------+------------------------+
| endcap=round          | endcap=square              | endcap=flat            |
| join=round (default)  |                            |                        |
+-----------------------+----------------------------+------------------------+
| SELECT ST\_Buffer(    | SELECT ST\_Buffer(         | SELECT ST\_Buffer(     |
| ST\_GeomFromText(     | ST\_GeomFromText(          | ST\_GeomFromText(      |
| 'LINESTRING(50 50,150 | 'LINESTRING(50 50,150      | 'LINESTRING(50 50,150  |
| 150,150 50)' ), 10,   | 150,150 50)' ), 10,        | 150,150 50)' ), 10,    |
| 'endcap=round         | 'endcap=square             | 'endcap=flat           |
| join=round');         | join=round');              | join=round');          |
+-----------------------+----------------------------+------------------------+
| join=bevel            | join=mitre                 | join=mitre             |
|                       | mitre\_limit=5.0 (default  | mitre\_limit=1         |
|                       | mitre limit)               |                        |
+-----------------------+----------------------------+------------------------+
| SELECT ST\_Buffer(    | SELECT ST\_Buffer(         | SELECT ST\_Buffer(     |
| ST\_GeomFromText(     | ST\_GeomFromText(          | ST\_GeomFromText(      |
| 'LINESTRING(50 50,150 | 'LINESTRING(50 50,150      | 'LINESTRING(50 50,150  |
| 150,150 50)' ), 10,   | 150,150 50)' ), 10,        | 150,150 50)' ), 10,    |
| 'join=bevel');        | 'join=mitre                | 'join=mitre            |
|                       | mitre\_limit=5.0');        | mitre\_limit=1.0');    |
+-----------------------+----------------------------+------------------------+

::

    --A buffered point approximates a circle
    -- A buffered point forcing approximation of (see diagram)
    -- 2 points per circle is poly with 8 sides (see diagram)
    SELECT ST_NPoints(ST_Buffer(ST_GeomFromText('POINT(100 90)'), 50)) As promisingcircle_pcount,
    ST_NPoints(ST_Buffer(ST_GeomFromText('POINT(100 90)'), 50, 2)) As lamecircle_pcount;

    promisingcircle_pcount | lamecircle_pcount
    ------------------------+-------------------
                 33 |                9

    --A lighter but lamer circle
    -- only 2 points per quarter circle is an octagon
    --Below is a 100 meter octagon
    -- Note coordinates are in NAD 83 long lat which we transform
    to Mass state plane meter and then buffer to get measurements in meters;
    SELECT ST_AsText(ST_Buffer(
    ST_Transform(
    ST_SetSRID(ST_MakePoint(-71.063526, 42.35785),4269), 26986)
    ,100,2)) As octagon;
    ----------------------
    POLYGON((236057.59057465 900908.759918696,236028.301252769 900838.049240578,235
    957.59057465 900808.759918696,235886.879896532 900838.049240578,235857.59057465
    900908.759918696,235886.879896532 900979.470596815,235957.59057465 901008.759918
    696,236028.301252769 900979.470596815,236057.59057465 900908.759918696))
            

See Also
--------

?, ?, ?, ?, ?

ST\_BuildArea Creates an areal geometry formed by the constituent
linework of given geometry geometry ST\_BuildArea geometry A Description
-----------

Creates an areal geometry formed by the constituent linework of given
geometry. The return type can be a Polygon or MultiPolygon, depending on
input. If the input lineworks do not form polygons NULL is returned. The
inputs can be LINESTRINGS, MULTILINESTRINGS, POLYGONS, MULTIPOLYGONS,
and GeometryCollections.

This function will assume all inner geometries represent holes

    **Note**

    Input linework must be correctly noded for this function to work
    properly

Availability: 1.1.0 - requires GEOS >= 2.1.0.

Examples
--------

+------------------------------------------------------------------------+
| This will create a donut                                               |
+------------------------------------------------------------------------+
| SELECT ST\_BuildArea(ST\_Collect(smallc,bigc)) FROM (SELECT            |
| ST\_Buffer( ST\_GeomFromText('POINT(100 90)'), 25) As smallc,          |
| ST\_Buffer(ST\_GeomFromText('POINT(100 90)'), 50) As bigc) As foo;     |
+------------------------------------------------------------------------+
| This will create a gaping hole inside the circle with prongs sticking  |
| out                                                                    |
+------------------------------------------------------------------------+
| SELECT ST\_BuildArea(ST\_Collect(line,circle)) FROM (SELECT            |
| ST\_Buffer( ST\_MakeLine(ST\_MakePoint(10, 10),ST\_MakePoint(190,      |
| 190)), 5) As line, ST\_Buffer(ST\_GeomFromText('POINT(100 90)'), 50)   |
| As circle) As foo;                                                     |
+------------------------------------------------------------------------+
| --this creates the same gaping hole --but using linestrings instead of |
| polygons SELECT ST\_BuildArea(                                         |
| ST\_Collect(ST\_ExteriorRing(line),ST\_ExteriorRing(circle)) ) FROM    |
| (SELECT ST\_Buffer( ST\_MakeLine(ST\_MakePoint(10,                     |
| 10),ST\_MakePoint(190, 190)) ,5) As line,                              |
| ST\_Buffer(ST\_GeomFromText('POINT(100 90)'), 50) As circle) As foo;   |
+------------------------------------------------------------------------+

See Also
--------

?, ?, ?, ?wrappers to this function with standard OGC interface

ST\_Collect Return a specified ST\_Geometry value from a collection of
other geometries. geometry ST\_Collect geometry set g1field geometry
ST\_Collect geometry g1 geometry g2 geometry ST\_Collect geometry[]
g1\_array Description -----------

Output type can be a MULTI\* or a GEOMETRYCOLLECTION. Comes in 2
variants. Variant 1 collects 2 geometries. Variant 2 is an aggregate
function that takes a set of geometries and collects them into a single
ST\_Geometry.

Aggregate version: This function returns a GEOMETRYCOLLECTION or a MULTI
object from a set of geometries. The ST\_Collect() function is an
"aggregate" function in the terminology of PostgreSQL. That means that
it operates on rows of data, in the same way the SUM() and AVG()
functions do. For example, "SELECT ST\_Collect(GEOM) FROM GEOMTABLE
GROUP BY ATTRCOLUMN" will return a separate GEOMETRYCOLLECTION for each
distinct value of ATTRCOLUMN.

Non-Aggregate version: This function returns a geometry being a
collection of two input geometries. Output type can be a MULTI\* or a
GEOMETRYCOLLECTION.

    **Note**

    ST\_Collect and ST\_Union are often interchangeable. ST\_Collect is
    in general orders of magnitude faster than ST\_Union because it does
    not try to dissolve boundaries or validate that a constructed
    MultiPolgon doesn't have overlapping regions. It merely rolls up
    single geometries into MULTI and MULTI or mixed geometry types into
    Geometry Collections. Unfortunately geometry collections are not
    well-supported by GIS tools. To prevent ST\_Collect from returning a
    Geometry Collection when collecting MULTI geometries, one can use
    the below trick that utilizes ? to expand the MULTIs out to singles
    and then regroup them.

Availability: 1.4.0 - ST\_Collect(geomarray) was introduced. ST\_Collect
was enhanced to handle more geometries faster.

Z\_SUPPORT

CURVE\_SUPPORT This method supports Circular Strings and Curves, but
will never return a MULTICURVE or MULTI as one would expect and PostGIS
does not currently support those.

Examples
--------

Aggregate example
(http://postgis.refractions.net/pipermail/postgis-users/2008-June/020331.html)

::

    SELECT stusps,
           ST_Multi(ST_Collect(f.the_geom)) as singlegeom
         FROM (SELECT stusps, (ST_Dump(the_geom)).geom As the_geom
                    FROM
                    somestatetable ) As f
    GROUP BY stusps

Non-Aggregate example

::

    SELECT ST_AsText(ST_Collect(ST_GeomFromText('POINT(1 2)'),
        ST_GeomFromText('POINT(-2 3)') ));

    st_astext
    ----------
    MULTIPOINT(1 2,-2 3)

    --Collect 2 d points
    SELECT ST_AsText(ST_Collect(ST_GeomFromText('POINT(1 2)'),
            ST_GeomFromText('POINT(1 2)') ) );

    st_astext
    ----------
    MULTIPOINT(1 2,1 2)

    --Collect 3d points
    SELECT ST_AsEWKT(ST_Collect(ST_GeomFromEWKT('POINT(1 2 3)'),
            ST_GeomFromEWKT('POINT(1 2 4)') ) );

            st_asewkt
    -------------------------
     MULTIPOINT(1 2 3,1 2 4)

     --Example with curves
    SELECT ST_AsText(ST_Collect(ST_GeomFromText('CIRCULARSTRING(220268 150415,220227 150505,220227 150406)'),
    ST_GeomFromText('CIRCULARSTRING(220227 150406,2220227 150407,220227 150406)')));
                                                                    st_astext
    ------------------------------------------------------------------------------------
     GEOMETRYCOLLECTION(CIRCULARSTRING(220268 150415,220227 150505,220227 150406),
     CIRCULARSTRING(220227 150406,2220227 150407,220227 150406))

    --New ST_Collect array construct
    SELECT ST_Collect(ARRAY(SELECT the_geom FROM sometable));

    SELECT ST_AsText(ST_Collect(ARRAY[ST_GeomFromText('LINESTRING(1 2, 3 4)'),
                ST_GeomFromText('LINESTRING(3 4, 4 5)')])) As wktcollect;

    --wkt collect --
    MULTILINESTRING((1 2,3 4),(3 4,4 5))

See Also
--------

?, ?

ST\_ConcaveHull The concave hull of a geometry represents a possibly
concave geometry that encloses all geometries within the set. You can
think of it as shrink wrapping. geometry ST\_ConcaveHull geometry geomA
float target\_percent boolean allow\_holes=false Description -----------

The concave hull of a geometry represents a possibly concave geometry
that encloses all geometries within the set. Defaults to false for
allowing polygons with holes. The result is never higher than a single
polygon.

The target\_percent is the target percent of area of convex hull the
PostGIS solution will try to approach before giving up or exiting. One
can think of the concave hull as the geometry you get by vacuum sealing
a set of geometries. The target\_percent of 1 will give you the same
answer as the convex hull. A target\_percent between 0 and 0.99 will
give you something that should have a smaller area than the convex hull.
This is different from a convex hull which is more like wrapping a
rubber band around the set of geometries.

It is usually used with MULTI and Geometry Collections. Although it is
not an aggregate - you can use it in conjunction with ST\_Collect or
ST\_Union to get the concave hull of a set of points/linestring/polygons
ST\_ConcaveHull(ST\_Collect(somepointfield), 0.80).

It is much slower to compute than convex hull but encloses the geometry
better and is also useful for image recognition.

Performed by the GEOS module

    **Note**

    Note - If you are using with points, linestrings, or geometry
    collections use ST\_Collect. If you are using with polygons, use
    ST\_Union since it may fail with invalid geometries.

    **Note**

    Note - The smaller you make the target percent, the longer it takes
    to process the concave hull and more likely to run into topological
    exceptions. Also the more floating points and number of points you
    accrue. First try a 0.99 which does a first hop, is usually very
    fast, sometimes as fast as computing the convex hull, and usually
    gives much better than 99% of shrink since it almost always
    overshoots. Second hope of 0.98 it slower, others get slower usually
    quadratically. To reduce precision and float points, use ? or ?
    after ST\_ConcaveHull. ST\_SnapToGrid is a bit faster, but could
    result in invalid geometries where as ST\_SimplifyPreserveTopology
    almost always preserves the validity of the geometry.

More real world examples and brief explanation of the technique are
shown http://www.bostongis.com/postgis_concavehull.snippet

Also check out Simon Greener's article on demonstrating ConcaveHull
introduced in Oracle 11G R2.
http://www.spatialdbadvisor.com/oracle_spatial_tips_tricks/172/concave-hull-geometries-in-oracle-11gr2.
The solution we get at 0.75 target percent of convex hull is similar to
the shape Simon gets with Oracle SDO\_CONCAVEHULL\_BOUNDARY.

Availability: 2.0.0

Examples
--------

::

    --Get estimate of infected area based on point observations
    SELECT d.disease_type,
        ST_ConcaveHull(ST_Collect(d.pnt_geom), 0.99) As geom
        FROM disease_obs As d
        GROUP BY d.disease_type;

+-----------------------------+---------------------------------------------+
| ST\_ConcaveHull of 2        | -- geometries overlaid with concavehull at  |
| polygons encased in target  | target 90% of convex hull area              |
| 100% shrink concave hull    |                                             |
+-----------------------------+---------------------------------------------+
| -- geometries overlaid with | -- geometries overlaid with concavehull at  |
| concavehull -- at target    | target 90% shrink SELECT ST\_ConcaveHull(   |
| 100% shrink (this is the    | ST\_Union(ST\_GeomFromText('POLYGON((175    |
| same as convex hull - since | 150, 20 40, 50 60, 125 100, 175 150))'),    |
| no shrink) SELECT           | ST\_Buffer(ST\_GeomFromText('POINT(110      |
| ST\_ConcaveHull(            | 170)'), 20) ), 0.9) As target\_90;          |
| ST\_Union(ST\_GeomFromText( |                                             |
| 'POLYGON((175               |                                             |
| 150, 20 40, 50 60, 125 100, |                                             |
| 175 150))'),                |                                             |
| ST\_Buffer(ST\_GeomFromText |                                             |
| ('POINT(110                 |                                             |
| 170)'), 20) ), 1) As        |                                             |
| convexhull;                 |                                             |
+-----------------------------+---------------------------------------------+
| L Shape points overlaid     | ST\_ConcaveHull of L points at target 99%   |
| with convex hull            | of convex hull                              |
+-----------------------------+---------------------------------------------+
| -- this produces a table of | SELECT ST\_ConcaveHull(ST\_Collect(geom),   |
| 42 points that form an L    | 0.99) FROM l\_shape;                        |
| shape SELECT                |                                             |
| (ST\_DumpPoints(ST\_GeomFro |                                             |
| mText(                      |                                             |
| 'MULTIPOINT(14 14,34 14,54  |                                             |
| 14,74 14,94 14,114 14,134   |                                             |
| 14, 150 14,154 14,154 6,134 |                                             |
| 6,114 6,94 6,74 6,54 6,34   |                                             |
| 6, 14 6,10 6,8 6,7 7,6 8,6  |                                             |
| 10,6 30,6 50,6 70,6 90,6    |                                             |
| 110,6 130, 6 150,6 170,6    |                                             |
| 190,6 194,14 194,14 174,14  |                                             |
| 154,14 134,14 114, 14 94,14 |                                             |
| 74,14 54,14 34,14           |                                             |
| 14)'))).geom INTO TABLE     |                                             |
| l\_shape;                   |                                             |
+-----------------------------+---------------------------------------------+
| SELECT                      |                                             |
| ST\_ConvexHull(ST\_Collect( |                                             |
| geom))                      |                                             |
| FROM l\_shape;              |                                             |
+-----------------------------+---------------------------------------------+
| Concave Hull of L points at | multilinestring overlaid with Convex hull   |
| target 80% convex hull area | multilinestring with overlaid with Concave  |
|                             | hull of linestrings at 99% target -- first  |
|                             | hop                                         |
+-----------------------------+---------------------------------------------+
| -- Concave Hull L shape     | SELECT                                      |
| points -- at target 80% of  | ST\_ConcaveHull(ST\_GeomFromText('MULTILINE |
| convexhull SELECT           | STRING((106                                 |
| ST\_ConcaveHull(ST\_Collect | 164,30 112,74 70,82 112,130 94, 130 62,122  |
| (geom),                     | 40,156 32,162 76,172 88), (132 178,134      |
| 0.80) FROM l\_shape;        | 148,128 136,96 128,132 108,150 130, 170     |
|                             | 142,174 110,156 96,158 90,158 88), (22      |
|                             | 64,66 28,94 38,94 68,114 76,112 30, 132     |
|                             | 10,168 18,178 34,186 52,184 74,190 100, 190 |
|                             | 122,182 148,178 170,176 184,156 164,146     |
|                             | 178, 132 186,92 182,56 158,36 150,62 150,76 |
|                             | 128,88 118))'),0.99)                        |
+-----------------------------+---------------------------------------------+

See Also
--------

?, ?, ?, ?

ST\_ConvexHull The convex hull of a geometry represents the minimum
convex geometry that encloses all geometries within the set. geometry
ST\_ConvexHull geometry geomA Description -----------

The convex hull of a geometry represents the minimum convex geometry
that encloses all geometries within the set.

One can think of the convex hull as the geometry you get by wrapping an
elastic band around a set of geometries. This is different from a
concave hull which is analogous to shrink-wrapping your geometries.

It is usually used with MULTI and Geometry Collections. Although it is
not an aggregate - you can use it in conjunction with ST\_Collect to get
the convex hull of a set of points.
ST\_ConvexHull(ST\_Collect(somepointfield)).

It is often used to determine an affected area based on a set of point
observations.

Performed by the GEOS module

SFS\_COMPLIANT s2.1.1.3

SQLMM\_COMPLIANT SQL-MM 3: 5.1.16

Z\_SUPPORT

Examples
--------

::

    --Get estimate of infected area based on point observations
    SELECT d.disease_type,
        ST_ConvexHull(ST_Collect(d.the_geom)) As the_geom
        FROM disease_obs As d
        GROUP BY d.disease_type;

Convex Hull of a MultiLinestring and a MultiPoint seen together with the
MultiLinestring and MultiPoint

::

    SELECT ST_AsText(ST_ConvexHull(
        ST_Collect(
            ST_GeomFromText('MULTILINESTRING((100 190,10 8),(150 10, 20 30))'),
                ST_GeomFromText('MULTIPOINT(50 5, 150 30, 50 10, 10 10)')
                )) );
    ---st_astext--
    POLYGON((50 5,10 8,10 10,100 190,150 30,150 10,50 5))
        

See Also
--------

?, ?, ?

ST\_CurveToLine Converts a CIRCULARSTRING/CURVEDPOLYGON to a
LINESTRING/POLYGON geometry ST\_CurveToLine geometry curveGeom geometry
ST\_CurveToLine geometry curveGeom integer segments\_per\_qtr\_circle
Description -----------

Converst a CIRCULAR STRING to regular LINESTRING or CURVEPOLYGON to
POLYGON. Useful for outputting to devices that can't support
CIRCULARSTRING geometry types

Converts a given geometry to a linear geometry. Each curved geometry or
segment is converted into a linear approximation using the default value
of 32 segments per quarter circle

Availability: 1.2.2?

SFS\_COMPLIANT

SQLMM\_COMPLIANT SQL-MM 3: 7.1.7

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    SELECT ST_AsText(ST_CurveToLine(ST_GeomFromText('CIRCULARSTRING(220268 150415,220227 150505,220227 150406)')));

    --Result --
     LINESTRING(220268 150415,220269.95064912 150416.539364228,220271.823415575 150418.17258804,220273.613787707 150419.895736857,
     220275.317452352 150421.704659462,220276.930305234 150423.594998003,220278.448460847 150425.562198489,
     220279.868261823 150427.60152176,220281.186287736 150429.708054909,220282.399363347 150431.876723113,
     220283.50456625 150434.10230186,220284.499233914 150436.379429536,220285.380970099 150438.702620341,220286.147650624 150441.066277505,
     220286.797428488 150443.464706771,220287.328738321 150445.892130112,220287.740300149 150448.342699654,
     220288.031122486 150450.810511759,220288.200504713 150453.289621251,220288.248038775 150455.77405574,
     220288.173610157 150458.257830005,220287.977398166 150460.734960415,220287.659875492 150463.199479347,
     220287.221807076 150465.64544956,220286.664248262 150468.066978495,220285.988542259 150470.458232479,220285.196316903 150472.81345077,
     220284.289480732 150475.126959442,220283.270218395 150477.39318505,220282.140985384 150479.606668057,
     220280.90450212 150481.762075989,220279.5637474 150483.85421628,220278.12195122 150485.87804878,
     220276.582586992 150487.828697901,220274.949363179 150489.701464356,220273.226214362 150491.491836488,
     220271.417291757 150493.195501133,220269.526953216 150494.808354014,220267.559752731 150496.326509628,
     220265.520429459 150497.746310603,220263.41389631 150499.064336517,220261.245228106 150500.277412127,
     220259.019649359 150501.38261503,220256.742521683 150502.377282695,220254.419330878 150503.259018879,
     220252.055673714 150504.025699404,220249.657244448 150504.675477269,220247.229821107 150505.206787101,
     220244.779251566 150505.61834893,220242.311439461 150505.909171266,220239.832329968 150506.078553494,
     220237.347895479 150506.126087555,220234.864121215 150506.051658938,220232.386990804 150505.855446946,
     220229.922471872 150505.537924272,220227.47650166 150505.099855856,220225.054972724 150504.542297043,
     220222.663718741 150503.86659104,220220.308500449 150503.074365683,
     220217.994991777 150502.167529512,220215.72876617 150501.148267175,
     220213.515283163 150500.019034164,220211.35987523 150498.7825509,
     220209.267734939 150497.441796181,220207.243902439 150496,
     220205.293253319 150494.460635772,220203.420486864 150492.82741196,220201.630114732 150491.104263143,
     220199.926450087 150489.295340538,220198.313597205 150487.405001997,220196.795441592 150485.437801511,
     220195.375640616 150483.39847824,220194.057614703 150481.291945091,220192.844539092 150479.123276887,220191.739336189 150476.89769814,
     220190.744668525 150474.620570464,220189.86293234 150472.297379659,220189.096251815 150469.933722495,
     220188.446473951 150467.535293229,220187.915164118 150465.107869888,220187.50360229 150462.657300346,
     220187.212779953 150460.189488241,220187.043397726 150457.710378749,220186.995863664 150455.22594426,
     220187.070292282 150452.742169995,220187.266504273 150450.265039585,220187.584026947 150447.800520653,
     220188.022095363 150445.35455044,220188.579654177 150442.933021505,220189.25536018 150440.541767521,
     220190.047585536 150438.18654923,220190.954421707 150435.873040558,220191.973684044 150433.60681495,
     220193.102917055 150431.393331943,220194.339400319 150429.237924011,220195.680155039 150427.14578372,220197.12195122 150425.12195122,
     220198.661315447 150423.171302099,220200.29453926 150421.298535644,220202.017688077 150419.508163512,220203.826610682 150417.804498867,
     220205.716949223 150416.191645986,220207.684149708 150414.673490372,220209.72347298 150413.253689397,220211.830006129 150411.935663483,
     220213.998674333 150410.722587873,220216.22425308 150409.61738497,220218.501380756 150408.622717305,220220.824571561 150407.740981121,
     220223.188228725 150406.974300596,220225.586657991 150406.324522731,220227 150406)

    --3d example
    SELECT ST_AsEWKT(ST_CurveToLine(ST_GeomFromEWKT('CIRCULARSTRING(220268 150415 1,220227 150505 2,220227 150406 3)')));
    Output
    ------
     LINESTRING(220268 150415 1,220269.95064912 150416.539364228 1.0181172856673,
     220271.823415575 150418.17258804 1.03623457133459,220273.613787707 150419.895736857 1.05435185700189,....AD INFINITUM ....
        220225.586657991 150406.324522731 1.32611114201132,220227 150406 3)

    --use only 2 segments to approximate quarter circle
    SELECT ST_AsText(ST_CurveToLine(ST_GeomFromText('CIRCULARSTRING(220268 150415,220227 150505,220227 150406)'),2));
    st_astext
    ------------------------------
     LINESTRING(220268 150415,220287.740300149 150448.342699654,220278.12195122 150485.87804878,
     220244.779251566 150505.61834893,220207.243902439 150496,220187.50360229 150462.657300346,
     220197.12195122 150425.12195122,220227 150406)

See Also
--------

?

ST\_DelaunayTriangles Return a Delaunay triangulation around the given
input points. geometry ST\_DelaunayTriangles geometry g1 float tolerance
int4 flags Description -----------

Return a `Delaunay
triangulation <http://en.wikipedia.org/wiki/Delaunay_triangulation>`__
around the vertices of the input geometry. Output is a COLLECTION of
polygons (for flags=0) or a MULTILINESTRING (for flags=1) or TIN (for
flags=2). The tolerance, if any, is used to snap input vertices
togheter.

Availability: 2.1.0 - requires GEOS >= 3.4.0.

Z\_SUPPORT

T\_SUPPORT

2D Examples
-----------

+------------------------------------------------------------------------+
| Original polygons                                                      |
+------------------------------------------------------------------------+
| -- our original geometry -- ST\_Union(ST\_GeomFromText('POLYGON((175   |
| 150, 20 40, 50 60, 125 100, 175 150))'),                               |
| ST\_Buffer(ST\_GeomFromText('POINT(110 170)'), 20) )                   |
+------------------------------------------------------------------------+
| ST\_DelaunayTriangles of 2 polygons: delaunay triangle polygons each   |
| triangle themed in different color                                     |
+------------------------------------------------------------------------+
| -- geometries overlaid multilinestring triangles SELECT                |
| ST\_DelaunayTriangles( ST\_Union(ST\_GeomFromText('POLYGON((175 150,   |
| 20 40, 50 60, 125 100, 175 150))'),                                    |
| ST\_Buffer(ST\_GeomFromText('POINT(110 170)'), 20) )) As dtriag;       |
+------------------------------------------------------------------------+
| -- delaunay triangles as multilinestring                               |
+------------------------------------------------------------------------+
| SELECT ST\_DelaunayTriangles( ST\_Union(ST\_GeomFromText('POLYGON((175 |
| 150, 20 40, 50 60, 125 100, 175 150))'),                               |
| ST\_Buffer(ST\_GeomFromText('POINT(110 170)'), 20) ),0.001,1) As       |
| dtriag;                                                                |
+------------------------------------------------------------------------+
| -- delaunay triangles of 45 points as 55 triangle polygons             |
+------------------------------------------------------------------------+
| -- this produces a table of 42 points that form an L shape SELECT      |
| (ST\_DumpPoints(ST\_GeomFromText( 'MULTIPOINT(14 14,34 14,54 14,74     |
| 14,94 14,114 14,134 14, 150 14,154 14,154 6,134 6,114 6,94 6,74 6,54   |
| 6,34 6, 14 6,10 6,8 6,7 7,6 8,6 10,6 30,6 50,6 70,6 90,6 110,6 130, 6  |
| 150,6 170,6 190,6 194,14 194,14 174,14 154,14 134,14 114, 14 94,14     |
| 74,14 54,14 34,14 14)'))).geom INTO TABLE l\_shape; -- output as       |
| individual polygon triangles SELECT ST\_AsText((ST\_Dump(geom)).geom)  |
| As wkt FROM ( SELECT ST\_DelaunayTriangles(ST\_Collect(geom)) As geom  |
| FROM l\_shape) As foo;                                                 |
+------------------------------------------------------------------------+
| ---wkt --- POLYGON((6 194,6 190,14 194,6 194)) POLYGON((14 194,6       |
| 190,14 174,14 194)) POLYGON((14 194,14 174,154 14,14 194))             |
| POLYGON((154 14,14 174,14 154,154 14)) POLYGON((154 14,14 154,150      |
| 14,154 14)) POLYGON((154 14,150 14,154 6,154 14)) : :                  |
+------------------------------------------------------------------------+

3D Examples
-----------

::

    -- 3D multipoint --
    SELECT ST_AsText(ST_DelaunayTriangles(ST_GeomFromText(
    'MULTIPOINT Z(14 14 10,
    150 14 100,34 6 25, 20 10 150)'))) As wkt;

    -----wkt----
    GEOMETRYCOLLECTION Z (POLYGON Z ((14 14 10,20 10 150,34 6 25,14 14 10))
     ,POLYGON Z ((14 14 10,34 6 25,150 14 100,14 14 10)))

See Also
--------

?, ?

ST\_Difference Returns a geometry that represents that part of geometry
A that does not intersect with geometry B. geometry ST\_Difference
geometry geomA geometry geomB Description -----------

Returns a geometry that represents that part of geometry A that does not
intersect with geometry B. One can think of this as GeometryA -
ST\_Intersection(A,B). If A is completely contained in B then an empty
geometry collection is returned.

    **Note**

    Note - order matters. B - A will always return a portion of B

Performed by the GEOS module

    **Note**

    Do not call with a GeometryCollection as an argument

SFS\_COMPLIANT s2.1.1.3

SQLMM\_COMPLIANT SQL-MM 3: 5.1.20

Z\_SUPPORT However it seems to only consider x y when doing the
difference and tacks back on the Z-Index

Examples
--------

+--------------------------------------------+-----------------------------------------+
| The original linestrings shown together.   | The difference of the two linestrings   |
+--------------------------------------------+-----------------------------------------+

::

    --Safe for 2d. This is same geometries as what is shown for st_symdifference
    SELECT ST_AsText(
        ST_Difference(
                ST_GeomFromText('LINESTRING(50 100, 50 200)'),
                ST_GeomFromText('LINESTRING(50 50, 50 150)')
            )
        );

    st_astext
    ---------
    LINESTRING(50 150,50 200)


    --When used in 3d doesn't quite do the right thing
    SELECT ST_AsEWKT(ST_Difference(ST_GeomFromEWKT('MULTIPOINT(-118.58 38.38 5,-118.60 38.329 6,-118.614 38.281 7)'), ST_GeomFromEWKT('POINT(-118.614 38.281 5)')));
    st_asewkt
    ---------
    MULTIPOINT(-118.6 38.329 6,-118.58 38.38 5)
            

See Also
--------

?

ST\_Dump Returns a set of geometry\_dump (geom,path) rows, that make up
a geometry g1. geometry\_dump[] ST\_Dump geometry g1 Description
-----------

This is a set-returning function (SRF). It returns a set of
geometry\_dump rows, formed by a geometry (geom) and an array of
integers (path). When the input geometry is a simple type
(POINT,LINESTRING,POLYGON) a single record will be returned with an
empty path array and the input geometry as geom. When the input geometry
is a collection or multi it will return a record for each of the
collection components, and the path will express the position of the
component inside the collection.

ST\_Dump is useful for expanding geometries. It is the reverse of a
GROUP BY in that it creates new rows. For example it can be use to
expand MULTIPOLYGONS into POLYGONS.

Enhanced: 2.0.0 support for Polyhedral surfaces, Triangles and TIN was
introduced.

Availability: PostGIS 1.0.0RC1. Requires PostgreSQL 7.3 or higher.

    **Note**

    Prior to 1.3.4, this function crashes if used with geometries that
    contain CURVES. This is fixed in 1.3.4+

CURVE\_SUPPORT

P\_SUPPORT

T\_SUPPORT

Z\_SUPPORT

Standard Examples
-----------------

::

    SELECT sometable.field1, sometable.field1,
          (ST_Dump(sometable.the_geom)).geom AS the_geom
    FROM sometable;

    -- Break a compound curve into its constituent linestrings and circularstrings
    SELECT ST_AsEWKT(a.geom), ST_HasArc(a.geom)
      FROM ( SELECT (ST_Dump(p_geom)).geom AS geom
             FROM (SELECT ST_GeomFromEWKT('COMPOUNDCURVE(CIRCULARSTRING(0 0, 1 1, 1 0),(1 0, 0 1))') AS p_geom) AS b
            ) AS a;
              st_asewkt          | st_hasarc
    -----------------------------+----------
     CIRCULARSTRING(0 0,1 1,1 0) | t
     LINESTRING(1 0,0 1)         | f
    (2 rows)

Polyhedral Surfaces, TIN and Triangle Examples
----------------------------------------------

::

    -- Polyhedral surface example
    -- Break a Polyhedral surface into its faces
    SELECT (a.p_geom).path[1] As path, ST_AsEWKT((a.p_geom).geom) As geom_ewkt
      FROM (SELECT ST_Dump(ST_GeomFromEWKT('POLYHEDRALSURFACE( 
    ((0 0 0, 0 0 1, 0 1 1, 0 1 0, 0 0 0)),  
    ((0 0 0, 0 1 0, 1 1 0, 1 0 0, 0 0 0)), ((0 0 0, 1 0 0, 1 0 1, 0 0 1, 0 0 0)),  ((1 1 0, 1 1 1, 1 0 1, 1 0 0, 1 1 0)),  
    ((0 1 0, 0 1 1, 1 1 1, 1 1 0, 0 1 0)),  ((0 0 1, 1 0 1, 1 1 1, 0 1 1, 0 0 1)) 
    )') ) AS p_geom )  AS a;

     path |                geom_ewkt
    ------+------------------------------------------
        1 | POLYGON((0 0 0,0 0 1,0 1 1,0 1 0,0 0 0))
        2 | POLYGON((0 0 0,0 1 0,1 1 0,1 0 0,0 0 0))
        3 | POLYGON((0 0 0,1 0 0,1 0 1,0 0 1,0 0 0))
        4 | POLYGON((1 1 0,1 1 1,1 0 1,1 0 0,1 1 0))
        5 | POLYGON((0 1 0,0 1 1,1 1 1,1 1 0,0 1 0))
        6 | POLYGON((0 0 1,1 0 1,1 1 1,0 1 1,0 0 1))

    -- TIN --       
    SELECT (g.gdump).path, ST_AsEWKT((g.gdump).geom) as wkt
      FROM
        (SELECT 
           ST_Dump( ST_GeomFromEWKT('TIN (((
                    0 0 0, 
                    0 0 1, 
                    0 1 0, 
                    0 0 0
                )), ((
                    0 0 0, 
                    0 1 0, 
                    1 1 0, 
                    0 0 0
                ))
                )') ) AS gdump
        ) AS g;
    -- result --
     path |                 wkt
    ------+-------------------------------------
     {1}  | TRIANGLE((0 0 0,0 0 1,0 1 0,0 0 0))
     {2}  | TRIANGLE((0 0 0,0 1 0,1 1 0,0 0 0))

See Also
--------

?, ?, ?, ?, ?

ST\_DumpPoints Returns a set of geometry\_dump (geom,path) rows of all
points that make up a geometry. geometry\_dump[] ST\_DumpPoints geometry
geom Description -----------

This set-returning function (SRF) returns a set of ``geometry_dump``
rows formed by a geometry (``geom``) and an array of integers
(``path``).

The ``geom`` component of ``geometry_dump`` are all the ``POINT``\ s
that make up the supplied geometry

The ``path`` component of ``geometry_dump`` (an ``integer[]``) is an
index reference enumerating the ``POINT``\ s of the supplied geometry.
For example, if a ``LINESTRING`` is supplied, a path of ``{i}`` is
returned where ``i`` is the ``nth`` coordinate in the ``LINESTRING``. If
a ``POLYGON`` is supplied, a path of ``{i,j}`` is returned where ``i``
is the ring number (1 is outer; inner rings follow) and ``j`` enumerates
the ``POINT``\ s (again 1-based index).

Enhanced: 2.1.0 Faster speed. Reimplemented as native-C.

Enhanced: 2.0.0 support for Polyhedral surfaces, Triangles and TIN was
introduced.

Availability: 1.5.0

CURVE\_SUPPORT

P\_SUPPORT

T\_SUPPORT

Z\_SUPPORT

Classic Explode a Table of LineStrings into nodes
-------------------------------------------------

::

    SELECT edge_id, (dp).path[1] As index, ST_AsText((dp).geom) As wktnode
    FROM (SELECT 1 As edge_id
        , ST_DumpPoints(ST_GeomFromText('LINESTRING(1 2, 3 4, 10 10)')) AS dp
         UNION ALL
         SELECT 2 As edge_id
        , ST_DumpPoints(ST_GeomFromText('LINESTRING(3 5, 5 6, 9 10)')) AS dp
       ) As foo;
     edge_id | index |    wktnode
    ---------+-------+--------------
           1 |     1 | POINT(1 2)
           1 |     2 | POINT(3 4)
           1 |     3 | POINT(10 10)
           2 |     1 | POINT(3 5)
           2 |     2 | POINT(5 6)
           2 |     3 | POINT(9 10)

Standard Geometry Examples
--------------------------

.. figure:: images/st_dumppoints01.png
   :alt: 

::

    SELECT path, ST_AsText(geom) 
    FROM (
      SELECT (ST_DumpPoints(g.geom)).* 
      FROM
        (SELECT 
           'GEOMETRYCOLLECTION(
              POINT ( 0 1 ), 
              LINESTRING ( 0 3, 3 4 ),
              POLYGON (( 2 0, 2 3, 0 2, 2 0 )),
              POLYGON (( 3 0, 3 3, 6 3, 6 0, 3 0 ), 
                       ( 5 1, 4 2, 5 2, 5 1 )),
              MULTIPOLYGON (
                      (( 0 5, 0 8, 4 8, 4 5, 0 5 ), 
                       ( 1 6, 3 6, 2 7, 1 6 )), 
                      (( 5 4, 5 8, 6 7, 5 4 ))
              )
            )'::geometry AS geom
        ) AS g
      ) j;
      
       path    | st_astext  
    -----------+------------
     {1,1}     | POINT(0 1)
     {2,1}     | POINT(0 3)
     {2,2}     | POINT(3 4)
     {3,1,1}   | POINT(2 0)
     {3,1,2}   | POINT(2 3)
     {3,1,3}   | POINT(0 2)
     {3,1,4}   | POINT(2 0)
     {4,1,1}   | POINT(3 0)
     {4,1,2}   | POINT(3 3)
     {4,1,3}   | POINT(6 3)
     {4,1,4}   | POINT(6 0)
     {4,1,5}   | POINT(3 0)
     {4,2,1}   | POINT(5 1)
     {4,2,2}   | POINT(4 2)
     {4,2,3}   | POINT(5 2)
     {4,2,4}   | POINT(5 1)
     {5,1,1,1} | POINT(0 5)
     {5,1,1,2} | POINT(0 8)
     {5,1,1,3} | POINT(4 8)
     {5,1,1,4} | POINT(4 5)
     {5,1,1,5} | POINT(0 5)
     {5,1,2,1} | POINT(1 6)
     {5,1,2,2} | POINT(3 6)
     {5,1,2,3} | POINT(2 7)
     {5,1,2,4} | POINT(1 6)
     {5,2,1,1} | POINT(5 4)
     {5,2,1,2} | POINT(5 8)
     {5,2,1,3} | POINT(6 7)
     {5,2,1,4} | POINT(5 4)
    (29 rows)

Polyhedral Surfaces, TIN and Triangle Examples
----------------------------------------------

::

    -- Polyhedral surface cube --       
    SELECT (g.gdump).path, ST_AsEWKT((g.gdump).geom) as wkt
      FROM
        (SELECT 
           ST_DumpPoints(ST_GeomFromEWKT('POLYHEDRALSURFACE( ((0 0 0, 0 0 1, 0 1 1, 0 1 0, 0 0 0)), 
    ((0 0 0, 0 1 0, 1 1 0, 1 0 0, 0 0 0)), ((0 0 0, 1 0 0, 1 0 1, 0 0 1, 0 0 0)), 
    ((1 1 0, 1 1 1, 1 0 1, 1 0 0, 1 1 0)), 
    ((0 1 0, 0 1 1, 1 1 1, 1 1 0, 0 1 0)), ((0 0 1, 1 0 1, 1 1 1, 0 1 1, 0 0 1)) )') ) AS gdump
        ) AS g;
    -- result --
      path   |     wkt
    ---------+--------------
     {1,1,1} | POINT(0 0 0)
     {1,1,2} | POINT(0 0 1)
     {1,1,3} | POINT(0 1 1)
     {1,1,4} | POINT(0 1 0)
     {1,1,5} | POINT(0 0 0)
     {2,1,1} | POINT(0 0 0)
     {2,1,2} | POINT(0 1 0)
     {2,1,3} | POINT(1 1 0)
     {2,1,4} | POINT(1 0 0)
     {2,1,5} | POINT(0 0 0)
     {3,1,1} | POINT(0 0 0)
     {3,1,2} | POINT(1 0 0)
     {3,1,3} | POINT(1 0 1)
     {3,1,4} | POINT(0 0 1)
     {3,1,5} | POINT(0 0 0)
     {4,1,1} | POINT(1 1 0)
     {4,1,2} | POINT(1 1 1)
     {4,1,3} | POINT(1 0 1)
     {4,1,4} | POINT(1 0 0)
     {4,1,5} | POINT(1 1 0)
     {5,1,1} | POINT(0 1 0)
     {5,1,2} | POINT(0 1 1)
     {5,1,3} | POINT(1 1 1)
     {5,1,4} | POINT(1 1 0)
     {5,1,5} | POINT(0 1 0)
     {6,1,1} | POINT(0 0 1)
     {6,1,2} | POINT(1 0 1)
     {6,1,3} | POINT(1 1 1)
     {6,1,4} | POINT(0 1 1)
     {6,1,5} | POINT(0 0 1)
    (30 rows)

    -- Triangle --      
    SELECT (g.gdump).path, ST_AsText((g.gdump).geom) as wkt
      FROM
        (SELECT 
           ST_DumpPoints( ST_GeomFromEWKT('TRIANGLE ((
                    0 0, 
                    0 9, 
                    9 0, 
                    0 0
                ))') ) AS gdump
        ) AS g;
    -- result --
     path |    wkt
    ------+------------
     {1}  | POINT(0 0)
     {2}  | POINT(0 9)
     {3}  | POINT(9 0)
     {4}  | POINT(0 0)

    -- TIN --       
    SELECT (g.gdump).path, ST_AsEWKT((g.gdump).geom) as wkt
      FROM
        (SELECT 
           ST_DumpPoints( ST_GeomFromEWKT('TIN (((
                    0 0 0, 
                    0 0 1, 
                    0 1 0, 
                    0 0 0
                )), ((
                    0 0 0, 
                    0 1 0, 
                    1 1 0, 
                    0 0 0
                ))
                )') ) AS gdump
        ) AS g;
    -- result --
      path   |     wkt
    ---------+--------------
     {1,1,1} | POINT(0 0 0)
     {1,1,2} | POINT(0 0 1)
     {1,1,3} | POINT(0 1 0)
     {1,1,4} | POINT(0 0 0)
     {2,1,1} | POINT(0 0 0)
     {2,1,2} | POINT(0 1 0)
     {2,1,3} | POINT(1 1 0)
     {2,1,4} | POINT(0 0 0)
    (8 rows)

See Also
--------

?, ?, ?, ?

ST\_DumpRings Returns a set of geometry\_dump rows, representing the
exterior and interior rings of a polygon. geometry\_dump[] ST\_DumpRings
geometry a\_polygon Description -----------

This is a set-returning function (SRF). It returns a set of
``geometry_dump`` rows, defined as an ``integer[]`` and a ``geometry``,
aliased "path" and "geom" respectively. The "path" field holds the
polygon ring index containing a single integer: 0 for the shell, >0 for
holes. The "geom" field contains the corresponding ring as a polygon.

Availability: PostGIS 1.1.3. Requires PostgreSQL 7.3 or higher.

    **Note**

    This only works for POLYGON geometries. It will not work for
    MULTIPOLYGONS

Z\_SUPPORT

Examples
--------

::

    SELECT sometable.field1, sometable.field1,
          (ST_DumpRings(sometable.the_geom)).geom As the_geom
    FROM sometableOfpolys;

    SELECT ST_AsEWKT(geom) As the_geom, path
        FROM ST_DumpRings(
            ST_GeomFromEWKT('POLYGON((-8149064 5133092 1,-8149064 5132986 1,-8148996 5132839 1,-8148972 5132767 1,-8148958 5132508 1,-8148941 5132466 1,-8148924 5132394 1,
            -8148903 5132210 1,-8148930 5131967 1,-8148992 5131978 1,-8149237 5132093 1,-8149404 5132211 1,-8149647 5132310 1,-8149757 5132394 1,
            -8150305 5132788 1,-8149064 5133092 1),
            (-8149362 5132394 1,-8149446 5132501 1,-8149548 5132597 1,-8149695 5132675 1,-8149362 5132394 1))')
            )  as foo;
     path |                                            the_geom
    ----------------------------------------------------------------------------------------------------------------
      {0} | POLYGON((-8149064 5133092 1,-8149064 5132986 1,-8148996 5132839 1,-8148972 5132767 1,-8148958 5132508 1,
          |          -8148941 5132466 1,-8148924 5132394 1,
          |          -8148903 5132210 1,-8148930 5131967 1,
          |          -8148992 5131978 1,-8149237 5132093 1,
          |          -8149404 5132211 1,-8149647 5132310 1,-8149757 5132394 1,-8150305 5132788 1,-8149064 5133092 1))
      {1} | POLYGON((-8149362 5132394 1,-8149446 5132501 1,
          |          -8149548 5132597 1,-8149695 5132675 1,-8149362 5132394 1))

See Also
--------

?, ?, ?, ?, ?

ST\_FlipCoordinates Returns a version of the given geometry with X and Y
axis flipped. Useful for people who have built latitude/longitude
features and need to fix them. geometry ST\_FlipCoordinates geometry
geom Description -----------

Returns a version of the given geometry with X and Y axis flipped.

CURVE\_SUPPORT

Z\_SUPPORT

M\_SUPPORT

Availability: 2.0.0

P\_SUPPORT

T\_SUPPORT

Example
-------

::

    SELECT ST_AsEWKT(ST_FlipCoordinates(GeomFromEWKT('POINT(1 2)')));
     st_asewkt  
    ------------
    POINT(2 1)
             

ST\_Intersection (T) Returns a geometry that represents the shared
portion of geomA and geomB. The geography implementation does a
transform to geometry to do the intersection and then transform back to
WGS84. geometry ST\_Intersection geometry geomA geometry geomB geography
ST\_Intersection geography geogA geography geogB Description -----------

Returns a geometry that represents the point set intersection of the
Geometries.

In other words - that portion of geometry A and geometry B that is
shared between the two geometries.

If the geometries do not share any space (are disjoint), then an empty
geometry collection is returned.

ST\_Intersection in conjunction with ST\_Intersects is very useful for
clipping geometries such as in bounding box, buffer, region queries
where you only want to return that portion of a geometry that sits in a
country or region of interest.

    **Note**

    Geography: For geography this is really a thin wrapper around the
    geometry implementation. It first determines the best SRID that fits
    the bounding box of the 2 geography objects (if geography objects
    are within one half zone UTM but not same UTM will pick one of
    those) (favoring UTM or Lambert Azimuthal Equal Area (LAEA)
    north/south pole, and falling back on mercator in worst case
    scenario) and then intersection in that best fit planar spatial ref
    and retransforms back to WGS84 geography.

    **Important**

    Do not call with a ``GEOMETRYCOLLECTION`` as an argument

Performed by the GEOS module

SFCGAL\_ENHANCED

Availability: 1.5 support for geography data type was introduced.

SFS\_COMPLIANT s2.1.1.3

SQLMM\_COMPLIANT SQL-MM 3: 5.1.18

Examples
--------

::

    SELECT ST_AsText(ST_Intersection('POINT(0 0)'::geometry, 'LINESTRING ( 2 0, 0 2 )'::geometry));
     st_astext
    ---------------
    GEOMETRYCOLLECTION EMPTY
    (1 row)
    SELECT ST_AsText(ST_Intersection('POINT(0 0)'::geometry, 'LINESTRING ( 0 0, 0 2 )'::geometry));
     st_astext
    ---------------
    POINT(0 0)
    (1 row)

    ---Clip all lines (trails) by country (here we assume country geom are POLYGON or MULTIPOLYGONS)
    -- NOTE: we are only keeping intersections that result in a LINESTRING or MULTILINESTRING because we don't
    -- care about trails that just share a point
    -- the dump is needed to expand a geometry collection into individual single MULT* parts
    -- the below is fairly generic and will work for polys, etc. by just changing the where clause
    SELECT clipped.gid, clipped.f_name, clipped_geom
    FROM (SELECT trails.gid, trails.f_name, (ST_Dump(ST_Intersection(country.the_geom, trails.the_geom))).geom As clipped_geom
    FROM country
        INNER JOIN trails
        ON ST_Intersects(country.the_geom, trails.the_geom))  As clipped
        WHERE ST_Dimension(clipped.clipped_geom) = 1 ;

    --For polys e.g. polygon landmarks, you can also use the sometimes faster hack that buffering anything by 0.0
    -- except a polygon results in an empty geometry collection
    --(so a geometry collection containing polys, lines and points)
    -- buffered by 0.0 would only leave the polygons and dissolve the collection shell
    SELECT poly.gid,  ST_Multi(ST_Buffer(
                    ST_Intersection(country.the_geom, poly.the_geom),
                    0.0)
                    ) As clipped_geom
    FROM country
        INNER JOIN poly
        ON ST_Intersects(country.the_geom, poly.the_geom)
        WHERE Not ST_IsEmpty(ST_Buffer(ST_Intersection(country.the_geom, poly.the_geom),0.0));
            

See Also
--------

?, ?, ?, ?, ?, ?

ST\_LineToCurve Converts a LINESTRING/POLYGON to a CIRCULARSTRING,
CURVED POLYGON geometry ST\_LineToCurve geometry geomANoncircular
Description -----------

Converts plain LINESTRING/POLYGONS to CIRCULAR STRINGs and Curved
Polygons. Note much fewer points are needed to describe the curved
equivalent.

Availability: 1.2.2?

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    SELECT ST_AsText(ST_LineToCurve(foo.the_geom)) As curvedastext,ST_AsText(foo.the_geom) As non_curvedastext
        FROM (SELECT ST_Buffer('POINT(1 3)'::geometry, 3) As the_geom) As foo;

    curvedatext                                                            non_curvedastext
    --------------------------------------------------------------------|-----------------------------------------------------------------
    CURVEPOLYGON(CIRCULARSTRING(4 3,3.12132034355964 0.878679656440359, | POLYGON((4 3,3.94235584120969 2.41472903395162,3.77163859753386 1.85194970290473,
    1 0,-1.12132034355965 5.12132034355963,4 3))                        |  3.49440883690764 1.33328930094119,3.12132034355964 0.878679656440359,
                                                                        |  2.66671069905881 0.505591163092366,2.14805029709527 0.228361402466141,
                                                                        |  1.58527096604839 0.0576441587903094,1 0,
                                                                        |  0.414729033951621 0.0576441587903077,-0.148050297095264 0.228361402466137,
                                                                        |  -0.666710699058802 0.505591163092361,-1.12132034355964 0.878679656440353,
                                                                        |  -1.49440883690763 1.33328930094119,-1.77163859753386 1.85194970290472
                                                                        |  --ETC-- ,3.94235584120969 3.58527096604839,4 3))
    --3D example
    SELECT ST_AsEWKT(ST_LineToCurve(ST_GeomFromEWKT('LINESTRING(1 2 3, 3 4 8, 5 6 4, 7 8 4, 9 10 4)')));

                 st_asewkt
    ------------------------------------
     CIRCULARSTRING(1 2 3,5 6 4,9 10 4)

See Also
--------

?

ST\_MakeValid Attempts to make an invalid geometry valid without losing
vertices. geometry ST\_MakeValid geometry input Description -----------

The function attempts to create a valid representation of a given
invalid geometry without losing any of the input vertices. Already-valid
geometries are returned without further intervention.

Supported inputs are: POINTS, MULTIPOINTS, LINESTRINGS,
MULTILINESTRINGS, POLYGONS, MULTIPOLYGONS and GEOMETRYCOLLECTIONS
containing any mix of them.

In case of full or partial dimensional collapses, the output geometry
may be a collection of lower-to-equal dimension geometries or a geometry
of lower dimension.

Single polygons may become multi-geometries in case of
self-intersections.

Availability: 2.0.0, requires GEOS-3.3.0

Enhanced: 2.0.1, speed improvements requires GEOS-3.3.4

Enhanced: 2.1.0 added support for GEOMETRYCOLLECTION and MULTIPOINT.

Z\_SUPPORT

See Also
--------

? ?

ST\_MemUnion Same as ST\_Union, only memory-friendly (uses less memory
and more processor time). geometry ST\_MemUnion geometry set geomfield
Description -----------

Some useful description here.

    **Note**

    Same as ST\_Union, only memory-friendly (uses less memory and more
    processor time). This aggregate function works by unioning the
    geometries one at a time to previous result as opposed to ST\_Union
    aggregate which first creates an array and then unions

Z\_SUPPORT

Examples
--------

::

    See ST_Union

See Also
--------

?

ST\_MinimumBoundingCircle Returns the smallest circle polygon that can
fully contain a geometry. Default uses 48 segments per quarter circle.
geometry ST\_MinimumBoundingCircle geometry geomA integer
num\_segs\_per\_qt\_circ=48 Description -----------

Returns the smallest circle polygon that can fully contain a geometry.

    **Note**

    The circle is approximated by a polygon with a default of 48
    segments per quarter circle. This number can be increased with
    little performance penalty to obtain a more accurate result.

It is often used with MULTI and Geometry Collections. Although it is not
an aggregate - you can use it in conjunction with ST\_Collect to get the
minimum bounding circle of a set of geometries.
ST\_MinimumBoundingCircle(ST\_Collect(somepointfield)).

The ratio of the area of a polygon divided by the area of its Minimum
Bounding Circle is often referred to as the Roeck test.

Availability: 1.4.0 - requires GEOS

Examples
--------

::

    SELECT d.disease_type,
        ST_MinimumBoundingCircle(ST_Collect(d.the_geom)) As the_geom
        FROM disease_obs As d
        GROUP BY d.disease_type;

.. figure:: images/st_minimumboundingcircle01.png
   :alt: Minimum bounding circle of a point and linestring. Using 8 segs
   to approximate a quarter circle

   Minimum bounding circle of a point and linestring. Using 8 segs to
   approximate a quarter circle
::

    SELECT ST_AsText(ST_MinimumBoundingCircle(
            ST_Collect(
                ST_GeomFromEWKT('LINESTRING(55 75,125 150)'),
                    ST_Point(20, 80)), 8
                    )) As wktmbc;
    wktmbc
    -----------
    POLYGON((135.59714732062 115,134.384753327498 102.690357210921,130.79416296937 90.8537670908995,124.963360620072 79.9451031602111,117.116420743937 70.3835792560632,107.554896839789 62.5366393799277,96.6462329091006 56.70583703063,84.8096427890789 53.115246672502,72.5000000000001 51.9028526793802,60.1903572109213 53.1152466725019,48.3537670908996 56.7058370306299,37.4451031602112 62.5366393799276,27.8835792560632 70.383579256063,20.0366393799278 79.9451031602109,14.20583703063 90.8537670908993,10.615246672502 102.690357210921,9.40285267938019 115,10.6152466725019 127.309642789079,14.2058370306299 139.1462329091,20.0366393799275 150.054896839789,27.883579256063 159.616420743937,
    37.4451031602108 167.463360620072,48.3537670908992 173.29416296937,60.190357210921 176.884753327498,
    72.4999999999998 178.09714732062,84.8096427890786 176.884753327498,96.6462329091003 173.29416296937,107.554896839789 167.463360620072,
    117.116420743937 159.616420743937,124.963360620072 150.054896839789,130.79416296937 139.146232909101,134.384753327498 127.309642789079,135.59714732062 115))
                    

See Also
--------

?, ?

ST\_Polygonize Aggregate. Creates a GeometryCollection containing
possible polygons formed from the constituent linework of a set of
geometries. geometry ST\_Polygonize geometry set geomfield geometry
ST\_Polygonize geometry[] geom\_array Description -----------

Creates a GeometryCollection containing possible polygons formed from
the constituent linework of a set of geometries.

    **Note**

    Geometry Collections are often difficult to deal with with third
    party tools, so use ST\_Polygonize in conjunction with ? to dump the
    polygons out into individual polygons.

    **Note**

    Input linework must be correctly noded for this function to work
    properly

Availability: 1.0.0RC1 - requires GEOS >= 2.1.0.

Examples: Polygonizing single linestrings
-----------------------------------------

::

    SELECT ST_AsEWKT(ST_Polygonize(the_geom_4269)) As geomtextrep
    FROM (SELECT the_geom_4269 FROM ma.suffolk_edges ORDER BY tlid LIMIT 45) As foo;

    geomtextrep
    -------------------------------------
     SRID=4269;GEOMETRYCOLLECTION(POLYGON((-71.040878 42.285678,-71.040943 42.2856,-71.04096 42.285752,-71.040878 42.285678)),
     POLYGON((-71.17166 42.353675,-71.172026 42.354044,-71.17239 42.354358,-71.171794 42.354971,-71.170511 42.354855,
     -71.17112 42.354238,-71.17166 42.353675)))
    (1 row)

    --Use ST_Dump to dump out the polygonize geoms into individual polygons
    SELECT ST_AsEWKT((ST_Dump(foofoo.polycoll)).geom) As geomtextrep
    FROM (SELECT ST_Polygonize(the_geom_4269) As polycoll
        FROM (SELECT the_geom_4269 FROM ma.suffolk_edges
            ORDER BY tlid LIMIT 45) As foo) As foofoo;

    geomtextrep
    ------------------------
     SRID=4269;POLYGON((-71.040878 42.285678,-71.040943 42.2856,-71.04096 42.285752,
    -71.040878 42.285678))
     SRID=4269;POLYGON((-71.17166 42.353675,-71.172026 42.354044,-71.17239 42.354358
    ,-71.171794 42.354971,-71.170511 42.354855,-71.17112 42.354238,-71.17166 42.353675))
    (2 rows)

See Also
--------

?, ?

ST\_Node Node a set of linestrings. geometry ST\_Node geometry geom
Description -----------

Fully node a set of linestrings using the least possible number of nodes
while preserving all of the input ones.

Z\_SUPPORT

Availability: 2.0.0 - requires GEOS >= 3.3.0.

    **Note**

    Due to a bug in GEOS up to 3.3.1 this function fails to node
    self-intersecting lines. This is fixed with GEOS 3.3.2 or higher.

Examples
--------

::

    SELECT ST_AsEWKT(
            ST_Node('LINESTRINGZ(0 0 0, 10 10 10, 0 10 5, 10 0 3)'::geometry)
        ) As  output;
    output
    -----------
    MULTILINESTRING((0 0 0,5 5 4.5),(5 5 4.5,10 10 10,0 10 5,5 5 4.5),(5 5 4.5,10 0 3)) 
            

See Also
--------

?

ST\_OffsetCurve Return an offset line at a given distance and side from
an input line. Useful for computing parallel lines about a center line
geometry ST\_OffsetCurve geometry line float signed\_distance text
style\_parameters='' Description -----------

Return an offset line at a given distance and side from an input line.
All points of the returned geometries are not further than the given
distance from the input geometry.

For positive distance the offset will be at the left side of the input
line and retain the same direction. For a negative distance it'll be at
the right side and in the opposite direction.

Availability: 2.0 - requires GEOS >= 3.2, improved with GEOS >= 3.3

The optional third parameter allows specifying a list of blank-separated
key=value pairs to tweak operations as follows:

-  'quad\_segs=#' : number of segments used to approximate a quarter
   circle (defaults to 8).

-  'join=round\|mitre\|bevel' : join style (defaults to "round").
   'miter' is also accepted as a synonym for 'mitre'.

-  'mitre\_limit=#.#' : mitre ratio limit (only affects mitred join
   style). 'miter\_limit' is also accepted as a synonym for
   'mitre\_limit'.

Units of distance are measured in units of the spatial reference system.

The inputs can only be LINESTRINGS.

Performed by the GEOS module.

    **Note**

    This function ignores the third dimension (z) and will always give a
    2-d result even when presented with a 3d-geometry.

Examples
--------

Compute an open buffer around roads

::

    SELECT ST_Union(
     ST_OffsetCurve(f.the_geom,  f.width/2, 'quad_segs=4 join=round'),
     ST_OffsetCurve(f.the_geom, -f.width/2, 'quad_segs=4 join=round')
    ) as track
    FROM someroadstable;

+---------------------------------+-----------------------------------------+
| 15, 'quad\_segs=4 join=round'   | -15, 'quad\_segs=4 join=round' original |
| original line and its offset 15 | line and its offset -15 units           |
| units.                          |                                         |
+---------------------------------+-----------------------------------------+
| SELECT                          | SELECT ST\_AsText(ST\_OffsetCurve(geom, |
| ST\_AsText(ST\_OffsetCurve(ST\_ | -15, 'quad\_segs=4 join=round')) As     |
| GeomFromText(                   | notsocurvy FROM ST\_GeomFromText(       |
| 'LINESTRING(164 16,144 16,124   | 'LINESTRING(164 16,144 16,124 16,104    |
| 16,104 16,84 16,64 16, 44 16,24 | 16,84 16,64 16, 44 16,24 16,20 16,18    |
| 16,20 16,18 16,17 17, 16 18,16  | 16,17 17, 16 18,16 20,16 40,16 60,16    |
| 20,16 40,16 60,16 80,16 100, 16 | 80,16 100, 16 120,16 140,16 160,16      |
| 120,16 140,16 160,16 180,16     | 180,16 195)') As geom; -- notsocurvy -- |
| 195)'), 15, 'quad\_segs=4       | LINESTRING(31 195,31 31,164 31)         |
| join=round')); --output --      |                                         |
| LINESTRING(164 1,18             |                                         |
| 1,12.2597485145237              |                                         |
| 2.1418070123307,                |                                         |
| 7.39339828220179                |                                         |
| 5.39339828220179,               |                                         |
| 5.39339828220179                |                                         |
| 7.39339828220179,               |                                         |
| 2.14180701233067                |                                         |
| 12.2597485145237,1 18,1 195)    |                                         |
+---------------------------------+-----------------------------------------+
| double-offset to get more       | double-offset to get more               |
| curvy, note the first reverses  | curvy,combined with regular offset 15   |
| direction, so -30 + 15 = -15    | to get parallel lines. Overlaid with    |
|                                 | original.                               |
+---------------------------------+-----------------------------------------+
| SELECT                          | SELECT ST\_AsText(ST\_Collect(          |
| ST\_AsText(ST\_OffsetCurve(ST\_ | ST\_OffsetCurve(geom, 15, 'quad\_segs=4 |
| OffsetCurve(geom,               | join=round'),                           |
| -30, 'quad\_segs=4              | ST\_OffsetCurve(ST\_OffsetCurve(geom,   |
| join=round'), -15,              | -30, 'quad\_segs=4 join=round'), -15,   |
| 'quad\_segs=4 join=round')) As  | 'quad\_segs=4 join=round') ) ) As       |
| morecurvy FROM                  | parallel\_curves FROM ST\_GeomFromText( |
| ST\_GeomFromText(               | 'LINESTRING(164 16,144 16,124 16,104    |
| 'LINESTRING(164 16,144 16,124   | 16,84 16,64 16, 44 16,24 16,20 16,18    |
| 16,104 16,84 16,64 16, 44 16,24 | 16,17 17, 16 18,16 20,16 40,16 60,16    |
| 16,20 16,18 16,17 17, 16 18,16  | 80,16 100, 16 120,16 140,16 160,16      |
| 20,16 40,16 60,16 80,16 100, 16 | 180,16 195)') As geom; -- parallel      |
| 120,16 140,16 160,16 180,16     | curves -- MULTILINESTRING((164 1,18     |
| 195)') As geom; -- morecurvy -- | 1,12.2597485145237 2.1418070123307,     |
| LINESTRING(164 31,46            | 7.39339828220179                        |
| 31,40.2597485145236             | 5.39339828220179,5.39339828220179       |
| 32.1418070123307,               | 7.39339828220179, 2.14180701233067      |
| 35.3933982822018                | 12.2597485145237,1 18,1 195), (164      |
| 35.3933982822018,               | 31,46 31,40.2597485145236               |
| 32.1418070123307                | 32.1418070123307,35.3933982822018       |
| 40.2597485145237,31 46,31 195)  | 35.3933982822018, 32.1418070123307      |
|                                 | 40.2597485145237,31 46,31 195))         |
+---------------------------------+-----------------------------------------+
| 15, 'quad\_segs=4 join=bevel'   | 15,-15 collected, join=mitre            |
| shown with original line        | mitre\_limit=2.1                        |
+---------------------------------+-----------------------------------------+
| SELECT                          | SELECT ST\_AsText(ST\_Collect(          |
| ST\_AsText(ST\_OffsetCurve(ST\_ | ST\_OffsetCurve(geom, 15, 'quad\_segs=4 |
| GeomFromText(                   | join=mitre mitre\_limit=2.2'),          |
| 'LINESTRING(164 16,144 16,124   | ST\_OffsetCurve(geom, -15,              |
| 16,104 16,84 16,64 16, 44 16,24 | 'quad\_segs=4 join=mitre                |
| 16,20 16,18 16,17 17, 16 18,16  | mitre\_limit=2.2') ) ) FROM             |
| 20,16 40,16 60,16 80,16 100, 16 | ST\_GeomFromText( 'LINESTRING(164       |
| 120,16 140,16 160,16 180,16     | 16,144 16,124 16,104 16,84 16,64 16, 44 |
| 195)'), 15, 'quad\_segs=4       | 16,24 16,20 16,18 16,17 17, 16 18,16    |
| join=bevel')); -- output --     | 20,16 40,16 60,16 80,16 100, 16 120,16  |
| LINESTRING(164 1,18             | 140,16 160,16 180,16 195)') As geom; -- |
| 1,7.39339828220179              | output -- MULTILINESTRING((164          |
| 5.39339828220179,               | 1,11.7867965644036 1,1                  |
| 5.39339828220179                | 11.7867965644036,1 195), (31 195,31     |
| 7.39339828220179,1 18,1 195)    | 31,164 31))                             |
+---------------------------------+-----------------------------------------+

See Also
--------

?

ST\_RemoveRepeatedPoints Returns a version of the given geometry with
duplicated points removed. geometry ST\_RemoveRepeatedPoints geometry
geom Description -----------

Returns a version of the given geometry with duplicated points removed.
Will actually do something only with (multi)lines, (multi)polygons and
multipoints but you can safely call it with any kind of geometry. Since
simplification occurs on a object-by-object basis you can also feed a
GeometryCollection to this function.

Availability: 2.0.0

P\_SUPPORT

Z\_SUPPORT

See Also
--------

?

ST\_SharedPaths Returns a collection containing paths shared by the two
input linestrings/multilinestrings. geometry ST\_SharedPaths geometry
lineal1 geometry lineal2 Description -----------

Returns a collection containing paths shared by the two input
geometries. Those going in the same direction are in the first element
of the collection, those going in the opposite direction are in the
second element. The paths themselves are given in the direction of the
first geometry.

Availability: 2.0.0 requires GEOS >= 3.3.0.

Examples: Finding shared paths
------------------------------

+------------------------------------------------------------------------+
| A multilinestring and a linestring                                     |
+------------------------------------------------------------------------+
| The shared path of multilinestring and linestring overlaid with        |
| original geometries.                                                   |
+------------------------------------------------------------------------+
| SELECT ST\_AsText( ST\_SharedPaths(                                    |
| ST\_GeomFromText('MULTILINESTRING((26 125,26 200,126 200,126 125,26    |
| 125), (51 150,101 150,76 175,51 150))'),                               |
| ST\_GeomFromText('LINESTRING(151 100,126 156.25,126 125,90 161, 76     |
| 175)') ) ) As wkt                                                      |
+------------------------------------------------------------------------+
| wkt -------------------------------------------------------------      |
| GEOMETRYCOLLECTION(MULTILINESTRING((126 156.25,126 125), (101 150,90   |
| 161),(90 161,76 175)),MULTILINESTRING EMPTY)                           |
+------------------------------------------------------------------------+
| -- same example but linestring orientation flipped SELECT ST\_AsText(  |
| ST\_SharedPaths( ST\_GeomFromText('LINESTRING(76 175,90 161,126        |
| 125,126 156.25,151 100)'), ST\_GeomFromText('MULTILINESTRING((26       |
| 125,26 200,126 200,126 125,26 125), (51 150,101 150,76 175,51 150))')  |
| ) ) As wkt                                                             |
+------------------------------------------------------------------------+
| wkt -------------------------------------------------------------      |
| GEOMETRYCOLLECTION(MULTILINESTRING EMPTY, MULTILINESTRING((76 175,90   |
| 161),(90 161,101 150),(126 125,126 156.25)))                           |
+------------------------------------------------------------------------+

See Also
--------

?, ?, ?

ST\_Shift\_Longitude Reads every point/vertex in every component of
every feature in a geometry, and if the longitude coordinate is <0, adds
360 to it. The result would be a 0-360 version of the data to be plotted
in a 180 centric map geometry ST\_Shift\_Longitude geometry geomA
Description -----------

Reads every point/vertex in every component of every feature in a
geometry, and if the longitude coordinate is <0, adds 360 to it. The
result would be a 0-360 version of the data to be plotted in a 180
centric map

    **Note**

    This is only useful for data in long lat e.g. 4326 (WGS 84 long lat)

Pre-1.3.4 bug prevented this from working for MULTIPOINT. 1.3.4+ works
with MULTIPOINT as well.

Z\_SUPPORT

Enhanced: 2.0.0 support for Polyhedral surfaces and TIN was introduced.

P\_SUPPORT

T\_SUPPORT

Examples
--------

::

    --3d points
    SELECT ST_AsEWKT(ST_Shift_Longitude(ST_GeomFromEWKT('SRID=4326;POINT(-118.58 38.38 10)'))) As geomA,
        ST_AsEWKT(ST_Shift_Longitude(ST_GeomFromEWKT('SRID=4326;POINT(241.42 38.38 10)'))) As geomb
    geomA                             geomB
    ----------                        -----------
    SRID=4326;POINT(241.42 38.38 10) SRID=4326;POINT(-118.58 38.38 10)

    --regular line string
    SELECT ST_AsText(ST_Shift_Longitude(ST_GeomFromText('LINESTRING(-118.58 38.38, -118.20 38.45)')))

    st_astext
    ----------
    LINESTRING(241.42 38.38,241.8 38.45)
            

See Also
--------

?, ?, ?

ST\_Simplify Returns a "simplified" version of the given geometry using
the Douglas-Peucker algorithm. geometry ST\_Simplify geometry geomA
float tolerance Description -----------

Returns a "simplified" version of the given geometry using the
Douglas-Peucker algorithm. Will actually do something only with
(multi)lines and (multi)polygons but you can safely call it with any
kind of geometry. Since simplification occurs on a object-by-object
basis you can also feed a GeometryCollection to this function.

    **Note**

    Note that returned geometry might loose its simplicity (see ?)

    **Note**

    Note topology may not be preserved and may result in invalid
    geometries. Use (see ?) to preserve topology.

Performed by the GEOS module.

Availability: 1.2.2

Examples
--------

A circle simplified too much becomes a triangle, medium an octagon,

::

    SELECT ST_Npoints(the_geom) As np_before, ST_NPoints(ST_Simplify(the_geom,0.1)) As np01_notbadcircle, ST_NPoints(ST_Simplify(the_geom,0.5)) As np05_notquitecircle,
    ST_NPoints(ST_Simplify(the_geom,1)) As np1_octagon, ST_NPoints(ST_Simplify(the_geom,10)) As np10_triangle,
    (ST_Simplify(the_geom,100) is null) As  np100_geometrygoesaway
    FROM (SELECT ST_Buffer('POINT(1 3)', 10,12) As the_geom) As foo;
    -result
     np_before | np01_notbadcircle | np05_notquitecircle | np1_octagon | np10_triangle | np100_geometrygoesaway
    -----------+-------------------+---------------------+-------------+---------------+------------------------
            49 |                33 |                  17 |           9 |             4 | t

See Also
--------

?, ?, Topology ?

ST\_SimplifyPreserveTopology Returns a "simplified" version of the given
geometry using the Douglas-Peucker algorithm. Will avoid creating
derived geometries (polygons in particular) that are invalid. geometry
ST\_SimplifyPreserveTopology geometry geomA float tolerance Description
-----------

Returns a "simplified" version of the given geometry using the
Douglas-Peucker algorithm. Will avoid creating derived geometries
(polygons in particular) that are invalid. Will actually do something
only with (multi)lines and (multi)polygons but you can safely call it
with any kind of geometry. Since simplification occurs on a
object-by-object basis you can also feed a GeometryCollection to this
function.

Performed by the GEOS module.

    **Note**

    Requires GEOS 3.0.0+

Availability: 1.3.3

Examples
--------

Same example as Simplify, but we see Preserve Topology prevents
oversimplification. The circle can at most become a square.

::

    SELECT ST_Npoints(the_geom) As np_before, ST_NPoints(ST_SimplifyPreserveTopology(the_geom,0.1)) As np01_notbadcircle, ST_NPoints(ST_SimplifyPreserveTopology(the_geom,0.5)) As np05_notquitecircle,
    ST_NPoints(ST_SimplifyPreserveTopology(the_geom,1)) As np1_octagon, ST_NPoints(ST_SimplifyPreserveTopology(the_geom,10)) As np10_square,
    ST_NPoints(ST_SimplifyPreserveTopology(the_geom,100)) As  np100_stillsquare
    FROM (SELECT ST_Buffer('POINT(1 3)', 10,12) As the_geom) As foo;

    --result--
     np_before | np01_notbadcircle | np05_notquitecircle | np1_octagon | np10_square | np100_stillsquare
    -----------+-------------------+---------------------+-------------+---------------+-------------------
            49 |                33 |                  17 |           9 |             5 |                 5
                    

See Also
--------

?

ST\_Split Returns a collection of geometries resulting by splitting a
geometry. geometry ST\_Split geometry input geometry blade Description
-----------

The function supports splitting a line by point, a line by line, a
polygon by line. The returned geometry is always a collection.

Think of this function as the opposite of ST\_Union. Theoretically
applying ST\_Union to the elements of the returned collection should
always yield the original geometry.

Availability: 2.0.0

    **Note**

    To improve the robustness of ST\_Split it may be convenient to ? the
    input to the blade in advance using a very low tolerance. Otherwise
    the internally used coordinate grid may cause tolerance problems,
    where coordinates of input and blade do not fall onto each other and
    the input is not being split correctly (see
    `#2192 <http://trac.osgeo.org/postgis/ticket/2192>`__).

Examples
--------

Polygon Cut by Line

+----------------+---------------+
| Before Split   | After split   |
+----------------+---------------+

::

    -- this creates a geometry collection consisting of the 2 halves of the polygon
    -- this is similar to the example we demonstrated in ST_BuildArea
    SELECT ST_Split(circle, line)
    FROM (SELECT 
        ST_MakeLine(ST_MakePoint(10, 10),ST_MakePoint(190, 190)) As line,
        ST_Buffer(ST_GeomFromText('POINT(100 90)'), 50) As circle) As foo;
        
    -- result --
     GEOMETRYCOLLECTION(POLYGON((150 90,149.039264020162 80.2454838991936,146.193976625564 70.8658283817455,..), POLYGON(..)))
     
    -- To convert to individual polygons, you can use ST_Dump or ST_GeometryN
    SELECT ST_AsText((ST_Dump(ST_Split(circle, line))).geom) As wkt
    FROM (SELECT 
        ST_MakeLine(ST_MakePoint(10, 10),ST_MakePoint(190, 190)) As line,
        ST_Buffer(ST_GeomFromText('POINT(100 90)'), 50) As circle) As foo;
        
    -- result --
    wkt
    ---------------
    POLYGON((150 90,149.039264020162 80.2454838991936,..))
    POLYGON((60.1371179574584 60.1371179574584,58.4265193848728 62.2214883490198,53.8060233744357 ..))
                

Multilinestring Cut by point

+----------------+---------------+
| Before Split   | After split   |
+----------------+---------------+

::

    SELECT ST_AsText(ST_Split(mline, pt)) As wktcut
            FROM (SELECT 
        ST_GeomFromText('MULTILINESTRING((10 10, 190 190), (15 15, 30 30, 100 90))') As mline,
        ST_Point(30,30) As pt) As foo;
        
    wktcut
    ------
    GEOMETRYCOLLECTION(
        LINESTRING(10 10,30 30),
        LINESTRING(30 30,190 190),
        LINESTRING(15 15,30 30),
        LINESTRING(30 30,100 90)
    )
                

See Also
--------

?, ?, ?, ?, ?

ST\_SymDifference Returns a geometry that represents the portions of A
and B that do not intersect. It is called a symmetric difference because
ST\_SymDifference(A,B) = ST\_SymDifference(B,A). geometry
ST\_SymDifference geometry geomA geometry geomB Description -----------

Returns a geometry that represents the portions of A and B that do not
intersect. It is called a symmetric difference because
ST\_SymDifference(A,B) = ST\_SymDifference(B,A). One can think of this
as ST\_Union(geomA,geomB) - ST\_Intersection(A,B).

Performed by the GEOS module

    **Note**

    Do not call with a GeometryCollection as an argument

SFS\_COMPLIANT s2.1.1.3

SQLMM\_COMPLIANT SQL-MM 3: 5.1.21

Z\_SUPPORT However it seems to only consider x y when doing the
difference and tacks back on the Z-Index

Examples
--------

+-------------------------------------------+---------------------------------------------------+
| The original linestrings shown together   | The symmetric difference of the two linestrings   |
+-------------------------------------------+---------------------------------------------------+

::

    --Safe for 2d - symmetric difference of 2 linestrings
    SELECT ST_AsText(
        ST_SymDifference(
            ST_GeomFromText('LINESTRING(50 100, 50 200)'),
            ST_GeomFromText('LINESTRING(50 50, 50 150)')
        )
    );

    st_astext
    ---------
    MULTILINESTRING((50 150,50 200),(50 50,50 100))


    --When used in 3d doesn't quite do the right thing
    SELECT ST_AsEWKT(ST_SymDifference(ST_GeomFromEWKT('LINESTRING(1 2 1, 1 4 2)'),
        ST_GeomFromEWKT('LINESTRING(1 1 3, 1 3 4)')))

    st_astext
    ------------
    MULTILINESTRING((1 3 2.75,1 4 2),(1 1 3,1 2 2.25))
            

See Also
--------

?, ?, ?

ST\_Union Returns a geometry that represents the point set union of the
Geometries. geometry ST\_Union geometry set g1field geometry ST\_Union
geometry g1 geometry g2 geometry ST\_Union geometry[] g1\_array
Description -----------

Output type can be a MULTI\*, single geometry, or Geometry Collection.
Comes in 2 variants. Variant 1 unions 2 geometries resulting in a new
geometry with no intersecting regions. Variant 2 is an aggregate
function that takes a set of geometries and unions them into a single
ST\_Geometry resulting in no intersecting regions.

Aggregate version: This function returns a MULTI geometry or NON-MULTI
geometry from a set of geometries. The ST\_Union() function is an
"aggregate" function in the terminology of PostgreSQL. That means that
it operates on rows of data, in the same way the SUM() and AVG()
functions do and like most aggregates, it also ignores NULL geometries.

Non-Aggregate version: This function returns a geometry being a union of
two input geometries. Output type can be a MULTI\*, NON-MULTI or
GEOMETRYCOLLECTION. If any are NULL, then NULL is returned.

    **Note**

    ST\_Collect and ST\_Union are often interchangeable. ST\_Union is in
    general orders of magnitude slower than ST\_Collect because it tries
    to dissolve boundaries and reorder geometries to ensure that a
    constructed Multi\* doesn't have intersecting regions.

Performed by the GEOS module.

NOTE: this function was formerly called GeomUnion(), which was renamed
from "Union" because UNION is an SQL reserved word.

Availability: 1.4.0 - ST\_Union was enhanced. ST\_Union(geomarray) was
introduced and also faster aggregate collection in PostgreSQL. If you
are using GEOS 3.1.0+ ST\_Union will use the faster Cascaded Union
algorithm described in
http://blog.cleverelephant.ca/2009/01/must-faster-unions-in-postgis-14.html

SFS\_COMPLIANT s2.1.1.3

    **Note**

    Aggregate version is not explicitly defined in OGC SPEC.

SQLMM\_COMPLIANT SQL-MM 3: 5.1.19 the z-index (elevation) when polygons
are involved.

Examples
--------

Aggregate example

::

    SELECT stusps,
           ST_Multi(ST_Union(f.the_geom)) as singlegeom
         FROM sometable As f
    GROUP BY stusps
                  

Non-Aggregate example

::

    SELECT ST_AsText(ST_Union(ST_GeomFromText('POINT(1 2)'),
        ST_GeomFromText('POINT(-2 3)') ) )

    st_astext
    ----------
    MULTIPOINT(-2 3,1 2)


    SELECT ST_AsText(ST_Union(ST_GeomFromText('POINT(1 2)'),
            ST_GeomFromText('POINT(1 2)') ) );
    st_astext
    ----------
    POINT(1 2)

    --3d example - sort of supports 3d (and with mixed dimensions!)
    SELECT ST_AsEWKT(st_union(the_geom))
    FROM
    (SELECT ST_GeomFromEWKT('POLYGON((-7 4.2,-7.1 4.2,-7.1 4.3,
    -7 4.2))') as the_geom
    UNION ALL
    SELECT ST_GeomFromEWKT('POINT(5 5 5)') as the_geom
    UNION ALL
        SELECT ST_GeomFromEWKT('POINT(-2 3 1)') as the_geom
    UNION ALL
    SELECT ST_GeomFromEWKT('LINESTRING(5 5 5, 10 10 10)') as the_geom ) as foo;

    st_asewkt
    ---------
    GEOMETRYCOLLECTION(POINT(-2 3 1),LINESTRING(5 5 5,10 10 10),POLYGON((-7 4.2 5,-7.1 4.2 5,-7.1 4.3 5,-7 4.2 5)));

    --3d example not mixing dimensions
    SELECT ST_AsEWKT(st_union(the_geom))
    FROM
    (SELECT ST_GeomFromEWKT('POLYGON((-7 4.2 2,-7.1 4.2 3,-7.1 4.3 2,
    -7 4.2 2))') as the_geom
    UNION ALL
    SELECT ST_GeomFromEWKT('POINT(5 5 5)') as the_geom
    UNION ALL
        SELECT ST_GeomFromEWKT('POINT(-2 3 1)') as the_geom
    UNION ALL
    SELECT ST_GeomFromEWKT('LINESTRING(5 5 5, 10 10 10)') as the_geom ) as foo;

    st_asewkt
    ---------
    GEOMETRYCOLLECTION(POINT(-2 3 1),LINESTRING(5 5 5,10 10 10),POLYGON((-7 4.2 2,-7.1 4.2 3,-7.1 4.3 2,-7 4.2 2)))

    --Examples using new Array construct
    SELECT ST_Union(ARRAY(SELECT the_geom FROM sometable));

    SELECT ST_AsText(ST_Union(ARRAY[ST_GeomFromText('LINESTRING(1 2, 3 4)'),
                ST_GeomFromText('LINESTRING(3 4, 4 5)')])) As wktunion;

    --wktunion---
    MULTILINESTRING((3 4,4 5),(1 2,3 4))

See Also
--------

? ?

ST\_UnaryUnion Like ST\_Union, but working at the geometry component
level. geometry ST\_UnaryUnion geometry geom Description -----------

Unlike ST\_Union, ST\_UnaryUnion does dissolve boundaries between
components of a multipolygon (invalid) and does perform union between
the components of a geometrycollection. Each components of the input
geometry is assumed to be valid, so you won't get a valid multipolygon
out of a bow-tie polygon (invalid).

You may use this function to node a set of linestrings. You may mix
ST\_UnaryUnion with ST\_Collect to fine-tune how many geometries at once
you want to dissolve to be nice on both memory size and CPU time,
finding the balance between ST\_Union and ST\_MemUnion.

Z\_SUPPORT

Availability: 2.0.0 - requires GEOS >= 3.3.0.

See Also
--------

? ? ?
