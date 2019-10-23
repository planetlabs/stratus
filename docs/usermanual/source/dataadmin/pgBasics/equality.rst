.. _dataadmin.pgBasics.equality:


Equality
========
 
PostGIS supports three different functions to determine different levels of equality. To illustrate these functions, the following sample polygons are referenced.

.. figure:: img/equality_polygons.png

   *Some "equal" polygons*

To create these polygons, execute the following SQL commands.

.. code-block:: sql

   CREATE TABLE polygons (name varchar, poly geometry);
  
   INSERT INTO polygons VALUES 
    ('Polygon 1', 'POLYGON((-1 1.732,1 1.732,2 0,1 -1.732,
        -1 -1.732,-2 0,-1 1.732))'),
    ('Polygon 2', 'POLYGON((-1 1.732,-2 0,-1 -1.732,1 -1.732,
        2 0,1 1.732,-1 1.732))'),
    ('Polygon 3', 'POLYGON((1 -1.732,2 0,1 1.732,-1 1.732,
        -2 0,-1 -1.732,1 -1.732))'),
    ('Polygon 4', 'POLYGON((-1 1.732,0 1.732, 1 1.732,1.5 0.866,
        2 0,1.5 -0.866,1 -1.732,0 -1.732,-1 -1.732,-1.5 -0.866,
        -2 0,-1.5 0.866,-1 1.732))'),
    ('Polygon 5', 'POLYGON((-2 -1.732,2 -1.732,2 1.732, 
        -2 1.732,-2 -1.732))');


Exactly Equal
-------------

Exact equality is determined using the :command:`ST_OrderingEquals` function, which compares two geometries, vertex by vertex, and in order, to ensure they are identical in position.

.. code-block:: sql

  SELECT a.name, b.name, CASE WHEN ST_OrderingEquals(a.poly, b.poly)
      THEN 'Exactly Equal' ELSE 'Not Exactly Equal' end
    FROM polygons as a, polygons as b;

::

     name    |   name    |       case
  -----------+-----------+-------------------
   Polygon 1 | Polygon 1 | Exactly Equal
   Polygon 1 | Polygon 2 | Not Exactly Equal
   Polygon 1 | Polygon 3 | Not Exactly Equal
   Polygon 1 | Polygon 4 | Not Exactly Equal
   Polygon 1 | Polygon 5 | Not Exactly Equal
   Polygon 2 | Polygon 1 | Not Exactly Equal
   Polygon 2 | Polygon 2 | Exactly Equal
   Polygon 2 | Polygon 3 | Not Exactly Equal
   Polygon 2 | Polygon 4 | Not Exactly Equal
   Polygon 2 | Polygon 5 | Not Exactly Equal
   Polygon 3 | Polygon 1 | Not Exactly Equal
   Polygon 3 | Polygon 2 | Not Exactly Equal
   Polygon 3 | Polygon 3 | Exactly Equal
   Polygon 3 | Polygon 4 | Not Exactly Equal
   Polygon 3 | Polygon 5 | Not Exactly Equal
   Polygon 4 | Polygon 1 | Not Exactly Equal
   Polygon 4 | Polygon 2 | Not Exactly Equal
   Polygon 4 | Polygon 3 | Not Exactly Equal
   Polygon 4 | Polygon 4 | Exactly Equal
   Polygon 4 | Polygon 5 | Not Exactly Equal
   Polygon 5 | Polygon 1 | Not Exactly Equal
   Polygon 5 | Polygon 2 | Not Exactly Equal
   Polygon 5 | Polygon 3 | Not Exactly Equal
   Polygon 5 | Polygon 4 | Not Exactly Equal
   Polygon 5 | Polygon 5 | Exactly Equal

In this example, the polygons are only equal to themselves, not to other seemingly equivalent polygons (as in the case of Polygons 1, 2, and 3). For Polygons 1, 2, and 3, the vertices are in identical positions but are defined in different orders. Polygon 4 has colinear, and therefore redundant, vertices on the hexagon edges causing inequality with Polygon 1.

Spatially Equal
---------------

Exact equality does not take into account the spatial nature of the geometries. The function :command:`ST_Equals` will test the spatial equality, or equivalence, of geometries.

.. code-block:: sql

  SELECT a.name, b.name, CASE WHEN ST_Equals(a.poly, b.poly) 
      THEN 'Spatially Equal' ELSE 'Not Equal' end
    FROM polygons as a, polygons as b;

::

     name    |   name    |      case
  -----------+-----------+-----------------
   Polygon 1 | Polygon 1 | Spatially Equal
   Polygon 1 | Polygon 2 | Spatially Equal
   Polygon 1 | Polygon 3 | Spatially Equal
   Polygon 1 | Polygon 4 | Spatially Equal
   Polygon 1 | Polygon 5 | Not Equal
   Polygon 2 | Polygon 1 | Spatially Equal
   Polygon 2 | Polygon 2 | Spatially Equal
   Polygon 2 | Polygon 3 | Spatially Equal
   Polygon 2 | Polygon 4 | Spatially Equal
   Polygon 2 | Polygon 5 | Not Equal
   Polygon 3 | Polygon 1 | Spatially Equal
   Polygon 3 | Polygon 2 | Spatially Equal
   Polygon 3 | Polygon 3 | Spatially Equal
   Polygon 3 | Polygon 4 | Spatially Equal
   Polygon 3 | Polygon 5 | Not Equal
   Polygon 4 | Polygon 1 | Spatially Equal
   Polygon 4 | Polygon 2 | Spatially Equal
   Polygon 4 | Polygon 3 | Spatially Equal
   Polygon 4 | Polygon 4 | Spatially Equal
   Polygon 4 | Polygon 5 | Not Equal
   Polygon 5 | Polygon 1 | Not Equal
   Polygon 5 | Polygon 2 | Not Equal
   Polygon 5 | Polygon 3 | Not Equal
   Polygon 5 | Polygon 4 | Not Equal
   Polygon 5 | Polygon 5 | Spatially Equal

Polygons 1 through 4 are considered equal, since they enclose the same area. Neither the direction of the polygon is drawn, the starting point for defining the polygon, nor the number of points used are important in this comparison. The primary consideration for assessing equality with :command:`ST_Equals` is whether or not the polygons contain the same space. 

Equal Bounds
------------

Exact equality requires comparison of each and every vertex in the geometry to determine equality. This may adversely affect processing performance, and may not be appropriate for comparing huge numbers of geometries. 

To allow for speedier comparison, the equal bounds operator "=" is provided. This operates only on the bounding box (rectangle), ensuring that the geometries occupy the same two dimensional extent, but not necessarily the same space.

.. code-block:: sql

  SELECT a.name, b.name, CASE WHEN a.poly = b.poly 
      THEN 'Equal Bounds' ELSE 'Non-equal Bounds' end
    FROM polygons as a, polygons as b;

::

     name    |   name    |     case
  -----------+-----------+--------------
   Polygon 1 | Polygon 1 | Equal Bounds
   Polygon 1 | Polygon 2 | Equal Bounds
   Polygon 1 | Polygon 3 | Equal Bounds
   Polygon 1 | Polygon 4 | Equal Bounds
   Polygon 1 | Polygon 5 | Equal Bounds
   Polygon 2 | Polygon 1 | Equal Bounds
   Polygon 2 | Polygon 2 | Equal Bounds
   Polygon 2 | Polygon 3 | Equal Bounds
   Polygon 2 | Polygon 4 | Equal Bounds
   Polygon 2 | Polygon 5 | Equal Bounds
   Polygon 3 | Polygon 1 | Equal Bounds
   Polygon 3 | Polygon 2 | Equal Bounds
   Polygon 3 | Polygon 3 | Equal Bounds
   Polygon 3 | Polygon 4 | Equal Bounds
   Polygon 3 | Polygon 5 | Equal Bounds
   Polygon 4 | Polygon 1 | Equal Bounds
   Polygon 4 | Polygon 2 | Equal Bounds
   Polygon 4 | Polygon 3 | Equal Bounds
   Polygon 4 | Polygon 4 | Equal Bounds
   Polygon 4 | Polygon 5 | Equal Bounds
   Polygon 5 | Polygon 1 | Equal Bounds
   Polygon 5 | Polygon 2 | Equal Bounds
   Polygon 5 | Polygon 3 | Equal Bounds
   Polygon 5 | Polygon 4 | Equal Bounds
   Polygon 5 | Polygon 5 | Equal Bounds

All of the spatially equal geometries also have equal bounds. Unfortunately, Polygon 5 is also identified as equal using this test, because it shares the same bounding box as the other geometries. However, this equality test supports the use of spatial indexing to reduce huge comparison sets into more manageable blocks when joining and filtering data.



