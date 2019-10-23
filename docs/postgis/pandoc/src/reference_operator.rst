Operators
=========

&&
Returns
TRUE
if A's 2D bounding box intersects B's 2D bounding box.
boolean
&&
geometry
A
geometry
B
boolean
&&
geography
A
geography
B
Description
-----------

The ``&&`` operator returns ``TRUE`` if the 2D bounding box of geometry
A intersects the 2D bounding box of geometry B.

    **Note**

    This operand will make use of any indexes that may be available on
    the geometries.

Enhanced: 2.0.0 support for Polyhedral surfaces was introduced.

Availability: 1.5.0 support for geography was introduced.

CURVE\_SUPPORT

P\_SUPPORT

Examples
--------

::

    SELECT tbl1.column1, tbl2.column1, tbl1.column2 && tbl2.column2 AS overlaps
    FROM ( VALUES
        (1, 'LINESTRING(0 0, 3 3)'::geometry),
        (2, 'LINESTRING(0 1, 0 5)'::geometry)) AS tbl1,
    ( VALUES
        (3, 'LINESTRING(1 2, 4 6)'::geometry)) AS tbl2;

     column1 | column1 | overlaps
    ---------+---------+----------
           1 |       3 | t
           2 |       3 | f
    (2 rows)

See Also
--------

?, ?, ?, ?, ?, ?

&&&
Returns
TRUE
if A's 3D bounding box intersects B's 3D bounding box.
boolean
&&&
geometry
A
geometry
B
Description
-----------

The ``&&&`` operator returns ``TRUE`` if the n-D bounding box of
geometry A intersects the n-D bounding box of geometry B.

    **Note**

    This operand will make use of any indexes that may be available on
    the geometries.

Availability: 2.0.0

CURVE\_SUPPORT

P\_SUPPORT

T\_SUPPORT

Z\_SUPPORT

Examples: 3D LineStrings
------------------------

::

    SELECT tbl1.column1, tbl2.column1, tbl1.column2 &&& tbl2.column2 AS overlaps_3d, 
                            tbl1.column2 && tbl2.column2 AS overlaps_2d
    FROM ( VALUES
        (1, 'LINESTRING Z(0 0 1, 3 3 2)'::geometry),
        (2, 'LINESTRING Z(1 2 0, 0 5 -1)'::geometry)) AS tbl1,
    ( VALUES
        (3, 'LINESTRING Z(1 2 1, 4 6 1)'::geometry)) AS tbl2;

     column1 | column1 | overlaps_3d | overlaps_2d
    ---------+---------+-------------+-------------
           1 |       3 | t           | t
           2 |       3 | f           | t

Examples: 3M LineStrings
------------------------

::

    SELECT tbl1.column1, tbl2.column1, tbl1.column2 &&& tbl2.column2 AS overlaps_3zm, 
                            tbl1.column2 && tbl2.column2 AS overlaps_2d
    FROM ( VALUES
        (1, 'LINESTRING M(0 0 1, 3 3 2)'::geometry),
        (2, 'LINESTRING M(1 2 0, 0 5 -1)'::geometry)) AS tbl1,
    ( VALUES
        (3, 'LINESTRING M(1 2 1, 4 6 1)'::geometry)) AS tbl2;

     column1 | column1 | overlaps_3zm | overlaps_2d
    ---------+---------+-------------+-------------
           1 |       3 | t           | t
           2 |       3 | f           | t

See Also
--------

?

&<
Returns
TRUE
if A's bounding box overlaps or is to the left of B's.
boolean
&<
geometry
A
geometry
B
Description
-----------

The ``&<`` operator returns ``TRUE`` if the bounding box of geometry A
overlaps or is to the left of the bounding box of geometry B, or more
accurately, overlaps or is NOT to the right of the bounding box of
geometry B.

    **Note**

    This operand will make use of any indexes that may be available on
    the geometries.

Examples
--------

::

    SELECT tbl1.column1, tbl2.column1, tbl1.column2 &< tbl2.column2 AS overleft
    FROM
      ( VALUES
        (1, 'LINESTRING(1 2, 4 6)'::geometry)) AS tbl1,
      ( VALUES
        (2, 'LINESTRING(0 0, 3 3)'::geometry),
        (3, 'LINESTRING(0 1, 0 5)'::geometry),
        (4, 'LINESTRING(6 0, 6 1)'::geometry)) AS tbl2;

     column1 | column1 | overleft
    ---------+---------+----------
           1 |       2 | f
           1 |       3 | f
           1 |       4 | t
    (3 rows)

See Also
--------

?, ?, ?, ?

&<\|
Returns
TRUE
if A's bounding box overlaps or is below B's.
boolean
&<\|
geometry
A
geometry
B
Description
-----------

The ``&<|`` operator returns ``TRUE`` if the bounding box of geometry A
overlaps or is below of the bounding box of geometry B, or more
accurately, overlaps or is NOT above the bounding box of geometry B.

CURVE\_SUPPORT

P\_SUPPORT

    **Note**

    This operand will make use of any indexes that may be available on
    the geometries.

Examples
--------

::

    SELECT tbl1.column1, tbl2.column1, tbl1.column2 &<| tbl2.column2 AS overbelow
    FROM
      ( VALUES
        (1, 'LINESTRING(6 0, 6 4)'::geometry)) AS tbl1,
      ( VALUES
        (2, 'LINESTRING(0 0, 3 3)'::geometry),
        (3, 'LINESTRING(0 1, 0 5)'::geometry),
        (4, 'LINESTRING(1 2, 4 6)'::geometry)) AS tbl2;

     column1 | column1 | overbelow
    ---------+---------+-----------
           1 |       2 | f
           1 |       3 | t
           1 |       4 | t
    (3 rows)

See Also
--------

?, ?, ?, ?

&>
Returns
TRUE
if A' bounding box overlaps or is to the right of B's.
boolean
&>
geometry
A
geometry
B
Description
-----------

The ``&>`` operator returns ``TRUE`` if the bounding box of geometry A
overlaps or is to the right of the bounding box of geometry B, or more
accurately, overlaps or is NOT to the left of the bounding box of
geometry B.

    **Note**

    This operand will make use of any indexes that may be available on
    the geometries.

Examples
--------

::

    SELECT tbl1.column1, tbl2.column1, tbl1.column2 &> tbl2.column2 AS overright
    FROM
      ( VALUES
        (1, 'LINESTRING(1 2, 4 6)'::geometry)) AS tbl1,
      ( VALUES
        (2, 'LINESTRING(0 0, 3 3)'::geometry),
        (3, 'LINESTRING(0 1, 0 5)'::geometry),
        (4, 'LINESTRING(6 0, 6 1)'::geometry)) AS tbl2;

     column1 | column1 | overright
    ---------+---------+-----------
           1 |       2 | t
           1 |       3 | t
           1 |       4 | f
    (3 rows)

See Also
--------

?, ?, ?, ?

<<
Returns
TRUE
if A's bounding box is strictly to the left of B's.
boolean
<<
geometry
A
geometry
B
Description
-----------

The ``<<`` operator returns ``TRUE`` if the bounding box of geometry A
is strictly to the left of the bounding box of geometry B.

    **Note**

    This operand will make use of any indexes that may be available on
    the geometries.

Examples
--------

::

    SELECT tbl1.column1, tbl2.column1, tbl1.column2 << tbl2.column2 AS left
    FROM
      ( VALUES
        (1, 'LINESTRING (1 2, 1 5)'::geometry)) AS tbl1,
      ( VALUES
        (2, 'LINESTRING (0 0, 4 3)'::geometry),
        (3, 'LINESTRING (6 0, 6 5)'::geometry),
        (4, 'LINESTRING (2 2, 5 6)'::geometry)) AS tbl2;

     column1 | column1 | left
    ---------+---------+------
           1 |       2 | f
           1 |       3 | t
           1 |       4 | t
    (3 rows)

See Also
--------

?, ?, ?

<<\|
Returns
TRUE
if A's bounding box is strictly below B's.
boolean
<<\|
geometry
A
geometry
B
Description
-----------

The ``<<|`` operator returns ``TRUE`` if the bounding box of geometry A
is strictly below the bounding box of geometry B.

    **Note**

    This operand will make use of any indexes that may be available on
    the geometries.

Examples
--------

::

    SELECT tbl1.column1, tbl2.column1, tbl1.column2 <<| tbl2.column2 AS below
    FROM
      ( VALUES
        (1, 'LINESTRING (0 0, 4 3)'::geometry)) AS tbl1,
      ( VALUES
        (2, 'LINESTRING (1 4, 1 7)'::geometry),
        (3, 'LINESTRING (6 1, 6 5)'::geometry),
        (4, 'LINESTRING (2 3, 5 6)'::geometry)) AS tbl2;

     column1 | column1 | below
    ---------+---------+-------
           1 |       2 | t
           1 |       3 | f
           1 |       4 | f
    (3 rows)

See Also
--------

?, ?, ?

=
Returns
TRUE
if A's bounding box is the same as B's. Uses double precision bounding
box.
boolean
=
geometry
A
geometry
B
boolean
=
geography
A
geography
B
Description
-----------

The ``=`` operator returns ``TRUE`` if the bounding box of
geometry/geography A is the same as the bounding box of
geometry/geography B. PostgreSQL uses the =, <, and > operators defined
for geometries to perform internal orderings and comparison of
geometries (ie. in a GROUP BY or ORDER BY clause).

    **Warning**

    This is cause for a lot of confusion. When you compare geometryA =
    geometryB it will return true even when the geometries are clearly
    different IF their bounding boxes are the same. To check for true
    equality use ? or ?

    **Caution**

    This operand will NOT make use of any indexes that may be available
    on the geometries.

CURVE\_SUPPORT

P\_SUPPORT

Changed: 2.0.0 , the bounding box of geometries was changed to use
double precision instead of float4 precision of prior. The side effect
of this is that in particular points in prior versions that were a
little different may have returned true in prior versions and false in
2.0+ since their float4 boxes would be the same but there float8 (double
precision), would be different.

Examples
--------

::

    SELECT 'LINESTRING(0 0, 0 1, 1 0)'::geometry = 'LINESTRING(1 1, 0 0)'::geometry;
     ?column?
    ----------
     t
    (1 row)

    SELECT ST_AsText(column1)
    FROM ( VALUES
        ('LINESTRING(0 0, 1 1)'::geometry),
        ('LINESTRING(1 1, 0 0)'::geometry)) AS foo;
          st_astext
    ---------------------
     LINESTRING(0 0,1 1)
     LINESTRING(1 1,0 0)
    (2 rows)

    -- Note: the GROUP BY uses the "=" to compare for geometry equivalency.
    SELECT ST_AsText(column1)
    FROM ( VALUES
        ('LINESTRING(0 0, 1 1)'::geometry),
        ('LINESTRING(1 1, 0 0)'::geometry)) AS foo
    GROUP BY column1;
          st_astext
    ---------------------
     LINESTRING(0 0,1 1)
    (1 row)

    -- In versions prior to 2.0, this used to return true --
     SELECT ST_GeomFromText('POINT(1707296.37 4820536.77)') =
        ST_GeomFromText('POINT(1707296.27 4820536.87)') As pt_intersect;
        
    --pt_intersect --
    f

See Also
--------

?, ?

>>
Returns
TRUE
if A's bounding box is strictly to the right of B's.
boolean
>>
geometry
A
geometry
B
Description
-----------

The ``>>`` operator returns ``TRUE`` if the bounding box of geometry A
is strictly to the right of the bounding box of geometry B.

    **Note**

    This operand will make use of any indexes that may be available on
    the geometries.

Examples
--------

::

    SELECT tbl1.column1, tbl2.column1, tbl1.column2 >> tbl2.column2 AS right
    FROM
      ( VALUES
        (1, 'LINESTRING (2 3, 5 6)'::geometry)) AS tbl1,
      ( VALUES
        (2, 'LINESTRING (1 4, 1 7)'::geometry),
        (3, 'LINESTRING (6 1, 6 5)'::geometry),
        (4, 'LINESTRING (0 0, 4 3)'::geometry)) AS tbl2;

     column1 | column1 | right
    ---------+---------+-------
           1 |       2 | t
           1 |       3 | f
           1 |       4 | f
    (3 rows)

See Also
--------

?, ?, ?

@
Returns
TRUE
if A's bounding box is contained by B's.
boolean
@
geometry
A
geometry
B
Description
-----------

The ``@`` operator returns ``TRUE`` if the bounding box of geometry A is
completely contained by the bounding box of geometry B.

    **Note**

    This operand will make use of any indexes that may be available on
    the geometries.

Examples
--------

::

    SELECT tbl1.column1, tbl2.column1, tbl1.column2 @ tbl2.column2 AS contained
    FROM
      ( VALUES
        (1, 'LINESTRING (1 1, 3 3)'::geometry)) AS tbl1,
      ( VALUES
        (2, 'LINESTRING (0 0, 4 4)'::geometry),
        (3, 'LINESTRING (2 2, 4 4)'::geometry),
        (4, 'LINESTRING (1 1, 3 3)'::geometry)) AS tbl2;

     column1 | column1 | contained
    ---------+---------+-----------
           1 |       2 | t
           1 |       3 | f
           1 |       4 | t
    (3 rows)

See Also
--------

?, ?

\|&>
Returns
TRUE
if A's bounding box overlaps or is above B's.
boolean
\|&>
geometry
A
geometry
B
Description
-----------

The ``|&>`` operator returns ``TRUE`` if the bounding box of geometry A
overlaps or is above the bounding box of geometry B, or more accurately,
overlaps or is NOT below the bounding box of geometry B.

    **Note**

    This operand will make use of any indexes that may be available on
    the geometries.

Examples
--------

::

    SELECT tbl1.column1, tbl2.column1, tbl1.column2 |&> tbl2.column2 AS overabove
    FROM
      ( VALUES
        (1, 'LINESTRING(6 0, 6 4)'::geometry)) AS tbl1,
      ( VALUES
        (2, 'LINESTRING(0 0, 3 3)'::geometry),
        (3, 'LINESTRING(0 1, 0 5)'::geometry),
        (4, 'LINESTRING(1 2, 4 6)'::geometry)) AS tbl2;

     column1 | column1 | overabove
    ---------+---------+-----------
           1 |       2 | t
           1 |       3 | f
           1 |       4 | f
    (3 rows)

See Also
--------

?, ?, ?, ?

\|>>
Returns
TRUE
if A's bounding box is strictly above B's.
boolean
\|>>
geometry
A
geometry
B
Description
-----------

The ``|>>`` operator returns ``TRUE`` if the bounding box of geometry A
is strictly to the right of the bounding box of geometry B.

    **Note**

    This operand will make use of any indexes that may be available on
    the geometries.

Examples
--------

::

    SELECT tbl1.column1, tbl2.column1, tbl1.column2 |>> tbl2.column2 AS above
    FROM
      ( VALUES
        (1, 'LINESTRING (1 4, 1 7)'::geometry)) AS tbl1,
      ( VALUES
        (2, 'LINESTRING (0 0, 4 2)'::geometry),
        (3, 'LINESTRING (6 1, 6 5)'::geometry),
        (4, 'LINESTRING (2 3, 5 6)'::geometry)) AS tbl2;

     column1 | column1 | above
    ---------+---------+-------
           1 |       2 | t
           1 |       3 | f
           1 |       4 | f
    (3 rows)

See Also
--------

?, ?, ?

~
Returns
TRUE
if A's bounding box contains B's.
boolean
~
geometry
A
geometry
B
Description
-----------

The ``~`` operator returns ``TRUE`` if the bounding box of geometry A
completely contains the bounding box of geometry B.

    **Note**

    This operand will make use of any indexes that may be available on
    the geometries.

Examples
--------

::

    SELECT tbl1.column1, tbl2.column1, tbl1.column2 ~ tbl2.column2 AS contains
    FROM
      ( VALUES
        (1, 'LINESTRING (0 0, 3 3)'::geometry)) AS tbl1,
      ( VALUES
        (2, 'LINESTRING (0 0, 4 4)'::geometry),
        (3, 'LINESTRING (1 1, 2 2)'::geometry),
        (4, 'LINESTRING (0 0, 3 3)'::geometry)) AS tbl2;

     column1 | column1 | contains
    ---------+---------+----------
           1 |       2 | f
           1 |       3 | t
           1 |       4 | t
    (3 rows)

See Also
--------

?, ?

~=
Returns
TRUE
if A's bounding box is the same as B's.
boolean
~=
geometry
A
geometry
B
Description
-----------

The ``~=`` operator returns ``TRUE`` if the bounding box of
geometry/geography A is the same as the bounding box of
geometry/geography B.

    **Note**

    This operand will make use of any indexes that may be available on
    the geometries.

Availability: 1.5.0 changed behavior

P\_SUPPORT

    **Warning**

    This operator has changed behavior in PostGIS 1.5 from testing for
    actual geometric equality to only checking for bounding box
    equality. To complicate things it also depends on if you have done a
    hard or soft upgrade which behavior your database has. To find out
    which behavior your database has you can run the query below. To
    check for true equality use ? or ? and to check for bounding box
    equality ?; operator is a safer option.

Examples
--------

::


    select 'LINESTRING(0 0, 1 1)'::geometry ~= 'LINESTRING(0 1, 1 0)'::geometry as equality;
     equality   |
    -----------------+
              t    |
                

The above can be used to test if you have the new or old behavior of ~=
operator.

See Also
--------

?, ?, ?

<->
Returns the distance between two points. For point / point checks it
uses floating point accuracy (as opposed to the double precision
accuracy of the underlying point geometry). For other geometry types the
distance between the floating point bounding box centroids is returned.
Useful for doing distance ordering and nearest neighbor limits using KNN
gist functionality.
double precision
<->
geometry
A
geometry
B
Description
-----------

The ``<->`` operator returns distance between two points read from the
spatial index for points (float precision). For other geometries it
returns the distance from centroid of bounding box of geometries. Useful
for doing nearest neighbor **approximate** distance ordering.

    **Note**

    This operand will make use of any indexes that may be available on
    the geometries. It is different from other operators that use
    spatial indexes in that the spatial index is only used when the
    operator is in the ORDER BY clause.

    **Note**

    Index only kicks in if one of the geometries is a constant (not in a
    subquery/cte). e.g. 'SRID=3005;POINT(1011102 450541)'::geometry
    instead of a.geom

Refer to `OpenGeo workshop: Nearest-Neighbour
Searching <http://workshops.opengeo.org/postgis-intro/knn.html>`__ for
real live example.

Availability: 2.0.0 only available for PostgreSQL 9.1+

Examples
--------

::

    SELECT ST_Distance(geom, 'SRID=3005;POINT(1011102 450541)'::geometry) as d,edabbr, vaabbr 
    FROM va2005 
    ORDER BY d limit 10;

            d         | edabbr | vaabbr
    ------------------+--------+--------
                    0 | ALQ    | 128
     5541.57712511724 | ALQ    | 129A
     5579.67450712005 | ALQ    | 001
      6083.4207708641 | ALQ    | 131
      7691.2205404848 | ALQ    | 003
     7900.75451037313 | ALQ    | 122
     8694.20710669982 | ALQ    | 129B
     9564.24289057111 | ALQ    | 130
      12089.665931705 | ALQ    | 127
     18472.5531479404 | ALQ    | 002
    (10 rows)

Then the KNN raw answer:

::

    SELECT st_distance(geom, 'SRID=3005;POINT(1011102 450541)'::geometry) as d,edabbr, vaabbr 
    FROM va2005 
    ORDER BY geom <-> 'SRID=3005;POINT(1011102 450541)'::geometry limit 10;

            d         | edabbr | vaabbr
    ------------------+--------+--------
                    0 | ALQ    | 128
     5579.67450712005 | ALQ    | 001
     5541.57712511724 | ALQ    | 129A
     8694.20710669982 | ALQ    | 129B
     9564.24289057111 | ALQ    | 130
      6083.4207708641 | ALQ    | 131
      12089.665931705 | ALQ    | 127
      24795.264503022 | ALQ    | 124
     24587.6584922302 | ALQ    | 123
     26764.2555463114 | ALQ    | 125
    (10 rows)

Note the misordering in the actual distances and the different entries
that actually show up in the top 10.

Finally the hybrid:

::

    WITH index_query AS (
      SELECT ST_Distance(geom, 'SRID=3005;POINT(1011102 450541)'::geometry) as d,edabbr, vaabbr
        FROM va2005
      ORDER BY geom <-> 'SRID=3005;POINT(1011102 450541)'::geometry LIMIT 100) 
      SELECT * 
        FROM index_query 
      ORDER BY d limit 10;

            d         | edabbr | vaabbr
    ------------------+--------+--------
                    0 | ALQ    | 128
     5541.57712511724 | ALQ    | 129A
     5579.67450712005 | ALQ    | 001
      6083.4207708641 | ALQ    | 131
      7691.2205404848 | ALQ    | 003
     7900.75451037313 | ALQ    | 122
     8694.20710669982 | ALQ    | 129B
     9564.24289057111 | ALQ    | 130
      12089.665931705 | ALQ    | 127
     18472.5531479404 | ALQ    | 002
    (10 rows)

                

See Also
--------

?, ?, ?

<#>
Returns the distance between bounding box of 2 geometries. For point /
point checks it's almost the same as distance (though may be different
since the bounding box is at floating point accuracy and geometries are
double precision). Useful for doing distance ordering and nearest
neighbor limits using KNN gist functionality.
double precision
<#>
geometry
A
geometry
B
Description
-----------

The ``<#>`` KNN GIST operator returns distance between two floating
point bounding boxes read from the spatial index if available. Useful
for doing nearest neighbor **approximate** distance ordering.

    **Note**

    This operand will make use of any indexes that may be available on
    the geometries. It is different from other operators that use
    spatial indexes in that the spatial index is only used when the
    operator is in the ORDER BY clause.

    **Note**

    Index only kicks in if one of the geometries is a constant e.g.
    ORDER BY (ST\_GeomFromText('POINT(1 2)') <#> geom) instead of
    g1.geom <#>.

Availability: 2.0.0 only available for PostgreSQL 9.1+

Examples
--------

::

    SELECT *
    FROM (
    SELECT b.tlid, b.mtfcc, 
        b.geom <#> ST_GeomFromText('LINESTRING(746149 2948672,745954 2948576,
            745787 2948499,745740 2948468,745712 2948438,
            745690 2948384,745677 2948319)',2249) As b_dist, 
            ST_Distance(b.geom, ST_GeomFromText('LINESTRING(746149 2948672,745954 2948576,
            745787 2948499,745740 2948468,745712 2948438,
            745690 2948384,745677 2948319)',2249)) As act_dist
        FROM bos_roads As b 
        ORDER BY b_dist, b.tlid
        LIMIT 100) As foo
        ORDER BY act_dist, tlid LIMIT 10;

       tlid    | mtfcc |      b_dist      |     act_dist
    -----------+-------+------------------+------------------
      85732027 | S1400 |                0 |                0
      85732029 | S1400 |                0 |                0
      85732031 | S1400 |                0 |                0
      85734335 | S1400 |                0 |                0
      85736037 | S1400 |                0 |                0
     624683742 | S1400 |                0 | 128.528874268666
      85719343 | S1400 | 260.839270432962 | 260.839270432962
      85741826 | S1400 | 164.759294123275 | 260.839270432962
      85732032 | S1400 |           277.75 | 311.830282365264
      85735592 | S1400 |           222.25 | 311.830282365264
    (10 rows)

See Also
--------

?, ?, ?
