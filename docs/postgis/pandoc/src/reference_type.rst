PostgreSQL PostGIS Geometry/Geography/Box Types
===============================================

This section lists the PostgreSQL data types installed by PostGIS. Note we describe the casting behavior of these which is very important especially when designing your own functions.

A Cast is when one type is coerced into another type. PostgreSQL is unique from most databases in that it allows you to define casting behavior for custom types and the functions used for casting. A cast can be specified as automatic in which case, you do not have to do a ``CAST(myfoo As otherfootype) or myfoo::otherfootype`` if you are feeding it to a function that only works with otherfootype and there is an automatic cast in place for it.

The danger of relying on automatic cast behavior is when you have an overloaded function say one that takes a box2d and one that takes a box3d but no geometry. What happens is that both functions are equally good to use with geometry since geometry has an autocast for both -- so you end up with an ambiguous function error. To force PostgreSQL to choose, you do a ``CAST(mygeom As box3d) or mygeom::box3d``.

At least as of PostgreSQL 8.3 - Everything can be CAST to text (presumably because of the magical unknown type), so no defined CASTS for that need to be present for you to CAST an object to text.


**box2d**
  A box composed of x min, ymin, xmax, ymax. Often used to return the 2d enclosing box of a geometry.

  *Description*
    A spatial data type used to represent the enclosing box of a geometry or set of geometries. ST\_Extent in earlier versions prior to PostGIS 1.4 would return a box2d.


**box3d**
  A box composed of x min, ymin, zmin, xmax, ymax, zmax. Often used to return the 3d extent of a geometry or collection of geometries.

  *Description*
    A postgis spatial data type used to represent the enclosing box of a geometry or set of geometries. ST\_3DExtent returns a box3d object.

  *Casting Behavior* for **box 3d**

    Automatic as well as explicit casts allowed for this data type:

    +------------+-------------+
    | Cast To    | Behavior    |
    +------------+-------------+
    | box        | automatic   |
    +------------+-------------+
    | box2d      | automatic   |
    +------------+-------------+
    | geometry   | automatic   |
    +------------+-------------+

**geometry**
  Planar spatial data type.

  *Description*
    A fundamental postgis spatial data type used to represent a feature in the Euclidean coordinate system.

  *Casting Behavior* for **geometry**
    Automatic as well as explicit casts allowed for sthis data type:

    +-------------+-------------+
    | Cast To     | Behavior    |
    +-------------+-------------+
    | box         | automatic   |
    +-------------+-------------+
    | box2d       | automatic   |
    +-------------+-------------+
    | box3d       | automatic   |
    +-------------+-------------+
    | bytea       | automatic   |
    +-------------+-------------+
    | geography   | automatic   |
    +-------------+-------------+
    | text        | automatic   |
    +-------------+-------------+

    See Also :ref:`GIS Objects`


**geometry_dump**
  A spatial datatype with two fields - geom (holding a geometry object) and path[] (a 1-d array holding the position of the geometry within the dumped object.)

  *Description*
    A compound data type consisting of a geometry object referenced by the .geom field and path[] a 1-dimensional integer array (starting at 1 e.g. path[1] to get first element) array that defines the navigation path within the dumped geometry to find this element. It is used by the ST_Dump\* family of functions as an output type to explode a more complex geometry into its constituent parts and location of parts.

    See Also :ref:`PostGIS Geography Support Functions`, :ref:`PostGIS Geography Type`


**geography**
  Ellipsoidal spatial data type.

  *Description*
    A spatial data type used to represent a feature in the round-earth coordinate system.

  *Casting Behavior*
    Automatic as well as explicit casts allowed for sthis data type:

    +------------+------------+
    | Cast To    | Behavior   |
    +------------+------------+
    | geometry   | explicit   |
    +------------+------------+

    See also :ref:`PostGIS_GeographyFunctions`, :ref:`PostGIS_Geography`
