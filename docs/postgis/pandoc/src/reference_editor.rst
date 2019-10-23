Geometry Editors
================

ST\_AddPoint Adds a point to a LineString before point <position>
(0-based index). geometry ST\_AddPoint geometry linestring geometry
point geometry ST\_AddPoint geometry linestring geometry point integer
position Description -----------

Adds a point to a LineString before point <position> (0-based index).
Third parameter can be omitted or set to -1 for appending.

Availability: 1.1.0

Z\_SUPPORT

Examples
--------

::

            --guarantee all linestrings in a table are closed
            --by adding the start point of each linestring to the end of the line string
            --only for those that are not closed
            UPDATE sometable
            SET the_geom = ST_AddPoint(the_geom, ST_StartPoint(the_geom))
            FROM sometable
            WHERE ST_IsClosed(the_geom) = false;

            --Adding point to a 3-d line
            SELECT ST_AsEWKT(ST_AddPoint(ST_GeomFromEWKT('LINESTRING(0 0 1, 1 1 1)'), ST_MakePoint(1, 2, 3)));

            --result
            st_asewkt
            ----------
            LINESTRING(0 0 1,1 1 1,1 2 3)
                

See Also
--------

?, ?

ST\_Affine Applies a 3d affine transformation to the geometry to do
things like translate, rotate, scale in one step. geometry ST\_Affine
geometry geomA float a float b float c float d float e float f float g
float h float i float xoff float yoff float zoff geometry ST\_Affine
geometry geomA float a float b float d float e float xoff float yoff
Description -----------

Applies a 3d affine transformation to the geometry to do things like
translate, rotate, scale in one step.

Version 1: The call

::

    ST_Affine(geom, a, b, c, d, e, f, g, h, i, xoff, yoff, zoff) 

represents the transformation matrix

::

    / a  b  c  xoff \
    | d  e  f  yoff |
    | g  h  i  zoff |
    \ 0  0  0     1 /

and the vertices are transformed as follows:

::

    x' = a*x + b*y + c*z + xoff
    y' = d*x + e*y + f*z + yoff
    z' = g*x + h*y + i*z + zoff

All of the translate / scale functions below are expressed via such an
affine transformation.

Version 2: Applies a 2d affine transformation to the geometry. The call

::

    ST_Affine(geom, a, b, d, e, xoff, yoff)

represents the transformation matrix

::

    /  a  b  0  xoff  \       /  a  b  xoff  \
    |  d  e  0  yoff  | rsp.  |  d  e  yoff  |
    |  0  0  1     0  |       \  0  0     1  /
    \  0  0  0     1  /

and the vertices are transformed as follows:

::

    x' = a*x + b*y + xoff
    y' = d*x + e*y + yoff
    z' = z 

This method is a subcase of the 3D method above.

Enhanced: 2.0.0 support for Polyhedral surfaces, Triangles and TIN was
introduced.

Availability: 1.1.2. Name changed from Affine to ST\_Affine in 1.2.2

    **Note**

    Prior to 1.3.4, this function crashes if used with geometries that
    contain CURVES. This is fixed in 1.3.4+

P\_SUPPORT

T\_SUPPORT

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    --Rotate a 3d line 180 degrees about the z axis.  Note this is long-hand for doing ST_Rotate();
     SELECT ST_AsEWKT(ST_Affine(the_geom,  cos(pi()), -sin(pi()), 0,  sin(pi()), cos(pi()), 0,  0, 0, 1,  0, 0, 0)) As using_affine,
         ST_AsEWKT(ST_Rotate(the_geom, pi())) As using_rotate
        FROM (SELECT ST_GeomFromEWKT('LINESTRING(1 2 3, 1 4 3)') As the_geom) As foo;
            using_affine         |        using_rotate
    -----------------------------+-----------------------------
     LINESTRING(-1 -2 3,-1 -4 3) | LINESTRING(-1 -2 3,-1 -4 3)
    (1 row)

    --Rotate a 3d line 180 degrees in both the x and z axis
    SELECT ST_AsEWKT(ST_Affine(the_geom, cos(pi()), -sin(pi()), 0, sin(pi()), cos(pi()), -sin(pi()), 0, sin(pi()), cos(pi()), 0, 0, 0))
        FROM (SELECT ST_GeomFromEWKT('LINESTRING(1 2 3, 1 4 3)') As the_geom) As foo;
               st_asewkt
    -------------------------------
     LINESTRING(-1 -2 -3,-1 -4 -3)
    (1 row)
            

See Also
--------

?, ?, ?, ?

ST\_Force2D Forces the geometries into a "2-dimensional mode" so that
all output representations will only have the X and Y coordinates.
geometry ST\_Force2D geometry geomA Description -----------

Forces the geometries into a "2-dimensional mode" so that all output
representations will only have the X and Y coordinates. This is useful
for force OGC-compliant output (since OGC only specifies 2-D
geometries).

Enhanced: 2.0.0 support for Polyhedral surfaces was introduced.

Changed: 2.1.0. Up to 2.0.x this was called ST\_Force\_2D.

CURVE\_SUPPORT

P\_SUPPORT

Z\_SUPPORT

Examples
--------

::

    SELECT ST_AsEWKT(ST_Force2D(ST_GeomFromEWKT('CIRCULARSTRING(1 1 2, 2 3 2, 4 5 2, 6 7 2, 5 6 2)')));
            st_asewkt
    -------------------------------------
    CIRCULARSTRING(1 1,2 3,4 5,6 7,5 6)

    SELECT  ST_AsEWKT(ST_Force2D('POLYGON((0 0 2,0 5 2,5 0 2,0 0 2),(1 1 2,3 1 2,1 3 2,1 1 2))'));

                      st_asewkt
    ----------------------------------------------
     POLYGON((0 0,0 5,5 0,0 0),(1 1,3 1,1 3,1 1))

See Also
--------

?

ST\_Force3D Forces the geometries into XYZ mode. This is an alias for
ST\_Force3DZ. geometry ST\_Force3D geometry geomA Description
-----------

Forces the geometries into XYZ mode. This is an alias for
ST\_Force\_3DZ. If a geometry has no Z component, then a 0 Z coordinate
is tacked on.

Enhanced: 2.0.0 support for Polyhedral surfaces was introduced.

Changed: 2.1.0. Up to 2.0.x this was called ST\_Force\_3D.

P\_SUPPORT

CURVE\_SUPPORT

Z\_SUPPORT

Examples
--------

::

            --Nothing happens to an already 3D geometry
            SELECT ST_AsEWKT(ST_Force3D(ST_GeomFromEWKT('CIRCULARSTRING(1 1 2, 2 3 2, 4 5 2, 6 7 2, 5 6 2)')));
                       st_asewkt
    -----------------------------------------------
     CIRCULARSTRING(1 1 2,2 3 2,4 5 2,6 7 2,5 6 2)


    SELECT  ST_AsEWKT(ST_Force3D('POLYGON((0 0,0 5,5 0,0 0),(1 1,3 1,1 3,1 1))'));

                             st_asewkt
    --------------------------------------------------------------
     POLYGON((0 0 0,0 5 0,5 0 0,0 0 0),(1 1 0,3 1 0,1 3 0,1 1 0))
            

See Also
--------

?, ?, ?, ?

ST\_Force3DZ Forces the geometries into XYZ mode. This is a synonym for
ST\_Force3D. geometry ST\_Force3DZ geometry geomA Description
-----------

Forces the geometries into XYZ mode. This is a synonym for ST\_Force3DZ.
If a geometry has no Z component, then a 0 Z coordinate is tacked on.

Enhanced: 2.0.0 support for Polyhedral surfaces was introduced.

Changed: 2.1.0. Up to 2.0.x this was called ST\_Force\_3DZ.

P\_SUPPORT

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    --Nothing happens to an already 3D geometry
    SELECT ST_AsEWKT(ST_Force3DZ(ST_GeomFromEWKT('CIRCULARSTRING(1 1 2, 2 3 2, 4 5 2, 6 7 2, 5 6 2)')));
                       st_asewkt
    -----------------------------------------------
     CIRCULARSTRING(1 1 2,2 3 2,4 5 2,6 7 2,5 6 2)


    SELECT  ST_AsEWKT(ST_Force3DZ('POLYGON((0 0,0 5,5 0,0 0),(1 1,3 1,1 3,1 1))'));

                             st_asewkt
    --------------------------------------------------------------
     POLYGON((0 0 0,0 5 0,5 0 0,0 0 0),(1 1 0,3 1 0,1 3 0,1 1 0))
            

See Also
--------

?, ?, ?, ?

ST\_Force3DM Forces the geometries into XYM mode. geometry ST\_Force3DM
geometry geomA Description -----------

Forces the geometries into XYM mode. If a geometry has no M component,
then a 0 M coordinate is tacked on. If it has a Z component, then Z is
removed

Changed: 2.1.0. Up to 2.0.x this was called ST\_Force\_3DM.

CURVE\_SUPPORT

Examples
--------

::

    --Nothing happens to an already 3D geometry
    SELECT ST_AsEWKT(ST_Force3DM(ST_GeomFromEWKT('CIRCULARSTRING(1 1 2, 2 3 2, 4 5 2, 6 7 2, 5 6 2)')));
                       st_asewkt
    ------------------------------------------------
     CIRCULARSTRINGM(1 1 0,2 3 0,4 5 0,6 7 0,5 6 0)


    SELECT  ST_AsEWKT(ST_Force3DM('POLYGON((0 0 1,0 5 1,5 0 1,0 0 1),(1 1 1,3 1 1,1 3 1,1 1 1))'));

                              st_asewkt
    ---------------------------------------------------------------
     POLYGONM((0 0 0,0 5 0,5 0 0,0 0 0),(1 1 0,3 1 0,1 3 0,1 1 0))

See Also
--------

?, ?, ?, ?, ?

ST\_Force4D Forces the geometries into XYZM mode. geometry ST\_Force4D
geometry geomA Description -----------

Forces the geometries into XYZM mode. 0 is tacked on for missing Z and M
dimensions.

Changed: 2.1.0. Up to 2.0.x this was called ST\_Force\_4D.

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    --Nothing happens to an already 3D geometry
    SELECT ST_AsEWKT(ST_Force4D(ST_GeomFromEWKT('CIRCULARSTRING(1 1 2, 2 3 2, 4 5 2, 6 7 2, 5 6 2)')));
                            st_asewkt
    ---------------------------------------------------------
     CIRCULARSTRING(1 1 2 0,2 3 2 0,4 5 2 0,6 7 2 0,5 6 2 0)



    SELECT  ST_AsEWKT(ST_Force4D('MULTILINESTRINGM((0 0 1,0 5 2,5 0 3,0 0 4),(1 1 1,3 1 1,1 3 1,1 1 1))'));

                                          st_asewkt
    --------------------------------------------------------------------------------------
     MULTILINESTRING((0 0 0 1,0 5 0 2,5 0 0 3,0 0 0 4),(1 1 0 1,3 1 0 1,1 3 0 1,1 1 0 1))

See Also
--------

?, ?, ?, ?

ST\_ForceCollection Converts the geometry into a GEOMETRYCOLLECTION.
geometry ST\_ForceCollection geometry geomA Description -----------

Converts the geometry into a GEOMETRYCOLLECTION. This is useful for
simplifying the WKB representation.

Enhanced: 2.0.0 support for Polyhedral surfaces was introduced.

Availability: 1.2.2, prior to 1.3.4 this function will crash with
Curves. This is fixed in 1.3.4+

Changed: 2.1.0. Up to 2.0.x this was called ST\_Force\_Collection.

P\_SUPPORT

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    SELECT  ST_AsEWKT(ST_ForceCollection('POLYGON((0 0 1,0 5 1,5 0 1,0 0 1),(1 1 1,3 1 1,1 3 1,1 1 1))'));

                                       st_asewkt
    ----------------------------------------------------------------------------------
     GEOMETRYCOLLECTION(POLYGON((0 0 1,0 5 1,5 0 1,0 0 1),(1 1 1,3 1 1,1 3 1,1 1 1)))


      SELECT ST_AsText(ST_ForceCollection('CIRCULARSTRING(220227 150406,2220227 150407,220227 150406)'));
                                       st_astext
    --------------------------------------------------------------------------------
     GEOMETRYCOLLECTION(CIRCULARSTRING(220227 150406,2220227 150407,220227 150406))
    (1 row)



    -- POLYHEDRAL example --
    SELECT ST_AsEWKT(ST_ForceCollection('POLYHEDRALSURFACE(((0 0 0,0 0 1,0 1 1,0 1 0,0 0 0)),
     ((0 0 0,0 1 0,1 1 0,1 0 0,0 0 0)),
     ((0 0 0,1 0 0,1 0 1,0 0 1,0 0 0)),
     ((1 1 0,1 1 1,1 0 1,1 0 0,1 1 0)),
     ((0 1 0,0 1 1,1 1 1,1 1 0,0 1 0)),
     ((0 0 1,1 0 1,1 1 1,0 1 1,0 0 1)))'))

                                       st_asewkt
    ----------------------------------------------------------------------------------
    GEOMETRYCOLLECTION(
      POLYGON((0 0 0,0 0 1,0 1 1,0 1 0,0 0 0)),
      POLYGON((0 0 0,0 1 0,1 1 0,1 0 0,0 0 0)),
      POLYGON((0 0 0,1 0 0,1 0 1,0 0 1,0 0 0)),
      POLYGON((1 1 0,1 1 1,1 0 1,1 0 0,1 1 0)),
      POLYGON((0 1 0,0 1 1,1 1 1,1 1 0,0 1 0)),
      POLYGON((0 0 1,1 0 1,1 1 1,0 1 1,0 0 1))
    )
            

See Also
--------

?, ?, ?, ?, ?

ST\_ForceSFS Forces the geometries to use SFS 1.1 geometry types only.
geometry ST\_ForceSFS geometry geomA geometry ST\_ForceSFS geometry
geomA text version Description -----------

P\_SUPPORT

T\_SUPPORT

CURVE\_SUPPORT

Z\_SUPPORT

ST\_ForceRHR Forces the orientation of the vertices in a polygon to
follow the Right-Hand-Rule. boolean ST\_ForceRHR geometry g Description
-----------

Forces the orientation of the vertices in a polygon to follow the
Right-Hand-Rule. In GIS terminology, this means that the area that is
bounded by the polygon is to the right of the boundary. In particular,
the exterior ring is orientated in a clockwise direction and the
interior rings in a counter-clockwise direction.

Enhanced: 2.0.0 support for Polyhedral surfaces was introduced.

Z\_SUPPORT

P\_SUPPORT

Examples
--------

::

    SELECT ST_AsEWKT(
      ST_ForceRHR(
        'POLYGON((0 0 2, 5 0 2, 0 5 2, 0 0 2),(1 1 2, 1 3 2, 3 1 2, 1 1 2))'
      )
    );
                              st_asewkt
    --------------------------------------------------------------
     POLYGON((0 0 2,0 5 2,5 0 2,0 0 2),(1 1 2,3 1 2,1 3 2,1 1 2))
    (1 row)

See Also
--------

?, ?, ?

ST\_LineMerge Returns a (set of) LineString(s) formed by sewing together
a MULTILINESTRING. geometry ST\_LineMerge geometry amultilinestring
Description -----------

Returns a (set of) LineString(s) formed by sewing together the
constituent line work of a MULTILINESTRING.

    **Note**

    Only use with MULTILINESTRING/LINESTRINGs. If you feed a polygon or
    geometry collection into this function, it will return an empty
    GEOMETRYCOLLECTION

Availability: 1.1.0

    **Note**

    requires GEOS >= 2.1.0

Examples
--------

::

    SELECT ST_AsText(ST_LineMerge(
    ST_GeomFromText('MULTILINESTRING((-29 -27,-30 -29.7,-36 -31,-45 -33),(-45 -33,-46 -32))')
            )
    );
    st_astext
    --------------------------------------------------------------------------------------------------
    LINESTRING(-29 -27,-30 -29.7,-36 -31,-45 -33,-46 -32)
    (1 row)

    --If can't be merged - original MULTILINESTRING is returned
    SELECT ST_AsText(ST_LineMerge(
    ST_GeomFromText('MULTILINESTRING((-29 -27,-30 -29.7,-36 -31,-45 -33),(-45.2 -33.2,-46 -32))')
    )
    );
    st_astext
    ----------------
    MULTILINESTRING((-45.2 -33.2,-46 -32),(-29 -27,-30 -29.7,-36 -31,-45 -33))
                

See Also
--------

?, ?

ST\_CollectionExtract Given a (multi)geometry, returns a (multi)geometry
consisting only of elements of the specified type. geometry
ST\_CollectionExtract geometry collection integer type Description
-----------

Given a (multi)geometry, returns a (multi)geometry consisting only of
elements of the specified type. Sub-geometries that are not the
specified type are ignored. If there are no sub-geometries of the right
type, an EMPTY geometry will be returned. Only points, lines and
polygons are supported. Type numbers are 1 == POINT, 2 == LINESTRING, 3
== POLYGON.

    **Warning**

    When a multipolygon is returned the multipolygon may have shared
    edges. This results in an invalid multipolygon.

Availability: 1.5.0

    **Note**

    Prior to 1.5.3 this function returned non-collection inputs
    untouched, no matter type. In 1.5.3 non-matching single geometries
    result in a NULL return. In of 2.0.0 every case of missing match
    results in a typed EMPTY return.

Examples
--------

::

    -- Constants: 1 == POINT, 2 == LINESTRING, 3 == POLYGON
    SELECT ST_AsText(ST_CollectionExtract(ST_GeomFromText('GEOMETRYCOLLECTION(GEOMETRYCOLLECTION(POINT(0 0)))'),1));
    st_astext
    ---------------
    MULTIPOINT(0 0)
    (1 row)

    SELECT ST_AsText(ST_CollectionExtract(ST_GeomFromText('GEOMETRYCOLLECTION(GEOMETRYCOLLECTION(LINESTRING(0 0, 1 1)),LINESTRING(2 2, 3 3))'),2));
    st_astext
    ---------------
    MULTILINESTRING((0 0, 1 1), (2 2, 3 3))
    (1 row)
                

See Also
--------

?, ?, ?

ST\_CollectionHomogenize Given a geometry collection, returns the
"simplest" representation of the contents. geometry
ST\_CollectionHomogenize geometry collection Description -----------

Given a geometry collection, returns the "simplest" representation of
the contents. Singletons will be returned as singletons. Collections
that are homogeneous will be returned as the appropriate multi-type.

    **Warning**

    When a multipolygon is returned the multipolygon may have shared
    edges. This results in an invalid multipolygon.

Availability: 2.0.0

Examples
--------

::

      SELECT ST_AsText(ST_CollectionHomogenize('GEOMETRYCOLLECTION(POINT(0 0))'));  

        st_astext
        ------------
         POINT(0 0)
        (1 row)

      SELECT ST_AsText(ST_CollectionHomogenize('GEOMETRYCOLLECTION(POINT(0 0),POINT(1 1))'));   

        st_astext
        ---------------------
         MULTIPOINT(0 0,1 1)
        (1 row)

See Also
--------

?, ?

ST\_Multi Returns the geometry as a MULTI\* geometry. If the geometry is
already a MULTI\*, it is returned unchanged. geometry ST\_Multi geometry
g1 Description -----------

Returns the geometry as a MULTI\* geometry. If the geometry is already a
MULTI\*, it is returned unchanged.

Examples
--------

::

    SELECT ST_AsText(ST_Multi(ST_GeomFromText('POLYGON((743238 2967416,743238 2967450,
                743265 2967450,743265.625 2967416,743238 2967416))')));
                st_astext
                --------------------------------------------------------------------------------------------------
                MULTIPOLYGON(((743238 2967416,743238 2967450,743265 2967450,743265.625 2967416,
                743238 2967416)))
                (1 row)
                

See Also
--------

?

ST\_RemovePoint Removes point from a linestring. Offset is 0-based.
geometry ST\_RemovePoint geometry linestring integer offset Description
-----------

Removes point from a linestring. Useful for turning a closed ring into
an open line string

Availability: 1.1.0

Z\_SUPPORT

Examples
--------

::

    --guarantee no LINESTRINGS are closed
    --by removing the end point.  The below assumes the_geom is of type LINESTRING
    UPDATE sometable
        SET the_geom = ST_RemovePoint(the_geom, ST_NPoints(the_geom) - 1)
        FROM sometable
        WHERE ST_IsClosed(the_geom) = true;
            

See Also
--------

?, ?, ?

ST\_Reverse Returns the geometry with vertex order reversed. geometry
ST\_Reverse geometry g1 Description -----------

Can be used on any geometry and reverses the order of the vertexes.

Examples
--------

::

    SELECT ST_AsText(the_geom) as line, ST_AsText(ST_Reverse(the_geom)) As reverseline
    FROM
    (SELECT ST_MakeLine(ST_MakePoint(1,2),
            ST_MakePoint(1,10)) As the_geom) as foo;
    --result
            line         |     reverseline
    ---------------------+----------------------
    LINESTRING(1 2,1 10) | LINESTRING(1 10,1 2)

ST\_Rotate Rotate a geometry rotRadians counter-clockwise about an
origin. geometry ST\_Rotate geometry geomA float rotRadians geometry
ST\_Rotate geometry geomA float rotRadians float x0 float y0 geometry
ST\_Rotate geometry geomA float rotRadians geometry pointOrigin
Description -----------

Rotates geometry rotRadians counter-clockwise about the origin. The
rotation origin can be specified either as a POINT geometry, or as x and
y coordinates. If the origin is not specified, the geometry is rotated
about POINT(0 0).

Enhanced: 2.0.0 support for Polyhedral surfaces, Triangles and TIN was
introduced.

Enhanced: 2.0.0 additional parameters for specifying the origin of
rotation were added.

Availability: 1.1.2. Name changed from Rotate to ST\_Rotate in 1.2.2

Z\_SUPPORT

CURVE\_SUPPORT

P\_SUPPORT

T\_SUPPORT

Examples
--------

::

    --Rotate 180 degrees
    SELECT ST_AsEWKT(ST_Rotate('LINESTRING (50 160, 50 50, 100 50)', pi()));
                   st_asewkt
    ---------------------------------------
     LINESTRING(-50 -160,-50 -50,-100 -50)
    (1 row)

    --Rotate 30 degrees counter-clockwise at x=50, y=160
    SELECT ST_AsEWKT(ST_Rotate('LINESTRING (50 160, 50 50, 100 50)', pi()/6, 50, 160));
                                     st_asewkt
    ---------------------------------------------------------------------------
     LINESTRING(50 160,105 64.7372055837117,148.301270189222 89.7372055837117)
    (1 row)

    --Rotate 60 degrees clockwise from centroid
    SELECT ST_AsEWKT(ST_Rotate(geom, -pi()/3, ST_Centroid(geom)))
    FROM (SELECT 'LINESTRING (50 160, 50 50, 100 50)'::geometry AS geom) AS foo;
                               st_asewkt
    --------------------------------------------------------------
     LINESTRING(116.4225 130.6721,21.1597 75.6721,46.1597 32.3708)
    (1 row)
            

See Also
--------

?, ?, ?, ?

ST\_RotateX Rotate a geometry rotRadians about the X axis. geometry
ST\_RotateX geometry geomA float rotRadians Description -----------

Rotate a geometry geomA - rotRadians about the X axis.

    **Note**

    ``ST_RotateX(geomA,  rotRadians)`` is short-hand for
    ``ST_Affine(geomA, 1, 0, 0, 0, cos(rotRadians), -sin(rotRadians), 0, sin(rotRadians), cos(rotRadians), 0, 0, 0)``.

Enhanced: 2.0.0 support for Polyhedral surfaces, Triangles and TIN was
introduced.

Availability: 1.1.2. Name changed from RotateX to ST\_RotateX in 1.2.2

P\_SUPPORT

Z\_SUPPORT

T\_SUPPORT

Examples
--------

::

    --Rotate a line 90 degrees along x-axis
    SELECT ST_AsEWKT(ST_RotateX(ST_GeomFromEWKT('LINESTRING(1 2 3, 1 1 1)'), pi()/2));
             st_asewkt
    ---------------------------
     LINESTRING(1 -3 2,1 -1 1)

See Also
--------

?, ?, ?

ST\_RotateY Rotate a geometry rotRadians about the Y axis. geometry
ST\_RotateY geometry geomA float rotRadians Description -----------

Rotate a geometry geomA - rotRadians about the y axis.

    **Note**

    ``ST_RotateY(geomA,  rotRadians)`` is short-hand for
    ``ST_Affine(geomA,  cos(rotRadians), 0, sin(rotRadians),  0, 1, 0,  -sin(rotRadians), 0, cos(rotRadians), 0,  0, 0)``.

Availability: 1.1.2. Name changed from RotateY to ST\_RotateY in 1.2.2

Enhanced: 2.0.0 support for Polyhedral surfaces, Triangles and TIN was
introduced.

P\_SUPPORT

Z\_SUPPORT

T\_SUPPORT

Examples
--------

::

    --Rotate a line 90 degrees along y-axis
     SELECT ST_AsEWKT(ST_RotateY(ST_GeomFromEWKT('LINESTRING(1 2 3, 1 1 1)'), pi()/2));
             st_asewkt
    ---------------------------
     LINESTRING(3 2 -1,1 1 -1)

See Also
--------

?, ?, ?

ST\_RotateZ Rotate a geometry rotRadians about the Z axis. geometry
ST\_RotateZ geometry geomA float rotRadians Description -----------

Rotate a geometry geomA - rotRadians about the Z axis.

    **Note**

    This is a synonym for ST\_Rotate

    **Note**

    ``ST_RotateZ(geomA,  rotRadians)`` is short-hand for
    ``SELECT ST_Affine(geomA,  cos(rotRadians), -sin(rotRadians), 0,  sin(rotRadians), cos(rotRadians), 0,  0, 0, 1,  0, 0, 0)``.

Enhanced: 2.0.0 support for Polyhedral surfaces, Triangles and TIN was
introduced.

Availability: 1.1.2. Name changed from RotateZ to ST\_RotateZ in 1.2.2

    **Note**

    Prior to 1.3.4, this function crashes if used with geometries that
    contain CURVES. This is fixed in 1.3.4+

Z\_SUPPORT

CURVE\_SUPPORT

P\_SUPPORT

T\_SUPPORT

Examples
--------

::

    --Rotate a line 90 degrees along z-axis
    SELECT ST_AsEWKT(ST_RotateZ(ST_GeomFromEWKT('LINESTRING(1 2 3, 1 1 1)'), pi()/2));
             st_asewkt
    ---------------------------
     LINESTRING(-2 1 3,-1 1 1)

     --Rotate a curved circle around z-axis
    SELECT ST_AsEWKT(ST_RotateZ(the_geom, pi()/2))
    FROM (SELECT ST_LineToCurve(ST_Buffer(ST_GeomFromText('POINT(234 567)'), 3)) As the_geom) As foo;

                                                           st_asewkt
    ----------------------------------------------------------------------------------------------------------------------------
     CURVEPOLYGON(CIRCULARSTRING(-567 237,-564.87867965644 236.12132034356,-564 234,-569.12132034356 231.87867965644,-567 237))

See Also
--------

?, ?, ?

ST\_Scale Scales the geometry to a new size by multiplying the ordinates
with the parameters. Ie: ST\_Scale(geom, Xfactor, Yfactor, Zfactor).
geometry ST\_Scale geometry geomA float XFactor float YFactor float
ZFactor geometry ST\_Scale geometry geomA float XFactor float YFactor
Description -----------

Scales the geometry to a new size by multiplying the ordinates with the
parameters. Ie: ST\_Scale(geom, Xfactor, Yfactor, Zfactor).

    **Note**

    ``ST_Scale(geomA,  XFactor, YFactor, ZFactor)`` is short-hand for
    ``ST_Affine(geomA,  XFactor, 0, 0,  0, YFactor, 0,  0, 0, ZFactor,  0, 0, 0)``.

    **Note**

    Prior to 1.3.4, this function crashes if used with geometries that
    contain CURVES. This is fixed in 1.3.4+

Availability: 1.1.0.

Enhanced: 2.0.0 support for Polyhedral surfaces, Triangles and TIN was
introduced.

P\_SUPPORT

Z\_SUPPORT

CURVE\_SUPPORT

T\_SUPPORT

Examples
--------

::

    --Version 1: scale X, Y, Z
    SELECT ST_AsEWKT(ST_Scale(ST_GeomFromEWKT('LINESTRING(1 2 3, 1 1 1)'), 0.5, 0.75, 0.8));
                  st_asewkt
    --------------------------------------
     LINESTRING(0.5 1.5 2.4,0.5 0.75 0.8)

    --Version 2: Scale X Y
     SELECT ST_AsEWKT(ST_Scale(ST_GeomFromEWKT('LINESTRING(1 2 3, 1 1 1)'), 0.5, 0.75));
                st_asewkt
    ----------------------------------
     LINESTRING(0.5 1.5 3,0.5 0.75 1)

See Also
--------

?, ?

ST\_Segmentize Return a modified geometry/geography having no segment
longer than the given distance. Distance computation is performed in 2d
only. For geometry, length units are in units of spatial reference. For
geography, units are in meters. geometry ST\_Segmentize geometry geom
float max\_segment\_length geometry ST\_Segmentize geography geog float
max\_segment\_length Description -----------

Returns a modified geometry having no segment longer than the given
``max_segment_length``. Distance computation is performed in 2d only.
For geometry, length units are in units of spatial reference. For
geography, units are in meters.

Availability: 1.2.2

Enhanced: 2.1.0 support for geography was introduced.

Changed: 2.1.0 As a result of the introduction of geography support: The
construct ``SELECT ST_Segmentize('LINESTRING(1 2, 3 4)',0.5);`` will
result in ambiguous function error. You need to have properly typed
object e.g. a geometry/geography column, use ST\_GeomFromText,
ST\_GeogFromText or
``SELECT ST_Segmentize('LINESTRING(1 2, 3 4)'::geometry,0.5);``

    **Note**

    This will only increase segments. It will not lengthen segments
    shorter than max length

Examples
--------

::

    SELECT ST_AsText(ST_Segmentize(
    ST_GeomFromText('MULTILINESTRING((-29 -27,-30 -29.7,-36 -31,-45 -33),(-45 -33,-46 -32))')
            ,5)
    );
    st_astext
    --------------------------------------------------------------------------------------------------
    MULTILINESTRING((-29 -27,-30 -29.7,-34.886615700134 -30.758766735029,-36 -31,
    -40.8809353009198 -32.0846522890933,-45 -33),
    (-45 -33,-46 -32))
    (1 row)

    SELECT ST_AsText(ST_Segmentize(ST_GeomFromText('POLYGON((-29 28, -30 40, -29 28))'),10));
    st_astext
    -----------------------
    POLYGON((-29 28,-29.8304547985374 37.9654575824488,-30 40,-29.1695452014626 30.0345424175512,-29 28))
    (1 row)

See Also
--------

?

ST\_SetPoint Replace point N of linestring with given point. Index is
0-based. geometry ST\_SetPoint geometry linestring integer
zerobasedposition geometry point Description -----------

Replace point N of linestring with given point. Index is 0-based. This
is especially useful in triggers when trying to maintain relationship of
joints when one vertex moves.

Availability: 1.1.0

Z\_SUPPORT

Examples
--------

::

    --Change first point in line string from -1 3 to -1 1
    SELECT ST_AsText(ST_SetPoint('LINESTRING(-1 2,-1 3)', 0, 'POINT(-1 1)'));
           st_astext
    -----------------------
     LINESTRING(-1 1,-1 3)

    ---Change last point in a line string (lets play with 3d linestring this time)
    SELECT ST_AsEWKT(ST_SetPoint(foo.the_geom, ST_NumPoints(foo.the_geom) - 1, ST_GeomFromEWKT('POINT(-1 1 3)')))
    FROM (SELECT ST_GeomFromEWKT('LINESTRING(-1 2 3,-1 3 4, 5 6 7)') As the_geom) As foo;
           st_asewkt
    -----------------------
    LINESTRING(-1 2 3,-1 3 4,-1 1 3)
                

See Also
--------

?, ?, ?, ?, ?

ST\_SetSRID Sets the SRID on a geometry to a particular integer value.
geometry ST\_SetSRID geometry geom integer srid Description -----------

Sets the SRID on a geometry to a particular integer value. Useful in
constructing bounding boxes for queries.

    **Note**

    This function does not transform the geometry coordinates in any way
    - it simply sets the meta data defining the spatial reference system
    the geometry is assumed to be in. Use ? if you want to transform the
    geometry into a new projection.

SFS\_COMPLIANT

CURVE\_SUPPORT

Examples
--------

-- Mark a point as WGS 84 long lat --

::

    SELECT ST_SetSRID(ST_Point(-123.365556, 48.428611),4326) As wgs84long_lat;
    -- the ewkt representation (wrap with ST_AsEWKT) -
    SRID=4326;POINT(-123.365556 48.428611)
                

-- Mark a point as WGS 84 long lat and then transform to web mercator
(Spherical Mercator) --

::

    SELECT ST_Transform(ST_SetSRID(ST_Point(-123.365556, 48.428611),4326),3785) As spere_merc;
    -- the ewkt representation (wrap with ST_AsEWKT) -
    SRID=3785;POINT(-13732990.8753491 6178458.96425423)
                

See Also
--------

?, ?, ?, ?, ?, ?

ST\_SnapToGrid Snap all points of the input geometry to a regular grid.
geometry ST\_SnapToGrid geometry geomA float originX float originY float
sizeX float sizeY geometry ST\_SnapToGrid geometry geomA float sizeX
float sizeY geometry ST\_SnapToGrid geometry geomA float size geometry
ST\_SnapToGrid geometry geomA geometry pointOrigin float sizeX float
sizeY float sizeZ float sizeM Description -----------

Variant 1,2,3: Snap all points of the input geometry to the grid defined
by its origin and cell size. Remove consecutive points falling on the
same cell, eventually returning NULL if output points are not enough to
define a geometry of the given type. Collapsed geometries in a
collection are stripped from it. Useful for reducing precision.

Variant 4: Introduced 1.1.0 - Snap all points of the input geometry to
the grid defined by its origin (the second argument, must be a point)
and cell sizes. Specify 0 as size for any dimension you don't want to
snap to a grid.

    **Note**

    The returned geometry might loose its simplicity (see ?).

    **Note**

    Before release 1.1.0 this function always returned a 2d geometry.
    Starting at 1.1.0 the returned geometry will have same
    dimensionality as the input one with higher dimension values
    untouched. Use the version taking a second geometry argument to
    define all grid dimensions.

Availability: 1.0.0RC1

Availability: 1.1.0 - Z and M support

Z\_SUPPORT

Examples
--------

::

    --Snap your geometries to a precision grid of 10^-3
    UPDATE mytable
       SET the_geom = ST_SnapToGrid(the_geom, 0.001);

    SELECT ST_AsText(ST_SnapToGrid(
                ST_GeomFromText('LINESTRING(1.1115678 2.123, 4.111111 3.2374897, 4.11112 3.23748667)'),
                0.001)
            );
                  st_astext
    -------------------------------------
     LINESTRING(1.112 2.123,4.111 3.237)
     --Snap a 4d geometry
    SELECT ST_AsEWKT(ST_SnapToGrid(
        ST_GeomFromEWKT('LINESTRING(-1.1115678 2.123 2.3456 1.11111,
            4.111111 3.2374897 3.1234 1.1111, -1.11111112 2.123 2.3456 1.1111112)'),
     ST_GeomFromEWKT('POINT(1.12 2.22 3.2 4.4444)'),
     0.1, 0.1, 0.1, 0.01) );
                                      st_asewkt
    ------------------------------------------------------------------------------
     LINESTRING(-1.08 2.12 2.3 1.1144,4.12 3.22 3.1 1.1144,-1.08 2.12 2.3 1.1144)


    --With a 4d geometry - the ST_SnapToGrid(geom,size) only touches x and y coords but keeps m and z the same
    SELECT ST_AsEWKT(ST_SnapToGrid(ST_GeomFromEWKT('LINESTRING(-1.1115678 2.123 3 2.3456,
            4.111111 3.2374897 3.1234 1.1111)'),
           0.01)      );
                            st_asewkt
    ---------------------------------------------------------
     LINESTRING(-1.11 2.12 3 2.3456,4.11 3.24 3.1234 1.1111)

See Also
--------

?, ?, ?, ?, ?, ?

ST\_Snap Snap segments and vertices of input geometry to vertices of a
reference geometry. geometry ST\_Snap geometry input geometry reference
float tolerance Description -----------

Snaps the vertices and segments of a geometry another Geometry's
vertices. A snap distance tolerance is used to control where snapping is
performed.

Snapping one geometry to another can improve robustness for overlay
operations by eliminating nearly-coincident edges (which cause problems
during noding and intersection calculation).

Too much snapping can result in invalid topology being created, so the
number and location of snapped vertices is decided using heuristics to
determine when it is safe to snap. This can result in some potential
snaps being omitted, however.

    **Note**

    The returned geometry might loose its simplicity (see ?) and
    validity (see ?).

Availability: 2.0.0 requires GEOS >= 3.3.0.

Examples
--------

+-------------------------------------+-------------------------------------+
| A multipolygon shown with a         |                                     |
| linestring (before any snapping)    |                                     |
+-------------------------------------+-------------------------------------+
| A multipolygon snapped to           | A multipolygon snapped to           |
| linestring to tolerance: 1.01 of    | linestring to tolerance: 1.25 of    |
| distance. The new multipolygon is   | distance. The new multipolygon is   |
| shown with reference linestring     | shown with reference linestring     |
+-------------------------------------+-------------------------------------+
| SELECT                              | SELECT ST\_AsText(                  |
| ST\_AsText(ST\_Snap(poly,line,      | ST\_Snap(poly,line,                 |
| ST\_Distance(poly,line)\*1.01)) AS  | ST\_Distance(poly,line)\*1.25) ) AS |
| polysnapped FROM (SELECT            | polysnapped FROM (SELECT            |
| ST\_GeomFromText('MULTIPOLYGON(     | ST\_GeomFromText('MULTIPOLYGON( ((  |
| ((26 125, 26 200, 126 200, 126 125, | 26 125, 26 200, 126 200, 126 125,   |
| 26 125 ), ( 51 150, 101 150, 76     | 26 125 ), ( 51 150, 101 150, 76     |
| 175, 51 150 )), (( 151 100, 151     | 175, 51 150 )), (( 151 100, 151     |
| 200, 176 175, 151 100 )))') As      | 200, 176 175, 151 100 )))') As      |
| poly, ST\_GeomFromText('LINESTRING  | poly, ST\_GeomFromText('LINESTRING  |
| (5 107, 54 84, 101 100)') As line   | (5 107, 54 84, 101 100)') As line   |
+-------------------------------------+-------------------------------------+
| polysnapped                         | ) As foo;                           |
| ----------------------------------- |                                     |
| ----------------------------------  |                                     |
| MULTIPOLYGON(((26 125,26 200,126    |                                     |
| 200,126 125,101 100,26 125), (51    |                                     |
| 150,101 150,76 175,51 150)),((151   |                                     |
| 100,151 200,176 175,151 100)))      |                                     |
+-------------------------------------+-------------------------------------+
| The linestring snapped to the       | The linestring snapped to the       |
| original multipolygon at tolerance  | original multipolygon at tolerance  |
| 1.01 of distance. The new           | 1.25 of distance. The new           |
| linestring is shown with reference  | linestring is shown with reference  |
| multipolygon                        | multipolygon                        |
+-------------------------------------+-------------------------------------+
| SELECT ST\_AsText( ST\_Snap(line,   | SELECT ST\_AsText( ST\_Snap(line,   |
| poly,                               | poly,                               |
| ST\_Distance(poly,line)\*1.01) ) AS | ST\_Distance(poly,line)\*1.25) ) AS |
| linesnapped FROM (SELECT            | linesnapped FROM (SELECT            |
| ST\_GeomFromText('MULTIPOLYGON(     | ST\_GeomFromText('MULTIPOLYGON( ((  |
| ((26 125, 26 200, 126 200, 126 125, | 26 125, 26 200, 126 200, 126 125,   |
| 26 125), (51 150, 101 150, 76 175,  | 26 125 ), (51 150, 101 150, 76 175, |
| 51 150 )), ((151 100, 151 200, 176  | 51 150 )), ((151 100, 151 200, 176  |
| 175, 151 100)))') As poly,          | 175, 151 100 )))') As poly,         |
| ST\_GeomFromText('LINESTRING (5     | ST\_GeomFromText('LINESTRING (5     |
| 107, 54 84, 101 100)') As line ) As | 107, 54 84, 101 100)') As line ) As |
| foo;                                | foo; linesnapped                    |
|                                     | ----------------------------------- |
|                                     | ----                                |
|                                     | LINESTRING(26 125,54 84,101 100)    |
+-------------------------------------+-------------------------------------+

See Also
--------

?

ST\_Transform Returns a new geometry with its coordinates transformed to
the SRID referenced by the integer parameter. geometry ST\_Transform
geometry g1 integer srid Description -----------

Returns a new geometry with its coordinates transformed to spatial
reference system referenced by the SRID integer parameter. The
destination SRID must exist in the ``SPATIAL_REF_SYS`` table.

ST\_Transform is often confused with ST\_SetSRID(). ST\_Transform
actually changes the coordinates of a geometry from one spatial
reference system to another, while ST\_SetSRID() simply changes the SRID
identifier of the geometry

    **Note**

    Requires PostGIS be compiled with Proj support. Use ? to confirm you
    have proj support compiled in.

    **Note**

    If using more than one transformation, it is useful to have a
    functional index on the commonly used transformations to take
    advantage of index usage.

    **Note**

    Prior to 1.3.4, this function crashes if used with geometries that
    contain CURVES. This is fixed in 1.3.4+

Enhanced: 2.0.0 support for Polyhedral surfaces was introduced.

SQLMM\_COMPLIANT SQL-MM 3: 5.1.6

CURVE\_SUPPORT

P\_SUPPORT

Examples
--------

Change Mass state plane US feet geometry to WGS 84 long lat

::

    SELECT ST_AsText(ST_Transform(ST_GeomFromText('POLYGON((743238 2967416,743238 2967450,
        743265 2967450,743265.625 2967416,743238 2967416))',2249),4326)) As wgs_geom;

     wgs_geom
    ---------------------------
     POLYGON((-71.1776848522251 42.3902896512902,-71.1776843766326 42.3903829478009,
    -71.1775844305465 42.3903826677917,-71.1775825927231 42.3902893647987,-71.177684
    8522251 42.3902896512902));
    (1 row)

    --3D Circular String example
    SELECT ST_AsEWKT(ST_Transform(ST_GeomFromEWKT('SRID=2249;CIRCULARSTRING(743238 2967416 1,743238 2967450 2,743265 2967450 3,743265.625 2967416 3,743238 2967416 4)'),4326));

                     st_asewkt
    --------------------------------------------------------------------------------------
     SRID=4326;CIRCULARSTRING(-71.1776848522251 42.3902896512902 1,-71.1776843766326 42.3903829478009 2,
     -71.1775844305465 42.3903826677917 3,
     -71.1775825927231 42.3902893647987 3,-71.1776848522251 42.3902896512902 4)

Example of creating a partial functional index. For tables where you are
not sure all the geometries will be filled in, its best to use a partial
index that leaves out null geometries which will both conserve space and
make your index smaller and more efficient.

::

    CREATE INDEX idx_the_geom_26986_parcels
      ON parcels
      USING gist
      (ST_Transform(the_geom, 26986))
      WHERE the_geom IS NOT NULL;
            

Configuring transformation behaviour
------------------------------------

Sometimes coordinate transformation involving a grid-shift can fail, for
example if PROJ.4 has not been built with grid-shift files or the
coordinate does not lie within the range for which the grid shift is
defined. By default, PostGIS will throw an error if a grid shift file is
not present, but this behaviour can be configured on a per-SRID basis by
altering the proj4text value within the spatial\_ref\_sys table.

For example, the proj4text parameter +datum=NAD87 is a shorthand form
for the following +nadgrids parameter:

::

    +nadgrids=@conus,@alaska,@ntv2_0.gsb,@ntv1_can.dat

The @ prefix means no error is reported if the files are not present,
but if the end of the list is reached with no file having been
appropriate (ie. found and overlapping) then an error is issued.

If, conversely, you wanted to ensure that at least the standard files
were present, but that if all files were scanned without a hit a null
transformation is applied you could use:

::

    +nadgrids=@conus,@alaska,@ntv2_0.gsb,@ntv1_can.dat,null

The null grid shift file is a valid grid shift file covering the whole
world and applying no shift. So for a complete example, if you wanted to
alter PostGIS so that transformations to SRID 4267 that didn't lie
within the correct range did not throw an ERROR, you would use the
following:

::

    UPDATE spatial_ref_sys SET proj4text = '+proj=longlat +ellps=clrk66 +nadgrids=@conus,@alaska,@ntv2_0.gsb,@ntv1_can.dat,null +no_defs' WHERE srid = 4267;

See Also
--------

?, ?, ?, ?

ST\_Translate Translates the geometry to a new location using the
numeric parameters as offsets. Ie: ST\_Translate(geom, X, Y) or
ST\_Translate(geom, X, Y,Z). geometry ST\_Translate geometry g1 float
deltax float deltay geometry ST\_Translate geometry g1 float deltax
float deltay float deltaz Description -----------

Returns a new geometry whose coordinates are translated delta x,delta
y,delta z units. Units are based on the units defined in spatial
reference (SRID) for this geometry.

    **Note**

    Prior to 1.3.4, this function crashes if used with geometries that
    contain CURVES. This is fixed in 1.3.4+

Availability: 1.2.2

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

Move a point 1 degree longitude

::

        SELECT ST_AsText(ST_Translate(ST_GeomFromText('POINT(-71.01 42.37)',4326),1,0)) As wgs_transgeomtxt;

        wgs_transgeomtxt
        ---------------------
        POINT(-70.01 42.37)
            

Move a linestring 1 degree longitude and 1/2 degree latitude

::

    SELECT ST_AsText(ST_Translate(ST_GeomFromText('LINESTRING(-71.01 42.37,-71.11 42.38)',4326),1,0.5)) As wgs_transgeomtxt;
               wgs_transgeomtxt
        ---------------------------------------
        LINESTRING(-70.01 42.87,-70.11 42.88)
            

Move a 3d point

::

    SELECT ST_AsEWKT(ST_Translate(CAST('POINT(0 0 0)' As geometry), 5, 12,3));
        st_asewkt
        ---------
        POINT(5 12 3)
            

Move a curve and a point

::

    SELECT ST_AsText(ST_Translate(ST_Collect('CURVEPOLYGON(CIRCULARSTRING(4 3,3.12 0.878,1 0,-1.121 5.1213,6 7, 8 9,4 3))','POINT(1 3)'),1,2));
                                                             st_astext
    ------------------------------------------------------------------------------------------------------------
     GEOMETRYCOLLECTION(CURVEPOLYGON(CIRCULARSTRING(5 5,4.12 2.878,2 2,-0.121 7.1213,7 9,9 11,5 5)),POINT(2 5))

See Also
--------

?, ?, ?

ST\_TransScale Translates the geometry using the deltaX and deltaY args,
then scales it using the XFactor, YFactor args, working in 2D only.
geometry ST\_TransScale geometry geomA float deltaX float deltaY float
XFactor float YFactor Description -----------

Translates the geometry using the deltaX and deltaY args, then scales it
using the XFactor, YFactor args, working in 2D only.

    **Note**

    ``ST_TransScale(geomA, deltaX, deltaY, XFactor, YFactor)`` is
    short-hand for
    ``ST_Affine(geomA, XFactor, 0, 0, 0, YFactor, 0,             0, 0, 1, deltaX*XFactor, deltaY*YFactor, 0)``.

    **Note**

    Prior to 1.3.4, this function crashes if used with geometries that
    contain CURVES. This is fixed in 1.3.4+

Availability: 1.1.0.

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    SELECT ST_AsEWKT(ST_TransScale(ST_GeomFromEWKT('LINESTRING(1 2 3, 1 1 1)'), 0.5, 1, 1, 2));
              st_asewkt
    -----------------------------
     LINESTRING(1.5 6 3,1.5 4 1)


    --Buffer a point to get an approximation of a circle, convert to curve and then translate 1,2 and scale it 3,4
      SELECT ST_AsText(ST_Transscale(ST_LineToCurve(ST_Buffer('POINT(234 567)', 3)),1,2,3,4));
                                                              st_astext
    ------------------------------------------------------------------------------------------------------------------------------
     CURVEPOLYGON(CIRCULARSTRING(714 2276,711.363961030679 2267.51471862576,705 2264,698.636038969321 2284.48528137424,714 2276))

See Also
--------

?, ?
