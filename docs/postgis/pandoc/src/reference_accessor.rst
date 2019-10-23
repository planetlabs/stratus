Geometry Accessors
==================

GeometryType
Returns the type of the geometry as a string. Eg: 'LINESTRING',
'POLYGON', 'MULTIPOINT', etc.
text
GeometryType
geometry
geomA
Description
-----------

Returns the type of the geometry as a string. Eg: 'LINESTRING',
'POLYGON', 'MULTIPOINT', etc.

OGC SPEC s2.1.1.1 - Returns the name of the instantiable subtype of
Geometry of which this Geometry instance is a member. The name of the
instantiable subtype of Geometry is returned as a string.

    **Note**

    This function also indicates if the geometry is measured, by
    returning a string of the form 'POINTM'.

Enhanced: 2.0.0 support for Polyhedral surfaces, Triangles and TIN was
introduced.

SFS\_COMPLIANT

CURVE\_SUPPORT

Z\_SUPPORT

P\_SUPPORT

T\_SUPPORT

Examples
--------

::

    SELECT GeometryType(ST_GeomFromText('LINESTRING(77.29 29.07,77.42 29.26,77.27 29.31,77.29 29.07)'));
     geometrytype
    --------------
     LINESTRING

::

    SELECT ST_GeometryType(ST_GeomFromEWKT('POLYHEDRALSURFACE( ((0 0 0, 0 0 1, 0 1 1, 0 1 0, 0 0 0)), 
            ((0 0 0, 0 1 0, 1 1 0, 1 0 0, 0 0 0)), ((0 0 0, 1 0 0, 1 0 1, 0 0 1, 0 0 0)), 
            ((1 1 0, 1 1 1, 1 0 1, 1 0 0, 1 1 0)), 
            ((0 1 0, 0 1 1, 1 1 1, 1 1 0, 0 1 0)), ((0 0 1, 1 0 1, 1 1 1, 0 1 1, 0 0 1)) )'));
                --result
                POLYHEDRALSURFACE
                

::

    SELECT GeometryType(geom) as result
      FROM
        (SELECT 
           ST_GeomFromEWKT('TIN (((
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
                )')  AS geom
        ) AS g;
     result
    --------
     TIN    

See Also
--------

?

ST\_Boundary
Returns the closure of the combinatorial boundary of this Geometry.
geometry
ST\_Boundary
geometry
geomA
Description
-----------

Returns the closure of the combinatorial boundary of this Geometry. The
combinatorial boundary is defined as described in section 3.12.3.2 of
the OGC SPEC. Because the result of this function is a closure, and
hence topologically closed, the resulting boundary can be represented
using representational geometry primitives as discussed in the OGC SPEC,
section 3.12.2.

Performed by the GEOS module

    **Note**

    Prior to 2.0.0, this function throws an exception if used with
    ``GEOMETRYCOLLECTION``. From 2.0.0 up it will return NULL instead
    (unsupported input).

SFS\_COMPLIANT OGC SPEC s2.1.1.1

SQLMM\_COMPLIANT SQL-MM 3: 5.1.14

Z\_SUPPORT

Enhanced: 2.1.0 support for Triangle was introduced

Examples
--------

::

    SELECT ST_AsText(ST_Boundary(ST_GeomFromText('LINESTRING(1 1,0 0, -1 1)')));
    st_astext
    -----------
    MULTIPOINT(1 1,-1 1)

    SELECT ST_AsText(ST_Boundary(ST_GeomFromText('POLYGON((1 1,0 0, -1 1, 1 1))')));
    st_astext
    ----------
    LINESTRING(1 1,0 0,-1 1,1 1)

    --Using a 3d polygon
    SELECT ST_AsEWKT(ST_Boundary(ST_GeomFromEWKT('POLYGON((1 1 1,0 0 1, -1 1 1, 1 1 1))')));

    st_asewkt
    -----------------------------------
    LINESTRING(1 1 1,0 0 1,-1 1 1,1 1 1)

    --Using a 3d multilinestring
    SELECT ST_AsEWKT(ST_Boundary(ST_GeomFromEWKT('MULTILINESTRING((1 1 1,0 0 0.5, -1 1 1),(1 1 0.5,0 0 0.5, -1 1 0.5, 1 1 0.5) )')));

    st_asewkt
    ----------
    MULTIPOINT(-1 1 1,1 1 0.75)

See Also
--------

?, ?

ST\_CoordDim
Return the coordinate dimension of the ST\_Geometry value.
integer
ST\_CoordDim
geometry
geomA
Description
-----------

Return the coordinate dimension of the ST\_Geometry value.

This is the MM compliant alias name for ?

SFS\_COMPLIANT

SQLMM\_COMPLIANT SQL-MM 3: 5.1.3

CURVE\_SUPPORT

Z\_SUPPORT

P\_SUPPORT

T\_SUPPORT

Examples
--------

::

    SELECT ST_CoordDim('CIRCULARSTRING(1 2 3, 1 3 4, 5 6 7, 8 9 10, 11 12 13)');
                ---result--
                    3

                    SELECT ST_CoordDim(ST_Point(1,2));
                --result--
                    2

            

See Also
--------

?

ST\_Dimension
The inherent dimension of this Geometry object, which must be less than
or equal to the coordinate dimension.
integer
ST\_Dimension
geometry
g
Description
-----------

The inherent dimension of this Geometry object, which must be less than
or equal to the coordinate dimension. OGC SPEC s2.1.1.1 - returns 0 for
``POINT``, 1 for ``LINESTRING``, 2 for ``POLYGON``, and the largest
dimension of the components of a ``GEOMETRYCOLLECTION``. If unknown
(empty geometry) null is returned.

SQLMM\_COMPLIANT SQL-MM 3: 5.1.2

Enhanced: 2.0.0 support for Polyhedral surfaces and TINs was introduced.
No longer throws an exception if given empty geometry.

    **Note**

    Prior to 2.0.0, this function throws an exception if used with empty
    geometry.

P\_SUPPORT

T\_SUPPORT

Examples
--------

::

    SELECT ST_Dimension('GEOMETRYCOLLECTION(LINESTRING(1 1,0 0),POINT(0 0))');
    ST_Dimension
    -----------
    1

See Also
--------

?

ST\_EndPoint
Returns the last point of a
LINESTRING
geometry as a
POINT
.
boolean
ST\_EndPoint
geometry
g
Description
-----------

Returns the last point of a ``LINESTRING`` geometry as a ``POINT`` or
``NULL`` if the input parameter is not a ``LINESTRING``.

SQLMM\_COMPLIANT SQL-MM 3: 7.1.4

Z\_SUPPORT

    **Note**

    Changed: 2.0.0 no longer works with single geometry
    multilinestrings. In older versions of PostGIS -- a single line
    multilinestring would work happily with this function and return the
    start point. In 2.0.0 it just returns NULL like any other
    multilinestring. The older behavior was an undocumented feature, but
    people who assumed they had their data stored as LINESTRING may
    experience these returning NULL in 2.0 now.

Examples
--------

::

    postgis=# SELECT ST_AsText(ST_EndPoint('LINESTRING(1 1, 2 2, 3 3)'::geometry));
     st_astext
    ------------
     POINT(3 3)
    (1 row)

    postgis=# SELECT ST_EndPoint('POINT(1 1)'::geometry) IS NULL AS is_null;
      is_null
    ----------
     t
    (1 row)

    --3d endpoint
    SELECT ST_AsEWKT(ST_EndPoint('LINESTRING(1 1 2, 1 2 3, 0 0 5)'));
      st_asewkt
    --------------
     POINT(0 0 5)
    (1 row)

See Also
--------

?, ?

ST\_Envelope
Returns a geometry representing the double precision (float8) bounding
box of the supplied geometry.
geometry
ST\_Envelope
geometry
g1
Description
-----------

Returns the float8 minimum bounding box for the supplied geometry, as a
geometry. The polygon is defined by the corner points of the bounding
box ((``MINX``, ``MINY``), (``MINX``, ``MAXY``), (``MAXX``, ``MAXY``),
(``MAXX``, ``MINY``), (``MINX``, ``MINY``)). (PostGIS will add a
``ZMIN``/``ZMAX`` coordinate as well).

Degenerate cases (vertical lines, points) will return a geometry of
lower dimension than ``POLYGON``, ie. ``POINT`` or ``LINESTRING``.

Availability: 1.5.0 behavior changed to output double precision instead
of float4

SFS\_COMPLIANT s2.1.1.1

SQLMM\_COMPLIANT SQL-MM 3: 5.1.15

Examples
--------

::

    SELECT ST_AsText(ST_Envelope('POINT(1 3)'::geometry));
     st_astext
    ------------
     POINT(1 3)
    (1 row)


    SELECT ST_AsText(ST_Envelope('LINESTRING(0 0, 1 3)'::geometry));
               st_astext
    --------------------------------
     POLYGON((0 0,0 3,1 3,1 0,0 0))
    (1 row)


    SELECT ST_AsText(ST_Envelope('POLYGON((0 0, 0 1, 1.0000001 1, 1.0000001 0, 0 0))'::geometry));
                              st_astext
    --------------------------------------------------------------
     POLYGON((0 0,0 1,1.00000011920929 1,1.00000011920929 0,0 0))
    (1 row)
    SELECT ST_AsText(ST_Envelope('POLYGON((0 0, 0 1, 1.0000000001 1, 1.0000000001 0, 0 0))'::geometry));
                              st_astext
    --------------------------------------------------------------
     POLYGON((0 0,0 1,1.00000011920929 1,1.00000011920929 0,0 0))
    (1 row)
        
    SELECT Box3D(geom), Box2D(geom), ST_AsText(ST_Envelope(geom)) As envelopewkt
        FROM (SELECT 'POLYGON((0 0, 0 1000012333334.34545678, 1.0000001 1, 1.0000001 0, 0 0))'::geometry As geom) As foo;


        

See Also
--------

?, ?

ST\_ExteriorRing
Returns a line string representing the exterior ring of the
POLYGON
geometry. Return NULL if the geometry is not a polygon. Will not work
with MULTIPOLYGON
geometry
ST\_ExteriorRing
geometry
a\_polygon
Description
-----------

Returns a line string representing the exterior ring of the ``POLYGON``
geometry. Return NULL if the geometry is not a polygon.

    **Note**

    Only works with POLYGON geometry types

SFS\_COMPLIANT 2.1.5.1

SQLMM\_COMPLIANT SQL-MM 3: 8.2.3, 8.3.3

Z\_SUPPORT

Examples
--------

::

    --If you have a table of polygons
    SELECT gid, ST_ExteriorRing(the_geom) AS ering
    FROM sometable;

    --If you have a table of MULTIPOLYGONs
    --and want to return a MULTILINESTRING composed of the exterior rings of each polygon
    SELECT gid, ST_Collect(ST_ExteriorRing(the_geom)) AS erings
        FROM (SELECT gid, (ST_Dump(the_geom)).geom As the_geom
                FROM sometable) As foo
    GROUP BY gid;

    --3d Example
    SELECT ST_AsEWKT(
        ST_ExteriorRing(
        ST_GeomFromEWKT('POLYGON((0 0 1, 1 1 1, 1 2 1, 1 1 1, 0 0 1))')
        )
    );

    st_asewkt
    ---------
    LINESTRING(0 0 1,1 1 1,1 2 1,1 1 1,0 0 1)

See Also
--------

?, ?, ?

ST\_GeometryN
Return the 1-based Nth geometry if the geometry is a GEOMETRYCOLLECTION,
(MULTI)POINT, (MULTI)LINESTRING, MULTICURVE or (MULTI)POLYGON,
POLYHEDRALSURFACE Otherwise, return NULL.
geometry
ST\_GeometryN
geometry
geomA
integer
n
Description
-----------

Return the 1-based Nth geometry if the geometry is a GEOMETRYCOLLECTION,
(MULTI)POINT, (MULTI)LINESTRING, MULTICURVE or (MULTI)POLYGON,
POLYHEDRALSURFACE Otherwise, return NULL

    **Note**

    Index is 1-based as for OGC specs since version 0.8.0. Previous
    versions implemented this as 0-based instead.

    **Note**

    If you want to extract all geometries, of a geometry, ST\_Dump is
    more efficient and will also work for singular geoms.

Enhanced: 2.0.0 support for Polyhedral surfaces, Triangles and TIN was
introduced.

Changed: 2.0.0 Prior versions would return NULL for singular geometries.
This was changed to return the geometry for ST\_GeometryN(..,1) case.

SFS\_COMPLIANT

SQLMM\_COMPLIANT SQL-MM 3: 9.1.5

Z\_SUPPORT

CURVE\_SUPPORT

P\_SUPPORT

T\_SUPPORT

Standard Examples
-----------------

::

    --Extracting a subset of points from a 3d multipoint
    SELECT n, ST_AsEWKT(ST_GeometryN(the_geom, n)) As geomewkt
    FROM (
    VALUES (ST_GeomFromEWKT('MULTIPOINT(1 2 7, 3 4 7, 5 6 7, 8 9 10)') ),
    ( ST_GeomFromEWKT('MULTICURVE(CIRCULARSTRING(2.5 2.5,4.5 2.5, 3.5 3.5), (10 11, 12 11))') )
        )As foo(the_geom)
        CROSS JOIN generate_series(1,100) n
    WHERE n <= ST_NumGeometries(the_geom);

     n |               geomewkt
    ---+-----------------------------------------
     1 | POINT(1 2 7)
     2 | POINT(3 4 7)
     3 | POINT(5 6 7)
     4 | POINT(8 9 10)
     1 | CIRCULARSTRING(2.5 2.5,4.5 2.5,3.5 3.5)
     2 | LINESTRING(10 11,12 11)


    --Extracting all geometries (useful when you want to assign an id)
    SELECT gid, n, ST_GeometryN(the_geom, n)
    FROM sometable CROSS JOIN generate_series(1,100) n
    WHERE n <= ST_NumGeometries(the_geom);

Polyhedral Surfaces, TIN and Triangle Examples
----------------------------------------------

::

    -- Polyhedral surface example
    -- Break a Polyhedral surface into its faces
    SELECT ST_AsEWKT(ST_GeometryN(p_geom,3)) As geom_ewkt
      FROM (SELECT ST_GeomFromEWKT('POLYHEDRALSURFACE( 
    ((0 0 0, 0 0 1, 0 1 1, 0 1 0, 0 0 0)),  
    ((0 0 0, 0 1 0, 1 1 0, 1 0 0, 0 0 0)), 
    ((0 0 0, 1 0 0, 1 0 1, 0 0 1, 0 0 0)), 
    ((1 1 0, 1 1 1, 1 0 1, 1 0 0, 1 1 0)),  
    ((0 1 0, 0 1 1, 1 1 1, 1 1 0, 0 1 0)),  
    ((0 0 1, 1 0 1, 1 1 1, 0 1 1, 0 0 1)) 
    )')  AS p_geom )  AS a;

                    geom_ewkt
    ------------------------------------------
     POLYGON((0 0 0,1 0 0,1 0 1,0 0 1,0 0 0))

::

    -- TIN --       
    SELECT ST_AsEWKT(ST_GeometryN(geom,2)) as wkt
      FROM
        (SELECT 
           ST_GeomFromEWKT('TIN (((
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
                )')  AS geom
        ) AS g;
    -- result --
                     wkt
    -------------------------------------
     TRIANGLE((0 0 0,0 1 0,1 1 0,0 0 0))

See Also
--------

?, ?

ST\_GeometryType
Return the geometry type of the ST\_Geometry value.
text
ST\_GeometryType
geometry
g1
Description
-----------

Returns the type of the geometry as a string. EG: 'ST\_Linestring',
'ST\_Polygon','ST\_MultiPolygon' etc. This function differs from
GeometryType(geometry) in the case of the string and ST in front that is
returned, as well as the fact that it will not indicate whether the
geometry is measured.

Enhanced: 2.0.0 support for Polyhedral surfaces was introduced.

SQLMM\_COMPLIANT SQL-MM 3: 5.1.4

Z\_SUPPORT

P\_SUPPORT

Examples
--------

::

    SELECT ST_GeometryType(ST_GeomFromText('LINESTRING(77.29 29.07,77.42 29.26,77.27 29.31,77.29 29.07)'));
                --result
                ST_LineString

::

    SELECT ST_GeometryType(ST_GeomFromEWKT('POLYHEDRALSURFACE( ((0 0 0, 0 0 1, 0 1 1, 0 1 0, 0 0 0)), 
            ((0 0 0, 0 1 0, 1 1 0, 1 0 0, 0 0 0)), ((0 0 0, 1 0 0, 1 0 1, 0 0 1, 0 0 0)), 
            ((1 1 0, 1 1 1, 1 0 1, 1 0 0, 1 1 0)), 
            ((0 1 0, 0 1 1, 1 1 1, 1 1 0, 0 1 0)), ((0 0 1, 1 0 1, 1 1 1, 0 1 1, 0 0 1)) )'));
                --result
                ST_PolyhedralSurface

::

    SELECT ST_GeometryType(ST_GeomFromEWKT('POLYHEDRALSURFACE( ((0 0 0, 0 0 1, 0 1 1, 0 1 0, 0 0 0)), 
            ((0 0 0, 0 1 0, 1 1 0, 1 0 0, 0 0 0)), ((0 0 0, 1 0 0, 1 0 1, 0 0 1, 0 0 0)), 
            ((1 1 0, 1 1 1, 1 0 1, 1 0 0, 1 1 0)), 
            ((0 1 0, 0 1 1, 1 1 1, 1 1 0, 0 1 0)), ((0 0 1, 1 0 1, 1 1 1, 0 1 1, 0 0 1)) )'));
                --result
                ST_PolyhedralSurface

::

    SELECT ST_GeometryType(geom) as result
      FROM
        (SELECT 
           ST_GeomFromEWKT('TIN (((
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
                )')  AS geom
        ) AS g;
     result
    --------
     ST_Tin    

See Also
--------

?

ST\_InteriorRingN
Return the Nth interior linestring ring of the polygon geometry. Return
NULL if the geometry is not a polygon or the given N is out of range.
geometry
ST\_InteriorRingN
geometry
a\_polygon
integer
n
Description
-----------

Return the Nth interior linestring ring of the polygon geometry. Return
NULL if the geometry is not a polygon or the given N is out of range.
index starts at 1.

    **Note**

    This will not work for MULTIPOLYGONs. Use in conjunction with
    ST\_Dump for MULTIPOLYGONS

SFS\_COMPLIANT

SQLMM\_COMPLIANT SQL-MM 3: 8.2.6, 8.3.5

Z\_SUPPORT

Examples
--------

::

    SELECT ST_AsText(ST_InteriorRingN(the_geom, 1)) As the_geom
    FROM (SELECT ST_BuildArea(
            ST_Collect(ST_Buffer(ST_Point(1,2), 20,3),
                ST_Buffer(ST_Point(1, 2), 10,3))) As the_geom
            )  as foo
            

See Also
--------

? ?, ?, ?, ?, ?

ST\_IsClosed
Returns
TRUE
if the
LINESTRING
's start and end points are coincident. For Polyhedral surface is closed
(volumetric).
boolean
ST\_IsClosed
geometry
g
Description
-----------

Returns ``TRUE`` if the ``LINESTRING``'s start and end points are
coincident. For Polyhedral Surfaces, it tells you if the surface is
areal (open) or volumetric (closed).

SFS\_COMPLIANT

SQLMM\_COMPLIANT SQL-MM 3: 7.1.5, 9.3.3

    **Note**

    SQL-MM defines the result of ``ST_IsClosed()`` to be 0, while
    PostGIS returns ``NULL``.

Z\_SUPPORT

CURVE\_SUPPORT

Enhanced: 2.0.0 support for Polyhedral surfaces was introduced.

P\_SUPPORT

Line String and Point Examples
------------------------------

::

    postgis=# SELECT ST_IsClosed('LINESTRING(0 0, 1 1)'::geometry);
     st_isclosed
    -------------
     f
    (1 row)

    postgis=# SELECT ST_IsClosed('LINESTRING(0 0, 0 1, 1 1, 0 0)'::geometry);
     st_isclosed
    -------------
     t
    (1 row)

    postgis=# SELECT ST_IsClosed('MULTILINESTRING((0 0, 0 1, 1 1, 0 0),(0 0, 1 1))'::geometry);
     st_isclosed
    -------------
     f
    (1 row)

    postgis=# SELECT ST_IsClosed('POINT(0 0)'::geometry);
     st_isclosed
    -------------
     t
    (1 row)

    postgis=# SELECT ST_IsClosed('MULTIPOINT((0 0), (1 1))'::geometry);
     st_isclosed
    -------------
     t
    (1 row)

Polyhedral Surface Examples
---------------------------

::

            -- A cube --
            SELECT ST_IsClosed(ST_GeomFromEWKT('POLYHEDRALSURFACE( ((0 0 0, 0 0 1, 0 1 1, 0 1 0, 0 0 0)), 
            ((0 0 0, 0 1 0, 1 1 0, 1 0 0, 0 0 0)), ((0 0 0, 1 0 0, 1 0 1, 0 0 1, 0 0 0)), 
            ((1 1 0, 1 1 1, 1 0 1, 1 0 0, 1 1 0)), 
            ((0 1 0, 0 1 1, 1 1 1, 1 1 0, 0 1 0)), ((0 0 1, 1 0 1, 1 1 1, 0 1 1, 0 0 1)) )'));

     st_isclosed
    -------------
     t


     -- Same as cube but missing a side --
     SELECT ST_IsClosed(ST_GeomFromEWKT('POLYHEDRALSURFACE( ((0 0 0, 0 0 1, 0 1 1, 0 1 0, 0 0 0)), 
            ((0 0 0, 0 1 0, 1 1 0, 1 0 0, 0 0 0)), ((0 0 0, 1 0 0, 1 0 1, 0 0 1, 0 0 0)), 
            ((1 1 0, 1 1 1, 1 0 1, 1 0 0, 1 1 0)), 
            ((0 1 0, 0 1 1, 1 1 1, 1 1 0, 0 1 0)) )'));

     st_isclosed
    -------------
     f

See Also
--------

?

ST\_IsCollection
Returns
TRUE
if the argument is a collection (
MULTI\*
,
GEOMETRYCOLLECTION
, ...)
boolean
ST\_IsCollection
geometry
g
Description
-----------

Returns ``TRUE`` if the geometry type of the argument is either:

-  GEOMETRYCOLLECTION

-  MULTI{POINT,POLYGON,LINESTRING,CURVE,SURFACE}

-  COMPOUNDCURVE

    **Note**

    This function analyzes the type of the geometry. This means that it
    will return ``TRUE`` on collections that are empty or that contain a
    single element.

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    postgis=# SELECT ST_IsCollection('LINESTRING(0 0, 1 1)'::geometry);
     st_iscollection
    -------------
     f
    (1 row)

    postgis=# SELECT ST_IsCollection('MULTIPOINT EMPTY'::geometry);
     st_iscollection
    -------------
     t
    (1 row)

    postgis=# SELECT ST_IsCollection('MULTIPOINT((0 0))'::geometry);
     st_iscollection
    -------------
     t
    (1 row)

    postgis=# SELECT ST_IsCollection('MULTIPOINT((0 0), (42 42))'::geometry);
     st_iscollection
    -------------
     t
    (1 row)

    postgis=# SELECT ST_IsCollection('GEOMETRYCOLLECTION(POINT(0 0))'::geometry);
     st_iscollection
    -------------
     t
    (1 row)

See Also
--------

?

ST\_IsEmpty
Returns true if this Geometry is an empty geometrycollection, polygon,
point etc.
boolean
ST\_IsEmpty
geometry
geomA
Description
-----------

Returns true if this Geometry is an empty geometry. If true, then this
Geometry represents an empty geometry collection, polygon, point etc.

    **Note**

    SQL-MM defines the result of ST\_IsEmpty(NULL) to be 0, while
    PostGIS returns NULL.

SFS\_COMPLIANT s2.1.1.1

SQLMM\_COMPLIANT SQL-MM 3: 5.1.7

CURVE\_SUPPORT

    **Warning**

    Changed: 2.0.0 In prior versions of PostGIS
    ST\_GeomFromText('GEOMETRYCOLLECTION(EMPTY)') was allowed. This is
    now illegal in PostGIS 2.0.0 to better conform with SQL/MM standards

Examples
--------

::

    SELECT ST_IsEmpty(ST_GeomFromText('GEOMETRYCOLLECTION EMPTY'));
     st_isempty
    ------------
     t
    (1 row)

     SELECT ST_IsEmpty(ST_GeomFromText('POLYGON EMPTY'));
     st_isempty
    ------------
     t
    (1 row)

    SELECT ST_IsEmpty(ST_GeomFromText('POLYGON((1 2, 3 4, 5 6, 1 2))'));

     st_isempty
    ------------
     f
    (1 row)

     SELECT ST_IsEmpty(ST_GeomFromText('POLYGON((1 2, 3 4, 5 6, 1 2))')) = false;
     ?column?
    ----------
     t
    (1 row)

     SELECT ST_IsEmpty(ST_GeomFromText('CIRCULARSTRING EMPTY'));
      st_isempty
    ------------
     t
    (1 row)


            

ST\_IsRing
Returns
TRUE
if this
LINESTRING
is both closed and simple.
boolean
ST\_IsRing
geometry
g
Description
-----------

Returns ``TRUE`` if this ``LINESTRING`` is both ? (``ST_StartPoint()``
``~=`` ``ST_Endpoint()``) and ? (does not self intersect).

SFS\_COMPLIANT 2.1.5.1

SQLMM\_COMPLIANT SQL-MM 3: 7.1.6

    **Note**

    SQL-MM defines the result of ``ST_IsRing()`` to be 0, while PostGIS
    returns ``NULL``.

Examples
--------

::

    SELECT ST_IsRing(the_geom), ST_IsClosed(the_geom), ST_IsSimple(the_geom)
    FROM (SELECT 'LINESTRING(0 0, 0 1, 1 1, 1 0, 0 0)'::geometry AS the_geom) AS foo;
     st_isring | st_isclosed | st_issimple
    -----------+-------------+-------------
     t         | t           | t
    (1 row)

    SELECT ST_IsRing(the_geom), ST_IsClosed(the_geom), ST_IsSimple(the_geom)
    FROM (SELECT 'LINESTRING(0 0, 0 1, 1 0, 1 1, 0 0)'::geometry AS the_geom) AS foo;
     st_isring | st_isclosed | st_issimple
    -----------+-------------+-------------
     f         | t           | f
    (1 row)

See Also
--------

?, ?, ?, ?

ST\_IsSimple
Returns (TRUE) if this Geometry has no anomalous geometric points, such
as self intersection or self tangency.
boolean
ST\_IsSimple
geometry
geomA
Description
-----------

Returns true if this Geometry has no anomalous geometric points, such as
self intersection or self tangency. For more information on the OGC's
definition of geometry simplicity and validity, refer to `"Ensuring
OpenGIS compliancy of geometries" <#OGC_Validity>`__

    **Note**

    SQL-MM defines the result of ST\_IsSimple(NULL) to be 0, while
    PostGIS returns NULL.

SFS\_COMPLIANT s2.1.1.1

SQLMM\_COMPLIANT SQL-MM 3: 5.1.8

Z\_SUPPORT

Examples
--------

::

     SELECT ST_IsSimple(ST_GeomFromText('POLYGON((1 2, 3 4, 5 6, 1 2))'));
     st_issimple
    -------------
     t
    (1 row)

     SELECT ST_IsSimple(ST_GeomFromText('LINESTRING(1 1,2 2,2 3.5,1 3,1 2,2 1)'));
     st_issimple
    -------------
     f
    (1 row)

See Also
--------

?

ST\_IsValid
Returns
true
if the
ST\_Geometry
is well formed.
boolean
ST\_IsValid
geometry
g
boolean
ST\_IsValid
geometry
g
integer
flags
Description
-----------

Test if an ST\_Geometry value is well formed. For geometries that are
invalid, the PostgreSQL NOTICE will provide details of why it is not
valid. For more information on the OGC's definition of geometry
simplicity and validity, refer to `"Ensuring OpenGIS compliancy of
geometries" <#OGC_Validity>`__

    **Note**

    SQL-MM defines the result of ST\_IsValid(NULL) to be 0, while
    PostGIS returns NULL.

The version accepting flags is available starting with 2.0.0 and
requires GEOS >= 3.3.0. Such version does not print a NOTICE explaining
the invalidity. Allowed ``flags`` are documented in ?.

SFS\_COMPLIANT

SQLMM\_COMPLIANT SQL-MM 3: 5.1.9

Examples
--------

::

    SELECT ST_IsValid(ST_GeomFromText('LINESTRING(0 0, 1 1)')) As good_line,
        ST_IsValid(ST_GeomFromText('POLYGON((0 0, 1 1, 1 2, 1 1, 0 0))')) As bad_poly
    --results
    NOTICE:  Self-intersection at or near point 0 0
     good_line | bad_poly
    -----------+----------
     t         | f

See Also
--------

?, ?, ?, ?

ST\_IsValidReason
Returns text stating if a geometry is valid or not and if not valid, a
reason why.
text
ST\_IsValidReason
geometry
geomA
text
ST\_IsValidReason
geometry
geomA
integer
flags
Description
-----------

Returns text stating if a geometry is valid or not an if not valid, a
reason why.

Useful in combination with ST\_IsValid to generate a detailed report of
invalid geometries and reasons.

Allowed ``flags`` are documented in ?.

Availability: 1.4 - requires GEOS >= 3.1.0.

Availability: 2.0 - requires GEOS >= 3.3.0 for the version taking flags.

Examples
--------

::

    --First 3 Rejects from a successful quintuplet experiment
    SELECT gid, ST_IsValidReason(the_geom) as validity_info
    FROM
    (SELECT ST_MakePolygon(ST_ExteriorRing(e.buff), ST_Accum(f.line)) As the_geom, gid
    FROM (SELECT ST_Buffer(ST_MakePoint(x1*10,y1), z1) As buff, x1*10 + y1*100 + z1*1000 As gid
        FROM generate_series(-4,6) x1
        CROSS JOIN generate_series(2,5) y1
        CROSS JOIN generate_series(1,8) z1
        WHERE x1 > y1*0.5 AND z1 < x1*y1) As e
        INNER JOIN (SELECT ST_Translate(ST_ExteriorRing(ST_Buffer(ST_MakePoint(x1*10,y1), z1)),y1*1, z1*2) As line
        FROM generate_series(-3,6) x1
        CROSS JOIN generate_series(2,5) y1
        CROSS JOIN generate_series(1,10) z1
        WHERE x1 > y1*0.75 AND z1 < x1*y1) As f
    ON (ST_Area(e.buff) > 78 AND ST_Contains(e.buff, f.line))
    GROUP BY gid, e.buff) As quintuplet_experiment
    WHERE ST_IsValid(the_geom) = false
    ORDER BY gid
    LIMIT 3;

     gid  |      validity_info
    ------+--------------------------
     5330 | Self-intersection [32 5]
     5340 | Self-intersection [42 5]
     5350 | Self-intersection [52 5]

     --simple example
    SELECT ST_IsValidReason('LINESTRING(220227 150406,2220227 150407,222020 150410)');

     st_isvalidreason
    ------------------
     Valid Geometry

            

See Also
--------

?, ?

ST\_IsValidDetail
Returns a valid\_detail (valid,reason,location) row stating if a
geometry is valid or not and if not valid, a reason why and a location
where.
valid\_detail
ST\_IsValidDetail
geometry
geom
valid\_detail
ST\_IsValidDetail
geometry
geom
integer
flags
Description
-----------

Returns a valid\_detail row, formed by a boolean (valid) stating if a
geometry is valid, a varchar (reason) stating a reason why it is invalid
and a geometry (location) pointing out where it is invalid.

Useful to substitute and improve the combination of ST\_IsValid and
ST\_IsValidReason to generate a detailed report of invalid geometries.

The 'flags' argument is a bitfield. It can have the following values:

-  1: Consider self-intersecting rings forming holes as valid. This is
   also know as "the ESRI flag". Note that this is against the OGC
   model.

Availability: 2.0.0 - requires GEOS >= 3.3.0.

Examples
--------

::

    --First 3 Rejects from a successful quintuplet experiment
    SELECT gid, reason(ST_IsValidDetail(the_geom)), ST_AsText(location(ST_IsValidDetail(the_geom))) as location 
    FROM
    (SELECT ST_MakePolygon(ST_ExteriorRing(e.buff), ST_Accum(f.line)) As the_geom, gid
    FROM (SELECT ST_Buffer(ST_MakePoint(x1*10,y1), z1) As buff, x1*10 + y1*100 + z1*1000 As gid
        FROM generate_series(-4,6) x1
        CROSS JOIN generate_series(2,5) y1
        CROSS JOIN generate_series(1,8) z1
        WHERE x1 > y1*0.5 AND z1 < x1*y1) As e
        INNER JOIN (SELECT ST_Translate(ST_ExteriorRing(ST_Buffer(ST_MakePoint(x1*10,y1), z1)),y1*1, z1*2) As line
        FROM generate_series(-3,6) x1
        CROSS JOIN generate_series(2,5) y1
        CROSS JOIN generate_series(1,10) z1
        WHERE x1 > y1*0.75 AND z1 < x1*y1) As f
    ON (ST_Area(e.buff) > 78 AND ST_Contains(e.buff, f.line))
    GROUP BY gid, e.buff) As quintuplet_experiment
    WHERE ST_IsValid(the_geom) = false
    ORDER BY gid
    LIMIT 3;

     gid  |      reason       |  location
    ------+-------------------+-------------
     5330 | Self-intersection | POINT(32 5)
     5340 | Self-intersection | POINT(42 5)
     5350 | Self-intersection | POINT(52 5)

     --simple example
    SELECT * FROM ST_IsValidDetail('LINESTRING(220227 150406,2220227 150407,222020 150410)');

     valid | reason | location
    -------+--------+----------
     t     |        |

            

See Also
--------

?, ?

ST\_M
Return the M coordinate of the point, or NULL if not available. Input
must be a point.
float
ST\_M
geometry
a\_point
Description
-----------

Return the M coordinate of the point, or NULL if not available. Input
must be a point.

    **Note**

    This is not (yet) part of the OGC spec, but is listed here to
    complete the point coordinate extractor function list.

SFS\_COMPLIANT

SQLMM\_COMPLIANT

Z\_SUPPORT

Examples
--------

::

    SELECT ST_M(ST_GeomFromEWKT('POINT(1 2 3 4)'));
     st_m
    ------
        4
    (1 row)

            

See Also
--------

?, ?, ?, ?

ST\_NDims
Returns coordinate dimension of the geometry as a small int. Values are:
2,3 or 4.
integer
ST\_NDims
geometry
g1
Description
-----------

Returns the coordinate dimension of the geometry. PostGIS supports 2 -
(x,y) , 3 - (x,y,z) or 2D with measure - x,y,m, and 4 - 3D with measure
space x,y,z,m

Z\_SUPPORT

Examples
--------

::

    SELECT ST_NDims(ST_GeomFromText('POINT(1 1)')) As d2point,
        ST_NDims(ST_GeomFromEWKT('POINT(1 1 2)')) As d3point,
        ST_NDims(ST_GeomFromEWKT('POINTM(1 1 0.5)')) As d2pointm;

         d2point | d3point | d2pointm
    ---------+---------+----------
           2 |       3 |        3
                

See Also
--------

?, ?, ?

ST\_NPoints
Return the number of points (vertexes) in a geometry.
integer
ST\_NPoints
geometry
g1
Description
-----------

Return the number of points in a geometry. Works for all geometries.

Enhanced: 2.0.0 support for Polyhedral surfaces was introduced.

    **Note**

    Prior to 1.3.4, this function crashes if used with geometries that
    contain CURVES. This is fixed in 1.3.4+

Z\_SUPPORT

CURVE\_SUPPORT

P\_SUPPORT

Examples
--------

::

    SELECT ST_NPoints(ST_GeomFromText('LINESTRING(77.29 29.07,77.42 29.26,77.27 29.31,77.29 29.07)'));
    --result
    4

    --Polygon in 3D space
    SELECT ST_NPoints(ST_GeomFromEWKT('LINESTRING(77.29 29.07 1,77.42 29.26 0,77.27 29.31 -1,77.29 29.07 3)'))
    --result
    4

See Also
--------

?

ST\_NRings
If the geometry is a polygon or multi-polygon returns the number of
rings.
integer
ST\_NRings
geometry
geomA
Description
-----------

If the geometry is a polygon or multi-polygon returns the number of
rings. Unlike NumInteriorRings, it counts the outer rings as well.

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    SELECT ST_NRings(the_geom) As Nrings, ST_NumInteriorRings(the_geom) As ninterrings
                        FROM (SELECT ST_GeomFromText('POLYGON((1 2, 3 4, 5 6, 1 2))') As the_geom) As foo;
         nrings | ninterrings
    --------+-------------
          1 |           0
    (1 row)

See Also
--------

?

ST\_NumGeometries
If geometry is a GEOMETRYCOLLECTION (or MULTI\*) return the number of
geometries, for single geometries will return 1, otherwise return NULL.
integer
ST\_NumGeometries
geometry
geom
Description
-----------

Returns the number of Geometries. If geometry is a GEOMETRYCOLLECTION
(or MULTI\*) return the number of geometries, for single geometries will
return 1, otherwise return NULL.

Enhanced: 2.0.0 support for Polyhedral surfaces, Triangles and TIN was
introduced.

Changed: 2.0.0 In prior versions this would return NULL if the geometry
was not a collection/MULTI type. 2.0.0+ now returns 1 for single
geometries e.g POLYGON, LINESTRING, POINT.

SQLMM\_COMPLIANT SQL-MM 3: 9.1.4

Z\_SUPPORT

P\_SUPPORT

T\_SUPPORT

Examples
--------

::

    --Prior versions would have returned NULL for this -- in 2.0.0 this returns 1
    SELECT ST_NumGeometries(ST_GeomFromText('LINESTRING(77.29 29.07,77.42 29.26,77.27 29.31,77.29 29.07)'));
    --result
    1

    --Geometry Collection Example - multis count as one geom in a collection
    SELECT ST_NumGeometries(ST_GeomFromEWKT('GEOMETRYCOLLECTION(MULTIPOINT(-2 3 , -2 2),
    LINESTRING(5 5 ,10 10),
    POLYGON((-7 4.2,-7.1 5,-7.1 4.3,-7 4.2)))'));
    --result
    3

See Also
--------

?, ?

ST\_NumInteriorRings
Return the number of interior rings of the first polygon in the
geometry. This will work with both POLYGON and MULTIPOLYGON types but
only looks at the first polygon. Return NULL if there is no polygon in
the geometry.
integer
ST\_NumInteriorRings
geometry
a\_polygon
Description
-----------

Return the number of interior rings of the first polygon in the
geometry. This will work with both POLYGON and MULTIPOLYGON types but
only looks at the first polygon. Return NULL if there is no polygon in
the geometry.

SQLMM\_COMPLIANT SQL-MM 3: 8.2.5

Examples
--------

::

    --If you have a regular polygon
    SELECT gid, field1, field2, ST_NumInteriorRings(the_geom) AS numholes
    FROM sometable;

    --If you have multipolygons
    --And you want to know the total number of interior rings in the MULTIPOLYGON
    SELECT gid, field1, field2, SUM(ST_NumInteriorRings(the_geom)) AS numholes
    FROM (SELECT gid, field1, field2, (ST_Dump(the_geom)).geom As the_geom
        FROM sometable) As foo
    GROUP BY gid, field1,field2;
                

See Also
--------

?

ST\_NumInteriorRing
Return the number of interior rings of the first polygon in the
geometry. Synonym to ST\_NumInteriorRings.
integer
ST\_NumInteriorRing
geometry
a\_polygon
Description
-----------

Return the number of interior rings of the first polygon in the
geometry. Synonym to ST\_NumInteriorRings. The OpenGIS specs are
ambiguous about the exact function naming, so we provide both spellings.

SQLMM\_COMPLIANT SQL-MM 3: 8.2.5

See Also
--------

?

ST\_NumPatches
Return the number of faces on a Polyhedral Surface. Will return null for
non-polyhedral geometries.
integer
ST\_NumPatches
geometry
g1
Description
-----------

Return the number of faces on a Polyhedral Surface. Will return null for
non-polyhedral geometries. This is an alias for ST\_NumGeometries to
support MM naming. Faster to use ST\_NumGeometries if you don't care
about MM convention.

Availability: 2.0.0

Z\_SUPPORT

SFS\_COMPLIANT

SQLMM\_COMPLIANT SQL-MM 3: ?

P\_SUPPORT

Examples
--------

::

    SELECT ST_NumPatches(ST_GeomFromEWKT('POLYHEDRALSURFACE( ((0 0 0, 0 0 1, 0 1 1, 0 1 0, 0 0 0)), 
            ((0 0 0, 0 1 0, 1 1 0, 1 0 0, 0 0 0)), ((0 0 0, 1 0 0, 1 0 1, 0 0 1, 0 0 0)), 
            ((1 1 0, 1 1 1, 1 0 1, 1 0 0, 1 1 0)), 
            ((0 1 0, 0 1 1, 1 1 1, 1 1 0, 0 1 0)), ((0 0 1, 1 0 1, 1 1 1, 0 1 1, 0 0 1)) )'));
            --result
            6
            

See Also
--------

?, ?

ST\_NumPoints
Return the number of points in an ST\_LineString or ST\_CircularString
value.
integer
ST\_NumPoints
geometry
g1
Description
-----------

Return the number of points in an ST\_LineString or ST\_CircularString
value. Prior to 1.4 only works with Linestrings as the specs state. From
1.4 forward this is an alias for ST\_NPoints which returns number of
vertexes for not just line strings. Consider using ST\_NPoints instead
which is multi-purpose and works with many geometry types.

SFS\_COMPLIANT

SQLMM\_COMPLIANT SQL-MM 3: 7.2.4

Examples
--------

::

    SELECT ST_NumPoints(ST_GeomFromText('LINESTRING(77.29 29.07,77.42 29.26,77.27 29.31,77.29 29.07)'));
            --result
            4
            

See Also
--------

?

ST\_PatchN
Return the 1-based Nth geometry (face) if the geometry is a
POLYHEDRALSURFACE, POLYHEDRALSURFACEM. Otherwise, return NULL.
geometry
ST\_PatchN
geometry
geomA
integer
n
Description
-----------

>Return the 1-based Nth geometry (face) if the geometry is a
POLYHEDRALSURFACE, POLYHEDRALSURFACEM. Otherwise, return NULL. This
returns the same answer as ST\_GeometryN for Polyhedral Surfaces. Using
ST\_GemoetryN is faster.

    **Note**

    Index is 1-based.

    **Note**

    If you want to extract all geometries, of a geometry, ST\_Dump is
    more efficient.

Availability: 2.0.0

SQLMM\_COMPLIANT SQL-MM 3: ?

Z\_SUPPORT

P\_SUPPORT

Examples
--------

::

    --Extract the 2nd face of the polyhedral surface
    SELECT ST_AsEWKT(ST_PatchN(geom, 2)) As geomewkt
    FROM (
    VALUES (ST_GeomFromEWKT('POLYHEDRALSURFACE( ((0 0 0, 0 0 1, 0 1 1, 0 1 0, 0 0 0)), 
        ((0 0 0, 0 1 0, 1 1 0, 1 0 0, 0 0 0)), ((0 0 0, 1 0 0, 1 0 1, 0 0 1, 0 0 0)), 
        ((1 1 0, 1 1 1, 1 0 1, 1 0 0, 1 1 0)), 
        ((0 1 0, 0 1 1, 1 1 1, 1 1 0, 0 1 0)), ((0 0 1, 1 0 1, 1 1 1, 0 1 1, 0 0 1)) )')) ) As foo(geom);

                  geomewkt
    ---+-----------------------------------------
     POLYGON((0 0 0,0 1 0,1 1 0,1 0 0,0 0 0))

See Also
--------

?, ?, ?, ?, ?

ST\_PointN
Return the Nth point in the first linestring or circular linestring in
the geometry. Return NULL if there is no linestring in the geometry.
geometry
ST\_PointN
geometry
a\_linestring
integer
n
Description
-----------

Return the Nth point in a single linestring or circular linestring in
the geometry. Return NULL if there is no linestring in the geometry.

    **Note**

    Index is 1-based as for OGC specs since version 0.8.0. Previous
    versions implemented this as 0-based instead.

    **Note**

    If you want to get the nth point of each line string in a
    multilinestring, use in conjunction with ST\_Dump

SFS\_COMPLIANT

SQLMM\_COMPLIANT SQL-MM 3: 7.2.5, 7.3.5

Z\_SUPPORT

CURVE\_SUPPORT

    **Note**

    Changed: 2.0.0 no longer works with single geometry
    multilinestrings. In older versions of PostGIS -- a single line
    multilinestring would work happily with this function and return the
    start point. In 2.0.0 it just returns NULL like any other
    multilinestring.

Examples
--------

::

    -- Extract all POINTs from a LINESTRING
    SELECT ST_AsText(
       ST_PointN(
          column1,
          generate_series(1, ST_NPoints(column1))
       ))
    FROM ( VALUES ('LINESTRING(0 0, 1 1, 2 2)'::geometry) ) AS foo;

     st_astext
    ------------
     POINT(0 0)
     POINT(1 1)
     POINT(2 2)
    (3 rows)

    --Example circular string
    SELECT ST_AsText(ST_PointN(ST_GeomFromText('CIRCULARSTRING(1 2, 3 2, 1 2)'),2));

    st_astext
    ----------
    POINT(3 2)

See Also
--------

?

ST\_SRID
Returns the spatial reference identifier for the ST\_Geometry as defined
in spatial\_ref\_sys table.
integer
ST\_SRID
geometry
g1
Description
-----------

Returns the spatial reference identifier for the ST\_Geometry as defined
in spatial\_ref\_sys table. ?

    **Note**

    spatial\_ref\_sys table is a table that catalogs all spatial
    reference systems known to PostGIS and is used for transformations
    from one spatial reference system to another. So verifying you have
    the right spatial reference system identifier is important if you
    plan to ever transform your geometries.

SFS\_COMPLIANT s2.1.1.1

SQLMM\_COMPLIANT SQL-MM 3: 5.1.5

CURVE\_SUPPORT

Examples
--------

::

    SELECT ST_SRID(ST_GeomFromText('POINT(-71.1043 42.315)',4326));
            --result
            4326
            

See Also
--------

?, ?, ?, ?

ST\_StartPoint
Returns the first point of a
LINESTRING
geometry as a
POINT
.
geometry
ST\_StartPoint
geometry
geomA
Description
-----------

Returns the first point of a ``LINESTRING`` geometry as a ``POINT`` or
``NULL`` if the input parameter is not a ``LINESTRING``.

SQLMM\_COMPLIANT SQL-MM 3: 7.1.3

Z\_SUPPORT

    **Note**

    Changed: 2.0.0 no longer works with single geometry
    multilinestrings. In older versions of PostGIS -- a single line
    multilinestring would work happily with this function and return the
    start point. In 2.0.0 it just returns NULL like any other
    multilinestring. The older behavior was an undocumented feature, but
    people who assumed they had their data stored as LINESTRING may
    experience these returning NULL in 2.0 now.

Examples
--------

::

    SELECT ST_AsText(ST_StartPoint('LINESTRING(0 1, 0 2)'::geometry));
     st_astext
    ------------
     POINT(0 1)
    (1 row)

    SELECT ST_StartPoint('POINT(0 1)'::geometry) IS NULL AS is_null;
      is_null
    ----------
     t
    (1 row)

    --3d line
    SELECT ST_AsEWKT(ST_StartPoint('LINESTRING(0 1 1, 0 2 2)'::geometry));
     st_asewkt
    ------------
     POINT(0 1 1)
    (1 row)

See Also
--------

?, ?

ST\_Summary
Returns a text summary of the contents of the geometry.
text
ST\_Summary
geometry
g
text
ST\_Summary
geography
g
Description
-----------

Returns a text summary of the contents of the geometry.

Flags shown square brackets after the geometry type have the following
meaning:

-  M: has M ordinate

-  Z: has Z ordinate

-  B: has a cached bounding box

-  G: is geodetic (geography)

-  S: has spatial reference system

Availability: 1.2.2

Enhanced: 2.0.0 added support for geography

Enhanced: 2.1.0 S flag to denote if has a known spatial reference system

Examples
--------

::

    =# SELECT ST_Summary(ST_GeomFromText('LINESTRING(0 0, 1 1)')) as geom,
            ST_Summary(ST_GeogFromText('POLYGON((0 0, 1 1, 1 2, 1 1, 0 0))')) geog;
                geom             |          geog    
    -----------------------------+--------------------------
     LineString[B] with 2 points | Polygon[BGS] with 1 rings
                                 | ring 0 has 5 points
                                 :
    (1 row)


    =# SELECT ST_Summary(ST_GeogFromText('LINESTRING(0 0 1, 1 1 1)')) As geog_line,
            ST_Summary(ST_GeomFromText('SRID=4326;POLYGON((0 0 1, 1 1 2, 1 2 3, 1 1 1, 0 0 1))')) As geom_poly;
    ;
               geog_line             |        geom_poly
    -------------------------------- +--------------------------
     LineString[ZBGS] with 2 points | Polygon[ZBS] with 1 rings
                                    :    ring 0 has 5 points
                                    :
    (1 row)

See Also
--------

?, ?, ?, ?, ?, ?

?, ?, ?, ?

ST\_X
Return the X coordinate of the point, or NULL if not available. Input
must be a point.
float
ST\_X
geometry
a\_point
Description
-----------

Return the X coordinate of the point, or NULL if not available. Input
must be a point.

    **Note**

    If you want to get the max min x values of any geometry look at
    ST\_XMin, ST\_XMax functions.

SQLMM\_COMPLIANT SQL-MM 3: 6.1.3

Z\_SUPPORT

Examples
--------

::

    SELECT ST_X(ST_GeomFromEWKT('POINT(1 2 3 4)'));
     st_x
    ------
        1
    (1 row)

    SELECT ST_Y(ST_Centroid(ST_GeomFromEWKT('LINESTRING(1 2 3 4, 1 1 1 1)')));
     st_y
    ------
      1.5
    (1 row)

            

See Also
--------

?, ?, ?, ?, ?, ?, ?

ST\_XMax
Returns X maxima of a bounding box 2d or 3d or a geometry.
float
ST\_XMax
box3d
aGeomorBox2DorBox3D
Description
-----------

Returns X maxima of a bounding box 2d or 3d or a geometry.

    **Note**

    Although this function is only defined for box3d, it will work for
    box2d and geometry because of the auto-casting behavior defined for
    geometries and box2d. However you can not feed it a geometry or
    box2d text representation, since that will not auto-cast.

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    SELECT ST_XMax('BOX3D(1 2 3, 4 5 6)');
    st_xmax
    -------
    4

    SELECT ST_XMax(ST_GeomFromText('LINESTRING(1 3 4, 5 6 7)'));
    st_xmax
    -------
    5

    SELECT ST_XMax(CAST('BOX(-3 2, 3 4)' As box2d));
    st_xmax
    -------
    3
    --Observe THIS DOES NOT WORK because it will try to autocast the string representation to a BOX3D
    SELECT ST_XMax('LINESTRING(1 3, 5 6)');

    --ERROR:  BOX3D parser - doesnt start with BOX3D(

    SELECT ST_XMax(ST_GeomFromEWKT('CIRCULARSTRING(220268 150415 1,220227 150505 2,220227 150406 3)'));
    st_xmax
    --------
    220288.248780547
            

See Also
--------

?, ?, ?, ?, ?

ST\_XMin
Returns X minima of a bounding box 2d or 3d or a geometry.
float
ST\_XMin
box3d
aGeomorBox2DorBox3D
Description
-----------

Returns X minima of a bounding box 2d or 3d or a geometry.

    **Note**

    Although this function is only defined for box3d, it will work for
    box2d and geometry because of the auto-casting behavior defined for
    geometries and box2d. However you can not feed it a geometry or
    box2d text representation, since that will not auto-cast.

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    SELECT ST_XMin('BOX3D(1 2 3, 4 5 6)');
    st_xmin
    -------
    1

    SELECT ST_XMin(ST_GeomFromText('LINESTRING(1 3 4, 5 6 7)'));
    st_xmin
    -------
    1

    SELECT ST_XMin(CAST('BOX(-3 2, 3 4)' As box2d));
    st_xmin
    -------
    -3
    --Observe THIS DOES NOT WORK because it will try to autocast the string representation to a BOX3D
    SELECT ST_XMin('LINESTRING(1 3, 5 6)');

    --ERROR:  BOX3D parser - doesnt start with BOX3D(

    SELECT ST_XMin(ST_GeomFromEWKT('CIRCULARSTRING(220268 150415 1,220227 150505 2,220227 150406 3)'));
    st_xmin
    --------
    220186.995121892
            

See Also
--------

?, ?, ?, ?, ?

ST\_Y
Return the Y coordinate of the point, or NULL if not available. Input
must be a point.
float
ST\_Y
geometry
a\_point
Description
-----------

Return the Y coordinate of the point, or NULL if not available. Input
must be a point.

SFS\_COMPLIANT

SQLMM\_COMPLIANT SQL-MM 3: 6.1.4

Z\_SUPPORT

Examples
--------

::

    SELECT ST_Y(ST_GeomFromEWKT('POINT(1 2 3 4)'));
     st_y
    ------
        2
    (1 row)

    SELECT ST_Y(ST_Centroid(ST_GeomFromEWKT('LINESTRING(1 2 3 4, 1 1 1 1)')));
     st_y
    ------
      1.5
    (1 row)


            

See Also
--------

?, ?, ?, ?, ?, ?, ?

ST\_YMax
Returns Y maxima of a bounding box 2d or 3d or a geometry.
float
ST\_YMax
box3d
aGeomorBox2DorBox3D
Description
-----------

Returns Y maxima of a bounding box 2d or 3d or a geometry.

    **Note**

    Although this function is only defined for box3d, it will work for
    box2d and geometry because of the auto-casting behavior defined for
    geometries and box2d. However you can not feed it a geometry or
    box2d text representation, since that will not auto-cast.

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    SELECT ST_YMax('BOX3D(1 2 3, 4 5 6)');
    st_ymax
    -------
    5

    SELECT ST_YMax(ST_GeomFromText('LINESTRING(1 3 4, 5 6 7)'));
    st_ymax
    -------
    6

    SELECT ST_YMax(CAST('BOX(-3 2, 3 4)' As box2d));
    st_ymax
    -------
    4
    --Observe THIS DOES NOT WORK because it will try to autocast the string representation to a BOX3D
    SELECT ST_YMax('LINESTRING(1 3, 5 6)');

    --ERROR:  BOX3D parser - doesnt start with BOX3D(

    SELECT ST_YMax(ST_GeomFromEWKT('CIRCULARSTRING(220268 150415 1,220227 150505 2,220227 150406 3)'));
    st_ymax
    --------
    150506.126829327
            

See Also
--------

?, ?, ?, ?, ?

ST\_YMin
Returns Y minima of a bounding box 2d or 3d or a geometry.
float
ST\_YMin
box3d
aGeomorBox2DorBox3D
Description
-----------

Returns Y minima of a bounding box 2d or 3d or a geometry.

    **Note**

    Although this function is only defined for box3d, it will work for
    box2d and geometry because of the auto-casting behavior defined for
    geometries and box2d. However you can not feed it a geometry or
    box2d text representation, since that will not auto-cast.

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    SELECT ST_YMin('BOX3D(1 2 3, 4 5 6)');
    st_ymin
    -------
    2

    SELECT ST_YMin(ST_GeomFromText('LINESTRING(1 3 4, 5 6 7)'));
    st_ymin
    -------
    3

    SELECT ST_YMin(CAST('BOX(-3 2, 3 4)' As box2d));
    st_ymin
    -------
    2
    --Observe THIS DOES NOT WORK because it will try to autocast the string representation to a BOX3D
    SELECT ST_YMin('LINESTRING(1 3, 5 6)');

    --ERROR:  BOX3D parser - doesnt start with BOX3D(

    SELECT ST_YMin(ST_GeomFromEWKT('CIRCULARSTRING(220268 150415 1,220227 150505 2,220227 150406 3)'));
    st_ymin
    --------
    150406
            

See Also
--------

?, ?, ?, ?, ?, ?

ST\_Z
Return the Z coordinate of the point, or NULL if not available. Input
must be a point.
float
ST\_Z
geometry
a\_point
Description
-----------

Return the Z coordinate of the point, or NULL if not available. Input
must be a point.

SQLMM\_COMPLIANT

Z\_SUPPORT

Examples
--------

::

    SELECT ST_Z(ST_GeomFromEWKT('POINT(1 2 3 4)'));
     st_z
    ------
        3
    (1 row)

            

See Also
--------

?, ?, ?, ?, ?, ?

ST\_ZMax
Returns Z minima of a bounding box 2d or 3d or a geometry.
float
ST\_ZMax
box3d
aGeomorBox2DorBox3D
Description
-----------

Returns Z maxima of a bounding box 2d or 3d or a geometry.

    **Note**

    Although this function is only defined for box3d, it will work for
    box2d and geometry because of the auto-casting behavior defined for
    geometries and box2d. However you can not feed it a geometry or
    box2d text representation, since that will not auto-cast.

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    SELECT ST_ZMax('BOX3D(1 2 3, 4 5 6)');
    st_zmax
    -------
    6

    SELECT ST_ZMax(ST_GeomFromEWKT('LINESTRING(1 3 4, 5 6 7)'));
    st_zmax
    -------
    7

    SELECT ST_ZMax('BOX3D(-3 2 1, 3 4 1)' );
    st_zmax
    -------
    1
    --Observe THIS DOES NOT WORK because it will try to autocast the string representation to a BOX3D
    SELECT ST_ZMax('LINESTRING(1 3 4, 5 6 7)');

    --ERROR:  BOX3D parser - doesnt start with BOX3D(

    SELECT ST_ZMax(ST_GeomFromEWKT('CIRCULARSTRING(220268 150415 1,220227 150505 2,220227 150406 3)'));
    st_zmax
    --------
    3
            

See Also
--------

?, ?, ?, ?, ?, ?

ST\_Zmflag
Returns ZM (dimension semantic) flag of the geometries as a small int.
Values are: 0=2d, 1=3dm, 2=3dz, 3=4d.
smallint
ST\_Zmflag
geometry
geomA
Description
-----------

Returns ZM (dimension semantic) flag of the geometries as a small int.
Values are: 0=2d, 1=3dm, 2=3dz, 3=4d.

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    SELECT ST_Zmflag(ST_GeomFromEWKT('LINESTRING(1 2, 3 4)'));
     st_zmflag
    -----------
             0

    SELECT ST_Zmflag(ST_GeomFromEWKT('LINESTRINGM(1 2 3, 3 4 3)'));
     st_zmflag
    -----------
             1

    SELECT ST_Zmflag(ST_GeomFromEWKT('CIRCULARSTRING(1 2 3, 3 4 3, 5 6 3)'));
     st_zmflag
    -----------
             2
    SELECT ST_Zmflag(ST_GeomFromEWKT('POINT(1 2 3 4)'));
     st_zmflag
    -----------
             3

See Also
--------

?, ?, ?

ST\_ZMin
Returns Z minima of a bounding box 2d or 3d or a geometry.
float
ST\_ZMin
box3d
aGeomorBox2DorBox3D
Description
-----------

Returns Z minima of a bounding box 2d or 3d or a geometry.

    **Note**

    Although this function is only defined for box3d, it will work for
    box2d and geometry because of the auto-casting behavior defined for
    geometries and box2d. However you can not feed it a geometry or
    box2d text representation, since that will not auto-cast.

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    SELECT ST_ZMin('BOX3D(1 2 3, 4 5 6)');
    st_zmin
    -------
    3

    SELECT ST_ZMin(ST_GeomFromEWKT('LINESTRING(1 3 4, 5 6 7)'));
    st_zmin
    -------
    4

    SELECT ST_ZMin('BOX3D(-3 2 1, 3 4 1)' );
    st_zmin
    -------
    1
    --Observe THIS DOES NOT WORK because it will try to autocast the string representation to a BOX3D
    SELECT ST_ZMin('LINESTRING(1 3 4, 5 6 7)');

    --ERROR:  BOX3D parser - doesnt start with BOX3D(

    SELECT ST_ZMin(ST_GeomFromEWKT('CIRCULARSTRING(220268 150415 1,220227 150505 2,220227 150406 3)'));
    st_zmin
    --------
    1
            

See Also
--------

?, ?, ?, ?, ?, ?, ?
