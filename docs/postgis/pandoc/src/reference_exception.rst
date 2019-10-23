Exceptional Functions
=====================

These functions are rarely used functions that should only be used if
your data is corrupted in someway. They are used for troubleshooting
corruption and also fixing things that should under normal
circumstances, never happen.

PostGIS\_AddBBox
Add bounding box to the geometry.
geometry
PostGIS\_AddBBox
geometry
geomA
Description
-----------

Add bounding box to the geometry. This would make bounding box based
queries faster, but will increase the size of the geometry.

    **Note**

    Bounding boxes are automatically added to geometries so in general
    this is not needed unless the generated bounding box somehow becomes
    corrupted or you have an old install that is lacking bounding boxes.
    Then you need to drop the old and readd.

CURVE\_SUPPORT

Examples
--------

::

    UPDATE sometable
     SET the_geom =  PostGIS_AddBBox(the_geom)
     WHERE PostGIS_HasBBox(the_geom) = false;

See Also
--------

?, ?

PostGIS\_DropBBox
Drop the bounding box cache from the geometry.
geometry
PostGIS\_DropBBox
geometry
geomA
Description
-----------

Drop the bounding box cache from the geometry. This reduces geometry
size, but makes bounding-box based queries slower. It is also used to
drop a corrupt bounding box. A tale-tell sign of a corrupt cached
bounding box is when your ST\_Intersects and other relation queries
leave out geometries that rightfully should return true.

    **Note**

    Bounding boxes are automatically added to geometries and improve
    speed of queries so in general this is not needed unless the
    generated bounding box somehow becomes corrupted or you have an old
    install that is lacking bounding boxes. Then you need to drop the
    old and readd. This kind of corruption has been observed in
    8.3-8.3.6 series whereby cached bboxes were not always recalculated
    when a geometry changed and upgrading to a newer version without a
    dump reload will not correct already corrupted boxes. So one can
    manually correct using below and readd the bbox or do a dump reload.

CURVE\_SUPPORT

Examples
--------

::

    --This example drops bounding boxes where the cached box is not correct
                --The force to ST_AsBinary before applying Box2D forces a recalculation of the box, and Box2D applied to the table geometry always
                -- returns the cached bounding box.
                UPDATE sometable
     SET the_geom =  PostGIS_DropBBox(the_geom)
     WHERE Not (Box2D(ST_AsBinary(the_geom)) = Box2D(the_geom));

        UPDATE sometable
     SET the_geom =  PostGIS_AddBBox(the_geom)
     WHERE Not PostGIS_HasBBOX(the_geom);


     

See Also
--------

?, ?, ?

PostGIS\_HasBBox
Returns TRUE if the bbox of this geometry is cached, FALSE otherwise.
boolean
PostGIS\_HasBBox
geometry
geomA
Description
-----------

Returns TRUE if the bbox of this geometry is cached, FALSE otherwise.
Use ? and ? to control caching.

CURVE\_SUPPORT

Examples
--------

::

    SELECT the_geom
    FROM sometable WHERE PostGIS_HasBBox(the_geom) = false;

See Also
--------

?, ?
