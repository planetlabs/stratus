PostGIS Special Functions Index
===============================

PostGIS Aggregate Functions
-----------------------------

The functions given below are spatial aggregate functions provided with
PostGIS that can be used just like any other sql aggregate function such
as sum, average.

-  `ST\_3DExtent <#ST_3DExtent>`__ - an aggregate function that returns
   the box3D bounding box that bounds rows of geometries.

-  `ST\_Accum <#ST_Accum>`__ - Aggregate. Constructs an array of
   geometries.

-  `ST\_Collect <#ST_Collect>`__ - Return a specified ST\_Geometry value
   from a collection of other geometries.

-  `ST\_Extent <#ST_Extent>`__ - an aggregate function that returns the
   bounding box that bounds rows of geometries.

-  `ST\_MakeLine <#ST_MakeLine>`__ - Creates a Linestring from point or
   line geometries.

-  `ST\_MemUnion <#ST_MemUnion>`__ - Same as ST\_Union, only
   memory-friendly (uses less memory and more processor time).

-  `ST\_Polygonize <#ST_Polygonize>`__ - Aggregate. Creates a
   GeometryCollection containing possible polygons formed from the
   constituent linework of a set of geometries.

-  `ST\_SameAlignment <#RT_ST_SameAlignment>`__ - Returns true if
   rasters have same skew, scale, spatial ref and false if they don't
   with notice detailing issue.

-  `ST\_Union <#ST_Union>`__ - Returns a geometry that represents the
   point set union of the Geometries.

-  `TopoElementArray\_Agg <#TopoElementArray_Agg>`__ - Returns a
   topoelementarray for a set of element\_id, type arrays (topoelements)

PostGIS SQL-MM Compliant Functions
------------------------------------

The functions given below are PostGIS functions that conform to the
SQL/MM 3 standard

  **Note**

    SQL-MM defines the default SRID of all geometry constructors as 0.
    PostGIS uses a default SRID of -1.

-  `ST\_3DDWithin <#ST_3DDWithin>`__ - For 3d (z) geometry type Returns
   true if two geometries 3d distance is within number of units. This
   method implements the SQL/MM specification. SQL-MM ?

-  `ST\_3DDistance <#ST_3DDistance>`__ - For geometry type Returns the
   3-dimensional cartesian minimum distance (based on spatial ref)
   between two geometries in projected units. This method implements the
   SQL/MM specification. SQL-MM ?

-  `ST\_3DIntersects <#ST_3DIntersects>`__ - Returns TRUE if the
   Geometries "spatially intersect" in 3d - only for points and
   linestrings This method implements the SQL/MM specification. SQL-MM
   3: ?

-  `ST\_AddEdgeModFace <#ST_AddEdgeModFace>`__ - Add a new edge and, if
   in doing so it splits a face, modify the original face and add a new
   face. This method implements the SQL/MM specification. SQL-MM:
   Topo-Geo and Topo-Net 3: Routine Details: X.3.13

-  `ST\_AddEdgeNewFaces <#ST_AddEdgeNewFaces>`__ - Add a new edge and,
   if in doing so it splits a face, delete the original face and replace
   it with two new faces. This method implements the SQL/MM
   specification. SQL-MM: Topo-Geo and Topo-Net 3: Routine Details:
   X.3.12

-  `ST\_AddIsoEdge <#ST_AddIsoEdge>`__ - Adds an isolated edge defined
   by geometry alinestring to a topology connecting two existing
   isolated nodes anode and anothernode and returns the edge id of the
   new edge. This method implements the SQL/MM specification. SQL-MM:
   Topo-Geo and Topo-Net 3: Routine Details: X.3.4

-  `ST\_AddIsoNode <#ST_AddIsoNode>`__ - Adds an isolated node to a face
   in a topology and returns the nodeid of the new node. If face is
   null, the node is still created. This method implements the SQL/MM
   specification. SQL-MM: Topo-Net Routines: X+1.3.1

-  `ST\_Area <#ST_Area>`__ - Returns the area of the surface if it is a
   polygon or multi-polygon. For "geometry" type area is in SRID units.
   For "geography" area is in square meters. This method implements the
   SQL/MM specification. SQL-MM 3: 8.1.2, 9.5.3

-  `ST\_AsBinary <#ST_AsBinary>`__ - Return the Well-Known Binary (WKB)
   representation of the geometry/geography without SRID meta data. This
   method implements the SQL/MM specification. SQL-MM 3: 5.1.37

-  `ST\_AsText <#ST_AsText>`__ - Return the Well-Known Text (WKT)
   representation of the geometry/geography without SRID metadata. This
   method implements the SQL/MM specification. SQL-MM 3: 5.1.25

-  `ST\_Boundary <#ST_Boundary>`__ - Returns the closure of the
   combinatorial boundary of this Geometry. This method implements the
   SQL/MM specification. SQL-MM 3: 5.1.14

-  `ST\_Buffer <#ST_Buffer>`__ - (T) For geometry: Returns a geometry
   that represents all points whose distance from this Geometry is less
   than or equal to distance. Calculations are in the Spatial Reference
   System of this Geometry. For geography: Uses a planar transform
   wrapper. Introduced in 1.5 support for different end cap and mitre
   settings to control shape. buffer\_style options:
   quad\_segs=#,endcap=round\|flat\|square,join=round\|mitre\|bevel,mitre\_limit=#.#
   This method implements the SQL/MM specification. SQL-MM 3: 5.1.17

-  `ST\_Centroid <#ST_Centroid>`__ - Returns the geometric center of a
   geometry. This method implements the SQL/MM specification. SQL-MM 3:
   8.1.4, 9.5.5

-  `ST\_ChangeEdgeGeom <#ST_ChangeEdgeGeom>`__ - Changes the shape of an
   edge without affecting the topology structure. This method implements
   the SQL/MM specification. SQL-MM: Topo-Geo and Topo-Net 3: Routine
   Details X.3.6

-  `ST\_Contains <#ST_Contains>`__ - Returns true if and only if no
   points of B lie in the exterior of A, and at least one point of the
   interior of B lies in the interior of A. This method implements the
   SQL/MM specification. SQL-MM 3: 5.1.31

-  `ST\_ConvexHull <#ST_ConvexHull>`__ - The convex hull of a geometry
   represents the minimum convex geometry that encloses all geometries
   within the set. This method implements the SQL/MM specification.
   SQL-MM 3: 5.1.16

-  `ST\_CoordDim <#ST_CoordDim>`__ - Return the coordinate dimension of
   the ST\_Geometry value. This method implements the SQL/MM
   specification. SQL-MM 3: 5.1.3

-  `ST\_CreateTopoGeo <#ST_CreateTopoGeo>`__ - Adds a collection of
   geometries to a given empty topology and returns a message detailing
   success. This method implements the SQL/MM specification. SQL-MM:
   Topo-Geo and Topo-Net 3: Routine Details -- X.3.18

-  `ST\_Crosses <#ST_Crosses>`__ - Returns TRUE if the supplied
   geometries have some, but not all, interior points in common. This
   method implements the SQL/MM specification. SQL-MM 3: 5.1.29

-  `ST\_CurveToLine <#ST_CurveToLine>`__ - Converts a
   CIRCULARSTRING/CURVEDPOLYGON to a LINESTRING/POLYGON This method
   implements the SQL/MM specification. SQL-MM 3: 7.1.7

-  `ST\_Difference <#ST_Difference>`__ - Returns a geometry that
   represents that part of geometry A that does not intersect with
   geometry B. This method implements the SQL/MM specification. SQL-MM
   3: 5.1.20

-  `ST\_Dimension <#ST_Dimension>`__ - The inherent dimension of this
   Geometry object, which must be less than or equal to the coordinate
   dimension. This method implements the SQL/MM specification. SQL-MM 3:
   5.1.2

-  `ST\_Disjoint <#ST_Disjoint>`__ - Returns TRUE if the Geometries do
   not "spatially intersect" - if they do not share any space together.
   This method implements the SQL/MM specification. SQL-MM 3: 5.1.26

-  `ST\_Distance <#ST_Distance>`__ - For geometry type Returns the
   2-dimensional cartesian minimum distance (based on spatial ref)
   between two geometries in projected units. For geography type
   defaults to return spheroidal minimum distance between two
   geographies in meters. This method implements the SQL/MM
   specification. SQL-MM 3: 5.1.23

-  `ST\_EndPoint <#ST_EndPoint>`__ - Returns the last point of a
   LINESTRING geometry as a POINT. This method implements the SQL/MM
   specification. SQL-MM 3: 7.1.4

-  `ST\_Envelope <#ST_Envelope>`__ - Returns a geometry representing the
   double precision (float8) bounding box of the supplied geometry. This
   method implements the SQL/MM specification. SQL-MM 3: 5.1.15

-  `ST\_Equals <#ST_Equals>`__ - Returns true if the given geometries
   represent the same geometry. Directionality is ignored. This method
   implements the SQL/MM specification. SQL-MM 3: 5.1.24

-  `ST\_ExteriorRing <#ST_ExteriorRing>`__ - Returns a line string
   representing the exterior ring of the POLYGON geometry. Return NULL
   if the geometry is not a polygon. Will not work with MULTIPOLYGON
   This method implements the SQL/MM specification. SQL-MM 3: 8.2.3,
   8.3.3

-  `ST\_GMLToSQL <#ST_GMLToSQL>`__ - Return a specified ST\_Geometry
   value from GML representation. This is an alias name for
   ST\_GeomFromGML This method implements the SQL/MM specification.
   SQL-MM 3: 5.1.50 (except for curves support).

-  `ST\_GeomCollFromText <#ST_GeomCollFromText>`__ - Makes a collection
   Geometry from collection WKT with the given SRID. If SRID is not
   give, it defaults to 0. This method implements the SQL/MM
   specification.

-  `ST\_GeomFromText <#ST_GeomFromText>`__ - Return a specified
   ST\_Geometry value from Well-Known Text representation (WKT). This
   method implements the SQL/MM specification. SQL-MM 3: 5.1.40

-  `ST\_GeomFromWKB <#ST_GeomFromWKB>`__ - Creates a geometry instance
   from a Well-Known Binary geometry representation (WKB) and optional
   SRID. This method implements the SQL/MM specification. SQL-MM 3:
   5.1.41

-  `ST\_GeometryFromText <#ST_GeometryFromText>`__ - Return a specified
   ST\_Geometry value from Well-Known Text representation (WKT). This is
   an alias name for ST\_GeomFromText This method implements the SQL/MM
   specification. SQL-MM 3: 5.1.40

-  `ST\_GeometryN <#ST_GeometryN>`__ - Return the 1-based Nth geometry
   if the geometry is a GEOMETRYCOLLECTION, (MULTI)POINT,
   (MULTI)LINESTRING, MULTICURVE or (MULTI)POLYGON, POLYHEDRALSURFACE
   Otherwise, return NULL. This method implements the SQL/MM
   specification. SQL-MM 3: 9.1.5

-  `ST\_GeometryType <#ST_GeometryType>`__ - Return the geometry type of
   the ST\_Geometry value. This method implements the SQL/MM
   specification. SQL-MM 3: 5.1.4

-  `ST\_GetFaceEdges <#ST_GetFaceEdges>`__ - Returns a set of ordered
   edges that bound aface. This method implements the SQL/MM
   specification. SQL-MM 3 Topo-Geo and Topo-Net 3: Routine Details:
   X.3.5

-  `ST\_GetFaceGeometry <#ST_GetFaceGeometry>`__ - Returns the polygon
   in the given topology with the specified face id. This method
   implements the SQL/MM specification. SQL-MM 3 Topo-Geo and Topo-Net
   3: Routine Details: X.3.16

-  `ST\_InitTopoGeo <#ST_InitTopoGeo>`__ - Creates a new topology schema
   and registers this new schema in the topology.topology table and
   details summary of process. This method implements the SQL/MM
   specification. SQL-MM 3 Topo-Geo and Topo-Net 3: Routine Details:
   X.3.17

-  `ST\_InteriorRingN <#ST_InteriorRingN>`__ - Return the Nth interior
   linestring ring of the polygon geometry. Return NULL if the geometry
   is not a polygon or the given N is out of range. This method
   implements the SQL/MM specification. SQL-MM 3: 8.2.6, 8.3.5

-  `ST\_Intersection <#ST_Intersection>`__ - (T) Returns a geometry that
   represents the shared portion of geomA and geomB. The geography
   implementation does a transform to geometry to do the intersection
   and then transform back to WGS84. This method implements the SQL/MM
   specification. SQL-MM 3: 5.1.18

-  `ST\_Intersects <#ST_Intersects>`__ - Returns TRUE if the
   Geometries/Geography "spatially intersect in 2D" - (share any portion
   of space) and FALSE if they don't (they are Disjoint). For geography
   -- tolerance is 0.00001 meters (so any points that close are
   considered to intersect) This method implements the SQL/MM
   specification. SQL-MM 3: 5.1.27

-  `ST\_IsClosed <#ST_IsClosed>`__ - Returns TRUE if the LINESTRING's
   start and end points are coincident. For Polyhedral surface is closed
   (volumetric). This method implements the SQL/MM specification. SQL-MM
   3: 7.1.5, 9.3.3

-  `ST\_IsEmpty <#ST_IsEmpty>`__ - Returns true if this Geometry is an
   empty geometrycollection, polygon, point etc. This method implements
   the SQL/MM specification. SQL-MM 3: 5.1.7

-  `ST\_IsRing <#ST_IsRing>`__ - Returns TRUE if this LINESTRING is both
   closed and simple. This method implements the SQL/MM specification.
   SQL-MM 3: 7.1.6

-  `ST\_IsSimple <#ST_IsSimple>`__ - Returns (TRUE) if this Geometry has
   no anomalous geometric points, such as self intersection or self
   tangency. This method implements the SQL/MM specification. SQL-MM 3:
   5.1.8

-  `ST\_IsValid <#ST_IsValid>`__ - Returns true if the ST\_Geometry is
   well formed. This method implements the SQL/MM specification. SQL-MM
   3: 5.1.9

-  `ST\_Length <#ST_Length>`__ - Returns the 2d length of the geometry
   if it is a linestring or multilinestring. geometry are in units of
   spatial reference and geography are in meters (default spheroid) This
   method implements the SQL/MM specification. SQL-MM 3: 7.1.2, 9.3.4

-  `ST\_LineFromText <#ST_LineFromText>`__ - Makes a Geometry from WKT
   representation with the given SRID. If SRID is not given, it defaults
   to 0. This method implements the SQL/MM specification. SQL-MM 3:
   7.2.8

-  `ST\_LineFromWKB <#ST_LineFromWKB>`__ - Makes a LINESTRING from WKB
   with the given SRID This method implements the SQL/MM specification.
   SQL-MM 3: 7.2.9

-  `ST\_LinestringFromWKB <#ST_LinestringFromWKB>`__ - Makes a geometry
   from WKB with the given SRID. This method implements the SQL/MM
   specification. SQL-MM 3: 7.2.9

-  `ST\_M <#ST_M>`__ - Return the M coordinate of the point, or NULL if
   not available. Input must be a point. This method implements the
   SQL/MM specification.

-  `ST\_MLineFromText <#ST_MLineFromText>`__ - Return a specified
   ST\_MultiLineString value from WKT representation. This method
   implements the SQL/MM specification.SQL-MM 3: 9.4.4

-  `ST\_MPointFromText <#ST_MPointFromText>`__ - Makes a Geometry from
   WKT with the given SRID. If SRID is not give, it defaults to 0. This
   method implements the SQL/MM specification. SQL-MM 3: 9.2.4

-  `ST\_MPolyFromText <#ST_MPolyFromText>`__ - Makes a MultiPolygon
   Geometry from WKT with the given SRID. If SRID is not give, it
   defaults to 0. This method implements the SQL/MM specification.
   SQL-MM 3: 9.6.4

-  `ST\_ModEdgeHeal <#ST_ModEdgeHeal>`__ - Heal two edges by deleting
   the node connecting them, modifying the first edgeand deleting the
   second edge. Returns the id of the deleted node. This method
   implements the SQL/MM specification. SQL-MM: Topo-Geo and Topo-Net 3:
   Routine Details: X.3.9

-  `ST\_ModEdgeSplit <#ST_ModEdgeSplit>`__ - Split an edge by creating a
   new node along an existing edge, modifying the original edge and
   adding a new edge. This method implements the SQL/MM specification.
   SQL-MM: Topo-Geo and Topo-Net 3: Routine Details: X.3.9

-  `ST\_MoveIsoNode <#ST_MoveIsoNode>`__ - Moves an isolated node in a
   topology from one point to another. If new apoint geometry exists as
   a node an error is thrown. REturns description of move. This method
   implements the SQL/MM specification. SQL-MM: Topo-Net Routines: X.3.2

-  `ST\_NewEdgeHeal <#ST_NewEdgeHeal>`__ - Heal two edges by deleting
   the node connecting them, deleting both edges,and replacing them with
   an edge whose direction is the same as the firstedge provided. This
   method implements the SQL/MM specification. SQL-MM: Topo-Geo and
   Topo-Net 3: Routine Details: X.3.9

-  `ST\_NewEdgesSplit <#ST_NewEdgesSplit>`__ - Split an edge by creating
   a new node along an existing edge, deleting the original edge and
   replacing it with two new edges. Returns the id of the new node
   created that joins the new edges. This method implements the SQL/MM
   specification. SQL-MM: Topo-Net Routines: X.3.8

-  `ST\_NumGeometries <#ST_NumGeometries>`__ - If geometry is a
   GEOMETRYCOLLECTION (or MULTI\*) return the number of geometries, for
   single geometries will return 1, otherwise return NULL. This method
   implements the SQL/MM specification. SQL-MM 3: 9.1.4

-  `ST\_NumInteriorRing <#ST_NumInteriorRing>`__ - Return the number of
   interior rings of the first polygon in the geometry. Synonym to
   ST\_NumInteriorRings. This method implements the SQL/MM
   specification. SQL-MM 3: 8.2.5

-  `ST\_NumInteriorRings <#ST_NumInteriorRings>`__ - Return the number
   of interior rings of the first polygon in the geometry. This will
   work with both POLYGON and MULTIPOLYGON types but only looks at the
   first polygon. Return NULL if there is no polygon in the geometry.
   This method implements the SQL/MM specification. SQL-MM 3: 8.2.5

-  `ST\_NumPatches <#ST_NumPatches>`__ - Return the number of faces on a
   Polyhedral Surface. Will return null for non-polyhedral geometries.
   This method implements the SQL/MM specification. SQL-MM 3: ?

-  `ST\_NumPoints <#ST_NumPoints>`__ - Return the number of points in an
   ST\_LineString or ST\_CircularString value. This method implements
   the SQL/MM specification. SQL-MM 3: 7.2.4

-  `ST\_OrderingEquals <#ST_OrderingEquals>`__ - Returns true if the
   given geometries represent the same geometry and points are in the
   same directional order. This method implements the SQL/MM
   specification. SQL-MM 3: 5.1.43

-  `ST\_Overlaps <#ST_Overlaps>`__ - Returns TRUE if the Geometries
   share space, are of the same dimension, but are not completely
   contained by each other. This method implements the SQL/MM
   specification. SQL-MM 3: 5.1.32

-  `ST\_PatchN <#ST_PatchN>`__ - Return the 1-based Nth geometry (face)
   if the geometry is a POLYHEDRALSURFACE, POLYHEDRALSURFACEM.
   Otherwise, return NULL. This method implements the SQL/MM
   specification. SQL-MM 3: ?

-  `ST\_Perimeter <#ST_Perimeter>`__ - Return the length measurement of
   the boundary of an ST\_Surface or ST\_MultiSurface geometry or
   geography. (Polygon, Multipolygon). geometry measurement is in units
   of spatial reference and geography is in meters. This method
   implements the SQL/MM specification. SQL-MM 3: 8.1.3, 9.5.4

-  `ST\_Point <#ST_Point>`__ - Returns an ST\_Point with the given
   coordinate values. OGC alias for ST\_MakePoint. This method
   implements the SQL/MM specification. SQL-MM 3: 6.1.2

-  `ST\_PointFromText <#ST_PointFromText>`__ - Makes a point Geometry
   from WKT with the given SRID. If SRID is not given, it defaults to
   unknown. This method implements the SQL/MM specification. SQL-MM 3:
   6.1.8

-  `ST\_PointFromWKB <#ST_PointFromWKB>`__ - Makes a geometry from WKB
   with the given SRID This method implements the SQL/MM specification.
   SQL-MM 3: 6.1.9

-  `ST\_PointN <#ST_PointN>`__ - Return the Nth point in the first
   linestring or circular linestring in the geometry. Return NULL if
   there is no linestring in the geometry. This method implements the
   SQL/MM specification. SQL-MM 3: 7.2.5, 7.3.5

-  `ST\_PointOnSurface <#ST_PointOnSurface>`__ - Returns a POINT
   guaranteed to lie on the surface. This method implements the SQL/MM
   specification. SQL-MM 3: 8.1.5, 9.5.6. According to the specs,
   ST\_PointOnSurface works for surface geometries (POLYGONs,
   MULTIPOLYGONS, CURVED POLYGONS). So PostGIS seems to be extending
   what the spec allows here. Most databases Oracle,DB II, ESRI SDE seem
   to only support this function for surfaces. SQL Server 2008 like
   PostGIS supports for all common geometries.

-  `ST\_Polygon <#ST_Polygon>`__ - Returns a polygon built from the
   specified linestring and SRID. This method implements the SQL/MM
   specification. SQL-MM 3: 8.3.2

-  `ST\_PolygonFromText <#ST_PolygonFromText>`__ - Makes a Geometry from
   WKT with the given SRID. If SRID is not give, it defaults to 0. This
   method implements the SQL/MM specification. SQL-MM 3: 8.3.6

-  `ST\_Relate <#ST_Relate>`__ - Returns true if this Geometry is
   spatially related to anotherGeometry, by testing for intersections
   between the Interior, Boundary and Exterior of the two geometries as
   specified by the values in the intersectionMatrixPattern. If no
   intersectionMatrixPattern is passed in, then returns the maximum
   intersectionMatrixPattern that relates the 2 geometries. This method
   implements the SQL/MM specification. SQL-MM 3: 5.1.25

-  `ST\_RemEdgeModFace <#ST_RemEdgeModFace>`__ - Removes an edge and, if
   the removed edge separated two faces,delete one of the them and
   modify the other to take the space of both. This method implements
   the SQL/MM specification. SQL-MM: Topo-Geo and Topo-Net 3: Routine
   Details: X.3.15

-  `ST\_RemEdgeNewFace <#ST_RemEdgeNewFace>`__ - Removes an edge and, if
   the removed edge separated two faces,delete the original faces and
   replace them with a new face. This method implements the SQL/MM
   specification. SQL-MM: Topo-Geo and Topo-Net 3: Routine Details:
   X.3.14

-  `ST\_RemoveIsoNode <#ST_RemoveIsoNode>`__ - Removes an isolated node
   and returns description of action. If the node is not isolated (is
   start or end of an edge), then an exception is thrown. This method
   implements the SQL/MM specification. SQL-MM: Topo-Geo and Topo-Net 3:
   Routine Details: X+1.3.3

-  `ST\_SRID <#ST_SRID>`__ - Returns the spatial reference identifier
   for the ST\_Geometry as defined in spatial\_ref\_sys table. This
   method implements the SQL/MM specification. SQL-MM 3: 5.1.5

-  `ST\_StartPoint <#ST_StartPoint>`__ - Returns the first point of a
   LINESTRING geometry as a POINT. This method implements the SQL/MM
   specification. SQL-MM 3: 7.1.3

-  `ST\_SymDifference <#ST_SymDifference>`__ - Returns a geometry that
   represents the portions of A and B that do not intersect. It is
   called a symmetric difference because ST\_SymDifference(A,B) =
   ST\_SymDifference(B,A). This method implements the SQL/MM
   specification. SQL-MM 3: 5.1.21

-  `ST\_Touches <#ST_Touches>`__ - Returns TRUE if the geometries have
   at least one point in common, but their interiors do not intersect.
   This method implements the SQL/MM specification. SQL-MM 3: 5.1.28

-  `ST\_Transform <#ST_Transform>`__ - Returns a new geometry with its
   coordinates transformed to the SRID referenced by the integer
   parameter. This method implements the SQL/MM specification. SQL-MM 3:
   5.1.6

-  `ST\_Union <#ST_Union>`__ - Returns a geometry that represents the
   point set union of the Geometries. This method implements the SQL/MM
   specification. SQL-MM 3: 5.1.19 the z-index (elevation) when polygons
   are involved.

-  `ST\_WKBToSQL <#ST_WKBToSQL>`__ - Return a specified ST\_Geometry
   value from Well-Known Binary representation (WKB). This is an alias
   name for ST\_GeomFromWKB that takes no srid This method implements
   the SQL/MM specification. SQL-MM 3: 5.1.36

-  `ST\_WKTToSQL <#ST_WKTToSQL>`__ - Return a specified ST\_Geometry
   value from Well-Known Text representation (WKT). This is an alias
   name for ST\_GeomFromText This method implements the SQL/MM
   specification. SQL-MM 3: 5.1.34

-  `ST\_Within <#ST_Within>`__ - Returns true if the geometry A is
   completely inside geometry B This method implements the SQL/MM
   specification. SQL-MM 3: 5.1.30

-  `ST\_X <#ST_X>`__ - Return the X coordinate of the point, or NULL if
   not available. Input must be a point. This method implements the
   SQL/MM specification. SQL-MM 3: 6.1.3

-  `ST\_Y <#ST_Y>`__ - Return the Y coordinate of the point, or NULL if
   not available. Input must be a point. This method implements the
   SQL/MM specification. SQL-MM 3: 6.1.4

-  `ST\_Z <#ST_Z>`__ - Return the Z coordinate of the point, or NULL if
   not available. Input must be a point. This method implements the
   SQL/MM specification.

PostGIS Geography Support Functions
----------------------------------------

The functions and operators given below are PostGIS functions/operators
that take as input or return as output a
`geography <#PostGIS_Geography>`__ data type object.

    **Note**

    Functions with a (T) are not native geodetic functions, and use a
    ST\_Transform call to and from geometry to do the operation. As a
    result, they may not behave as expected when going over dateline,
    poles, and for large geometries or geometry pairs that cover more
    than one UTM zone. Basic tranform - (favoring UTM, Lambert Azimuthal
    (North/South), and falling back on mercator in worst case scenario)

-  `ST\_Area <#ST_Area>`__ - Returns the area of the surface if it is a
   polygon or multi-polygon. For "geometry" type area is in SRID units.
   For "geography" area is in square meters.

-  `ST\_AsBinary <#ST_AsBinary>`__ - Return the Well-Known Binary (WKB)
   representation of the geometry/geography without SRID meta data.

-  `ST\_AsEWKT <#ST_AsEWKT>`__ - Return the Well-Known Text (WKT)
   representation of the geometry with SRID meta data.

-  `ST\_AsGML <#ST_AsGML>`__ - Return the geometry as a GML version 2 or
   3 element.

-  `ST\_AsGeoJSON <#ST_AsGeoJSON>`__ - Return the geometry as a GeoJSON
   element.

-  `ST\_AsKML <#ST_AsKML>`__ - Return the geometry as a KML element.
   Several variants. Default version=2, default precision=15

-  `ST\_AsSVG <#ST_AsSVG>`__ - Returns a Geometry in SVG path data given
   a geometry or geography object.

-  `ST\_AsText <#ST_AsText>`__ - Return the Well-Known Text (WKT)
   representation of the geometry/geography without SRID metadata.

-  `ST\_Azimuth <#ST_Azimuth>`__ - Returns the north-based azimuth as
   the angle in radians measured clockwise from the vertical on pointA
   to pointB.

-  `ST\_Buffer <#ST_Buffer>`__ - (T) For geometry: Returns a geometry
   that represents all points whose distance from this Geometry is less
   than or equal to distance. Calculations are in the Spatial Reference
   System of this Geometry. For geography: Uses a planar transform
   wrapper. Introduced in 1.5 support for different end cap and mitre
   settings to control shape. buffer\_style options:
   quad\_segs=#,endcap=round\|flat\|square,join=round\|mitre\|bevel,mitre\_limit=#.#

-  `ST\_CoveredBy <#ST_CoveredBy>`__ - Returns 1 (TRUE) if no point in
   Geometry/Geography A is outside Geometry/Geography B

-  `ST\_Covers <#ST_Covers>`__ - Returns 1 (TRUE) if no point in
   Geometry B is outside Geometry A

-  `ST\_DWithin <#ST_DWithin>`__ - Returns true if the geometries are
   within the specified distance of one another. For geometry units are
   in those of spatial reference and For geography units are in meters
   and measurement is defaulted to use\_spheroid=true (measure around
   spheroid), for faster check, use\_spheroid=false to measure along
   sphere.

-  `ST\_Distance <#ST_Distance>`__ - For geometry type Returns the
   2-dimensional cartesian minimum distance (based on spatial ref)
   between two geometries in projected units. For geography type
   defaults to return spheroidal minimum distance between two
   geographies in meters.

-  `ST\_GeogFromText <#ST_GeogFromText>`__ - Return a specified
   geography value from Well-Known Text representation or extended
   (WKT).

-  `ST\_GeogFromWKB <#ST_GeogFromWKB>`__ - Creates a geography instance
   from a Well-Known Binary geometry representation (WKB) or extended
   Well Known Binary (EWKB).

-  `ST\_GeographyFromText <#ST_GeographyFromText>`__ - Return a
   specified geography value from Well-Known Text representation or
   extended (WKT).

-  `= <#ST_Geometry_EQ>`__ - Returns TRUE if A's bounding box is the
   same as B's. Uses double precision bounding box.

-  `ST\_Intersection <#ST_Intersection>`__ - (T) Returns a geometry that
   represents the shared portion of geomA and geomB. The geography
   implementation does a transform to geometry to do the intersection
   and then transform back to WGS84.

-  `ST\_Intersects <#ST_Intersects>`__ - Returns TRUE if the
   Geometries/Geography "spatially intersect in 2D" - (share any portion
   of space) and FALSE if they don't (they are Disjoint). For geography
   -- tolerance is 0.00001 meters (so any points that close are
   considered to intersect)

-  `ST\_Length <#ST_Length>`__ - Returns the 2d length of the geometry
   if it is a linestring or multilinestring. geometry are in units of
   spatial reference and geography are in meters (default spheroid)

-  `ST\_Perimeter <#ST_Perimeter>`__ - Return the length measurement of
   the boundary of an ST\_Surface or ST\_MultiSurface geometry or
   geography. (Polygon, Multipolygon). geometry measurement is in units
   of spatial reference and geography is in meters.

-  `ST\_Project <#ST_Project>`__ - Returns a POINT projected from a
   start point using a distance in meters and bearing (azimuth) in
   radians.

-  `ST\_Segmentize <#ST_Segmentize>`__ - Return a modified
   geometry/geography having no segment longer than the given distance.
   Distance computation is performed in 2d only. For geometry, length
   units are in units of spatial reference. For geography, units are in
   meters.

-  `ST\_Summary <#ST_Summary>`__ - Returns a text summary of the
   contents of the geometry.

-  `&& <#geometry_overlaps>`__ - Returns TRUE if A's 2D bounding box
   intersects B's 2D bounding box.

PostGIS Raster Support Functions
----------------------------------

The functions and operators given below are PostGIS functions/operators
that take as input or return as output a ? data type object. Listed in
alphabetical order.

-  `Box3D <#RT_Box3D>`__ - Returns the box 3d representation of the
   enclosing box of the raster.

-  `&& <#RT_Raster_Intersect>`__ - Returns TRUE if A's bounding box
   intersects B's bounding box.

-  `&< <#RT_Raster_OverLeft>`__ - Returns TRUE if A's bounding box is to
   the left of B's.

-  `&> <#RT_Raster_OverRight>`__ - Returns TRUE if A's bounding box is
   to the right of B's.

-  `ST\_AddBand <#RT_ST_AddBand>`__ - Returns a raster with the new
   band(s) of given type added with given initial value in the given
   index location. If no index is specified, the band is added to the
   end.

-  `ST\_AsBinary <#RT_ST_AsBinary>`__ - Return the Well-Known Binary
   (WKB) representation of the raster without SRID meta data.

-  `ST\_AsGDALRaster <#RT_ST_AsGDALRaster>`__ - Return the raster tile
   in the designated GDAL Raster format. Raster formats are one of those
   supported by your compiled library. Use ST\_GDALRasters() to get a
   list of formats supported by your library.

-  `ST\_AsJPEG <#RT_ST_AsJPEG>`__ - Return the raster tile selected
   bands as a single Joint Photographic Exports Group (JPEG) image (byte
   array). If no band is specified and 1 or more than 3 bands, then only
   the first band is used. If only 3 bands then all 3 bands are used and
   mapped to RGB.

-  `ST\_AsPNG <#RT_ST_AsPNG>`__ - Return the raster tile selected bands
   as a single portable network graphics (PNG) image (byte array). If 1,
   3, or 4 bands in raster and no bands are specified, then all bands
   are used. If more 2 or more than 4 bands and no bands specified, then
   only band 1 is used. Bands are mapped to RGB or RGBA space.

-  `ST\_AsRaster <#RT_ST_AsRaster>`__ - Converts a PostGIS geometry to a
   PostGIS raster.

-  `ST\_AsTIFF <#RT_ST_AsTIFF>`__ - Return the raster selected bands as
   a single TIFF image (byte array). If no band is specified, then will
   try to use all bands.

-  `ST\_Aspect <#RT_ST_Aspect>`__ - Returns the aspect (in degrees by
   default) of an elevation raster band. Useful for analyzing terrain.

-  `ST\_Band <#RT_ST_Band>`__ - Returns one or more bands of an existing
   raster as a new raster. Useful for building new rasters from existing
   rasters.

-  `ST\_BandIsNoData <#RT_ST_BandIsNoData>`__ - Returns true if the band
   is filled with only nodata values.

-  `ST\_BandMetaData <#RT_ST_BandMetaData>`__ - Returns basic meta data
   for a specific raster band. band num 1 is assumed if none-specified.

-  `ST\_BandNoDataValue <#RT_ST_BandNoDataValue>`__ - Returns the value
   in a given band that represents no data. If no band num 1 is assumed.

-  `ST\_BandPath <#RT_ST_BandPath>`__ - Returns system file path to a
   band stored in file system. If no bandnum specified, 1 is assumed.

-  `ST\_BandPixelType <#RT_ST_BandPixelType>`__ - Returns the type of
   pixel for given band. If no bandnum specified, 1 is assumed.

-  `ST\_Clip <#RT_ST_Clip>`__ - Returns the raster clipped by the input
   geometry. If band number not is specified, all bands are processed.
   If crop is not specified or TRUE, the output raster is cropped.

-  `ST\_ColorMap <#RT_ST_ColorMap>`__ - Creates a new raster of up to
   four 8BUI bands (grayscale, RGB, RGBA) from the source raster and a
   specified band. Band 1 is assumed if not specified.

-  `ST\_Contains <#RT_ST_Contains>`__ - Return true if no points of
   raster rastB lie in the exterior of raster rastA and at least one
   point of the interior of rastB lies in the interior of rastA.

-  `ST\_ContainsProperly <#RT_ST_ContainsProperly>`__ - Return true if
   rastB intersects the interior of rastA but not the boundary or
   exterior of rastA.

-  `ST\_ConvexHull <#RT_ST_ConvexHull>`__ - Return the convex hull
   geometry of the raster including pixel values equal to
   BandNoDataValue. For regular shaped and non-skewed rasters, this
   gives the same result as ST\_Envelope so only useful for irregularly
   shaped or skewed rasters.

-  `ST\_Count <#RT_ST_Count>`__ - Returns the number of pixels in a
   given band of a raster or raster coverage. If no band is specified
   defaults to band 1. If exclude\_nodata\_value is set to true, will
   only count pixels that are not equal to the nodata value.

-  `ST\_CoveredBy <#RT_ST_CoveredBy>`__ - Return true if no points of
   raster rastA lie outside raster rastB.

-  `ST\_Covers <#RT_ST_Covers>`__ - Return true if no points of raster
   rastB lie outside raster rastA.

-  `ST\_DFullyWithin <#RT_ST_DFullyWithin>`__ - Return true if rasters
   rastA and rastB are fully within the specified distance of each
   other.

-  `ST\_DWithin <#RT_ST_DWithin>`__ - Return true if rasters rastA and
   rastB are within the specified distance of each other.

-  `ST\_Disjoint <#RT_ST_Disjoint>`__ - Return true if raster rastA does
   not spatially intersect rastB.

-  `ST\_DumpAsPolygons <#RT_ST_DumpAsPolygons>`__ - Returns a set of
   geomval (geom,val) rows, from a given raster band. If no band number
   is specified, band num defaults to 1.

-  `ST\_DumpValues <#RT_ST_DumpValues>`__ - Get the values of the
   specified band as a 2-dimension array.

-  `ST\_Envelope <#RT_ST_Envelope>`__ - Returns the polygon
   representation of the extent of the raster.

-  `ST\_FromGDALRaster <#RT_ST_FromGDALRaster>`__ - Returns a raster
   from a supported GDAL raster file.

-  `ST\_GeoReference <#RT_ST_GeoReference>`__ - Returns the georeference
   meta data in GDAL or ESRI format as commonly seen in a world file.
   Default is GDAL.

-  `ST\_HasNoBand <#RT_ST_HasNoBand>`__ - Returns true if there is no
   band with given band number. If no band number is specified, then
   band number 1 is assumed.

-  `ST\_Height <#RT_ST_Height>`__ - Returns the height of the raster in
   pixels.

-  `ST\_HillShade <#RT_ST_HillShade>`__ - Returns the hypothetical
   illumination of an elevation raster band using provided azimuth,
   altitude, brightness and scale inputs.

-  `ST\_Histogram <#RT_ST_Histogram>`__ - Returns a set of record
   summarizing a raster or raster coverage data distribution separate
   bin ranges. Number of bins are autocomputed if not specified.

-  `ST\_Intersection <#RT_ST_Intersection>`__ - Returns a raster or a
   set of geometry-pixelvalue pairs representing the shared portion of
   two rasters or the geometrical intersection of a vectorization of the
   raster and a geometry.

-  `ST\_Intersects <#RT_ST_Intersects>`__ - Return true if raster rastA
   spatially intersects raster rastB.

-  `ST\_IsEmpty <#RT_ST_IsEmpty>`__ - Returns true if the raster is
   empty (width = 0 and height = 0). Otherwise, returns false.

-  `ST\_MakeEmptyRaster <#RT_ST_MakeEmptyRaster>`__ - Returns an empty
   raster (having no bands) of given dimensions (width & height),
   upperleft X and Y, pixel size and rotation (scalex, scaley, skewx &
   skewy) and reference system (srid). If a raster is passed in, returns
   a new raster with the same size, alignment and SRID. If srid is left
   out, the spatial ref is set to unknown (0).

-  `ST\_MapAlgebra <#RT_ST_MapAlgebra>`__ - Callback function version -
   Returns a one-band raster given one or more input rasters, band
   indexes and one user-specified callback function.

-  `ST\_MapAlgebraExpr <#RT_ST_MapAlgebraExpr>`__ - 1 raster band
   version: Creates a new one band raster formed by applying a valid
   PostgreSQL algebraic operation on the input raster band and of
   pixeltype provided. Band 1 is assumed if no band is specified.

-  `ST\_MapAlgebraExpr <#RT_ST_MapAlgebraExpr2>`__ - 2 raster band
   version: Creates a new one band raster formed by applying a valid
   PostgreSQL algebraic operation on the two input raster bands and of
   pixeltype provided. band 1 of each raster is assumed if no band
   numbers are specified. The resulting raster will be aligned (scale,
   skew and pixel corners) on the grid defined by the first raster and
   have its extent defined by the "extenttype" parameter. Values for
   "extenttype" can be: INTERSECTION, UNION, FIRST, SECOND.

-  `ST\_MapAlgebraFct <#RT_ST_MapAlgebraFct>`__ - 1 band version -
   Creates a new one band raster formed by applying a valid PostgreSQL
   function on the input raster band and of pixeltype prodived. Band 1
   is assumed if no band is specified.

-  `ST\_MapAlgebraFct <#RT_ST_MapAlgebraFct2>`__ - 2 band version -
   Creates a new one band raster formed by applying a valid PostgreSQL
   function on the 2 input raster bands and of pixeltype prodived. Band
   1 is assumed if no band is specified. Extent type defaults to
   INTERSECTION if not specified.

-  `ST\_MapAlgebraFctNgb <#RT_ST_MapAlgebraFctNgb>`__ - 1-band version:
   Map Algebra Nearest Neighbor using user-defined PostgreSQL function.
   Return a raster which values are the result of a PLPGSQL user
   function involving a neighborhood of values from the input raster
   band.

-  `ST\_MapAlgebra <#RT_ST_MapAlgebra_expr>`__ - Expression version -
   Returns a one-band raster given one or two input rasters, band
   indexes and one or more user-specified SQL expressions.

-  `ST\_MetaData <#RT_ST_MetaData>`__ - Returns basic meta data about a
   raster object such as pixel size, rotation (skew), upper, lower left,
   etc.

-  `ST\_MinConvexHull <#RT_ST_MinConvexHull>`__ - Return the convex hull
   geometry of the raster excluding NODATA pixels.

-  `ST\_NearestValue <#RT_ST_NearestValue>`__ - Returns the nearest
   non-NODATA value of a given band's pixel specified by a columnx and
   rowy or a geometric point expressed in the same spatial reference
   coordinate system as the raster.

-  `ST\_Neighborhood <#RT_ST_Neighborhood>`__ - Returns a 2-D double
   precision array of the non-NODATA values around a given band's pixel
   specified by either a columnX and rowY or a geometric point expressed
   in the same spatial reference coordinate system as the raster.

-  `ST\_NotSameAlignmentReason <#RT_ST_NotSameAlignmentReason>`__ -
   Returns text stating if rasters are aligned and if not aligned, a
   reason why.

-  `ST\_NumBands <#RT_ST_NumBands>`__ - Returns the number of bands in
   the raster object.

-  `ST\_Overlaps <#RT_ST_Overlaps>`__ - Return true if raster rastA and
   rastB intersect but one does not completely contain the other.

-  `ST\_PixelAsCentroid <#RT_ST_PixelAsCentroid>`__ - Returns the
   centroid (point geometry) of the area represented by a pixel.

-  `ST\_PixelAsCentroids <#RT_ST_PixelAsCentroids>`__ - Returns the
   centroid (point geometry) for each pixel of a raster band along with
   the value, the X and the Y raster coordinates of each pixel. The
   point geometry is the centroid of the area represented by a pixel.

-  `ST\_PixelAsPoint <#RT_ST_PixelAsPoint>`__ - Returns a point geometry
   of the pixel's upper-left corner.

-  `ST\_PixelAsPoints <#RT_ST_PixelAsPoints>`__ - Returns a point
   geometry for each pixel of a raster band along with the value, the X
   and the Y raster coordinates of each pixel. The coordinates of the
   point geometry are of the pixel's upper-left corner.

-  `ST\_PixelAsPolygon <#RT_ST_PixelAsPolygon>`__ - Returns the polygon
   geometry that bounds the pixel for a particular row and column.

-  `ST\_PixelAsPolygons <#RT_ST_PixelAsPolygons>`__ - Returns the
   polygon geometry that bounds every pixel of a raster band along with
   the value, the X and the Y raster coordinates of each pixel.

-  `ST\_PixelHeight <#RT_ST_PixelHeight>`__ - Returns the pixel height
   in geometric units of the spatial reference system.

-  `ST\_PixelOfValue <#RT_ST_PixelOfValue>`__ - Get the columnx, rowy
   coordinates of the pixel whose value equals the search value.

-  `ST\_PixelWidth <#RT_ST_PixelWidth>`__ - Returns the pixel width in
   geometric units of the spatial reference system.

-  `ST\_Polygon <#RT_ST_Polygon>`__ - Returns a multipolygon geometry
   formed by the union of pixels that have a pixel value that is not no
   data value. If no band number is specified, band num defaults to 1.

-  `ST\_Quantile <#RT_ST_Quantile>`__ - Compute quantiles for a raster
   or raster table coverage in the context of the sample or population.
   Thus, a value could be examined to be at the raster's 25%, 50%, 75%
   percentile.

-  `ST\_RasterToWorldCoord <#RT_ST_RasterToWorldCoord>`__ - Returns the
   raster's upper left corner as geometric X and Y (longitude and
   latitude) given a column and row. Column and row starts at 1.

-  `ST\_RasterToWorldCoordX <#RT_ST_RasterToWorldCoordX>`__ - Returns
   the geometric X coordinate upper left of a raster, column and row.
   Numbering of columns and rows starts at 1.

-  `ST\_RasterToWorldCoordY <#RT_ST_RasterToWorldCoordY>`__ - Returns
   the geometric Y coordinate upper left corner of a raster, column and
   row. Numbering of columns and rows starts at 1.

-  `ST\_Reclass <#RT_ST_Reclass>`__ - Creates a new raster composed of
   band types reclassified from original. The nband is the band to be
   changed. If nband is not specified assumed to be 1. All other bands
   are returned unchanged. Use case: convert a 16BUI band to a 8BUI and
   so forth for simpler rendering as viewable formats.

-  `ST\_Resample <#RT_ST_Resample>`__ - Resample a raster using a
   specified resampling algorithm, new dimensions, an arbitrary grid
   corner and a set of raster georeferencing attributes defined or
   borrowed from another raster.

-  `ST\_Rescale <#RT_ST_Rescale>`__ - Resample a raster by adjusting
   only its scale (or pixel size). New pixel values are computed using
   the NearestNeighbor (english or american spelling), Bilinear, Cubic,
   CubicSpline or Lanczos resampling algorithm. Default is
   NearestNeighbor.

-  `ST\_Resize <#RT_ST_Resize>`__ - Resize a raster to a new
   width/height

-  `ST\_Reskew <#RT_ST_Reskew>`__ - Resample a raster by adjusting only
   its skew (or rotation parameters). New pixel values are computed
   using the NearestNeighbor (english or american spelling), Bilinear,
   Cubic, CubicSpline or Lanczos resampling algorithm. Default is
   NearestNeighbor.

-  `ST\_Rotation <#RT_ST_Rotation>`__ - Returns the rotation of the
   raster in radian.

-  `ST\_Roughness <#RT_ST_Roughness>`__ - Returns a raster with the
   calculated "roughness" of a DEM.

-  `ST\_SRID <#RT_ST_SRID>`__ - Returns the spatial reference identifier
   of the raster as defined in spatial\_ref\_sys table.

-  `ST\_SameAlignment <#RT_ST_SameAlignment>`__ - Returns true if
   rasters have same skew, scale, spatial ref and false if they don't
   with notice detailing issue.

-  `ST\_ScaleX <#RT_ST_ScaleX>`__ - Returns the X component of the pixel
   width in units of coordinate reference system.

-  `ST\_ScaleY <#RT_ST_ScaleY>`__ - Returns the Y component of the pixel
   height in units of coordinate reference system.

-  `ST\_SetBandIsNoData <#RT_ST_SetBandIsNoData>`__ - Sets the isnodata
   flag of the band to TRUE.

-  `ST\_SetBandNoDataValue <#RT_ST_SetBandNoDataValue>`__ - Sets the
   value for the given band that represents no data. Band 1 is assumed
   if no band is specified. To mark a band as having no nodata value,
   set the nodata value = NULL.

-  `ST\_SetGeoReference <#RT_ST_SetGeoReference>`__ - Set Georeference 6
   georeference parameters in a single call. Numbers should be separated
   by white space. Accepts inputs in GDAL or ESRI format. Default is
   GDAL.

-  `ST\_SetRotation <#RT_ST_SetRotation>`__ - Set the rotation of the
   raster in radian.

-  `ST\_SetSRID <#RT_ST_SetSRID>`__ - Sets the SRID of a raster to a
   particular integer srid defined in the spatial\_ref\_sys table.

-  `ST\_SetScale <#RT_ST_SetScale>`__ - Sets the X and Y size of pixels
   in units of coordinate reference system. Number units/pixel
   width/height.

-  `ST\_SetSkew <#RT_ST_SetSkew>`__ - Sets the georeference X and Y skew
   (or rotation parameter). If only one is passed in, sets X and Y to
   the same value.

-  `ST\_SetUpperLeft <#RT_ST_SetUpperLeft>`__ - Sets the value of the
   upper left corner of the pixel to projected X and Y coordinates.

-  `ST\_SetValue <#RT_ST_SetValue>`__ - Returns modified raster
   resulting from setting the value of a given band in a given columnx,
   rowy pixel or the pixels that intersect a particular geometry. Band
   numbers start at 1 and assumed to be 1 if not specified.

-  `ST\_SetValues <#RT_ST_SetValues>`__ - Returns modified raster
   resulting from setting the values of a given band.

-  `ST\_SkewX <#RT_ST_SkewX>`__ - Returns the georeference X skew (or
   rotation parameter).

-  `ST\_SkewY <#RT_ST_SkewY>`__ - Returns the georeference Y skew (or
   rotation parameter).

-  `ST\_Slope <#RT_ST_Slope>`__ - Returns the slope (in degrees by
   default) of an elevation raster band. Useful for analyzing terrain.

-  `ST\_SnapToGrid <#RT_ST_SnapToGrid>`__ - Resample a raster by
   snapping it to a grid. New pixel values are computed using the
   NearestNeighbor (english or american spelling), Bilinear, Cubic,
   CubicSpline or Lanczos resampling algorithm. Default is
   NearestNeighbor.

-  `ST\_Summary <#RT_ST_Summary>`__ - Returns a text summary of the
   contents of the raster.

-  `ST\_SummaryStats <#RT_ST_SummaryStats>`__ - Returns record
   consisting of count, sum, mean, stddev, min, max for a given raster
   band of a raster or raster coverage. Band 1 is assumed is no band is
   specified.

-  `ST\_TPI <#RT_ST_TPI>`__ - Returns a raster with the calculated
   Topographic Position Index.

-  `ST\_TRI <#RT_ST_TRI>`__ - Returns a raster with the calculated
   Terrain Ruggedness Index.

-  `ST\_Tile <#RT_ST_Tile>`__ - Returns a set of rasters resulting from
   the split of the input raster based upon the desired dimensions of
   the output rasters.

-  `ST\_Touches <#RT_ST_Touches>`__ - Return true if raster rastA and
   rastB have at least one point in common but their interiors do not
   intersect.

-  `ST\_Transform <#RT_ST_Transform>`__ - Reprojects a raster in a known
   spatial reference system to another known spatial reference system
   using specified resampling algorithm. Options are NearestNeighbor,
   Bilinear, Cubic, CubicSpline, Lanczos defaulting to NearestNeighbor.

-  `ST\_Union <#RT_ST_Union>`__ - Returns the union of a set of raster
   tiles into a single raster composed of 1 or more bands.

-  `ST\_UpperLeftX <#RT_ST_UpperLeftX>`__ - Returns the upper left X
   coordinate of raster in projected spatial ref.

-  `ST\_UpperLeftY <#RT_ST_UpperLeftY>`__ - Returns the upper left Y
   coordinate of raster in projected spatial ref.

-  `ST\_Value <#RT_ST_Value>`__ - Returns the value of a given band in a
   given columnx, rowy pixel or at a particular geometric point. Band
   numbers start at 1 and assumed to be 1 if not specified. If
   exclude\_nodata\_value is set to false, then all pixels include
   nodata pixels are considered to intersect and return value. If
   exclude\_nodata\_value is not passed in then reads it from metadata
   of raster.

-  `ST\_ValueCount <#RT_ST_ValueCount>`__ - Returns a set of records
   containing a pixel band value and count of the number of pixels in a
   given band of a raster (or a raster coverage) that have a given set
   of values. If no band is specified defaults to band 1. By default
   nodata value pixels are not counted. and all other values in the
   pixel are output and pixel band values are rounded to the nearest
   integer.

-  `ST\_Width <#RT_ST_Width>`__ - Returns the width of the raster in
   pixels.

-  `ST\_Within <#RT_ST_Within>`__ - Return true if no points of raster
   rastA lie in the exterior of raster rastB and at least one point of
   the interior of rastA lies in the interior of rastB.

-  `ST\_WorldToRasterCoord <#RT_ST_WorldToRasterCoord>`__ - Returns the
   upper left corner as column and row given geometric X and Y
   (longitude and latitude) or a point geometry expressed in the spatial
   reference coordinate system of the raster.

-  `ST\_WorldToRasterCoordX <#RT_ST_WorldToRasterCoordX>`__ - Returns
   the column in the raster of the point geometry (pt) or a X and Y
   world coordinate (xw, yw) represented in world spatial reference
   system of raster.

-  `ST\_WorldToRasterCoordY <#RT_ST_WorldToRasterCoordY>`__ - Returns
   the row in the raster of the point geometry (pt) or a X and Y world
   coordinate (xw, yw) represented in world spatial reference system of
   raster.

-  `UpdateRasterSRID <#RT_UpdateRasterSRID>`__ - Change the SRID of all
   rasters in the user-specified column and table.

PostGIS Geometry / Geography / Raster Dump Functions
====================================================

The functions given below are PostGIS functions that take as input or
return as output a set of or single `geometry\_dump <#geometry_dump>`__
or `geomval <#geomval>`__ data type object.

-  `ST\_DumpAsPolygons <#RT_ST_DumpAsPolygons>`__ - Returns a set of
   geomval (geom,val) rows, from a given raster band. If no band number
   is specified, band num defaults to 1.

-  `ST\_Intersection <#RT_ST_Intersection>`__ - Returns a raster or a
   set of geometry-pixelvalue pairs representing the shared portion of
   two rasters or the geometrical intersection of a vectorization of the
   raster and a geometry.

-  `ST\_Dump <#ST_Dump>`__ - Returns a set of geometry\_dump (geom,path)
   rows, that make up a geometry g1.

-  `ST\_DumpPoints <#ST_DumpPoints>`__ - Returns a set of geometry\_dump
   (geom,path) rows of all points that make up a geometry.

-  `ST\_DumpRings <#ST_DumpRings>`__ - Returns a set of geometry\_dump
   rows, representing the exterior and interior rings of a polygon.

PostGIS Box Functions
=====================

The functions given below are PostGIS functions that take as input or
return as output the box\* family of PostGIS spatial types. The box
family of types consists of `box2d <#box2d_type>`__, and
`box3d <#box3d_type>`__

-  `Box2D <#Box2D>`__ - Returns a BOX2D representing the maximum extents
   of the geometry.

-  `Box3D <#Box3D>`__ - Returns a BOX3D representing the maximum extents
   of the geometry.

-  `Box3D <#RT_Box3D>`__ - Returns the box 3d representation of the
   enclosing box of the raster.

-  `ST\_3DExtent <#ST_3DExtent>`__ - an aggregate function that returns
   the box3D bounding box that bounds rows of geometries.

-  `ST\_3DMakeBox <#ST_3DMakeBox>`__ - Creates a BOX3D defined by the
   given 3d point geometries.

-  `ST\_Box2dFromGeoHash <#ST_Box2dFromGeoHash>`__ - Return a BOX2D from
   a GeoHash string.

-  `ST\_EstimatedExtent <#ST_Estimated_Extent>`__ - Return the
   'estimated' extent of the given spatial table. The estimated is taken
   from the geometry column's statistics. The current schema will be
   used if not specified.

-  `ST\_Expand <#ST_Expand>`__ - Returns bounding box expanded in all
   directions from the bounding box of the input geometry. Uses
   double-precision

-  `ST\_Extent <#ST_Extent>`__ - an aggregate function that returns the
   bounding box that bounds rows of geometries.

-  `ST\_MakeBox2D <#ST_MakeBox2D>`__ - Creates a BOX2D defined by the
   given point geometries.

-  `ST\_XMax <#ST_XMax>`__ - Returns X maxima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_XMin <#ST_XMin>`__ - Returns X minima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_YMax <#ST_YMax>`__ - Returns Y maxima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_YMin <#ST_YMin>`__ - Returns Y minima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_ZMax <#ST_ZMax>`__ - Returns Z minima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_ZMin <#ST_ZMin>`__ - Returns Z minima of a bounding box 2d or 3d
   or a geometry.

PostGIS Functions that support 3D
=================================

The functions given below are PostGIS functions that do not throw away
the Z-Index.

-  `AddGeometryColumn <#AddGeometryColumn>`__ - Adds a geometry column
   to an existing table of attributes. By default uses type modifier to
   define rather than constraints. Pass in false for use\_typmod to get
   old check constraint based behavior

-  `Box3D <#Box3D>`__ - Returns a BOX3D representing the maximum extents
   of the geometry.

-  `DropGeometryColumn <#DropGeometryColumn>`__ - Removes a geometry
   column from a spatial table.

-  `GeometryType <#GeometryType>`__ - Returns the type of the geometry
   as a string. Eg: 'LINESTRING', 'POLYGON', 'MULTIPOINT', etc.

-  `ST\_3DClosestPoint <#ST_3DClosestPoint>`__ - Returns the
   3-dimensional point on g1 that is closest to g2. This is the first
   point of the 3D shortest line.

-  `ST\_3DDFullyWithin <#ST_3DDFullyWithin>`__ - Returns true if all of
   the 3D geometries are within the specified distance of one another.

-  `ST\_3DDWithin <#ST_3DDWithin>`__ - For 3d (z) geometry type Returns
   true if two geometries 3d distance is within number of units.

-  `ST\_3DDistance <#ST_3DDistance>`__ - For geometry type Returns the
   3-dimensional cartesian minimum distance (based on spatial ref)
   between two geometries in projected units.

-  `ST\_3DExtent <#ST_3DExtent>`__ - an aggregate function that returns
   the box3D bounding box that bounds rows of geometries.

-  `ST\_3DIntersects <#ST_3DIntersects>`__ - Returns TRUE if the
   Geometries "spatially intersect" in 3d - only for points and
   linestrings

-  `ST\_3DLength <#ST_3DLength>`__ - Returns the 3-dimensional or
   2-dimensional length of the geometry if it is a linestring or
   multi-linestring.

-  `ST\_3DLength\_Spheroid <#ST_3DLength_Spheroid>`__ - Calculates the
   length of a geometry on an ellipsoid, taking the elevation into
   account. This is just an alias for ST\_Length\_Spheroid.

-  `ST\_3DLongestLine <#ST_3DLongestLine>`__ - Returns the 3-dimensional
   longest line between two geometries

-  `ST\_3DMakeBox <#ST_3DMakeBox>`__ - Creates a BOX3D defined by the
   given 3d point geometries.

-  `ST\_3DMaxDistance <#ST_3DMaxDistance>`__ - For geometry type Returns
   the 3-dimensional cartesian maximum distance (based on spatial ref)
   between two geometries in projected units.

-  `ST\_3DPerimeter <#ST_3DPerimeter>`__ - Returns the 3-dimensional
   perimeter of the geometry, if it is a polygon or multi-polygon.

-  `ST\_3DShortestLine <#ST_3DShortestLine>`__ - Returns the
   3-dimensional shortest line between two geometries

-  `ST\_Accum <#ST_Accum>`__ - Aggregate. Constructs an array of
   geometries.

-  `ST\_AddMeasure <#ST_AddMeasure>`__ - Return a derived geometry with
   measure elements linearly interpolated between the start and end
   points. If the geometry has no measure dimension, one is added. If
   the geometry has a measure dimension, it is over-written with new
   values. Only LINESTRINGS and MULTILINESTRINGS are supported.

-  `ST\_AddPoint <#ST_AddPoint>`__ - Adds a point to a LineString before
   point <position> (0-based index).

-  `ST\_Affine <#ST_Affine>`__ - Applies a 3d affine transformation to
   the geometry to do things like translate, rotate, scale in one step.

-  `ST\_AsBinary <#ST_AsBinary>`__ - Return the Well-Known Binary (WKB)
   representation of the geometry/geography without SRID meta data.

-  `ST\_AsEWKB <#ST_AsEWKB>`__ - Return the Well-Known Binary (WKB)
   representation of the geometry with SRID meta data.

-  `ST\_AsEWKT <#ST_AsEWKT>`__ - Return the Well-Known Text (WKT)
   representation of the geometry with SRID meta data.

-  `ST\_AsGML <#ST_AsGML>`__ - Return the geometry as a GML version 2 or
   3 element.

-  `ST\_AsGeoJSON <#ST_AsGeoJSON>`__ - Return the geometry as a GeoJSON
   element.

-  `ST\_AsHEXEWKB <#ST_AsHEXEWKB>`__ - Returns a Geometry in HEXEWKB
   format (as text) using either little-endian (NDR) or big-endian (XDR)
   encoding.

-  `ST\_AsKML <#ST_AsKML>`__ - Return the geometry as a KML element.
   Several variants. Default version=2, default precision=15

-  `ST\_AsX3D <#ST_AsX3D>`__ - Returns a Geometry in X3D xml node
   element format: ISO-IEC-19776-1.2-X3DEncodings-XML

-  `ST\_Boundary <#ST_Boundary>`__ - Returns the closure of the
   combinatorial boundary of this Geometry.

-  `ST\_Collect <#ST_Collect>`__ - Return a specified ST\_Geometry value
   from a collection of other geometries.

-  `ST\_ConvexHull <#ST_ConvexHull>`__ - The convex hull of a geometry
   represents the minimum convex geometry that encloses all geometries
   within the set.

-  `ST\_CoordDim <#ST_CoordDim>`__ - Return the coordinate dimension of
   the ST\_Geometry value.

-  `ST\_CurveToLine <#ST_CurveToLine>`__ - Converts a
   CIRCULARSTRING/CURVEDPOLYGON to a LINESTRING/POLYGON

-  `ST\_DelaunayTriangles <#ST_DelaunayTriangles>`__ - Return a Delaunay
   triangulation around the given input points.

-  `ST\_Difference <#ST_Difference>`__ - Returns a geometry that
   represents that part of geometry A that does not intersect with
   geometry B.

-  `ST\_Dump <#ST_Dump>`__ - Returns a set of geometry\_dump (geom,path)
   rows, that make up a geometry g1.

-  `ST\_DumpPoints <#ST_DumpPoints>`__ - Returns a set of geometry\_dump
   (geom,path) rows of all points that make up a geometry.

-  `ST\_DumpRings <#ST_DumpRings>`__ - Returns a set of geometry\_dump
   rows, representing the exterior and interior rings of a polygon.

-  `ST\_EndPoint <#ST_EndPoint>`__ - Returns the last point of a
   LINESTRING geometry as a POINT.

-  `ST\_ExteriorRing <#ST_ExteriorRing>`__ - Returns a line string
   representing the exterior ring of the POLYGON geometry. Return NULL
   if the geometry is not a polygon. Will not work with MULTIPOLYGON

-  `ST\_Extrude <#ST_Extrude>`__ - Extrude a surface to a related volume

-  `ST\_FlipCoordinates <#ST_FlipCoordinates>`__ - Returns a version of
   the given geometry with X and Y axis flipped. Useful for people who
   have built latitude/longitude features and need to fix them.

-  `ST\_ForceLHR <#ST_ForceLHR>`__ - Force LHR orientation

-  `ST\_ForceRHR <#ST_ForceRHR>`__ - Forces the orientation of the
   vertices in a polygon to follow the Right-Hand-Rule.

-  `ST\_ForceSFS <#ST_ForceSFS>`__ - Forces the geometries to use SFS
   1.1 geometry types only.

-  `ST\_Force\_2D <#ST_Force_2D>`__ - Forces the geometries into a
   "2-dimensional mode" so that all output representations will only
   have the X and Y coordinates.

-  `ST\_Force\_3D <#ST_Force_3D>`__ - Forces the geometries into XYZ
   mode. This is an alias for ST\_Force3DZ.

-  `ST\_Force\_3DZ <#ST_Force_3DZ>`__ - Forces the geometries into XYZ
   mode. This is a synonym for ST\_Force3D.

-  `ST\_Force\_4D <#ST_Force_4D>`__ - Forces the geometries into XYZM
   mode.

-  `ST\_Force\_Collection <#ST_Force_Collection>`__ - Converts the
   geometry into a GEOMETRYCOLLECTION.

-  `ST\_GeomFromEWKB <#ST_GeomFromEWKB>`__ - Return a specified
   ST\_Geometry value from Extended Well-Known Binary representation
   (EWKB).

-  `ST\_GeomFromEWKT <#ST_GeomFromEWKT>`__ - Return a specified
   ST\_Geometry value from Extended Well-Known Text representation
   (EWKT).

-  `ST\_GeomFromGML <#ST_GeomFromGML>`__ - Takes as input GML
   representation of geometry and outputs a PostGIS geometry object

-  `ST\_GeomFromGeoJSON <#ST_GeomFromGeoJSON>`__ - Takes as input a
   geojson representation of a geometry and outputs a PostGIS geometry
   object

-  `ST\_GeomFromKML <#ST_GeomFromKML>`__ - Takes as input KML
   representation of geometry and outputs a PostGIS geometry object

-  `ST\_GeometryN <#ST_GeometryN>`__ - Return the 1-based Nth geometry
   if the geometry is a GEOMETRYCOLLECTION, (MULTI)POINT,
   (MULTI)LINESTRING, MULTICURVE or (MULTI)POLYGON, POLYHEDRALSURFACE
   Otherwise, return NULL.

-  `ST\_GeometryType <#ST_GeometryType>`__ - Return the geometry type of
   the ST\_Geometry value.

-  `ST\_HasArc <#ST_HasArc>`__ - Returns true if a geometry or geometry
   collection contains a circular string

-  `ST\_InteriorRingN <#ST_InteriorRingN>`__ - Return the Nth interior
   linestring ring of the polygon geometry. Return NULL if the geometry
   is not a polygon or the given N is out of range.

-  `ST\_InterpolatePoint <#ST_InterpolatePoint>`__ - Return the value of
   the measure dimension of a geometry at the point closed to the
   provided point.

-  `ST\_IsClosed <#ST_IsClosed>`__ - Returns TRUE if the LINESTRING's
   start and end points are coincident. For Polyhedral surface is closed
   (volumetric).

-  `ST\_IsCollection <#ST_IsCollection>`__ - Returns TRUE if the
   argument is a collection (MULTI\*, GEOMETRYCOLLECTION, ...)

-  `ST\_IsPlanar <#ST_IsPlanar>`__ - Check if a surface is or not planar

-  `ST\_IsSimple <#ST_IsSimple>`__ - Returns (TRUE) if this Geometry has
   no anomalous geometric points, such as self intersection or self
   tangency.

-  `ST\_Length\_Spheroid <#ST_Length_Spheroid>`__ - Calculates the 2D or
   3D length of a linestring/multilinestring on an ellipsoid. This is
   useful if the coordinates of the geometry are in longitude/latitude
   and a length is desired without reprojection.

-  `ST\_LineFromMultiPoint <#ST_LineFromMultiPoint>`__ - Creates a
   LineString from a MultiPoint geometry.

-  `ST\_LineToCurve <#ST_LineToCurve>`__ - Converts a LINESTRING/POLYGON
   to a CIRCULARSTRING, CURVED POLYGON

-  `ST\_Line\_Interpolate\_Point <#ST_Line_Interpolate_Point>`__ -
   Returns a point interpolated along a line. Second argument is a
   float8 between 0 and 1 representing fraction of total length of
   linestring the point has to be located.

-  `ST\_Line\_Substring <#ST_Line_Substring>`__ - Return a linestring
   being a substring of the input one starting and ending at the given
   fractions of total 2d length. Second and third arguments are float8
   values between 0 and 1.

-  `ST\_LocateBetweenElevations <#ST_LocateBetweenElevations>`__ -
   Return a derived geometry (collection) value with elements that
   intersect the specified range of elevations inclusively. Only 3D, 4D
   LINESTRINGS and MULTILINESTRINGS are supported.

-  `ST\_M <#ST_M>`__ - Return the M coordinate of the point, or NULL if
   not available. Input must be a point.

-  `ST\_MakeLine <#ST_MakeLine>`__ - Creates a Linestring from point or
   line geometries.

-  `ST\_MakePoint <#ST_MakePoint>`__ - Creates a 2D,3DZ or 4D point
   geometry.

-  `ST\_MakePolygon <#ST_MakePolygon>`__ - Creates a Polygon formed by
   the given shell. Input geometries must be closed LINESTRINGS.

-  `ST\_MakeValid <#ST_MakeValid>`__ - Attempts to make an invalid
   geometry valid without losing vertices.

-  `ST\_MemUnion <#ST_MemUnion>`__ - Same as ST\_Union, only
   memory-friendly (uses less memory and more processor time).

-  `ST\_Mem\_Size <#ST_Mem_Size>`__ - Returns the amount of space (in
   bytes) the geometry takes.

-  `ST\_MinkowskiSum <#ST_MinkowskiSum>`__ - Perform Minkowski sum

-  `ST\_NDims <#ST_NDims>`__ - Returns coordinate dimension of the
   geometry as a small int. Values are: 2,3 or 4.

-  `ST\_NPoints <#ST_NPoints>`__ - Return the number of points
   (vertexes) in a geometry.

-  `ST\_NRings <#ST_NRings>`__ - If the geometry is a polygon or
   multi-polygon returns the number of rings.

-  `ST\_Node <#ST_Node>`__ - Node a set of linestrings.

-  `ST\_NumGeometries <#ST_NumGeometries>`__ - If geometry is a
   GEOMETRYCOLLECTION (or MULTI\*) return the number of geometries, for
   single geometries will return 1, otherwise return NULL.

-  `ST\_NumPatches <#ST_NumPatches>`__ - Return the number of faces on a
   Polyhedral Surface. Will return null for non-polyhedral geometries.

-  `ST\_Orientation <#ST_Orientation>`__ - Determine surface orientation

-  `ST\_PatchN <#ST_PatchN>`__ - Return the 1-based Nth geometry (face)
   if the geometry is a POLYHEDRALSURFACE, POLYHEDRALSURFACEM.
   Otherwise, return NULL.

-  `ST\_PointFromWKB <#ST_PointFromWKB>`__ - Makes a geometry from WKB
   with the given SRID

-  `ST\_PointN <#ST_PointN>`__ - Return the Nth point in the first
   linestring or circular linestring in the geometry. Return NULL if
   there is no linestring in the geometry.

-  `ST\_PointOnSurface <#ST_PointOnSurface>`__ - Returns a POINT
   guaranteed to lie on the surface.

-  `ST\_Polygon <#ST_Polygon>`__ - Returns a polygon built from the
   specified linestring and SRID.

-  `ST\_RemovePoint <#ST_RemovePoint>`__ - Removes point from a
   linestring. Offset is 0-based.

-  `ST\_RemoveRepeatedPoints <#ST_RemoveRepeatedPoints>`__ - Returns a
   version of the given geometry with duplicated points removed.

-  `ST\_Rotate <#ST_Rotate>`__ - Rotate a geometry rotRadians
   counter-clockwise about an origin.

-  `ST\_RotateX <#ST_RotateX>`__ - Rotate a geometry rotRadians about
   the X axis.

-  `ST\_RotateY <#ST_RotateY>`__ - Rotate a geometry rotRadians about
   the Y axis.

-  `ST\_RotateZ <#ST_RotateZ>`__ - Rotate a geometry rotRadians about
   the Z axis.

-  `ST\_Scale <#ST_Scale>`__ - Scales the geometry to a new size by
   multiplying the ordinates with the parameters. Ie: ST\_Scale(geom,
   Xfactor, Yfactor, Zfactor).

-  `ST\_SetPoint <#ST_SetPoint>`__ - Replace point N of linestring with
   given point. Index is 0-based.

-  `ST\_Shift\_Longitude <#ST_Shift_Longitude>`__ - Reads every
   point/vertex in every component of every feature in a geometry, and
   if the longitude coordinate is <0, adds 360 to it. The result would
   be a 0-360 version of the data to be plotted in a 180 centric map

-  `ST\_SnapToGrid <#ST_SnapToGrid>`__ - Snap all points of the input
   geometry to a regular grid.

-  `ST\_StartPoint <#ST_StartPoint>`__ - Returns the first point of a
   LINESTRING geometry as a POINT.

-  `ST\_StraightSkeleton <#ST_StraightSkeleton>`__ - Compute a straight
   skeleton from a geometry

-  `ST\_SymDifference <#ST_SymDifference>`__ - Returns a geometry that
   represents the portions of A and B that do not intersect. It is
   called a symmetric difference because ST\_SymDifference(A,B) =
   ST\_SymDifference(B,A).

-  `ST\_Tesselate <#ST_Tesselate>`__ - Perform surface Tesselation

-  `ST\_TransScale <#ST_TransScale>`__ - Translates the geometry using
   the deltaX and deltaY args, then scales it using the XFactor, YFactor
   args, working in 2D only.

-  `ST\_Translate <#ST_Translate>`__ - Translates the geometry to a new
   location using the numeric parameters as offsets. Ie:
   ST\_Translate(geom, X, Y) or ST\_Translate(geom, X, Y,Z).

-  `ST\_UnaryUnion <#ST_UnaryUnion>`__ - Like ST\_Union, but working at
   the geometry component level.

-  `ST\_X <#ST_X>`__ - Return the X coordinate of the point, or NULL if
   not available. Input must be a point.

-  `ST\_XMax <#ST_XMax>`__ - Returns X maxima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_XMin <#ST_XMin>`__ - Returns X minima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_Y <#ST_Y>`__ - Return the Y coordinate of the point, or NULL if
   not available. Input must be a point.

-  `ST\_YMax <#ST_YMax>`__ - Returns Y maxima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_YMin <#ST_YMin>`__ - Returns Y minima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_Z <#ST_Z>`__ - Return the Z coordinate of the point, or NULL if
   not available. Input must be a point.

-  `ST\_ZMax <#ST_ZMax>`__ - Returns Z minima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_ZMin <#ST_ZMin>`__ - Returns Z minima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_Zmflag <#ST_Zmflag>`__ - Returns ZM (dimension semantic) flag of
   the geometries as a small int. Values are: 0=2d, 1=3dm, 2=3dz, 3=4d.

-  `TG\_Equals <#TG_Equals>`__ - Returns true if two topogeometries are
   composed of the same topology primitives.

-  `TG\_Intersects <#TG_Intersects>`__ - Returns true if two
   topogeometries are composed of the same topology primitives.

-  `UpdateGeometrySRID <#UpdateGeometrySRID>`__ - Updates the SRID of
   all features in a geometry column, geometry\_columns metadata and
   srid. If it was enforced with constraints, the constraints will be
   updated with new srid constraint. If the old was enforced by type
   definition, the type definition will be changed.

-  `geometry\_overlaps\_nd <#geometry_overlaps_nd>`__ - Returns TRUE if
   A's 3D bounding box intersects B's 3D bounding box.

PostGIS Curved Geometry Support Functions
=========================================

The functions given below are PostGIS functions that can use
CIRCULARSTRING, CURVEDPOLYGON, and other curved geometry types

-  `AddGeometryColumn <#AddGeometryColumn>`__ - Adds a geometry column
   to an existing table of attributes. By default uses type modifier to
   define rather than constraints. Pass in false for use\_typmod to get
   old check constraint based behavior

-  `Box2D <#Box2D>`__ - Returns a BOX2D representing the maximum extents
   of the geometry.

-  `Box3D <#Box3D>`__ - Returns a BOX3D representing the maximum extents
   of the geometry.

-  `DropGeometryColumn <#DropGeometryColumn>`__ - Removes a geometry
   column from a spatial table.

-  `GeometryType <#GeometryType>`__ - Returns the type of the geometry
   as a string. Eg: 'LINESTRING', 'POLYGON', 'MULTIPOINT', etc.

-  `PostGIS\_AddBBox <#PostGIS_AddBBox>`__ - Add bounding box to the
   geometry.

-  `PostGIS\_DropBBox <#PostGIS_DropBBox>`__ - Drop the bounding box
   cache from the geometry.

-  `PostGIS\_HasBBox <#PostGIS_HasBBox>`__ - Returns TRUE if the bbox of
   this geometry is cached, FALSE otherwise.

-  `ST\_3DExtent <#ST_3DExtent>`__ - an aggregate function that returns
   the box3D bounding box that bounds rows of geometries.

-  `ST\_Accum <#ST_Accum>`__ - Aggregate. Constructs an array of
   geometries.

-  `ST\_Affine <#ST_Affine>`__ - Applies a 3d affine transformation to
   the geometry to do things like translate, rotate, scale in one step.

-  `ST\_AsBinary <#ST_AsBinary>`__ - Return the Well-Known Binary (WKB)
   representation of the geometry/geography without SRID meta data.

-  `ST\_AsEWKB <#ST_AsEWKB>`__ - Return the Well-Known Binary (WKB)
   representation of the geometry with SRID meta data.

-  `ST\_AsEWKT <#ST_AsEWKT>`__ - Return the Well-Known Text (WKT)
   representation of the geometry with SRID meta data.

-  `ST\_AsHEXEWKB <#ST_AsHEXEWKB>`__ - Returns a Geometry in HEXEWKB
   format (as text) using either little-endian (NDR) or big-endian (XDR)
   encoding.

-  `ST\_AsText <#ST_AsText>`__ - Return the Well-Known Text (WKT)
   representation of the geometry/geography without SRID metadata.

-  `ST\_Collect <#ST_Collect>`__ - Return a specified ST\_Geometry value
   from a collection of other geometries.

-  `ST\_CoordDim <#ST_CoordDim>`__ - Return the coordinate dimension of
   the ST\_Geometry value.

-  `ST\_CurveToLine <#ST_CurveToLine>`__ - Converts a
   CIRCULARSTRING/CURVEDPOLYGON to a LINESTRING/POLYGON

-  `ST\_Distance <#ST_Distance>`__ - For geometry type Returns the
   2-dimensional cartesian minimum distance (based on spatial ref)
   between two geometries in projected units. For geography type
   defaults to return spheroidal minimum distance between two
   geographies in meters.

-  `ST\_Dump <#ST_Dump>`__ - Returns a set of geometry\_dump (geom,path)
   rows, that make up a geometry g1.

-  `ST\_DumpPoints <#ST_DumpPoints>`__ - Returns a set of geometry\_dump
   (geom,path) rows of all points that make up a geometry.

-  `ST\_EstimatedExtent <#ST_Estimated_Extent>`__ - Return the
   'estimated' extent of the given spatial table. The estimated is taken
   from the geometry column's statistics. The current schema will be
   used if not specified.

-  `ST\_FlipCoordinates <#ST_FlipCoordinates>`__ - Returns a version of
   the given geometry with X and Y axis flipped. Useful for people who
   have built latitude/longitude features and need to fix them.

-  `ST\_ForceSFS <#ST_ForceSFS>`__ - Forces the geometries to use SFS
   1.1 geometry types only.

-  `ST\_Force2D <#ST_Force_2D>`__ - Forces the geometries into a
   "2-dimensional mode" so that all output representations will only
   have the X and Y coordinates.

-  `ST\_Force3D <#ST_Force_3D>`__ - Forces the geometries into XYZ mode.
   This is an alias for ST\_Force3DZ.

-  `ST\_Force3DM <#ST_Force_3DM>`__ - Forces the geometries into XYM
   mode.

-  `ST\_Force3DZ <#ST_Force_3DZ>`__ - Forces the geometries into XYZ
   mode. This is a synonym for ST\_Force3D.

-  `ST\_Force4D <#ST_Force_4D>`__ - Forces the geometries into XYZM
   mode.

-  `ST\_ForceCollection <#ST_Force_Collection>`__ - Converts the
   geometry into a GEOMETRYCOLLECTION.

-  `ST\_GeoHash <#ST_GeoHash>`__ - Return a GeoHash representation of
   the geometry.

-  `ST\_GeogFromWKB <#ST_GeogFromWKB>`__ - Creates a geography instance
   from a Well-Known Binary geometry representation (WKB) or extended
   Well Known Binary (EWKB).

-  `ST\_GeomFromEWKB <#ST_GeomFromEWKB>`__ - Return a specified
   ST\_Geometry value from Extended Well-Known Binary representation
   (EWKB).

-  `ST\_GeomFromEWKT <#ST_GeomFromEWKT>`__ - Return a specified
   ST\_Geometry value from Extended Well-Known Text representation
   (EWKT).

-  `ST\_GeomFromText <#ST_GeomFromText>`__ - Return a specified
   ST\_Geometry value from Well-Known Text representation (WKT).

-  `ST\_GeomFromWKB <#ST_GeomFromWKB>`__ - Creates a geometry instance
   from a Well-Known Binary geometry representation (WKB) and optional
   SRID.

-  `ST\_GeometryN <#ST_GeometryN>`__ - Return the 1-based Nth geometry
   if the geometry is a GEOMETRYCOLLECTION, (MULTI)POINT,
   (MULTI)LINESTRING, MULTICURVE or (MULTI)POLYGON, POLYHEDRALSURFACE
   Otherwise, return NULL.

-  `= <#ST_Geometry_EQ>`__ - Returns TRUE if A's bounding box is the
   same as B's. Uses double precision bounding box.

-  `&<\| <#ST_Geometry_Overbelow>`__ - Returns TRUE if A's bounding box
   overlaps or is below B's.

-  `ST\_HasArc <#ST_HasArc>`__ - Returns true if a geometry or geometry
   collection contains a circular string

-  `ST\_IsClosed <#ST_IsClosed>`__ - Returns TRUE if the LINESTRING's
   start and end points are coincident. For Polyhedral surface is closed
   (volumetric).

-  `ST\_IsCollection <#ST_IsCollection>`__ - Returns TRUE if the
   argument is a collection (MULTI\*, GEOMETRYCOLLECTION, ...)

-  `ST\_IsEmpty <#ST_IsEmpty>`__ - Returns true if this Geometry is an
   empty geometrycollection, polygon, point etc.

-  `ST\_LineToCurve <#ST_LineToCurve>`__ - Converts a LINESTRING/POLYGON
   to a CIRCULARSTRING, CURVED POLYGON

-  `ST\_Mem\_Size <#ST_Mem_Size>`__ - Returns the amount of space (in
   bytes) the geometry takes.

-  `ST\_NPoints <#ST_NPoints>`__ - Return the number of points
   (vertexes) in a geometry.

-  `ST\_NRings <#ST_NRings>`__ - If the geometry is a polygon or
   multi-polygon returns the number of rings.

-  `ST\_PointFromWKB <#ST_PointFromWKB>`__ - Makes a geometry from WKB
   with the given SRID

-  `ST\_PointN <#ST_PointN>`__ - Return the Nth point in the first
   linestring or circular linestring in the geometry. Return NULL if
   there is no linestring in the geometry.

-  `ST\_Rotate <#ST_Rotate>`__ - Rotate a geometry rotRadians
   counter-clockwise about an origin.

-  `ST\_RotateZ <#ST_RotateZ>`__ - Rotate a geometry rotRadians about
   the Z axis.

-  `ST\_SRID <#ST_SRID>`__ - Returns the spatial reference identifier
   for the ST\_Geometry as defined in spatial\_ref\_sys table.

-  `ST\_Scale <#ST_Scale>`__ - Scales the geometry to a new size by
   multiplying the ordinates with the parameters. Ie: ST\_Scale(geom,
   Xfactor, Yfactor, Zfactor).

-  `ST\_SetSRID <#ST_SetSRID>`__ - Sets the SRID on a geometry to a
   particular integer value.

-  `ST\_TransScale <#ST_TransScale>`__ - Translates the geometry using
   the deltaX and deltaY args, then scales it using the XFactor, YFactor
   args, working in 2D only.

-  `ST\_Transform <#ST_Transform>`__ - Returns a new geometry with its
   coordinates transformed to the SRID referenced by the integer
   parameter.

-  `ST\_Translate <#ST_Translate>`__ - Translates the geometry to a new
   location using the numeric parameters as offsets. Ie:
   ST\_Translate(geom, X, Y) or ST\_Translate(geom, X, Y,Z).

-  `ST\_XMax <#ST_XMax>`__ - Returns X maxima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_XMin <#ST_XMin>`__ - Returns X minima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_YMax <#ST_YMax>`__ - Returns Y maxima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_YMin <#ST_YMin>`__ - Returns Y minima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_ZMax <#ST_ZMax>`__ - Returns Z minima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_ZMin <#ST_ZMin>`__ - Returns Z minima of a bounding box 2d or 3d
   or a geometry.

-  `ST\_Zmflag <#ST_Zmflag>`__ - Returns ZM (dimension semantic) flag of
   the geometries as a small int. Values are: 0=2d, 1=3dm, 2=3dz, 3=4d.

-  `UpdateGeometrySRID <#UpdateGeometrySRID>`__ - Updates the SRID of
   all features in a geometry column, geometry\_columns metadata and
   srid. If it was enforced with constraints, the constraints will be
   updated with new srid constraint. If the old was enforced by type
   definition, the type definition will be changed.

-  `&& <#geometry_overlaps>`__ - Returns TRUE if A's 2D bounding box
   intersects B's 2D bounding box.

-  `&&& <#geometry_overlaps_nd>`__ - Returns TRUE if A's 3D bounding box
   intersects B's 3D bounding box.

PostGIS Polyhedral Surface Support Functions
============================================

The functions given below are PostGIS functions that can use
POLYHEDRALSURFACE, POLYHEDRALSURFACEM geometries

-  `Box2D <#Box2D>`__ - Returns a BOX2D representing the maximum extents
   of the geometry.

-  `Box3D <#Box3D>`__ - Returns a BOX3D representing the maximum extents
   of the geometry.

-  `GeometryType <#GeometryType>`__ - Returns the type of the geometry
   as a string. Eg: 'LINESTRING', 'POLYGON', 'MULTIPOINT', etc.

-  `ST\_3DClosestPoint <#ST_3DClosestPoint>`__ - Returns the
   3-dimensional point on g1 that is closest to g2. This is the first
   point of the 3D shortest line.

-  `ST\_3DDFullyWithin <#ST_3DDFullyWithin>`__ - Returns true if all of
   the 3D geometries are within the specified distance of one another.

-  `ST\_3DDWithin <#ST_3DDWithin>`__ - For 3d (z) geometry type Returns
   true if two geometries 3d distance is within number of units.

-  `ST\_3DDistance <#ST_3DDistance>`__ - For geometry type Returns the
   3-dimensional cartesian minimum distance (based on spatial ref)
   between two geometries in projected units.

-  `ST\_3DExtent <#ST_3DExtent>`__ - an aggregate function that returns
   the box3D bounding box that bounds rows of geometries.

-  `ST\_3DIntersects <#ST_3DIntersects>`__ - Returns TRUE if the
   Geometries "spatially intersect" in 3d - only for points and
   linestrings

-  `ST\_3DLongestLine <#ST_3DLongestLine>`__ - Returns the 3-dimensional
   longest line between two geometries

-  `ST\_3DMaxDistance <#ST_3DMaxDistance>`__ - For geometry type Returns
   the 3-dimensional cartesian maximum distance (based on spatial ref)
   between two geometries in projected units.

-  `ST\_3DShortestLine <#ST_3DShortestLine>`__ - Returns the
   3-dimensional shortest line between two geometries

-  `ST\_Accum <#ST_Accum>`__ - Aggregate. Constructs an array of
   geometries.

-  `ST\_Affine <#ST_Affine>`__ - Applies a 3d affine transformation to
   the geometry to do things like translate, rotate, scale in one step.

-  `ST\_Area <#ST_Area>`__ - Returns the area of the surface if it is a
   polygon or multi-polygon. For "geometry" type area is in SRID units.
   For "geography" area is in square meters.

-  `ST\_AsBinary <#ST_AsBinary>`__ - Return the Well-Known Binary (WKB)
   representation of the geometry/geography without SRID meta data.

-  `ST\_AsEWKB <#ST_AsEWKB>`__ - Return the Well-Known Binary (WKB)
   representation of the geometry with SRID meta data.

-  `ST\_AsEWKT <#ST_AsEWKT>`__ - Return the Well-Known Text (WKT)
   representation of the geometry with SRID meta data.

-  `ST\_AsGML <#ST_AsGML>`__ - Return the geometry as a GML version 2 or
   3 element.

-  `ST\_AsX3D <#ST_AsX3D>`__ - Returns a Geometry in X3D xml node
   element format: ISO-IEC-19776-1.2-X3DEncodings-XML

-  `ST\_CoordDim <#ST_CoordDim>`__ - Return the coordinate dimension of
   the ST\_Geometry value.

-  `ST\_Dimension <#ST_Dimension>`__ - The inherent dimension of this
   Geometry object, which must be less than or equal to the coordinate
   dimension.

-  `ST\_Dump <#ST_Dump>`__ - Returns a set of geometry\_dump (geom,path)
   rows, that make up a geometry g1.

-  `ST\_DumpPoints <#ST_DumpPoints>`__ - Returns a set of geometry\_dump
   (geom,path) rows of all points that make up a geometry.

-  `ST\_Expand <#ST_Expand>`__ - Returns bounding box expanded in all
   directions from the bounding box of the input geometry. Uses
   double-precision

-  `ST\_Extent <#ST_Extent>`__ - an aggregate function that returns the
   bounding box that bounds rows of geometries.

-  `ST\_Extrude <#ST_Extrude>`__ - Extrude a surface to a related volume

-  `ST\_FlipCoordinates <#ST_FlipCoordinates>`__ - Returns a version of
   the given geometry with X and Y axis flipped. Useful for people who
   have built latitude/longitude features and need to fix them.

-  `ST\_ForceLHR <#ST_ForceLHR>`__ - Force LHR orientation

-  `ST\_ForceRHR <#ST_ForceRHR>`__ - Forces the orientation of the
   vertices in a polygon to follow the Right-Hand-Rule.

-  `ST\_ForceSFS <#ST_ForceSFS>`__ - Forces the geometries to use SFS
   1.1 geometry types only.

-  `ST\_Force2D <#ST_Force_2D>`__ - Forces the geometries into a
   "2-dimensional mode" so that all output representations will only
   have the X and Y coordinates.

-  `ST\_Force3D <#ST_Force_3D>`__ - Forces the geometries into XYZ mode.
   This is an alias for ST\_Force3DZ.

-  `ST\_Force3DZ <#ST_Force_3DZ>`__ - Forces the geometries into XYZ
   mode. This is a synonym for ST\_Force3D.

-  `ST\_ForceCollection <#ST_Force_Collection>`__ - Converts the
   geometry into a GEOMETRYCOLLECTION.

-  `ST\_GeomFromEWKB <#ST_GeomFromEWKB>`__ - Return a specified
   ST\_Geometry value from Extended Well-Known Binary representation
   (EWKB).

-  `ST\_GeomFromEWKT <#ST_GeomFromEWKT>`__ - Return a specified
   ST\_Geometry value from Extended Well-Known Text representation
   (EWKT).

-  `ST\_GeomFromGML <#ST_GeomFromGML>`__ - Takes as input GML
   representation of geometry and outputs a PostGIS geometry object

-  `ST\_GeometryN <#ST_GeometryN>`__ - Return the 1-based Nth geometry
   if the geometry is a GEOMETRYCOLLECTION, (MULTI)POINT,
   (MULTI)LINESTRING, MULTICURVE or (MULTI)POLYGON, POLYHEDRALSURFACE
   Otherwise, return NULL.

-  `ST\_GeometryType <#ST_GeometryType>`__ - Return the geometry type of
   the ST\_Geometry value.

-  `= <#ST_Geometry_EQ>`__ - Returns TRUE if A's bounding box is the
   same as B's. Uses double precision bounding box.

-  `&<\| <#ST_Geometry_Overbelow>`__ - Returns TRUE if A's bounding box
   overlaps or is below B's.

-  `~= <#ST_Geometry_Same>`__ - Returns TRUE if A's bounding box is the
   same as B's.

-  `ST\_IsClosed <#ST_IsClosed>`__ - Returns TRUE if the LINESTRING's
   start and end points are coincident. For Polyhedral surface is closed
   (volumetric).

-  `ST\_IsPlanar <#ST_IsPlanar>`__ - Check if a surface is or not planar

-  `ST\_Mem\_Size <#ST_Mem_Size>`__ - Returns the amount of space (in
   bytes) the geometry takes.

-  `ST\_MinkowskiSum <#ST_MinkowskiSum>`__ - Perform Minkowski sum

-  `ST\_NPoints <#ST_NPoints>`__ - Return the number of points
   (vertexes) in a geometry.

-  `ST\_NumGeometries <#ST_NumGeometries>`__ - If geometry is a
   GEOMETRYCOLLECTION (or MULTI\*) return the number of geometries, for
   single geometries will return 1, otherwise return NULL.

-  `ST\_NumPatches <#ST_NumPatches>`__ - Return the number of faces on a
   Polyhedral Surface. Will return null for non-polyhedral geometries.

-  `ST\_Orientation <#ST_Orientation>`__ - Determine surface orientation

-  `ST\_PatchN <#ST_PatchN>`__ - Return the 1-based Nth geometry (face)
   if the geometry is a POLYHEDRALSURFACE, POLYHEDRALSURFACEM.
   Otherwise, return NULL.

-  `ST\_RemoveRepeatedPoints <#ST_RemoveRepeatedPoints>`__ - Returns a
   version of the given geometry with duplicated points removed.

-  `ST\_Rotate <#ST_Rotate>`__ - Rotate a geometry rotRadians
   counter-clockwise about an origin.

-  `ST\_RotateX <#ST_RotateX>`__ - Rotate a geometry rotRadians about
   the X axis.

-  `ST\_RotateY <#ST_RotateY>`__ - Rotate a geometry rotRadians about
   the Y axis.

-  `ST\_RotateZ <#ST_RotateZ>`__ - Rotate a geometry rotRadians about
   the Z axis.

-  `ST\_Scale <#ST_Scale>`__ - Scales the geometry to a new size by
   multiplying the ordinates with the parameters. Ie: ST\_Scale(geom,
   Xfactor, Yfactor, Zfactor).

-  `ST\_Shift\_Longitude <#ST_Shift_Longitude>`__ - Reads every
   point/vertex in every component of every feature in a geometry, and
   if the longitude coordinate is <0, adds 360 to it. The result would
   be a 0-360 version of the data to be plotted in a 180 centric map

-  `ST\_StraightSkeleton <#ST_StraightSkeleton>`__ - Compute a straight
   skeleton from a geometry

-  `ST\_Tesselate <#ST_Tesselate>`__ - Perform surface Tesselation

-  `ST\_Transform <#ST_Transform>`__ - Returns a new geometry with its
   coordinates transformed to the SRID referenced by the integer
   parameter.

-  `&& <#geometry_overlaps>`__ - Returns TRUE if A's 2D bounding box
   intersects B's 2D bounding box.

-  `&&& <#geometry_overlaps_nd>`__ - Returns TRUE if A's 3D bounding box
   intersects B's 3D bounding box.

PostGIS Function Support Matrix
===============================

Below is an alphabetical listing of spatial specific functions in
PostGIS and the kinds of spatial types they work with or OGC/SQL
compliance they try to conform to.

-  A |image0| means the function works with the type or subtype
   natively.

-  A |image1| means it works but with a transform cast built-in using
   cast to geometry, transform to a "best srid" spatial ref and then
   cast back. Results may not be as expected for large areas or areas at
   poles and may accumulate floating point junk.

-  A |image2| means the function works with the type because of a
   auto-cast to another such as to box3d rather than direct type
   support.

-  A |image3| means the function only available if PostGIS compiled with
   SFCGAL support.

-  A |image4| means the function support is provided by SFCGAL if
   PostGIS compiled with SFCGAL support, otherwise GEOS/built-in
   support.

-  geom - Basic 2D geometry support (x,y).

-  geog - Basic 2D geography support (x,y).

-  2.5D - basic 2D geometries in 3 D/4D space (has Z or M coord).

-  PS - Polyhedral surfaces

-  T - Triangles and Triangulated Irregular Network surfaces (TIN)

+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| Function                                                          | geom         | geog         | 2.5D         | Curves       | SQL MM       | PS           | T            |
+===================================================================+==============+==============+==============+==============+==============+==============+==============+
| `Box2D <#Box2D>`__                                                | |image623|   |              |              | |image624|   |              | |image625|   | |image626|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `Box3D <#Box3D>`__                                                | |image627|   |              | |image628|   | |image629|   |              | |image630|   | |image631|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `Find\_SRID <#Find_SRID>`__                                       |              |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `GeometryType <#GeometryType>`__                                  | |image632|   |              | |image633|   | |image634|   |              | |image635|   | |image636|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_3DClosestPoint <#ST_3DClosestPoint>`__                       | |image637|   |              | |image638|   |              |              | |image639|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_3DDFullyWithin <#ST_3DDFullyWithin>`__                       | |image640|   |              | |image641|   |              |              | |image642|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_3DDWithin <#ST_3DDWithin>`__                                 | |image643|   |              | |image644|   |              | |image645|   | |image646|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_3DDistance <#ST_3DDistance>`__                               | |image647|   |              | |image648|   |              | |image649|   | |image650|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_3DExtent <#ST_3DExtent>`__                                   | |image651|   |              | |image652|   | |image653|   |              | |image654|   | |image655|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_3DIntersects <#ST_3DIntersects>`__                           | |image656|   |              | |image657|   |              | |image658|   | |image659|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_3DLength <#ST_3DLength>`__                                   | |image660|   |              | |image661|   |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_3DLength\_Spheroid <#ST_3DLength_Spheroid>`__                | |image662|   |              | |image663|   |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_3DLongestLine <#ST_3DLongestLine>`__                         | |image664|   |              | |image665|   |              |              | |image666|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_3DMakeBox <#ST_3DMakeBox>`__                                 | |image667|   |              | |image668|   |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_3DMaxDistance <#ST_3DMaxDistance>`__                         | |image669|   |              | |image670|   |              |              | |image671|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_3DPerimeter <#ST_3DPerimeter>`__                             | |image672|   |              | |image673|   |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_3DShortestLine <#ST_3DShortestLine>`__                       | |image674|   |              | |image675|   |              |              | |image676|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Accum <#ST_Accum>`__                                         | |image677|   |              | |image678|   | |image679|   |              | |image680|   | |image681|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_AddMeasure <#ST_AddMeasure>`__                               | |image682|   |              | |image683|   |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_AddPoint <#ST_AddPoint>`__                                   | |image684|   |              | |image685|   |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Affine <#ST_Affine>`__                                       | |image686|   |              | |image687|   | |image688|   |              | |image689|   | |image690|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Area <#ST_Area>`__                                           | |image691|   | |image692|   |              |              | |image693|   | |image694|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_AsBinary <#ST_AsBinary>`__                                   | |image695|   | |image696|   | |image697|   | |image698|   | |image699|   | |image700|   | |image701|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_AsEWKB <#ST_AsEWKB>`__                                       | |image702|   |              | |image703|   | |image704|   |              | |image705|   | |image706|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_AsEWKT <#ST_AsEWKT>`__                                       | |image707|   | |image708|   | |image709|   | |image710|   |              | |image711|   | |image712|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_AsGML <#ST_AsGML>`__                                         | |image713|   | |image714|   | |image715|   |              |              | |image716|   | |image717|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_AsGeoJSON <#ST_AsGeoJSON>`__                                 | |image718|   | |image719|   | |image720|   |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_AsHEXEWKB <#ST_AsHEXEWKB>`__                                 | |image721|   |              | |image722|   | |image723|   |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_AsKML <#ST_AsKML>`__                                         | |image724|   | |image725|   | |image726|   |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_AsLatLonText <#ST_AsLatLonText>`__                           | |image727|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_AsSVG <#ST_AsSVG>`__                                         | |image728|   | |image729|   |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_AsText <#ST_AsText>`__                                       | |image730|   | |image731|   |              | |image732|   | |image733|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_AsX3D <#ST_AsX3D>`__                                         | |image734|   |              | |image735|   |              |              | |image736|   | |image737|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Azimuth <#ST_Azimuth>`__                                     | |image738|   | |image739|   |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_BdMPolyFromText <#ST_BdMPolyFromText>`__                     | |image740|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_BdPolyFromText <#ST_BdPolyFromText>`__                       | |image741|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Boundary <#ST_Boundary>`__                                   | |image742|   |              | |image743|   |              | |image744|   |              | |image745|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Box2dFromGeoHash <#ST_Box2dFromGeoHash>`__                   | |image746|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Buffer <#ST_Buffer>`__                                       | |image747|   | |image748|   |              |              | |image749|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_BuildArea <#ST_BuildArea>`__                                 | |image750|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Centroid <#ST_Centroid>`__                                   | |image751|   |              |              |              | |image752|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_ClosestPoint <#ST_ClosestPoint>`__                           | |image753|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Collect <#ST_Collect>`__                                     | |image754|   |              | |image755|   | |image756|   |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_CollectionExtract <#ST_CollectionExtract>`__                 | |image757|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_CollectionHomogenize <#ST_CollectionHomogenize>`__           | |image758|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_ConcaveHull <#ST_ConcaveHull>`__                             | |image759|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Contains <#ST_Contains>`__                                   | |image760|   |              |              |              | |image761|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_ContainsProperly <#ST_ContainsProperly>`__                   | |image762|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_ConvexHull <#ST_ConvexHull>`__                               | |image763|   |              | |image764|   |              | |image765|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_CoordDim <#ST_CoordDim>`__                                   | |image766|   |              | |image767|   | |image768|   | |image769|   | |image770|   | |image771|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_CoveredBy <#ST_CoveredBy>`__                                 | |image772|   | |image773|   |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Covers <#ST_Covers>`__                                       | |image774|   | |image775|   |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Crosses <#ST_Crosses>`__                                     | |image776|   |              |              |              | |image777|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_CurveToLine <#ST_CurveToLine>`__                             | |image778|   |              | |image779|   | |image780|   | |image781|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_DFullyWithin <#ST_DFullyWithin>`__                           | |image782|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_DWithin <#ST_DWithin>`__                                     | |image783|   | |image784|   |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_DelaunayTriangles <#ST_DelaunayTriangles>`__                 | |image785|   |              | |image786|   |              |              |              | |image787|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Difference <#ST_Difference>`__                               | |image788|   |              | |image789|   |              | |image790|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Dimension <#ST_Dimension>`__                                 | |image791|   |              |              |              | |image792|   | |image793|   | |image794|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Disjoint <#ST_Disjoint>`__                                   | |image795|   |              |              |              | |image796|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Distance <#ST_Distance>`__                                   | |image797|   | |image798|   |              | |image799|   | |image800|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Distance\_Sphere <#ST_Distance_Sphere>`__                    | |image801|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Distance\_Spheroid <#ST_Distance_Spheroid>`__                | |image802|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Dump <#ST_Dump>`__                                           | |image803|   |              | |image804|   | |image805|   |              | |image806|   | |image807|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_DumpPoints <#ST_DumpPoints>`__                               | |image808|   |              | |image809|   | |image810|   |              | |image811|   | |image812|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_DumpRings <#ST_DumpRings>`__                                 | |image813|   |              | |image814|   |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_EndPoint <#ST_EndPoint>`__                                   | |image815|   |              | |image816|   |              | |image817|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Envelope <#ST_Envelope>`__                                   | |image818|   |              |              |              | |image819|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Equals <#ST_Equals>`__                                       | |image820|   |              |              |              | |image821|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_EstimatedExtent <#ST_Estimated_Extent>`__                    | |image822|   |              |              | |image823|   |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Expand <#ST_Expand>`__                                       | |image824|   |              |              |              |              | |image825|   | |image826|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Extent <#ST_Extent>`__                                       | |image827|   |              |              |              |              | |image828|   | |image829|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_ExteriorRing <#ST_ExteriorRing>`__                           | |image830|   |              | |image831|   |              | |image832|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Extrude <#ST_Extrude>`__                                     | |image833|   |              | |image834|   |              |              | |image835|   | |image836|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_FlipCoordinates <#ST_FlipCoordinates>`__                     | |image837|   |              | |image838|   | |image839|   |              | |image840|   | |image841|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_ForceLHR <#ST_ForceLHR>`__                                   | |image842|   |              | |image843|   |              |              | |image844|   | |image845|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_ForceRHR <#ST_ForceRHR>`__                                   | |image846|   |              | |image847|   |              |              | |image848|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_ForceSFS <#ST_ForceSFS>`__                                   | |image849|   |              | |image850|   | |image851|   |              | |image852|   | |image853|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Force2D <#ST_Force_2D>`__                                    | |image854|   |              | |image855|   | |image856|   |              | |image857|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Force3D <#ST_Force_3D>`__                                    | |image858|   |              | |image859|   | |image860|   |              | |image861|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Force3DM <#ST_Force_3DM>`__                                  | |image862|   |              |              | |image863|   |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Force3DZ <#ST_Force_3DZ>`__                                  | |image864|   |              | |image865|   | |image866|   |              | |image867|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Force4D <#ST_Force_4D>`__                                    | |image868|   |              | |image869|   | |image870|   |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_ForceCollection <#ST_Force_Collection>`__                    | |image871|   |              | |image872|   | |image873|   |              | |image874|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GMLToSQL <#ST_GMLToSQL>`__                                   | |image875|   |              |              |              | |image876|   | |image877|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GeoHash <#ST_GeoHash>`__                                     | |image878|   |              |              | |image879|   |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GeogFromText <#ST_GeogFromText>`__                           |              | |image880|   |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GeogFromWKB <#ST_GeogFromWKB>`__                             |              | |image881|   |              | |image882|   |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GeographyFromText <#ST_GeographyFromText>`__                 |              | |image883|   |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GeomCollFromText <#ST_GeomCollFromText>`__                   | |image884|   |              |              |              | |image885|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GeomFromEWKB <#ST_GeomFromEWKB>`__                           | |image886|   |              | |image887|   | |image888|   |              | |image889|   | |image890|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GeomFromEWKT <#ST_GeomFromEWKT>`__                           | |image891|   |              | |image892|   | |image893|   |              | |image894|   | |image895|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GeomFromGML <#ST_GeomFromGML>`__                             | |image896|   |              | |image897|   |              |              | |image898|   | |image899|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GeomFromGeoHash <#ST_GeomFromGeoHash>`__                     | |image900|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GeomFromGeoJSON <#ST_GeomFromGeoJSON>`__                     | |image901|   |              | |image902|   |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GeomFromKML <#ST_GeomFromKML>`__                             | |image903|   |              | |image904|   |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GeomFromText <#ST_GeomFromText>`__                           | |image905|   |              |              | |image906|   | |image907|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GeomFromWKB <#ST_GeomFromWKB>`__                             | |image908|   |              |              | |image909|   | |image910|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GeometryFromText <#ST_GeometryFromText>`__                   | |image911|   |              |              |              | |image912|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GeometryN <#ST_GeometryN>`__                                 | |image913|   |              | |image914|   | |image915|   | |image916|   | |image917|   | |image918|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_GeometryType <#ST_GeometryType>`__                           | |image919|   |              | |image920|   |              | |image921|   | |image922|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `\|>> <#ST_Geometry_Above>`__                                     | |image923|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `<<\| <#ST_Geometry_Below>`__                                     | |image924|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `~ <#ST_Geometry_Contain>`__                                      | |image925|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `@ <#ST_Geometry_Contained>`__                                    | |image926|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `= <#ST_Geometry_EQ>`__                                           | |image927|   | |image928|   |              | |image929|   |              | |image930|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `<< <#ST_Geometry_Left>`__                                        | |image931|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `\|&> <#ST_Geometry_Overabove>`__                                 | |image932|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `&<\| <#ST_Geometry_Overbelow>`__                                 | |image933|   |              |              | |image934|   |              | |image935|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `&< <#ST_Geometry_Overleft>`__                                    | |image936|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `&> <#ST_Geometry_Overright>`__                                   | |image937|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `>> <#ST_Geometry_Right>`__                                       | |image938|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `~= <#ST_Geometry_Same>`__                                        | |image939|   |              |              |              |              | |image940|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_HasArc <#ST_HasArc>`__                                       | |image941|   |              | |image942|   | |image943|   |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_HausdorffDistance <#ST_HausdorffDistance>`__                 | |image944|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_InteriorRingN <#ST_InteriorRingN>`__                         | |image945|   |              | |image946|   |              | |image947|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_InterpolatePoint <#ST_InterpolatePoint>`__                   | |image948|   |              | |image949|   |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Intersection <#ST_Intersection>`__                           | |image950|   | |image951|   |              |              | |image952|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Intersects <#ST_Intersects>`__                               | |image953|   | |image954|   |              |              | |image955|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_IsClosed <#ST_IsClosed>`__                                   | |image956|   |              | |image957|   | |image958|   | |image959|   | |image960|   |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_IsCollection <#ST_IsCollection>`__                           | |image961|   |              | |image962|   | |image963|   |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_IsEmpty <#ST_IsEmpty>`__                                     | |image964|   |              |              | |image965|   | |image966|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_IsPlanar <#ST_IsPlanar>`__                                   | |image967|   |              | |image968|   |              |              | |image969|   | |image970|   |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_IsRing <#ST_IsRing>`__                                       | |image971|   |              |              |              | |image972|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_IsSimple <#ST_IsSimple>`__                                   | |image973|   |              | |image974|   |              | |image975|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_IsValid <#ST_IsValid>`__                                     | |image976|   |              |              |              | |image977|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_IsValidDetail <#ST_IsValidDetail>`__                         | |image978|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_IsValidReason <#ST_IsValidReason>`__                         | |image979|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Length <#ST_Length>`__                                       | |image980|   | |image981|   |              |              | |image982|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Length2D <#ST_Length2D>`__                                   | |image983|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Length2D\_Spheroid <#ST_Length2D_Spheroid>`__                | |image984|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Length\_Spheroid <#ST_Length_Spheroid>`__                    | |image985|   |              | |image986|   |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_LineCrossingDirection <#ST_LineCrossingDirection>`__         | |image987|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_LineFromMultiPoint <#ST_LineFromMultiPoint>`__               | |image988|   |              | |image989|   |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_LineFromText <#ST_LineFromText>`__                           | |image990|   |              |              |              | |image991|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_LineFromWKB <#ST_LineFromWKB>`__                             | |image992|   |              |              |              | |image993|   |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_LineMerge <#ST_LineMerge>`__                                 | |image994|   |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_LineToCurve <#ST_LineToCurve>`__                             | |image995|   |              | |image996|   | |image997|   |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_LineInterpolatePoint <#ST_Line_Interpolate_Point>`__         | |image998|   |              | |image999|   |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_LineLocatePoint <#ST_Line_Locate_Point>`__                   | |image1000|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_LineSubstring <#ST_Line_Substring>`__                        | |image1001|  |              | |image1002|  |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_LinestringFromWKB <#ST_LinestringFromWKB>`__                 | |image1003|  |              |              |              | |image1004|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_LocateAlong <#ST_LocateAlong>`__                             | |image1005|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_LocateBetween <#ST_LocateBetween>`__                         | |image1006|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_LocateBetweenElevations <#ST_LocateBetweenElevations>`__     | |image1007|  |              | |image1008|  |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_LongestLine <#ST_LongestLine>`__                             | |image1009|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_M <#ST_M>`__                                                 | |image1010|  |              | |image1011|  |              | |image1012|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_MLineFromText <#ST_MLineFromText>`__                         | |image1013|  |              |              |              | |image1014|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_MPointFromText <#ST_MPointFromText>`__                       | |image1015|  |              |              |              | |image1016|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_MPolyFromText <#ST_MPolyFromText>`__                         | |image1017|  |              |              |              | |image1018|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_MakeBox2D <#ST_MakeBox2D>`__                                 | |image1019|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_MakeEnvelope <#ST_MakeEnvelope>`__                           | |image1020|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_MakeLine <#ST_MakeLine>`__                                   | |image1021|  |              | |image1022|  |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_MakePoint <#ST_MakePoint>`__                                 | |image1023|  |              | |image1024|  |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_MakePointM <#ST_MakePointM>`__                               | |image1025|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_MakePolygon <#ST_MakePolygon>`__                             | |image1026|  |              | |image1027|  |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_MakeValid <#ST_MakeValid>`__                                 | |image1028|  |              | |image1029|  |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_MaxDistance <#ST_MaxDistance>`__                             | |image1030|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_MemUnion <#ST_MemUnion>`__                                   | |image1031|  |              | |image1032|  |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Mem\_Size <#ST_Mem_Size>`__                                  | |image1033|  |              | |image1034|  | |image1035|  |              | |image1036|  | |image1037|  |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_MinimumBoundingCircle <#ST_MinimumBoundingCircle>`__         | |image1038|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_MinkowskiSum <#ST_MinkowskiSum>`__                           | |image1039|  |              | |image1040|  |              |              | |image1041|  | |image1042|  |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Multi <#ST_Multi>`__                                         | |image1043|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_NDims <#ST_NDims>`__                                         | |image1044|  |              | |image1045|  |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_NPoints <#ST_NPoints>`__                                     | |image1046|  |              | |image1047|  | |image1048|  |              | |image1049|  |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_NRings <#ST_NRings>`__                                       | |image1050|  |              | |image1051|  | |image1052|  |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Node <#ST_Node>`__                                           | |image1053|  |              | |image1054|  |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_NumGeometries <#ST_NumGeometries>`__                         | |image1055|  |              | |image1056|  |              | |image1057|  | |image1058|  | |image1059|  |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_NumInteriorRing <#ST_NumInteriorRing>`__                     | |image1060|  |              |              |              | |image1061|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_NumInteriorRings <#ST_NumInteriorRings>`__                   | |image1062|  |              |              |              | |image1063|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_NumPatches <#ST_NumPatches>`__                               | |image1064|  |              | |image1065|  |              | |image1066|  | |image1067|  |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_NumPoints <#ST_NumPoints>`__                                 | |image1068|  |              |              |              | |image1069|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_OffsetCurve <#ST_OffsetCurve>`__                             | |image1070|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_OrderingEquals <#ST_OrderingEquals>`__                       | |image1071|  |              |              |              | |image1072|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Orientation <#ST_Orientation>`__                             | |image1073|  |              | |image1074|  |              |              | |image1075|  | |image1076|  |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Overlaps <#ST_Overlaps>`__                                   | |image1077|  |              |              |              | |image1078|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_PatchN <#ST_PatchN>`__                                       | |image1079|  |              | |image1080|  |              | |image1081|  | |image1082|  |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Perimeter <#ST_Perimeter>`__                                 | |image1083|  | |image1084|  |              |              | |image1085|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Perimeter2D <#ST_Perimeter2D>`__                             | |image1086|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Point <#ST_Point>`__                                         | |image1087|  |              |              |              | |image1088|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_PointFromGeoHash <#ST_PointFromGeoHash>`__                   |              |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_PointFromText <#ST_PointFromText>`__                         | |image1089|  |              |              |              | |image1090|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_PointFromWKB <#ST_PointFromWKB>`__                           | |image1091|  |              | |image1092|  | |image1093|  | |image1094|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_PointN <#ST_PointN>`__                                       | |image1095|  |              | |image1096|  | |image1097|  | |image1098|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_PointOnSurface <#ST_PointOnSurface>`__                       | |image1099|  |              | |image1100|  |              | |image1101|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Point\_Inside\_Circle <#ST_Point_Inside_Circle>`__           | |image1102|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Polygon <#ST_Polygon>`__                                     | |image1103|  |              | |image1104|  |              | |image1105|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_PolygonFromText <#ST_PolygonFromText>`__                     | |image1106|  |              |              |              | |image1107|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Polygonize <#ST_Polygonize>`__                               | |image1108|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Project <#ST_Project>`__                                     |              | |image1109|  |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Relate <#ST_Relate>`__                                       | |image1110|  |              |              |              | |image1111|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_RelateMatch <#ST_RelateMatch>`__                             |              |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_RemovePoint <#ST_RemovePoint>`__                             | |image1112|  |              | |image1113|  |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_RemoveRepeatedPoints <#ST_RemoveRepeatedPoints>`__           | |image1114|  |              | |image1115|  |              |              | |image1116|  |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Reverse <#ST_Reverse>`__                                     | |image1117|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Rotate <#ST_Rotate>`__                                       | |image1118|  |              | |image1119|  | |image1120|  |              | |image1121|  | |image1122|  |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_RotateX <#ST_RotateX>`__                                     | |image1123|  |              | |image1124|  |              |              | |image1125|  | |image1126|  |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_RotateY <#ST_RotateY>`__                                     | |image1127|  |              | |image1128|  |              |              | |image1129|  | |image1130|  |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_RotateZ <#ST_RotateZ>`__                                     | |image1131|  |              | |image1132|  | |image1133|  |              | |image1134|  | |image1135|  |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_SRID <#ST_SRID>`__                                           | |image1136|  |              |              | |image1137|  | |image1138|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Scale <#ST_Scale>`__                                         | |image1139|  |              | |image1140|  | |image1141|  |              | |image1142|  | |image1143|  |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Segmentize <#ST_Segmentize>`__                               | |image1144|  | |image1145|  |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_SetPoint <#ST_SetPoint>`__                                   | |image1146|  |              | |image1147|  |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_SetSRID <#ST_SetSRID>`__                                     | |image1148|  |              |              | |image1149|  |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_SharedPaths <#ST_SharedPaths>`__                             | |image1150|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Shift\_Longitude <#ST_Shift_Longitude>`__                    | |image1151|  |              | |image1152|  |              |              | |image1153|  | |image1154|  |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_ShortestLine <#ST_ShortestLine>`__                           | |image1155|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Simplify <#ST_Simplify>`__                                   | |image1156|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_SimplifyPreserveTopology <#ST_SimplifyPreserveTopology>`__   | |image1157|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Snap <#ST_Snap>`__                                           | |image1158|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_SnapToGrid <#ST_SnapToGrid>`__                               | |image1159|  |              | |image1160|  |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Split <#ST_Split>`__                                         | |image1161|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_StartPoint <#ST_StartPoint>`__                               | |image1162|  |              | |image1163|  |              | |image1164|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_StraightSkeleton <#ST_StraightSkeleton>`__                   | |image1165|  |              | |image1166|  |              |              | |image1167|  | |image1168|  |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Summary <#ST_Summary>`__                                     | |image1169|  | |image1170|  |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_SymDifference <#ST_SymDifference>`__                         | |image1171|  |              | |image1172|  |              | |image1173|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Tesselate <#ST_Tesselate>`__                                 | |image1174|  |              | |image1175|  |              |              | |image1176|  | |image1177|  |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Touches <#ST_Touches>`__                                     | |image1178|  |              |              |              | |image1179|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_TransScale <#ST_TransScale>`__                               | |image1180|  |              | |image1181|  | |image1182|  |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Transform <#ST_Transform>`__                                 | |image1183|  |              |              | |image1184|  | |image1185|  | |image1186|  |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Translate <#ST_Translate>`__                                 | |image1187|  |              | |image1188|  | |image1189|  |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_UnaryUnion <#ST_UnaryUnion>`__                               | |image1190|  |              | |image1191|  |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Union <#ST_Union>`__                                         | |image1192|  |              |              |              | |image1193|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_WKBToSQL <#ST_WKBToSQL>`__                                   | |image1194|  |              |              |              | |image1195|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_WKTToSQL <#ST_WKTToSQL>`__                                   | |image1196|  |              |              |              | |image1197|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Within <#ST_Within>`__                                       | |image1198|  |              |              |              | |image1199|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_X <#ST_X>`__                                                 | |image1200|  |              | |image1201|  |              | |image1202|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_XMax <#ST_XMax>`__                                           | |image1203|  |              | |image1204|  | |image1205|  |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_XMin <#ST_XMin>`__                                           | |image1206|  |              | |image1207|  | |image1208|  |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Y <#ST_Y>`__                                                 | |image1209|  |              | |image1210|  |              | |image1211|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_YMax <#ST_YMax>`__                                           | |image1212|  |              | |image1213|  | |image1214|  |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_YMin <#ST_YMin>`__                                           | |image1215|  |              | |image1216|  | |image1217|  |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Z <#ST_Z>`__                                                 | |image1218|  |              | |image1219|  |              | |image1220|  |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_ZMax <#ST_ZMax>`__                                           | |image1221|  |              | |image1222|  | |image1223|  |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_ZMin <#ST_ZMin>`__                                           | |image1224|  |              | |image1225|  | |image1226|  |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `ST\_Zmflag <#ST_Zmflag>`__                                       | |image1227|  |              | |image1228|  | |image1229|  |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `<#> <#geometry_distance_box>`__                                  | |image1230|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `<-> <#geometry_distance_centroid>`__                             | |image1231|  |              |              |              |              |              |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `&& <#geometry_overlaps>`__                                       | |image1232|  | |image1233|  |              | |image1234|  |              | |image1235|  |              |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+
| `&&& <#geometry_overlaps_nd>`__                                   | |image1236|  |              | |image1237|  | |image1238|  |              | |image1239|  | |image1240|  |
+-------------------------------------------------------------------+--------------+--------------+--------------+--------------+--------------+--------------+--------------+

New, Enhanced or changed PostGIS Functions
==========================================

PostGIS Functions new or enhanced in 2.1
----------------------------------------

The functions given below are PostGIS functions that were added or
enhanced.

    **Note**

    More Topology performance Improvements. Please refer to ? for more
    details.

    **Note**

    Bug fixes (particularly with handling of out-of-band rasters), many
    new functions (often shortening code you have to write to accomplish
    a common task) and massive speed improvements to raster
    functionality. Refer to ? for more details.

    **Note**

    Tiger Geocoder upgraded to work with TIGER 2012 census data in 2.1.0
    and TIGER 2013 in 2.1.1. ``geocode_settings`` added for debugging
    and tweaking rating preferences, loader made less greedy, now only
    downloads tables to be loaded. Please refer to ? for more details.

    **Note**

    Raster bands can only reference the first 256 bands of out-db
    rasters.

Functions new in PostGIS 2.1

-  `AsTopoJSON <#AsTopoJSON>`__ - Availability: 2.1.0 Returns the
   TopoJSON representation of a topogeometry.

-  `Drop\_Nation\_Tables\_Generate\_Script <#Drop_Nation_Tables_Generate_Script>`__
   - Availability: 2.1.0 Generates a script that drops all tables in the
   specified schema that start with county\_all, state\_all or stae code
   followed by county or state.

-  `Get\_Geocode\_Setting <#Get_Geocode_Setting>`__ - Availability:
   2.1.0 Returns value of specific setting stored in
   tiger.geocode\_settings table.

-  `Loader\_Generate\_Nation\_Script <#Loader_Generate_Nation_Script>`__
   - Availability: 2.1.0 Generates a shell script for the specified
   platform that loads in the county and state lookup tables.

-  `Pagc\_Normalize\_Address <#Pagc_Normalize_Address>`__ -
   Availability: 2.1.0 Given a textual street address, returns a
   composite norm\_addy type that has road suffix, prefix and type
   standardized, street, streetname etc. broken into separate fields.
   This function will work with just the lookup data packaged with the
   tiger\_geocoder (no need for tiger census data). Requires
   address\_standardizer extension.

-  `ST\_Box2dFromGeoHash <#ST_Box2dFromGeoHash>`__ - Availability: 2.1.0
   Return a BOX2D from a GeoHash string.

-  `ST\_ColorMap <#RT_ST_ColorMap>`__ - Availability: 2.1.0 Creates a
   new raster of up to four 8BUI bands (grayscale, RGB, RGBA) from the
   source raster and a specified band. Band 1 is assumed if not
   specified.

-  `ST\_Contains <#RT_ST_Contains>`__ - Availability: 2.1.0 Return true
   if no points of raster rastB lie in the exterior of raster rastA and
   at least one point of the interior of rastB lies in the interior of
   rastA.

-  `ST\_ContainsProperly <#RT_ST_ContainsProperly>`__ - Availability:
   2.1.0 Return true if rastB intersects the interior of rastA but not
   the boundary or exterior of rastA.

-  `ST\_CoveredBy <#RT_ST_CoveredBy>`__ - Availability: 2.1.0 Return
   true if no points of raster rastA lie outside raster rastB.

-  `ST\_Covers <#RT_ST_Covers>`__ - Availability: 2.1.0 Return true if
   no points of raster rastB lie outside raster rastA.

-  `ST\_DFullyWithin <#RT_ST_DFullyWithin>`__ - Availability: 2.1.0
   Return true if rasters rastA and rastB are fully within the specified
   distance of each other.

-  `ST\_DWithin <#RT_ST_DWithin>`__ - Availability: 2.1.0 Return true if
   rasters rastA and rastB are within the specified distance of each
   other.

-  `ST\_DelaunayTriangles <#ST_DelaunayTriangles>`__ - Availability:
   2.1.0 - requires GEOS >= 3.4.0. Return a Delaunay triangulation
   around the given input points.

-  `ST\_Disjoint <#RT_ST_Disjoint>`__ - Availability: 2.1.0 Return true
   if raster rastA does not spatially intersect rastB.

-  `ST\_DumpValues <#RT_ST_DumpValues>`__ - Availability: 2.1.0 Get the
   values of the specified band as a 2-dimension array.

-  `ST\_FromGDALRaster <#RT_ST_FromGDALRaster>`__ - Availability: 2.1.0
   Returns a raster from a supported GDAL raster file.

-  `ST\_GeomFromGeoHash <#ST_GeomFromGeoHash>`__ - Availability: 2.1.0
   Return a geometry from a GeoHash string.

-  `ST\_InvDistWeight4ma <#RT_ST_InvDistWeight4ma>`__ - Availability:
   2.1.0 Raster processing function that interpolates a pixel's value
   from the pixel's neighborhood.

-  `ST\_MapAlgebra <#RT_ST_MapAlgebra>`__ - Availability: 2.1.0 Callback
   function version - Returns a one-band raster given one or more input
   rasters, band indexes and one user-specified callback function.

-  `ST\_MapAlgebra <#RT_ST_MapAlgebra_expr>`__ - Availability: 2.1.0
   Expression version - Returns a one-band raster given one or two input
   rasters, band indexes and one or more user-specified SQL expressions.

-  `ST\_MinConvexHull <#RT_ST_MinConvexHull>`__ - Availability: 2.1.0
   Return the convex hull geometry of the raster excluding NODATA
   pixels.

-  `ST\_MinDist4ma <#RT_ST_MinDist4ma>`__ - Availability: 2.1.0 Raster
   processing function that returns the minimum distance (in number of
   pixels) between the pixel of interest and a neighboring pixel with
   value.

-  `ST\_NearestValue <#RT_ST_NearestValue>`__ - Availability: 2.1.0
   Returns the nearest non-NODATA value of a given band's pixel
   specified by a columnx and rowy or a geometric point expressed in the
   same spatial reference coordinate system as the raster.

-  `ST\_Neighborhood <#RT_ST_Neighborhood>`__ - Availability: 2.1.0
   Returns a 2-D double precision array of the non-NODATA values around
   a given band's pixel specified by either a columnX and rowY or a
   geometric point expressed in the same spatial reference coordinate
   system as the raster.

-  `ST\_NotSameAlignmentReason <#RT_ST_NotSameAlignmentReason>`__ -
   Availability: 2.1.0 Returns text stating if rasters are aligned and
   if not aligned, a reason why.

-  `ST\_Overlaps <#RT_ST_Overlaps>`__ - Availability: 2.1.0 Return true
   if raster rastA and rastB intersect but one does not completely
   contain the other.

-  `ST\_PixelAsCentroid <#RT_ST_PixelAsCentroid>`__ - Availability:
   2.1.0 Returns the centroid (point geometry) of the area represented
   by a pixel.

-  `ST\_PixelAsCentroids <#RT_ST_PixelAsCentroids>`__ - Availability:
   2.1.0 Returns the centroid (point geometry) for each pixel of a
   raster band along with the value, the X and the Y raster coordinates
   of each pixel. The point geometry is the centroid of the area
   represented by a pixel.

-  `ST\_PixelAsPoint <#RT_ST_PixelAsPoint>`__ - Availability: 2.1.0
   Returns a point geometry of the pixel's upper-left corner.

-  `ST\_PixelAsPoints <#RT_ST_PixelAsPoints>`__ - Availability: 2.1.0
   Returns a point geometry for each pixel of a raster band along with
   the value, the X and the Y raster coordinates of each pixel. The
   coordinates of the point geometry are of the pixel's upper-left
   corner.

-  `ST\_PixelOfValue <#RT_ST_PixelOfValue>`__ - Availability: 2.1.0 Get
   the columnx, rowy coordinates of the pixel whose value equals the
   search value.

-  `ST\_PointFromGeoHash <#ST_PointFromGeoHash>`__ - Availability: 2.1.0
   Return a point from a GeoHash string.

-  `ST\_RasterToWorldCoord <#RT_ST_RasterToWorldCoord>`__ -
   Availability: 2.1.0 Returns the raster's upper left corner as
   geometric X and Y (longitude and latitude) given a column and row.
   Column and row starts at 1.

-  `ST\_Resize <#RT_ST_Resize>`__ - Availability: 2.1.0 Requires GDAL
   1.6.1+ Resize a raster to a new width/height

-  `ST\_Roughness <#RT_ST_Roughness>`__ - Availability: 2.1.0 Returns a
   raster with the calculated "roughness" of a DEM.

-  `ST\_SetValues <#RT_ST_SetValues>`__ - Availability: 2.1.0 Returns
   modified raster resulting from setting the values of a given band.

-  `ST\_Simplify <#TP_ST_Simplify>`__ - Availability: 2.1.0 Returns a
   "simplified" geometry version of the given TopoGeometry using the
   Douglas-Peucker algorithm.

-  `ST\_Summary <#RT_ST_Summary>`__ - Availability: 2.1.0 Returns a text
   summary of the contents of the raster.

-  `ST\_TPI <#RT_ST_TPI>`__ - Availability: 2.1.0 Returns a raster with
   the calculated Topographic Position Index.

-  `ST\_TRI <#RT_ST_TRI>`__ - Availability: 2.1.0 Returns a raster with
   the calculated Terrain Ruggedness Index.

-  `ST\_Tile <#RT_ST_Tile>`__ - Availability: 2.1.0 Returns a set of
   rasters resulting from the split of the input raster based upon the
   desired dimensions of the output rasters.

-  `ST\_Touches <#RT_ST_Touches>`__ - Availability: 2.1.0 Return true if
   raster rastA and rastB have at least one point in common but their
   interiors do not intersect.

-  `ST\_Union <#RT_ST_Union>`__ - Availability: 2.1.0 ST\_Union(rast,
   unionarg) variant was introduced. Returns the union of a set of
   raster tiles into a single raster composed of 1 or more bands.

-  `ST\_Within <#RT_ST_Within>`__ - Availability: 2.1.0 Return true if
   no points of raster rastA lie in the exterior of raster rastB and at
   least one point of the interior of rastA lies in the interior of
   rastB.

-  `ST\_WorldToRasterCoord <#RT_ST_WorldToRasterCoord>`__ -
   Availability: 2.1.0 Returns the upper left corner as column and row
   given geometric X and Y (longitude and latitude) or a point geometry
   expressed in the spatial reference coordinate system of the raster.

-  `Set\_Geocode\_Setting <#Set_Geocode_Setting>`__ - Availability:
   2.1.0 Sets a setting that affects behavior of geocoder functions.

-  `UpdateRasterSRID <#RT_UpdateRasterSRID>`__ - Availability: 2.1.0
   Change the SRID of all rasters in the user-specified column and
   table.

-  `clearTopoGeom <#clearTopoGeom>`__ - Availability: 2.1 Clears the
   content of a topo geometry

The functions given below are PostGIS functions that are enhanced in
PostGIS 2.1.

-  `ST\_AddBand <#RT_ST_AddBand>`__ - Enhanced: 2.1.0 support for
   addbandarg added.

-  `ST\_AddBand <#RT_ST_AddBand>`__ - Enhanced: 2.1.0 support for new
   out-db bands added.

-  `ST\_AsBinary <#RT_ST_AsBinary>`__ - Enhanced: 2.1.0 Addition of
   outasin

-  `ST\_Aspect <#RT_ST_Aspect>`__ - Enhanced: 2.1.0 Uses
   ST\_MapAlgebra() and added optional interpolate\_nodata function
   parameter

-  `ST\_Clip <#RT_ST_Clip>`__ - Enhanced: 2.1.0 Rewritten in C

-  `ST\_Distinct4ma <#RT_ST_Distinct4ma>`__ - Enhanced: 2.1.0 Addition
   of Variant 2

-  `ST\_HillShade <#RT_ST_HillShade>`__ - Enhanced: 2.1.0 Uses
   ST\_MapAlgebra() and added optional interpolate\_nodata function
   parameter

-  `ST\_Max4ma <#RT_ST_Max4ma>`__ - Enhanced: 2.1.0 Addition of Variant
   2

-  `ST\_Mean4ma <#RT_ST_Mean4ma>`__ - Enhanced: 2.1.0 Addition of
   Variant 2

-  `ST\_Min4ma <#RT_ST_Min4ma>`__ - Enhanced: 2.1.0 Addition of Variant
   2

-  `ST\_PixelAsPolygons <#RT_ST_PixelAsPolygons>`__ - Enhanced: 2.1.0
   exclude\_nodata\_value optional argument was added.

-  `ST\_Polygon <#RT_ST_Polygon>`__ - Enhanced: 2.1.0 Improved Speed
   (fully C-Based) and the returning multipolygon is ensured to be
   valid.

-  `ST\_Range4ma <#RT_ST_Range4ma>`__ - Enhanced: 2.1.0 Addition of
   Variant 2

-  `ST\_SameAlignment <#RT_ST_SameAlignment>`__ - Enhanced: 2.1.0
   addition of Aggegrate variant

-  `ST\_SetGeoReference <#RT_ST_SetGeoReference>`__ - Enhanced: 2.1.0
   Addition of ST\_SetGeoReference(raster, double precision, ...)
   variant

-  `ST\_SetValue <#RT_ST_SetValue>`__ - Enhanced: 2.1.0 Geometry variant
   of ST\_SetValue() now supports any geometry type, not just point. The
   geometry variant is a wrapper around the geomval[] variant of
   ST\_SetValues()

-  `ST\_Slope <#RT_ST_Slope>`__ - Enhanced: 2.1.0 Uses ST\_MapAlgebra()
   and added optional units, scale, interpolate\_nodata function
   parameters

-  `ST\_StdDev4ma <#RT_ST_StdDev4ma>`__ - Enhanced: 2.1.0 Addition of
   Variant 2

-  `ST\_Sum4ma <#RT_ST_Sum4ma>`__ - Enhanced: 2.1.0 Addition of Variant
   2

-  `ST\_Transform <#RT_ST_Transform>`__ - Enhanced: 2.1.0 Addition of
   ST\_Transform(rast, alignto) variant

-  `ST\_Union <#RT_ST_Union>`__ - Enhanced: 2.1.0 Improved Speed (fully
   C-Based).

-  `ST\_Union <#RT_ST_Union>`__ - Enhanced: 2.1.0 ST\_Union(rast)
   (variant 1) unions all bands of all input rasters. Prior versions of
   PostGIS assumed the first band.

-  `ST\_Union <#RT_ST_Union>`__ - Enhanced: 2.1.0 ST\_Union(rast,
   uniontype) (variant 4) unions all bands of all input rasters.

-  `ST\_AsGML <#ST_AsGML>`__ - Enhanced: 2.1.0 id support was
   introduced, for GML 3.

-  `ST\_Boundary <#ST_Boundary>`__ - Enhanced: 2.1.0 support for
   Triangle was introduced

-  `ST\_DWithin <#ST_DWithin>`__ - Enhanced: 2.1.0 improved speed for
   geography. See Making Geography faster for details.

-  `ST\_DWithin <#ST_DWithin>`__ - Enhanced: 2.1.0 support for curved
   geometries was introduced.

-  `ST\_Distance <#ST_Distance>`__ - Enhanced: 2.1.0 improved speed for
   geography. See Making Geography faster for details.

-  `ST\_Distance <#ST_Distance>`__ - Enhanced: 2.1.0 - support for
   curved geometries was introduced.

-  `ST\_DumpPoints <#ST_DumpPoints>`__ - Enhanced: 2.1.0 Faster speed.
   Reimplemented as native-C.

-  `ST\_MakeValid <#ST_MakeValid>`__ - Enhanced: 2.1.0 added support for
   GEOMETRYCOLLECTION and MULTIPOINT.

-  `ST\_Segmentize <#ST_Segmentize>`__ - Enhanced: 2.1.0 support for
   geography was introduced.

-  `ST\_Summary <#ST_Summary>`__ - Enhanced: 2.1.0 S flag to denote if
   has a known spatial reference system

-  `toTopoGeom <#toTopoGeom>`__ - Enhanced: 2.1.0 adds the version
   taking an existing TopoGeometry.

PostGIS functions breaking changes in 2.1
-----------------------------------------

The functions given below are PostGIS functions that have possibly
breaking changes in PostGIS 2.1. If you use any of these, you may need
to check your existing code.

-  `ST\_Aspect <#RT_ST_Aspect>`__ - Changed: 2.1.0 In prior versions,
   return values were in radians. Now, return values default to degrees

-  `ST\_HillShade <#RT_ST_HillShade>`__ - Changed: 2.1.0 In prior
   versions, azimuth and altitude were expressed in radians. Now,
   azimuth and altitude are expressed in degrees

-  `ST\_Intersects <#RT_ST_Intersects>`__ - Changed: 2.1.0 The behavior
   of the ST\_Intersects(raster, geometry) variants changed to match
   that of ST\_Intersects(geometry, raster).

-  `ST\_PixelAsCentroids <#RT_ST_PixelAsCentroids>`__ - Changed: 2.1.1
   Changed behavior of exclude\_nodata\_value.

-  `ST\_PixelAsPoints <#RT_ST_PixelAsPoints>`__ - Changed: 2.1.1 Changed
   behavior of exclude\_nodata\_value.

-  `ST\_PixelAsPolygons <#RT_ST_PixelAsPolygons>`__ - Changed: 2.1.1
   Changed behavior of exclude\_nodata\_value.

-  `ST\_Polygon <#RT_ST_Polygon>`__ - Changed: 2.1.0 In prior versions
   would sometimes return a polygon, changed to always return
   multipolygon.

-  `ST\_RasterToWorldCoordX <#RT_ST_RasterToWorldCoordX>`__ - Changed:
   2.1.0 In prior versions, this was called ST\_Raster2WorldCoordX

-  `ST\_RasterToWorldCoordY <#RT_ST_RasterToWorldCoordY>`__ - Changed:
   2.1.0 In prior versions, this was called ST\_Raster2WorldCoordY

-  `ST\_Resample <#RT_ST_Resample>`__ - Changed: 2.1.0 Parameter srid
   removed. Variants with a reference raster no longer applies the
   reference raster's SRID. Use ST\_Transform() to reproject raster.
   Works on rasters with no SRID.

-  `ST\_Rescale <#RT_ST_Rescale>`__ - Changed: 2.1.0 Works on rasters
   with no SRID

-  `ST\_Reskew <#RT_ST_Reskew>`__ - Changed: 2.1.0 Works on rasters with
   no SRID

-  `ST\_Slope <#RT_ST_Slope>`__ - Changed: 2.1.0 In prior versions,
   return values were in radians. Now, return values default to degrees

-  `ST\_SnapToGrid <#RT_ST_SnapToGrid>`__ - Changed: 2.1.0 Works on
   rasters with no SRID

-  `ST\_WorldToRasterCoordX <#RT_ST_WorldToRasterCoordX>`__ - Changed:
   2.1.0 In prior versions, this was called ST\_World2RasterCoordX

-  `ST\_WorldToRasterCoordY <#RT_ST_WorldToRasterCoordY>`__ - Changed:
   2.1.0 In prior versions, this was called ST\_World2RasterCoordY

-  `ST\_EstimatedExtent <#ST_Estimated_Extent>`__ - Changed: 2.1.0. Up
   to 2.0.x this was called ST\_Estimated\_Extent.

-  `ST\_Force2D <#ST_Force_2D>`__ - Changed: 2.1.0. Up to 2.0.x this was
   called ST\_Force\_2D.

-  `ST\_Force3D <#ST_Force_3D>`__ - Changed: 2.1.0. Up to 2.0.x this was
   called ST\_Force\_3D.

-  `ST\_Force3DM <#ST_Force_3DM>`__ - Changed: 2.1.0. Up to 2.0.x this
   was called ST\_Force\_3DM.

-  `ST\_Force3DZ <#ST_Force_3DZ>`__ - Changed: 2.1.0. Up to 2.0.x this
   was called ST\_Force\_3DZ.

-  `ST\_Force4D <#ST_Force_4D>`__ - Changed: 2.1.0. Up to 2.0.x this was
   called ST\_Force\_4D.

-  `ST\_ForceCollection <#ST_Force_Collection>`__ - Changed: 2.1.0. Up
   to 2.0.x this was called ST\_Force\_Collection.

-  `ST\_LineInterpolatePoint <#ST_Line_Interpolate_Point>`__ - Changed:
   2.1.0. Up to 2.0.x this was called ST\_LineInterpolatePoint.

-  `ST\_LineLocatePoint <#ST_Line_Locate_Point>`__ - Changed: 2.1.0. Up
   to 2.0.x this was called ST\_Line\_Locate\_Point.

-  `ST\_LineSubstring <#ST_Line_Substring>`__ - Changed: 2.1.0. Up to
   2.0.x this was called ST\_LineSubstring.

-  `ST\_Segmentize <#ST_Segmentize>`__ - Changed: 2.1.0 As a result of
   the introduction of geography support: The construct SELECT
   ST\_Segmentize('LINESTRING(1 2, 3 4)',0.5); will result in ambiguous
   function error. You need to have properly typed object e.g. a
   geometry/geography column, use ST\_GeomFromText, ST\_GeogFromText or
   SELECT ST\_Segmentize('LINESTRING(1 2, 3 4)'::geometry,0.5);

PostGIS Functions new, behavior changed, or enhanced in 2.0
-----------------------------------------------------------

The functions given below are PostGIS functions that were added,
enhanced, or have ? breaking changes in 2.0 releases.

New geometry types: TIN and Polyhedral surfaces was introduced in 2.0

    **Note**

    Greatly improved support for Topology. Please refer to ? for more
    details.

    **Note**

    In PostGIS 2.0, raster type and raster functionality has been
    integrated. There are way too many new raster functions to list here
    and all are new so please refer to ? for more details of the raster
    functions available. Earlier pre-2.0 versions had
    raster\_columns/raster\_overviews as real tables. These were changed
    to views before release. Functions such as ``ST_AddRasterColumn``
    were removed and replaced with ?, ? as a result some apps that
    created raster tables may need changing.

    **Note**

    Tiger Geocoder upgraded to work with TIGER 2010 census data and now
    included in the core PostGIS documentation. A reverse geocoder
    function was also added. Please refer to ? for more details.

-  `&&& <#geometry_overlaps_nd>`__ - Availability: 2.0.0 Returns TRUE if
   A's 3D bounding box intersects B's 3D bounding box.

-  `<#> <#geometry_distance_box>`__ - Availability: 2.0.0 only available
   for PostgreSQL 9.1+ Returns the distance between bounding box of 2
   geometries. For point / point checks it's almost the same as distance
   (though may be different since the bounding box is at floating point
   accuracy and geometries are double precision). Useful for doing
   distance ordering and nearest neighbor limits using KNN gist
   functionality.

-  `<-> <#geometry_distance_centroid>`__ - Availability: 2.0.0 only
   available for PostgreSQL 9.1+ Returns the distance between two
   points. For point / point checks it uses floating point accuracy (as
   opposed to the double precision accuracy of the underlying point
   geometry). For other geometry types the distance between the floating
   point bounding box centroids is returned. Useful for doing distance
   ordering and nearest neighbor limits using KNN gist functionality.

-  `AddEdge <#AddEdge>`__ - Availability: 2.0.0 requires GEOS >= 3.3.0.
   Adds a linestring edge to the edge table and associated start and end
   points to the point nodes table of the specified topology schema
   using the specified linestring geometry and returns the edgeid of the
   new (or existing) edge.

-  `AddFace <#AddFace>`__ - Availability: 2.0.0 Registers a face
   primitive to a topology and get it's identifier.

-  `AddNode <#AddNode>`__ - Availability: 2.0.0 Adds a point node to the
   node table in the specified topology schema and returns the nodeid of
   new node. If point already exists as node, the existing nodeid is
   returned.

-  `AddRasterConstraints <#RT_AddRasterConstraints>`__ - Availability:
   2.0.0 Adds raster constraints to a loaded raster table for a specific
   column that constrains spatial ref, scaling, blocksize, alignment,
   bands, band type and a flag to denote if raster column is regularly
   blocked. The table must be loaded with data for the constraints to be
   inferred. Returns true of the constraint setting was accomplished and
   if issues a notice.

-  `AsGML <#AsGML>`__ - Availability: 2.0.0 Returns the GML
   representation of a topogeometry.

-  `CopyTopology <#CopyTopology>`__ - Availability: 2.0.0 Makes a copy
   of a topology structure (nodes, edges, faces, layers and
   TopoGeometries).

-  `DropRasterConstraints <#RT_DropRasterConstraints>`__ - Availability:
   2.0.0 Drops PostGIS raster constraints that refer to a raster table
   column. Useful if you need to reload data or update your raster
   column data.

-  `Drop\_Indexes\_Generate\_Script <#Drop_Indexes_Generate_Script>`__ -
   Availability: 2.0.0 Generates a script that drops all non-primary key
   and non-unique indexes on tiger schema and user specified schema.
   Defaults schema to tiger\_data if no schema is specified.

-  `Drop\_State\_Tables\_Generate\_Script <#Drop_State_Tables_Generate_Script>`__
   - Availability: 2.0.0 Generates a script that drops all tables in the
   specified schema that are prefixed with the state abbreviation.
   Defaults schema to tiger\_data if no schema is specified.

-  `Geocode\_Intersection <#Geocode_Intersection>`__ - Availability:
   2.0.0 Takes in 2 streets that intersect and a state, city, zip, and
   outputs a set of possible locations on the first cross street that is
   at the intersection, also includes a point geometry in NAD 83 long
   lat, a normalized address for each location, and the rating. The
   lower the rating the more likely the match. Results are sorted by
   lowest rating first. Can optionally pass in maximum results, defaults
   to 10

-  `GetEdgeByPoint <#GetEdgeByPoint>`__ - Availability: 2.0.0 - requires
   GEOS >= 3.3.0. Find the edge-id of an edge that intersects a given
   point

-  `GetFaceByPoint <#GetFaceByPoint>`__ - Availability: 2.0.0 - requires
   GEOS >= 3.3.0. Find the face-id of a face that intersects a given
   point

-  `GetNodeByPoint <#GetNodeByPoint>`__ - Availability: 2.0.0 - requires
   GEOS >= 3.3.0. Find the id of a node at a point location

-  `GetNodeEdges <#GetNodeEdges>`__ - Availability: 2.0 Returns an
   ordered set of edges incident to the given node.

-  `GetRingEdges <#GetRingEdges>`__ - Availability: 2.0 Returns an
   ordered set of edges forming a ring with the given edge .

-  `GetTopologySRID <#GetTopologySRID>`__ - Availability: 2.0.0 Returns
   the SRID of a topology in the topology.topology table given the name
   of the topology.

-  `Get\_Tract <#Get_Tract>`__ - Availability: 2.0.0 Returns census
   tract or field from tract table of where the geometry is located.
   Default to returning short name of tract.

-  `Install\_Missing\_Indexes <#Install_Missing_Indexes>`__ -
   Availability: 2.0.0 Finds all tables with key columns used in
   geocoder joins and filter conditions that are missing used indexes on
   those columns and will add them.

-  `Loader\_Generate\_Census\_Script <#Loader_Generate_Census_Script>`__
   - Availability: 2.0.0 Generates a shell script for the specified
   platform for the specified states that will download Tiger census
   state tract, bg, and tabblocks data tables, stage and load into
   tiger\_data schema. Each state script is returned as a separate
   record.

-  `Loader\_Generate\_Script <#Loader_Generate_Script>`__ -
   Availability: 2.0.0 to support Tiger 2010 structured data and load
   census tract (tract), block groups (bg), and blocks (tabblocks)
   tables . Generates a shell script for the specified platform for the
   specified states that will download Tiger data, stage and load into
   tiger\_data schema. Each state script is returned as a separate
   record. Latest version supports Tiger 2010 structural changes and
   also loads census tract, block groups, and blocks tables.

-  `Missing\_Indexes\_Generate\_Script <#Missing_Indexes_Generate_Script>`__
   - Availability: 2.0.0 Finds all tables with key columns used in
   geocoder joins that are missing indexes on those columns and will
   output the SQL DDL to define the index for those tables.

-  `Polygonize <#TopologyPolygonize>`__ - Availability: 2.0.0 Find and
   register all faces defined by topology edges

-  `Reverse\_Geocode <#Reverse_Geocode>`__ - Availability: 2.0.0 Takes a
   geometry point in a known spatial ref sys and returns a record
   containing an array of theoretically possible addresses and an array
   of cross streets. If include\_strnum\_range = true, includes the
   street range in the cross streets.

-  `ST\_3DClosestPoint <#ST_3DClosestPoint>`__ - Availability: 2.0.0
   Returns the 3-dimensional point on g1 that is closest to g2. This is
   the first point of the 3D shortest line.

-  `ST\_3DDFullyWithin <#ST_3DDFullyWithin>`__ - Availability: 2.0.0
   Returns true if all of the 3D geometries are within the specified
   distance of one another.

-  `ST\_3DDWithin <#ST_3DDWithin>`__ - Availability: 2.0.0 For 3d (z)
   geometry type Returns true if two geometries 3d distance is within
   number of units.

-  `ST\_3DDistance <#ST_3DDistance>`__ - Availability: 2.0.0 For
   geometry type Returns the 3-dimensional cartesian minimum distance
   (based on spatial ref) between two geometries in projected units.

-  `ST\_3DIntersects <#ST_3DIntersects>`__ - Availability: 2.0.0 Returns
   TRUE if the Geometries "spatially intersect" in 3d - only for points
   and linestrings

-  `ST\_3DLongestLine <#ST_3DLongestLine>`__ - Availability: 2.0.0
   Returns the 3-dimensional longest line between two geometries

-  `ST\_3DMaxDistance <#ST_3DMaxDistance>`__ - Availability: 2.0.0 For
   geometry type Returns the 3-dimensional cartesian maximum distance
   (based on spatial ref) between two geometries in projected units.

-  `ST\_3DShortestLine <#ST_3DShortestLine>`__ - Availability: 2.0.0
   Returns the 3-dimensional shortest line between two geometries

-  `ST\_AddEdgeModFace <#ST_AddEdgeModFace>`__ - Availability: 2.0 Add a
   new edge and, if in doing so it splits a face, modify the original
   face and add a new face.

-  `ST\_AddEdgeNewFaces <#ST_AddEdgeNewFaces>`__ - Availability: 2.0 Add
   a new edge and, if in doing so it splits a face, delete the original
   face and replace it with two new faces.

-  `ST\_AsGDALRaster <#RT_ST_AsGDALRaster>`__ - Availability: 2.0.0 -
   requires GDAL >= 1.6.0. Return the raster tile in the designated GDAL
   Raster format. Raster formats are one of those supported by your
   compiled library. Use ST\_GDALRasters() to get a list of formats
   supported by your library.

-  `ST\_AsJPEG <#RT_ST_AsJPEG>`__ - Availability: 2.0.0 - requires GDAL
   >= 1.6.0. Return the raster tile selected bands as a single Joint
   Photographic Exports Group (JPEG) image (byte array). If no band is
   specified and 1 or more than 3 bands, then only the first band is
   used. If only 3 bands then all 3 bands are used and mapped to RGB.

-  `ST\_AsLatLonText <#ST_AsLatLonText>`__ - Availability: 2.0 Return
   the Degrees, Minutes, Seconds representation of the given point.

-  `ST\_AsPNG <#RT_ST_AsPNG>`__ - Availability: 2.0.0 - requires GDAL >=
   1.6.0. Return the raster tile selected bands as a single portable
   network graphics (PNG) image (byte array). If 1, 3, or 4 bands in
   raster and no bands are specified, then all bands are used. If more 2
   or more than 4 bands and no bands specified, then only band 1 is
   used. Bands are mapped to RGB or RGBA space.

-  `ST\_AsRaster <#RT_ST_AsRaster>`__ - Availability: 2.0.0 - requires
   GDAL >= 1.6.0. Converts a PostGIS geometry to a PostGIS raster.

-  `ST\_AsTIFF <#RT_ST_AsTIFF>`__ - Availability: 2.0.0 - requires GDAL
   >= 1.6.0. Return the raster selected bands as a single TIFF image
   (byte array). If no band is specified, then will try to use all
   bands.

-  `ST\_AsX3D <#ST_AsX3D>`__ - Availability: 2.0.0:
   ISO-IEC-19776-1.2-X3DEncodings-XML Returns a Geometry in X3D xml node
   element format: ISO-IEC-19776-1.2-X3DEncodings-XML

-  `ST\_Aspect <#RT_ST_Aspect>`__ - Availability: 2.0.0 Returns the
   aspect (in degrees by default) of an elevation raster band. Useful
   for analyzing terrain.

-  `ST\_Band <#RT_ST_Band>`__ - Availability: 2.0.0 Returns one or more
   bands of an existing raster as a new raster. Useful for building new
   rasters from existing rasters.

-  `ST\_BandIsNoData <#RT_ST_BandIsNoData>`__ - Availability: 2.0.0
   Returns true if the band is filled with only nodata values.

-  `ST\_Clip <#RT_ST_Clip>`__ - Availability: 2.0.0 Returns the raster
   clipped by the input geometry. If band number not is specified, all
   bands are processed. If crop is not specified or TRUE, the output
   raster is cropped.

-  `ST\_CollectionHomogenize <#ST_CollectionHomogenize>`__ -
   Availability: 2.0.0 Given a geometry collection, returns the
   "simplest" representation of the contents.

-  `ST\_ConcaveHull <#ST_ConcaveHull>`__ - Availability: 2.0.0 The
   concave hull of a geometry represents a possibly concave geometry
   that encloses all geometries within the set. You can think of it as
   shrink wrapping.

-  `ST\_Count <#RT_ST_Count>`__ - Availability: 2.0.0 Returns the number
   of pixels in a given band of a raster or raster coverage. If no band
   is specified defaults to band 1. If exclude\_nodata\_value is set to
   true, will only count pixels that are not equal to the nodata value.

-  `ST\_CreateTopoGeo <#ST_CreateTopoGeo>`__ - Availability: 2.0 Adds a
   collection of geometries to a given empty topology and returns a
   message detailing success.

-  `ST\_Distinct4ma <#RT_ST_Distinct4ma>`__ - Availability: 2.0.0 Raster
   processing function that calculates the number of unique pixel values
   in a neighborhood.

-  `ST\_FlipCoordinates <#ST_FlipCoordinates>`__ - Availability: 2.0.0
   Returns a version of the given geometry with X and Y axis flipped.
   Useful for people who have built latitude/longitude features and need
   to fix them.

-  `ST\_GDALDrivers <#RT_ST_GDALDrivers>`__ - Availability: 2.0.0 -
   requires GDAL >= 1.6.0. Returns a list of raster formats supported by
   your lib gdal. These are the formats you can output your raster using
   ST\_AsGDALRaster.

-  `ST\_GeomFromGeoJSON <#ST_GeomFromGeoJSON>`__ - Availability: 2.0.0
   requires - JSON-C >= 0.9 Takes as input a geojson representation of a
   geometry and outputs a PostGIS geometry object

-  `ST\_GetFaceEdges <#ST_GetFaceEdges>`__ - Availability: 2.0 Returns a
   set of ordered edges that bound aface.

-  `ST\_HasNoBand <#RT_ST_HasNoBand>`__ - Availability: 2.0.0 Returns
   true if there is no band with given band number. If no band number is
   specified, then band number 1 is assumed.

-  `ST\_HillShade <#RT_ST_HillShade>`__ - Availability: 2.0.0 Returns
   the hypothetical illumination of an elevation raster band using
   provided azimuth, altitude, brightness and scale inputs.

-  `ST\_Histogram <#RT_ST_Histogram>`__ - Availability: 2.0.0 Returns a
   set of record summarizing a raster or raster coverage data
   distribution separate bin ranges. Number of bins are autocomputed if
   not specified.

-  `ST\_InterpolatePoint <#ST_InterpolatePoint>`__ - Availability: 2.0.0
   Return the value of the measure dimension of a geometry at the point
   closed to the provided point.

-  `ST\_IsEmpty <#RT_ST_IsEmpty>`__ - Availability: 2.0.0 Returns true
   if the raster is empty (width = 0 and height = 0). Otherwise, returns
   false.

-  `ST\_IsValidDetail <#ST_IsValidDetail>`__ - Availability: 2.0.0 -
   requires GEOS >= 3.3.0. Returns a valid\_detail
   (valid,reason,location) row stating if a geometry is valid or not and
   if not valid, a reason why and a location where.

-  `ST\_IsValidReason <#ST_IsValidReason>`__ - Availability: 2.0 -
   requires GEOS >= 3.3.0 for the version taking flags. Returns text
   stating if a geometry is valid or not and if not valid, a reason why.

-  `ST\_MakeLine <#ST_MakeLine>`__ - Availability: 2.0.0 - Support for
   linestring input elements was introduced Creates a Linestring from
   point or line geometries.

-  `ST\_MakeValid <#ST_MakeValid>`__ - Availability: 2.0.0, requires
   GEOS-3.3.0 Attempts to make an invalid geometry valid without losing
   vertices.

-  `ST\_MapAlgebraExpr <#RT_ST_MapAlgebraExpr>`__ - Availability: 2.0.0
   1 raster band version: Creates a new one band raster formed by
   applying a valid PostgreSQL algebraic operation on the input raster
   band and of pixeltype provided. Band 1 is assumed if no band is
   specified.

-  `ST\_MapAlgebraExpr <#RT_ST_MapAlgebraExpr2>`__ - Availability: 2.0.0
   2 raster band version: Creates a new one band raster formed by
   applying a valid PostgreSQL algebraic operation on the two input
   raster bands and of pixeltype provided. band 1 of each raster is
   assumed if no band numbers are specified. The resulting raster will
   be aligned (scale, skew and pixel corners) on the grid defined by the
   first raster and have its extent defined by the "extenttype"
   parameter. Values for "extenttype" can be: INTERSECTION, UNION,
   FIRST, SECOND.

-  `ST\_MapAlgebraFct <#RT_ST_MapAlgebraFct>`__ - Availability: 2.0.0 1
   band version - Creates a new one band raster formed by applying a
   valid PostgreSQL function on the input raster band and of pixeltype
   prodived. Band 1 is assumed if no band is specified.

-  `ST\_MapAlgebraFct <#RT_ST_MapAlgebraFct2>`__ - Availability: 2.0.0 2
   band version - Creates a new one band raster formed by applying a
   valid PostgreSQL function on the 2 input raster bands and of
   pixeltype prodived. Band 1 is assumed if no band is specified. Extent
   type defaults to INTERSECTION if not specified.

-  `ST\_MapAlgebraFctNgb <#RT_ST_MapAlgebraFctNgb>`__ - Availability:
   2.0.0 1-band version: Map Algebra Nearest Neighbor using user-defined
   PostgreSQL function. Return a raster which values are the result of a
   PLPGSQL user function involving a neighborhood of values from the
   input raster band.

-  `ST\_Max4ma <#RT_ST_Max4ma>`__ - Availability: 2.0.0 Raster
   processing function that calculates the maximum pixel value in a
   neighborhood.

-  `ST\_Mean4ma <#RT_ST_Mean4ma>`__ - Availability: 2.0.0 Raster
   processing function that calculates the mean pixel value in a
   neighborhood.

-  `ST\_Min4ma <#RT_ST_Min4ma>`__ - Availability: 2.0.0 Raster
   processing function that calculates the minimum pixel value in a
   neighborhood.

-  `ST\_ModEdgeHeal <#ST_ModEdgeHeal>`__ - Availability: 2.0 Heal two
   edges by deleting the node connecting them, modifying the first
   edgeand deleting the second edge. Returns the id of the deleted node.

-  `ST\_NewEdgeHeal <#ST_NewEdgeHeal>`__ - Availability: 2.0 Heal two
   edges by deleting the node connecting them, deleting both edges,and
   replacing them with an edge whose direction is the same as the
   firstedge provided.

-  `ST\_Node <#ST_Node>`__ - Availability: 2.0.0 - requires GEOS >=
   3.3.0. Node a set of linestrings.

-  `ST\_NumPatches <#ST_NumPatches>`__ - Availability: 2.0.0 Return the
   number of faces on a Polyhedral Surface. Will return null for
   non-polyhedral geometries.

-  `ST\_OffsetCurve <#ST_OffsetCurve>`__ - Availability: 2.0 - requires
   GEOS >= 3.2, improved with GEOS >= 3.3 Return an offset line at a
   given distance and side from an input line. Useful for computing
   parallel lines about a center line

-  `ST\_PatchN <#ST_PatchN>`__ - Availability: 2.0.0 Return the 1-based
   Nth geometry (face) if the geometry is a POLYHEDRALSURFACE,
   POLYHEDRALSURFACEM. Otherwise, return NULL.

-  `ST\_PixelAsPolygon <#RT_ST_PixelAsPolygon>`__ - Availability: 2.0.0
   Returns the polygon geometry that bounds the pixel for a particular
   row and column.

-  `ST\_PixelAsPolygons <#RT_ST_PixelAsPolygons>`__ - Availability:
   2.0.0 Returns the polygon geometry that bounds every pixel of a
   raster band along with the value, the X and the Y raster coordinates
   of each pixel.

-  `ST\_Project <#ST_Project>`__ - Availability: 2.0.0 Returns a POINT
   projected from a start point using a distance in meters and bearing
   (azimuth) in radians.

-  `ST\_Quantile <#RT_ST_Quantile>`__ - Availability: 2.0.0 Compute
   quantiles for a raster or raster table coverage in the context of the
   sample or population. Thus, a value could be examined to be at the
   raster's 25%, 50%, 75% percentile.

-  `ST\_Range4ma <#RT_ST_Range4ma>`__ - Availability: 2.0.0 Raster
   processing function that calculates the range of pixel values in a
   neighborhood.

-  `ST\_Reclass <#RT_ST_Reclass>`__ - Availability: 2.0.0 Creates a new
   raster composed of band types reclassified from original. The nband
   is the band to be changed. If nband is not specified assumed to be 1.
   All other bands are returned unchanged. Use case: convert a 16BUI
   band to a 8BUI and so forth for simpler rendering as viewable
   formats.

-  `ST\_RelateMatch <#ST_RelateMatch>`__ - Availability: 2.0.0 -
   requires GEOS >= 3.3.0. Returns true if intersectionMattrixPattern1
   implies intersectionMatrixPattern2

-  `ST\_RemEdgeModFace <#ST_RemEdgeModFace>`__ - Availability: 2.0
   Removes an edge and, if the removed edge separated two faces,delete
   one of the them and modify the other to take the space of both.

-  `ST\_RemEdgeNewFace <#ST_RemEdgeNewFace>`__ - Availability: 2.0
   Removes an edge and, if the removed edge separated two faces,delete
   the original faces and replace them with a new face.

-  `ST\_RemoveRepeatedPoints <#ST_RemoveRepeatedPoints>`__ -
   Availability: 2.0.0 Returns a version of the given geometry with
   duplicated points removed.

-  `ST\_Resample <#RT_ST_Resample>`__ - Availability: 2.0.0 Requires
   GDAL 1.6.1+ Resample a raster using a specified resampling algorithm,
   new dimensions, an arbitrary grid corner and a set of raster
   georeferencing attributes defined or borrowed from another raster.

-  `ST\_Rescale <#RT_ST_Rescale>`__ - Availability: 2.0.0 Requires GDAL
   1.6.1+ Resample a raster by adjusting only its scale (or pixel size).
   New pixel values are computed using the NearestNeighbor (english or
   american spelling), Bilinear, Cubic, CubicSpline or Lanczos
   resampling algorithm. Default is NearestNeighbor.

-  `ST\_Reskew <#RT_ST_Reskew>`__ - Availability: 2.0.0 Requires GDAL
   1.6.1+ Resample a raster by adjusting only its skew (or rotation
   parameters). New pixel values are computed using the NearestNeighbor
   (english or american spelling), Bilinear, Cubic, CubicSpline or
   Lanczos resampling algorithm. Default is NearestNeighbor.

-  `ST\_SameAlignment <#RT_ST_SameAlignment>`__ - Availability: 2.0.0
   Returns true if rasters have same skew, scale, spatial ref and false
   if they don't with notice detailing issue.

-  `ST\_SetBandIsNoData <#RT_ST_SetBandIsNoData>`__ - Availability:
   2.0.0 Sets the isnodata flag of the band to TRUE.

-  `ST\_SharedPaths <#ST_SharedPaths>`__ - Availability: 2.0.0 requires
   GEOS >= 3.3.0. Returns a collection containing paths shared by the
   two input linestrings/multilinestrings.

-  `ST\_Slope <#RT_ST_Slope>`__ - Availability: 2.0.0 Returns the slope
   (in degrees by default) of an elevation raster band. Useful for
   analyzing terrain.

-  `ST\_Snap <#ST_Snap>`__ - Availability: 2.0.0 requires GEOS >= 3.3.0.
   Snap segments and vertices of input geometry to vertices of a
   reference geometry.

-  `ST\_SnapToGrid <#RT_ST_SnapToGrid>`__ - Availability: 2.0.0 Requires
   GDAL 1.6.1+ Resample a raster by snapping it to a grid. New pixel
   values are computed using the NearestNeighbor (english or american
   spelling), Bilinear, Cubic, CubicSpline or Lanczos resampling
   algorithm. Default is NearestNeighbor.

-  `ST\_Split <#ST_Split>`__ - Availability: 2.0.0 Returns a collection
   of geometries resulting by splitting a geometry.

-  `ST\_StdDev4ma <#RT_ST_StdDev4ma>`__ - Availability: 2.0.0 Raster
   processing function that calculates the standard deviation of pixel
   values in a neighborhood.

-  `ST\_Sum4ma <#RT_ST_Sum4ma>`__ - Availability: 2.0.0 Raster
   processing function that calculates the sum of all pixel values in a
   neighborhood.

-  `ST\_SummaryStats <#RT_ST_SummaryStats>`__ - Availability: 2.0.0
   Returns record consisting of count, sum, mean, stddev, min, max for a
   given raster band of a raster or raster coverage. Band 1 is assumed
   is no band is specified.

-  `ST\_Transform <#RT_ST_Transform>`__ - Availability: 2.0.0 Requires
   GDAL 1.6.1+ Reprojects a raster in a known spatial reference system
   to another known spatial reference system using specified resampling
   algorithm. Options are NearestNeighbor, Bilinear, Cubic, CubicSpline,
   Lanczos defaulting to NearestNeighbor.

-  `ST\_UnaryUnion <#ST_UnaryUnion>`__ - Availability: 2.0.0 - requires
   GEOS >= 3.3.0. Like ST\_Union, but working at the geometry component
   level.

-  `ST\_Union <#RT_ST_Union>`__ - Availability: 2.0.0 Returns the union
   of a set of raster tiles into a single raster composed of 1 or more
   bands.

-  `ST\_ValueCount <#RT_ST_ValueCount>`__ - Availability: 2.0.0 Returns
   a set of records containing a pixel band value and count of the
   number of pixels in a given band of a raster (or a raster coverage)
   that have a given set of values. If no band is specified defaults to
   band 1. By default nodata value pixels are not counted. and all other
   values in the pixel are output and pixel band values are rounded to
   the nearest integer.

-  `TopoElementArray\_Agg <#TopoElementArray_Agg>`__ - Availability:
   2.0.0 Returns a topoelementarray for a set of element\_id, type
   arrays (topoelements)

-  `TopoGeo\_AddLineString <#TopoGeo_AddLineString>`__ - Availability:
   2.0.0 Adds a linestring to an existing topology using a tolerance and
   possibly splitting existing edges/faces. Returns edge identifiers

-  `TopoGeo\_AddPoint <#TopoGeo_AddPoint>`__ - Availability: 2.0.0 Adds
   a point to an existing topology using a tolerance and possibly
   splitting an existing edge.

-  `TopoGeo\_AddPolygon <#TopoGeo_AddPolygon>`__ - Availability: 2.0.0
   Adds a polygon to an existing topology using a tolerance and possibly
   splitting existing edges/faces.

-  `TopologySummary <#TopologySummary>`__ - Availability: 2.0.0 Takes a
   topology name and provides summary totals of types of objects in
   topology

-  `Topology\_Load\_Tiger <#Topology_Load_Tiger>`__ - Availability:
   2.0.0 Loads a defined region of tiger data into a PostGIS Topology
   and transforming the tiger data to spatial reference of the topology
   and snapping to the precision tolerance of the topology.

-  `toTopoGeom <#toTopoGeom>`__ - Availability: 2.0 Converts a simple
   Geometry into a topo geometry

The functions given below are PostGIS functions that are enhanced in
PostGIS 2.0.

-  `AddGeometryColumn <#AddGeometryColumn>`__ - Enhanced: 2.0.0
   use\_typmod argument introduced. Defaults to creating typmod geometry
   column instead of constraint-based.

-  `Box2D <#Box2D>`__ - Enhanced: 2.0.0 support for Polyhedral surfaces,
   Triangles and TIN was introduced.

-  `Box3D <#Box3D>`__ - Enhanced: 2.0.0 support for Polyhedral surfaces,
   Triangles and TIN was introduced.

-  `Geocode <#Geocode>`__ - Enhanced: 2.0.0 to support Tiger 2010
   structured data and revised some logic to improve speed, accuracy of
   geocoding, and to offset point from centerline to side of street
   address is located on. New parameter max\_results useful for
   specifying ot just return the best result.

-  `GeometryType <#GeometryType>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces, Triangles and TIN was introduced.

-  `Populate\_Geometry\_Columns <#Populate_Geometry_Columns>`__ -
   Enhanced: 2.0.0 use\_typmod optional argument was introduced that
   allows controlling if columns are created with typmodifiers or with
   check constraints.

-  `ST\_Intersection <#RT_ST_Intersection>`__ - Enhanced: 2.0.0 -
   Intersection in the raster space was introduced. In earlier pre-2.0.0
   versions, only intersection performed in vector space were supported.

-  `ST\_Intersects <#RT_ST_Intersects>`__ - Enhanced: 2.0.0 support
   raster/raster intersects was introduced.

-  `ST\_Value <#RT_ST_Value>`__ - Enhanced: 2.0.0 exclude\_nodata\_value
   optional argument was added.

-  `ST\_3DExtent <#ST_3DExtent>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces, Triangles and TIN was introduced.

-  `ST\_Accum <#ST_Accum>`__ - Enhanced: 2.0.0 support for Polyhedral
   surfaces, Triangles and TIN was introduced.

-  `ST\_Affine <#ST_Affine>`__ - Enhanced: 2.0.0 support for Polyhedral
   surfaces, Triangles and TIN was introduced.

-  `ST\_Area <#ST_Area>`__ - Enhanced: 2.0.0 - support for 2D polyhedral
   surfaces was introduced.

-  `ST\_AsBinary <#ST_AsBinary>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces, Triangles and TIN was introduced.

-  `ST\_AsBinary <#ST_AsBinary>`__ - Enhanced: 2.0.0 support for higher
   coordinate dimensions was introduced.

-  `ST\_AsBinary <#ST_AsBinary>`__ - Enhanced: 2.0.0 support for
   specifying endian with geography was introduced.

-  `ST\_AsEWKB <#ST_AsEWKB>`__ - Enhanced: 2.0.0 support for Polyhedral
   surfaces, Triangles and TIN was introduced.

-  `ST\_AsEWKT <#ST_AsEWKT>`__ - Enhanced: 2.0.0 support for Geography,
   Polyhedral surfaces, Triangles and TIN was introduced.

-  `ST\_AsGML <#ST_AsGML>`__ - Enhanced: 2.0.0 prefix support was
   introduced. Option 4 for GML3 was introduced to allow using
   LineString instead of Curve tag for lines. GML3 Support for
   Polyhedral surfaces and TINS was introduced. Option 32 was introduced
   to output the box.

-  `ST\_AsKML <#ST_AsKML>`__ - Enhanced: 2.0.0 - Add prefix namespace.
   Default is no prefix

-  `ST\_Azimuth <#ST_Azimuth>`__ - Enhanced: 2.0.0 support for geography
   was introduced.

-  `ST\_ChangeEdgeGeom <#ST_ChangeEdgeGeom>`__ - Enhanced: 2.0.0 adds
   topological consistency enforcement

-  `ST\_Dimension <#ST_Dimension>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces and TINs was introduced. No longer throws an
   exception if given empty geometry.

-  `ST\_Dump <#ST_Dump>`__ - Enhanced: 2.0.0 support for Polyhedral
   surfaces, Triangles and TIN was introduced.

-  `ST\_DumpPoints <#ST_DumpPoints>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces, Triangles and TIN was introduced.

-  `ST\_Expand <#ST_Expand>`__ - Enhanced: 2.0.0 support for Polyhedral
   surfaces, Triangles and TIN was introduced.

-  `ST\_Extent <#ST_Extent>`__ - Enhanced: 2.0.0 support for Polyhedral
   surfaces, Triangles and TIN was introduced.

-  `ST\_ForceRHR <#ST_ForceRHR>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces was introduced.

-  `ST\_Force2D <#ST_Force_2D>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces was introduced.

-  `ST\_Force3D <#ST_Force_3D>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces was introduced.

-  `ST\_Force3DZ <#ST_Force_3DZ>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces was introduced.

-  `ST\_ForceCollection <#ST_Force_Collection>`__ - Enhanced: 2.0.0
   support for Polyhedral surfaces was introduced.

-  `ST\_GMLToSQL <#ST_GMLToSQL>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces and TIN was introduced.

-  `ST\_GMLToSQL <#ST_GMLToSQL>`__ - Enhanced: 2.0.0 default srid
   optional parameter added.

-  `ST\_GeomFromEWKB <#ST_GeomFromEWKB>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces and TIN was introduced.

-  `ST\_GeomFromEWKT <#ST_GeomFromEWKT>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces and TIN was introduced.

-  `ST\_GeomFromGML <#ST_GeomFromGML>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces and TIN was introduced.

-  `ST\_GeomFromGML <#ST_GeomFromGML>`__ - Enhanced: 2.0.0 default srid
   optional parameter added.

-  `ST\_GeometryN <#ST_GeometryN>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces, Triangles and TIN was introduced.

-  `ST\_GeometryType <#ST_GeometryType>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces was introduced.

-  `ST\_IsClosed <#ST_IsClosed>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces was introduced.

-  `ST\_MakeEnvelope <#ST_MakeEnvelope>`__ - Enhanced: 2.0: Ability to
   specify an envelope without specifying an SRID was introduced.

-  `ST\_MakeValid <#ST_MakeValid>`__ - Enhanced: 2.0.1, speed
   improvements requires GEOS-3.3.4

-  `ST\_NPoints <#ST_NPoints>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces was introduced.

-  `ST\_NumGeometries <#ST_NumGeometries>`__ - Enhanced: 2.0.0 support
   for Polyhedral surfaces, Triangles and TIN was introduced.

-  `ST\_Relate <#ST_Relate>`__ - Enhanced: 2.0.0 - added support for
   specifying boundary node rule (requires GEOS >= 3.0).

-  `ST\_Rotate <#ST_Rotate>`__ - Enhanced: 2.0.0 support for Polyhedral
   surfaces, Triangles and TIN was introduced.

-  `ST\_Rotate <#ST_Rotate>`__ - Enhanced: 2.0.0 additional parameters
   for specifying the origin of rotation were added.

-  `ST\_RotateX <#ST_RotateX>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces, Triangles and TIN was introduced.

-  `ST\_RotateY <#ST_RotateY>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces, Triangles and TIN was introduced.

-  `ST\_RotateZ <#ST_RotateZ>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces, Triangles and TIN was introduced.

-  `ST\_Scale <#ST_Scale>`__ - Enhanced: 2.0.0 support for Polyhedral
   surfaces, Triangles and TIN was introduced.

-  `ST\_Shift\_Longitude <#ST_Shift_Longitude>`__ - Enhanced: 2.0.0
   support for Polyhedral surfaces and TIN was introduced.

-  `ST\_Summary <#ST_Summary>`__ - Enhanced: 2.0.0 added support for
   geography

-  `ST\_Transform <#ST_Transform>`__ - Enhanced: 2.0.0 support for
   Polyhedral surfaces was introduced.

-  `ValidateTopology <#ValidateTopology>`__ - Enhanced: 2.0.0 more
   efficient edge crossing detection and fixes for false positives that
   were existent in prior versions.

-  `&& <#geometry_overlaps>`__ - Enhanced: 2.0.0 support for Polyhedral
   surfaces was introduced.

PostGIS Functions changed behavior in 2.0
-----------------------------------------

The functions given below are PostGIS functions that have changed
behavior in PostGIS 2.0 and may require application changes.

    **Note**

    Most deprecated functions have been removed. These are functions
    that haven't been documented since 1.2 or some internal functions
    that were never documented. If you are using a function that you
    don't see documented, it's probably deprecated, about to be
    deprecated, or internal and should be avoided. If you have
    applications or tools that rely on deprecated functions, please
    refer to ? for more details.

    **Note**

    Bounding boxes of geometries have been changed from float4 to double
    precision (float8). This has an impact on answers you get using
    bounding box operators and casting of bounding boxes to geometries.
    E.g ST\_SetSRID(abbox) will often return a different more accurate
    answer in PostGIS 2.0+ than it did in prior versions which may very
    well slightly change answers to view port queries.

    **Note**

    The arguments hasnodata was replaced with exclude\_nodata\_value
    which has the same meaning as the older hasnodata but clearer in
    purpose.

-  `AddGeometryColumn <#AddGeometryColumn>`__ - Changed: 2.0.0 This
   function no longer updates geometry\_columns since geometry\_columns
   is a view that reads from system catalogs. It by default also does
   not create constraints, but instead uses the built in type modifier
   behavior of PostgreSQL. So for example building a wgs84 POINT column
   with this function is now equivalent to: ALTER TABLE some\_table ADD
   COLUMN geom geometry(Point,4326);

-  `AddGeometryColumn <#AddGeometryColumn>`__ - Changed: 2.0.0 If you
   require the old behavior of constraints use the default use\_typmod,
   but set it to false.

-  `AddGeometryColumn <#AddGeometryColumn>`__ - Changed: 2.0.0 Views can
   no longer be manually registered in geometry\_columns, however views
   built against geometry typmod tables geometries and used without
   wrapper functions will register themselves correctly because they
   inherit the typmod behavior of their parent table column. Views that
   use geometry functions that output other geometries will need to be
   cast to typmod geometries for these view geometry columns to be
   registered correctly in geometry\_columns. Refer to .

-  `DropGeometryColumn <#DropGeometryColumn>`__ - Changed: 2.0.0 This
   function is provided for backward compatibility. Now that since
   geometry\_columns is now a view against the system catalogs, you can
   drop a geometry column like any other table column using ALTER TABLE

-  `DropGeometryTable <#DropGeometryTable>`__ - Changed: 2.0.0 This
   function is provided for backward compatibility. Now that since
   geometry\_columns is now a view against the system catalogs, you can
   drop a table with geometry columns like any other table using DROP
   TABLE

-  `Populate\_Geometry\_Columns <#Populate_Geometry_Columns>`__ -
   Changed: 2.0.0 By default, now uses type modifiers instead of check
   constraints to constrain geometry types. You can still use check
   constraint behavior instead by using the new use\_typmod and setting
   it to false.

-  `Box3D <#RT_Box3D>`__ - Changed: 2.0.0 In pre-2.0 versions, there
   used to be a box2d instead of box3d. Since box2d is a deprecated
   type, this was changed to box3d.

-  `ST\_ScaleX <#RT_ST_ScaleX>`__ - Changed: 2.0.0. In WKTRaster
   versions this was called ST\_PixelSizeX.

-  `ST\_ScaleY <#RT_ST_ScaleY>`__ - Changed: 2.0.0. In WKTRaster
   versions this was called ST\_PixelSizeY.

-  `ST\_SetScale <#RT_ST_SetScale>`__ - Changed: 2.0.0 In WKTRaster
   versions this was called ST\_SetPixelSize. This was changed in 2.0.0.

-  `ST\_3DExtent <#ST_3DExtent>`__ - Changed: 2.0.0 In prior versions
   this used to be called ST\_Extent3D

-  `ST\_3DLength <#ST_3DLength>`__ - Changed: 2.0.0 In prior versions
   this used to be called ST\_Length3D

-  `ST\_3DLength\_Spheroid <#ST_3DLength_Spheroid>`__ - Changed: 2.0.0
   In prior versions this used to return 0 for anything that is not a
   MULTILINESTRING or LINESTRING and in 2.0.0 on returns the perimeter
   of if given a polgon.

-  `ST\_3DLength\_Spheroid <#ST_3DLength_Spheroid>`__ - Changed: 2.0.0
   In prior versions this used to be called ST\_Length3d\_Spheroid

-  `ST\_3DMakeBox <#ST_3DMakeBox>`__ - Changed: 2.0.0 In prior versions
   this used to be called ST\_MakeBox3D

-  `ST\_3DPerimeter <#ST_3DPerimeter>`__ - Changed: 2.0.0 In prior
   versions this used to be called ST\_Perimeter3D

-  `ST\_AsBinary <#ST_AsBinary>`__ - Changed: 2.0.0 Inputs to this
   function can not be unknown -- must be geometry. Constructs such as
   ST\_AsBinary('POINT(1 2)') are no longer valid and you will get an n
   st\_asbinary(unknown) is not unique error. Code like that needs to be
   changed to ST\_AsBinary('POINT(1 2)'::geometry);. If that is not
   possible, then install legacy.sql.

-  `ST\_AsGML <#ST_AsGML>`__ - Changed: 2.0.0 use default named args

-  `ST\_AsGeoJSON <#ST_AsGeoJSON>`__ - Changed: 2.0.0 support default
   args and named args.

-  `ST\_AsKML <#ST_AsKML>`__ - Changed: 2.0.0 - uses default args and
   supports named args

-  `ST\_AsSVG <#ST_AsSVG>`__ - Changed: 2.0.0 to use default args and
   support named args

-  `ST\_EndPoint <#ST_EndPoint>`__ - Changed: 2.0.0 no longer works with
   single geometry multilinestrings. In older versions of PostGIS -- a
   single line multilinestring would work happily with this function and
   return the start point. In 2.0.0 it just returns NULL like any other
   multilinestring. The older behavior was an undocumented feature, but
   people who assumed they had their data stored as LINESTRING may
   experience these returning NULL in 2.0 now.

-  `ST\_GeomFromText <#ST_GeomFromText>`__ - Changed: 2.0.0 In prior
   versions of PostGIS ST\_GeomFromText('GEOMETRYCOLLECTION(EMPTY)') was
   allowed. This is now illegal in PostGIS 2.0.0 to better conform with
   SQL/MM standards. This should now be written as
   ST\_GeomFromText('GEOMETRYCOLLECTION EMPTY')

-  `ST\_GeometryN <#ST_GeometryN>`__ - Changed: 2.0.0 Prior versions
   would return NULL for singular geometries. This was changed to return
   the geometry for ST\_GeometryN(..,1) case.

-  `ST\_IsEmpty <#ST_IsEmpty>`__ - Changed: 2.0.0 In prior versions of
   PostGIS ST\_GeomFromText('GEOMETRYCOLLECTION(EMPTY)') was allowed.
   This is now illegal in PostGIS 2.0.0 to better conform with SQL/MM
   standards

-  `ST\_Length <#ST_Length>`__ - Changed: 2.0.0 Breaking change -- in
   prior versions applying this to a MULTI/POLYGON of type geography
   would give you the perimeter of the POLYGON/MULTIPOLYGON. In 2.0.0
   this was changed to return 0 to be in line with geometry behavior.
   Please use ST\_Perimeter if you want the perimeter of a polygon

-  `ST\_LocateAlong <#ST_LocateAlong>`__ - Changed: 2.0.0 in prior
   versions this used to be called ST\_Locate\_Along\_Measure. The old
   name has been deprecated and will be removed in the future but is
   still available.

-  `ST\_LocateBetween <#ST_LocateBetween>`__ - Changed: 2.0.0 - in prior
   versions this used to be called ST\_Locate\_Between\_Measures. The
   old name has been deprecated and will be removed in the future but is
   still available for backward compatibility.

-  `ST\_ModEdgeSplit <#ST_ModEdgeSplit>`__ - Changed: 2.0 - In prior
   versions, this was misnamed ST\_ModEdgesSplit

-  `ST\_NumGeometries <#ST_NumGeometries>`__ - Changed: 2.0.0 In prior
   versions this would return NULL if the geometry was not a
   collection/MULTI type. 2.0.0+ now returns 1 for single geometries e.g
   POLYGON, LINESTRING, POINT.

-  `ST\_PointN <#ST_PointN>`__ - Changed: 2.0.0 no longer works with
   single geometry multilinestrings. In older versions of PostGIS -- a
   single line multilinestring would work happily with this function and
   return the start point. In 2.0.0 it just returns NULL like any other
   multilinestring.

-  `ST\_StartPoint <#ST_StartPoint>`__ - Changed: 2.0.0 no longer works
   with single geometry multilinestrings. In older versions of PostGIS
   -- a single line multilinestring would work happily with this
   function and return the start point. In 2.0.0 it just returns NULL
   like any other multilinestring. The older behavior was an
   undocumented feature, but people who assumed they had their data
   stored as LINESTRING may experience these returning NULL in 2.0 now.

PostGIS Functions new, behavior changed, or enhanced in 1.5
-----------------------------------------------------------

The functions given below are PostGIS functions that were introduced or
enhanced in this minor release.

-  `PostGIS\_LibXML\_Version <#PostGIS_LibXML_Version>`__ -
   Availability: 1.5 Returns the version number of the libxml2 library.

-  `ST\_AddMeasure <#ST_AddMeasure>`__ - Availability: 1.5.0 Return a
   derived geometry with measure elements linearly interpolated between
   the start and end points. If the geometry has no measure dimension,
   one is added. If the geometry has a measure dimension, it is
   over-written with new values. Only LINESTRINGS and MULTILINESTRINGS
   are supported.

-  `ST\_AsBinary <#ST_AsBinary>`__ - Availability: 1.5.0 geography
   support was introduced. Return the Well-Known Binary (WKB)
   representation of the geometry/geography without SRID meta data.

-  `ST\_AsGML <#ST_AsGML>`__ - Availability: 1.5.0 geography support was
   introduced. Return the geometry as a GML version 2 or 3 element.

-  `ST\_AsGeoJSON <#ST_AsGeoJSON>`__ - Availability: 1.5.0 geography
   support was introduced. Return the geometry as a GeoJSON element.

-  `ST\_AsText <#ST_AsText>`__ - Availability: 1.5 - support for
   geography was introduced. Return the Well-Known Text (WKT)
   representation of the geometry/geography without SRID metadata.

-  `ST\_Buffer <#ST_Buffer>`__ - Availability: 1.5 - ST\_Buffer was
   enhanced to support different endcaps and join types. These are
   useful for example to convert road linestrings into polygon roads
   with flat or square edges instead of rounded edges. Thin wrapper for
   geography was added. - requires GEOS >= 3.2 to take advantage of
   advanced geometry functionality. (T) For geometry: Returns a geometry
   that represents all points whose distance from this Geometry is less
   than or equal to distance. Calculations are in the Spatial Reference
   System of this Geometry. For geography: Uses a planar transform
   wrapper. Introduced in 1.5 support for different end cap and mitre
   settings to control shape. buffer\_style options:
   quad\_segs=#,endcap=round\|flat\|square,join=round\|mitre\|bevel,mitre\_limit=#.#

-  `ST\_ClosestPoint <#ST_ClosestPoint>`__ - Availability: 1.5.0 Returns
   the 2-dimensional point on g1 that is closest to g2. This is the
   first point of the shortest line.

-  `ST\_CollectionExtract <#ST_CollectionExtract>`__ - Availability:
   1.5.0 Given a (multi)geometry, returns a (multi)geometry consisting
   only of elements of the specified type.

-  `ST\_Covers <#ST_Covers>`__ - Availability: 1.5 - support for
   geography was introduced. Returns 1 (TRUE) if no point in Geometry B
   is outside Geometry A

-  `ST\_DFullyWithin <#ST_DFullyWithin>`__ - Availability: 1.5.0 Returns
   true if all of the geometries are within the specified distance of
   one another

-  `ST\_DWithin <#ST_DWithin>`__ - Availability: 1.5.0 support for
   geography was introduced Returns true if the geometries are within
   the specified distance of one another. For geometry units are in
   those of spatial reference and For geography units are in meters and
   measurement is defaulted to use\_spheroid=true (measure around
   spheroid), for faster check, use\_spheroid=false to measure along
   sphere.

-  `ST\_Distance <#ST_Distance>`__ - Availability: 1.5.0 geography
   support was introduced in 1.5. Speed improvements for planar to
   better handle large or many vertex geometries For geometry type
   Returns the 2-dimensional cartesian minimum distance (based on
   spatial ref) between two geometries in projected units. For geography
   type defaults to return spheroidal minimum distance between two
   geographies in meters.

-  `ST\_Distance\_Sphere <#ST_Distance_Sphere>`__ - Availability: 1.5 -
   support for other geometry types besides points was introduced. Prior
   versions only work with points. Returns minimum distance in meters
   between two lon/lat geometries. Uses a spherical earth and radius of
   6370986 meters. Faster than ST\_Distance\_Spheroid , but less
   accurate. PostGIS versions prior to 1.5 only implemented for points.

-  `ST\_Distance\_Spheroid <#ST_Distance_Spheroid>`__ - Availability:
   1.5 - support for other geometry types besides points was introduced.
   Prior versions only work with points. Returns the minimum distance
   between two lon/lat geometries given a particular spheroid. PostGIS
   versions prior to 1.5 only support points.

-  `ST\_DumpPoints <#ST_DumpPoints>`__ - Availability: 1.5.0 Returns a
   set of geometry\_dump (geom,path) rows of all points that make up a
   geometry.

-  `ST\_Envelope <#ST_Envelope>`__ - Availability: 1.5.0 behavior
   changed to output double precision instead of float4 Returns a
   geometry representing the double precision (float8) bounding box of
   the supplied geometry.

-  `ST\_GMLToSQL <#ST_GMLToSQL>`__ - Availability: 1.5, requires libxml2
   1.6+ Return a specified ST\_Geometry value from GML representation.
   This is an alias name for ST\_GeomFromGML

-  `ST\_GeomFromGML <#ST_GeomFromGML>`__ - Availability: 1.5, requires
   libxml2 1.6+ Takes as input GML representation of geometry and
   outputs a PostGIS geometry object

-  `ST\_GeomFromKML <#ST_GeomFromKML>`__ - Availability: 1.5,libxml2
   2.6+ Takes as input KML representation of geometry and outputs a
   PostGIS geometry object

-  `~= <#ST_Geometry_Same>`__ - Availability: 1.5.0 changed behavior
   Returns TRUE if A's bounding box is the same as B's.

-  `ST\_HausdorffDistance <#ST_HausdorffDistance>`__ - Availability:
   1.5.0 - requires GEOS >= 3.2.0 Returns the Hausdorff distance between
   two geometries. Basically a measure of how similar or dissimilar 2
   geometries are. Units are in the units of the spatial reference
   system of the geometries.

-  `ST\_Intersection <#ST_Intersection>`__ - Availability: 1.5 support
   for geography data type was introduced. (T) Returns a geometry that
   represents the shared portion of geomA and geomB. The geography
   implementation does a transform to geometry to do the intersection
   and then transform back to WGS84.

-  `ST\_Intersects <#ST_Intersects>`__ - Availability: 1.5 support for
   geography was introduced. Returns TRUE if the Geometries/Geography
   "spatially intersect in 2D" - (share any portion of space) and FALSE
   if they don't (they are Disjoint). For geography -- tolerance is
   0.00001 meters (so any points that close are considered to intersect)

-  `ST\_Length <#ST_Length>`__ - Availability: 1.5.0 geography support
   was introduced in 1.5. Returns the 2d length of the geometry if it is
   a linestring or multilinestring. geometry are in units of spatial
   reference and geography are in meters (default spheroid)

-  `ST\_LongestLine <#ST_LongestLine>`__ - Availability: 1.5.0 Returns
   the 2-dimensional longest line points of two geometries. The function
   will only return the first longest line if more than one, that the
   function finds. The line returned will always start in g1 and end in
   g2. The length of the line this function returns will always be the
   same as st\_maxdistance returns for g1 and g2.

-  `ST\_MakeEnvelope <#ST_MakeEnvelope>`__ - Availability: 1.5 Creates a
   rectangular Polygon formed from the given minimums and maximums.
   Input values must be in SRS specified by the SRID.

-  `ST\_MaxDistance <#ST_MaxDistance>`__ - Availability: 1.5.0 Returns
   the 2-dimensional largest distance between two geometries in
   projected units.

-  `ST\_ShortestLine <#ST_ShortestLine>`__ - Availability: 1.5.0 Returns
   the 2-dimensional shortest line between two geometries

-  `&& <#geometry_overlaps>`__ - Availability: 1.5.0 support for
   geography was introduced. Returns TRUE if A's 2D bounding box
   intersects B's 2D bounding box.

PostGIS Functions new, behavior changed, or enhanced in 1.4
-----------------------------------------------------------

The functions given below are PostGIS functions that were introduced or
enhanced in the 1.4 release.

-  `Populate\_Geometry\_Columns <#Populate_Geometry_Columns>`__ -
   Ensures geometry columns are defined with type modifiers or have
   appropriate spatial constraints This ensures they will be registered
   correctly in geometry\_columns view. By default will convert all
   geometry columns with no type modifier to ones with type modifiers.
   To get old behavior set use\_typmod=false Availability: 1.4.0

-  `ST\_AsSVG <#ST_AsSVG>`__ - Returns a Geometry in SVG path data given
   a geometry or geography object. Availability: 1.2.2. Availability:
   1.4.0 Changed in PostGIS 1.4.0 to include L command in absolute path
   to conform to http://www.w3.org/TR/SVG/paths.html#PathDataBNF

-  `ST\_Collect <#ST_Collect>`__ - Return a specified ST\_Geometry value
   from a collection of other geometries. Availability: 1.4.0 -
   ST\_Collect(geomarray) was introduced. ST\_Collect was enhanced to
   handle more geometries faster.

-  `ST\_ContainsProperly <#ST_ContainsProperly>`__ - Returns true if B
   intersects the interior of A but not the boundary (or exterior). A
   does not contain properly itself, but does contain itself.
   Availability: 1.4.0 - requires GEOS >= 3.1.0.

-  `ST\_Extent <#ST_Extent>`__ - an aggregate function that returns the
   bounding box that bounds rows of geometries. Availability: 1.4.0

-  `ST\_GeoHash <#ST_GeoHash>`__ - Return a GeoHash representation of
   the geometry. Availability: 1.4.0

-  `ST\_IsValidReason <#ST_IsValidReason>`__ - Returns text stating if a
   geometry is valid or not and if not valid, a reason why.
   Availability: 1.4 - requires GEOS >= 3.1.0.

-  `ST\_LineCrossingDirection <#ST_LineCrossingDirection>`__ - Given 2
   linestrings, returns a number between -3 and 3 denoting what kind of
   crossing behavior. 0 is no crossing. Availability: 1.4

-  `ST\_LocateBetweenElevations <#ST_LocateBetweenElevations>`__ -
   Return a derived geometry (collection) value with elements that
   intersect the specified range of elevations inclusively. Only 3D, 4D
   LINESTRINGS and MULTILINESTRINGS are supported. Availability: 1.4.0

-  `ST\_MakeLine <#ST_MakeLine>`__ - Creates a Linestring from point or
   line geometries. Availability: 1.4.0 - ST\_MakeLine(geomarray) was
   introduced. ST\_MakeLine aggregate functions was enhanced to handle
   more points faster.

-  `ST\_MinimumBoundingCircle <#ST_MinimumBoundingCircle>`__ - Returns
   the smallest circle polygon that can fully contain a geometry.
   Default uses 48 segments per quarter circle. Availability: 1.4.0 -
   requires GEOS

-  `ST\_Union <#ST_Union>`__ - Returns a geometry that represents the
   point set union of the Geometries. Availability: 1.4.0 - ST\_Union
   was enhanced. ST\_Union(geomarray) was introduced and also faster
   aggregate collection in PostgreSQL. If you are using GEOS 3.1.0+
   ST\_Union will use the faster Cascaded Union algorithm described in
   http://blog.cleverelephant.ca/2009/01/must-faster-unions-in-postgis-14.html

PostGIS Functions new in 1.3
----------------------------

The functions given below are PostGIS functions that were introduced in
the 1.3 release.

-  `ST\_AsGML <#ST_AsGML>`__ - Return the geometry as a GML version 2 or
   3 element. Availability: 1.3.2

-  `ST\_AsGeoJSON <#ST_AsGeoJSON>`__ - Return the geometry as a GeoJSON
   element. Availability: 1.3.4

-  `ST\_SimplifyPreserveTopology <#ST_SimplifyPreserveTopology>`__ -
   Returns a "simplified" version of the given geometry using the
   Douglas-Peucker algorithm. Will avoid creating derived geometries
   (polygons in particular) that are invalid. Availability: 1.3.3

.. |image0| image:: images/matrix_checkmark.png
.. |image1| image:: images/matrix_transform.png
.. |image2| image:: images/matrix_autocast.png
.. |image3| image:: images/matrix_sfcgal_required.png
.. |image4| image:: images/matrix_sfcgal_enhanced.png
.. |image5| image:: images/matrix_checkmark.png
.. |image6| image:: images/matrix_checkmark.png
.. |image7| image:: images/matrix_checkmark.png
.. |image8| image:: images/matrix_checkmark.png
.. |image9| image:: images/matrix_checkmark.png
.. |image10| image:: images/matrix_checkmark.png
.. |image11| image:: images/matrix_checkmark.png
.. |image12| image:: images/matrix_checkmark.png
.. |image13| image:: images/matrix_checkmark.png
.. |image14| image:: images/matrix_checkmark.png
.. |image15| image:: images/matrix_checkmark.png
.. |image16| image:: images/matrix_checkmark.png
.. |image17| image:: images/matrix_checkmark.png
.. |image18| image:: images/matrix_checkmark.png
.. |image19| image:: images/matrix_checkmark.png
.. |image20| image:: images/matrix_checkmark.png
.. |image21| image:: images/matrix_checkmark.png
.. |image22| image:: images/matrix_checkmark.png
.. |image23| image:: images/matrix_checkmark.png
.. |image24| image:: images/matrix_checkmark.png
.. |image25| image:: images/matrix_checkmark.png
.. |image26| image:: images/matrix_checkmark.png
.. |image27| image:: images/matrix_checkmark.png
.. |image28| image:: images/matrix_checkmark.png
.. |image29| image:: images/matrix_checkmark.png
.. |image30| image:: images/matrix_sfcgal_enhanced.png
.. |image31| image:: images/matrix_sfcgal_enhanced.png
.. |image32| image:: images/matrix_sfcgal_enhanced.png
.. |image33| image:: images/matrix_checkmark.png
.. |image34| image:: images/matrix_checkmark.png
.. |image35| image:: images/matrix_checkmark.png
.. |image36| image:: images/matrix_checkmark.png
.. |image37| image:: images/matrix_checkmark.png
.. |image38| image:: images/matrix_checkmark.png
.. |image39| image:: images/matrix_checkmark.png
.. |image40| image:: images/matrix_checkmark.png
.. |image41| image:: images/matrix_checkmark.png
.. |image42| image:: images/matrix_checkmark.png
.. |image43| image:: images/matrix_checkmark.png
.. |image44| image:: images/matrix_checkmark.png
.. |image45| image:: images/matrix_checkmark.png
.. |image46| image:: images/matrix_checkmark.png
.. |image47| image:: images/matrix_checkmark.png
.. |image48| image:: images/matrix_checkmark.png
.. |image49| image:: images/matrix_checkmark.png
.. |image50| image:: images/matrix_checkmark.png
.. |image51| image:: images/matrix_checkmark.png
.. |image52| image:: images/matrix_checkmark.png
.. |image53| image:: images/matrix_checkmark.png
.. |image54| image:: images/matrix_checkmark.png
.. |image55| image:: images/matrix_checkmark.png
.. |image56| image:: images/matrix_checkmark.png
.. |image57| image:: images/matrix_checkmark.png
.. |image58| image:: images/matrix_checkmark.png
.. |image59| image:: images/matrix_checkmark.png
.. |image60| image:: images/matrix_checkmark.png
.. |image61| image:: images/matrix_checkmark.png
.. |image62| image:: images/matrix_checkmark.png
.. |image63| image:: images/matrix_checkmark.png
.. |image64| image:: images/matrix_checkmark.png
.. |image65| image:: images/matrix_checkmark.png
.. |image66| image:: images/matrix_checkmark.png
.. |image67| image:: images/matrix_checkmark.png
.. |image68| image:: images/matrix_checkmark.png
.. |image69| image:: images/matrix_checkmark.png
.. |image70| image:: images/matrix_checkmark.png
.. |image71| image:: images/matrix_checkmark.png
.. |image72| image:: images/matrix_checkmark.png
.. |image73| image:: images/matrix_checkmark.png
.. |image74| image:: images/matrix_checkmark.png
.. |image75| image:: images/matrix_sfcgal_enhanced.png
.. |image76| image:: images/matrix_sfcgal_enhanced.png
.. |image77| image:: images/matrix_checkmark.png
.. |image78| image:: images/matrix_checkmark.png
.. |image79| image:: images/matrix_checkmark.png
.. |image80| image:: images/matrix_checkmark.png
.. |image81| image:: images/matrix_checkmark.png
.. |image82| image:: images/matrix_checkmark.png
.. |image83| image:: images/matrix_checkmark.png
.. |image84| image:: images/matrix_checkmark.png
.. |image85| image:: images/matrix_checkmark.png
.. |image86| image:: images/matrix_checkmark.png
.. |image87| image:: images/matrix_checkmark.png
.. |image88| image:: images/matrix_checkmark.png
.. |image89| image:: images/matrix_checkmark.png
.. |image90| image:: images/matrix_checkmark.png
.. |image91| image:: images/matrix_checkmark.png
.. |image92| image:: images/matrix_checkmark.png
.. |image93| image:: images/matrix_checkmark.png
.. |image94| image:: images/matrix_checkmark.png
.. |image95| image:: images/matrix_checkmark.png
.. |image96| image:: images/matrix_checkmark.png
.. |image97| image:: images/matrix_checkmark.png
.. |image98| image:: images/matrix_checkmark.png
.. |image99| image:: images/matrix_checkmark.png
.. |image100| image:: images/matrix_checkmark.png
.. |image101| image:: images/matrix_checkmark.png
.. |image102| image:: images/matrix_checkmark.png
.. |image103| image:: images/matrix_checkmark.png
.. |image104| image:: images/matrix_checkmark.png
.. |image105| image:: images/matrix_checkmark.png
.. |image106| image:: images/matrix_checkmark.png
.. |image107| image:: images/matrix_checkmark.png
.. |image108| image:: images/matrix_checkmark.png
.. |image109| image:: images/matrix_checkmark.png
.. |image110| image:: images/matrix_checkmark.png
.. |image111| image:: images/matrix_checkmark.png
.. |image112| image:: images/matrix_checkmark.png
.. |image113| image:: images/matrix_checkmark.png
.. |image114| image:: images/matrix_checkmark.png
.. |image115| image:: images/matrix_checkmark.png
.. |image116| image:: images/matrix_checkmark.png
.. |image117| image:: images/matrix_checkmark.png
.. |image118| image:: images/matrix_checkmark.png
.. |image119| image:: images/matrix_checkmark.png
.. |image120| image:: images/matrix_checkmark.png
.. |image121| image:: images/matrix_checkmark.png
.. |image122| image:: images/matrix_checkmark.png
.. |image123| image:: images/matrix_checkmark.png
.. |image124| image:: images/matrix_checkmark.png
.. |image125| image:: images/matrix_checkmark.png
.. |image126| image:: images/matrix_checkmark.png
.. |image127| image:: images/matrix_checkmark.png
.. |image128| image:: images/matrix_autocast.png
.. |image129| image:: images/matrix_checkmark.png
.. |image130| image:: images/matrix_transform.png
.. |image131| image:: images/matrix_checkmark.png
.. |image132| image:: images/matrix_checkmark.png
.. |image133| image:: images/matrix_checkmark.png
.. |image134| image:: images/matrix_checkmark.png
.. |image135| image:: images/matrix_checkmark.png
.. |image136| image:: images/matrix_checkmark.png
.. |image137| image:: images/matrix_checkmark.png
.. |image138| image:: images/matrix_checkmark.png
.. |image139| image:: images/matrix_checkmark.png
.. |image140| image:: images/matrix_checkmark.png
.. |image141| image:: images/matrix_checkmark.png
.. |image142| image:: images/matrix_checkmark.png
.. |image143| image:: images/matrix_checkmark.png
.. |image144| image:: images/matrix_checkmark.png
.. |image145| image:: images/matrix_checkmark.png
.. |image146| image:: images/matrix_checkmark.png
.. |image147| image:: images/matrix_checkmark.png
.. |image148| image:: images/matrix_checkmark.png
.. |image149| image:: images/matrix_checkmark.png
.. |image150| image:: images/matrix_checkmark.png
.. |image151| image:: images/matrix_checkmark.png
.. |image152| image:: images/matrix_checkmark.png
.. |image153| image:: images/matrix_checkmark.png
.. |image154| image:: images/matrix_checkmark.png
.. |image155| image:: images/matrix_checkmark.png
.. |image156| image:: images/matrix_checkmark.png
.. |image157| image:: images/matrix_checkmark.png
.. |image158| image:: images/matrix_checkmark.png
.. |image159| image:: images/matrix_checkmark.png
.. |image160| image:: images/matrix_checkmark.png
.. |image161| image:: images/matrix_checkmark.png
.. |image162| image:: images/matrix_checkmark.png
.. |image163| image:: images/matrix_checkmark.png
.. |image164| image:: images/matrix_checkmark.png
.. |image165| image:: images/matrix_checkmark.png
.. |image166| image:: images/matrix_checkmark.png
.. |image167| image:: images/matrix_checkmark.png
.. |image168| image:: images/matrix_checkmark.png
.. |image169| image:: images/matrix_checkmark.png
.. |image170| image:: images/matrix_checkmark.png
.. |image171| image:: images/matrix_checkmark.png
.. |image172| image:: images/matrix_checkmark.png
.. |image173| image:: images/matrix_checkmark.png
.. |image174| image:: images/matrix_checkmark.png
.. |image175| image:: images/matrix_checkmark.png
.. |image176| image:: images/matrix_checkmark.png
.. |image177| image:: images/matrix_checkmark.png
.. |image178| image:: images/matrix_checkmark.png
.. |image179| image:: images/matrix_checkmark.png
.. |image180| image:: images/matrix_checkmark.png
.. |image181| image:: images/matrix_checkmark.png
.. |image182| image:: images/matrix_sfcgal_enhanced.png
.. |image183| image:: images/matrix_checkmark.png
.. |image184| image:: images/matrix_checkmark.png
.. |image185| image:: images/matrix_checkmark.png
.. |image186| image:: images/matrix_checkmark.png
.. |image187| image:: images/matrix_checkmark.png
.. |image188| image:: images/matrix_checkmark.png
.. |image189| image:: images/matrix_checkmark.png
.. |image190| image:: images/matrix_checkmark.png
.. |image191| image:: images/matrix_checkmark.png
.. |image192| image:: images/matrix_checkmark.png
.. |image193| image:: images/matrix_checkmark.png
.. |image194| image:: images/matrix_checkmark.png
.. |image195| image:: images/matrix_checkmark.png
.. |image196| image:: images/matrix_checkmark.png
.. |image197| image:: images/matrix_checkmark.png
.. |image198| image:: images/matrix_checkmark.png
.. |image199| image:: images/matrix_checkmark.png
.. |image200| image:: images/matrix_checkmark.png
.. |image201| image:: images/matrix_checkmark.png
.. |image202| image:: images/matrix_checkmark.png
.. |image203| image:: images/matrix_checkmark.png
.. |image204| image:: images/matrix_autocast.png
.. |image205| image:: images/matrix_checkmark.png
.. |image206| image:: images/matrix_checkmark.png
.. |image207| image:: images/matrix_checkmark.png
.. |image208| image:: images/matrix_checkmark.png
.. |image209| image:: images/matrix_checkmark.png
.. |image210| image:: images/matrix_checkmark.png
.. |image211| image:: images/matrix_checkmark.png
.. |image212| image:: images/matrix_checkmark.png
.. |image213| image:: images/matrix_checkmark.png
.. |image214| image:: images/matrix_checkmark.png
.. |image215| image:: images/matrix_checkmark.png
.. |image216| image:: images/matrix_sfcgal_required.png
.. |image217| image:: images/matrix_sfcgal_required.png
.. |image218| image:: images/matrix_sfcgal_required.png
.. |image219| image:: images/matrix_checkmark.png
.. |image220| image:: images/matrix_checkmark.png
.. |image221| image:: images/matrix_checkmark.png
.. |image222| image:: images/matrix_checkmark.png
.. |image223| image:: images/matrix_checkmark.png
.. |image224| image:: images/matrix_checkmark.png
.. |image225| image:: images/matrix_sfcgal_required.png
.. |image226| image:: images/matrix_sfcgal_required.png
.. |image227| image:: images/matrix_sfcgal_required.png
.. |image228| image:: images/matrix_checkmark.png
.. |image229| image:: images/matrix_checkmark.png
.. |image230| image:: images/matrix_checkmark.png
.. |image231| image:: images/matrix_checkmark.png
.. |image232| image:: images/matrix_checkmark.png
.. |image233| image:: images/matrix_checkmark.png
.. |image234| image:: images/matrix_checkmark.png
.. |image235| image:: images/matrix_checkmark.png
.. |image236| image:: images/matrix_checkmark.png
.. |image237| image:: images/matrix_checkmark.png
.. |image238| image:: images/matrix_checkmark.png
.. |image239| image:: images/matrix_checkmark.png
.. |image240| image:: images/matrix_checkmark.png
.. |image241| image:: images/matrix_checkmark.png
.. |image242| image:: images/matrix_checkmark.png
.. |image243| image:: images/matrix_checkmark.png
.. |image244| image:: images/matrix_checkmark.png
.. |image245| image:: images/matrix_checkmark.png
.. |image246| image:: images/matrix_checkmark.png
.. |image247| image:: images/matrix_checkmark.png
.. |image248| image:: images/matrix_checkmark.png
.. |image249| image:: images/matrix_checkmark.png
.. |image250| image:: images/matrix_checkmark.png
.. |image251| image:: images/matrix_checkmark.png
.. |image252| image:: images/matrix_checkmark.png
.. |image253| image:: images/matrix_checkmark.png
.. |image254| image:: images/matrix_checkmark.png
.. |image255| image:: images/matrix_checkmark.png
.. |image256| image:: images/matrix_checkmark.png
.. |image257| image:: images/matrix_checkmark.png
.. |image258| image:: images/matrix_checkmark.png
.. |image259| image:: images/matrix_checkmark.png
.. |image260| image:: images/matrix_checkmark.png
.. |image261| image:: images/matrix_checkmark.png
.. |image262| image:: images/matrix_checkmark.png
.. |image263| image:: images/matrix_checkmark.png
.. |image264| image:: images/matrix_checkmark.png
.. |image265| image:: images/matrix_checkmark.png
.. |image266| image:: images/matrix_checkmark.png
.. |image267| image:: images/matrix_checkmark.png
.. |image268| image:: images/matrix_checkmark.png
.. |image269| image:: images/matrix_checkmark.png
.. |image270| image:: images/matrix_checkmark.png
.. |image271| image:: images/matrix_checkmark.png
.. |image272| image:: images/matrix_checkmark.png
.. |image273| image:: images/matrix_checkmark.png
.. |image274| image:: images/matrix_checkmark.png
.. |image275| image:: images/matrix_checkmark.png
.. |image276| image:: images/matrix_checkmark.png
.. |image277| image:: images/matrix_checkmark.png
.. |image278| image:: images/matrix_checkmark.png
.. |image279| image:: images/matrix_checkmark.png
.. |image280| image:: images/matrix_checkmark.png
.. |image281| image:: images/matrix_checkmark.png
.. |image282| image:: images/matrix_checkmark.png
.. |image283| image:: images/matrix_checkmark.png
.. |image284| image:: images/matrix_checkmark.png
.. |image285| image:: images/matrix_checkmark.png
.. |image286| image:: images/matrix_checkmark.png
.. |image287| image:: images/matrix_checkmark.png
.. |image288| image:: images/matrix_checkmark.png
.. |image289| image:: images/matrix_checkmark.png
.. |image290| image:: images/matrix_checkmark.png
.. |image291| image:: images/matrix_checkmark.png
.. |image292| image:: images/matrix_checkmark.png
.. |image293| image:: images/matrix_checkmark.png
.. |image294| image:: images/matrix_checkmark.png
.. |image295| image:: images/matrix_checkmark.png
.. |image296| image:: images/matrix_checkmark.png
.. |image297| image:: images/matrix_checkmark.png
.. |image298| image:: images/matrix_checkmark.png
.. |image299| image:: images/matrix_checkmark.png
.. |image300| image:: images/matrix_checkmark.png
.. |image301| image:: images/matrix_checkmark.png
.. |image302| image:: images/matrix_checkmark.png
.. |image303| image:: images/matrix_checkmark.png
.. |image304| image:: images/matrix_checkmark.png
.. |image305| image:: images/matrix_checkmark.png
.. |image306| image:: images/matrix_checkmark.png
.. |image307| image:: images/matrix_checkmark.png
.. |image308| image:: images/matrix_checkmark.png
.. |image309| image:: images/matrix_checkmark.png
.. |image310| image:: images/matrix_checkmark.png
.. |image311| image:: images/matrix_checkmark.png
.. |image312| image:: images/matrix_checkmark.png
.. |image313| image:: images/matrix_checkmark.png
.. |image314| image:: images/matrix_checkmark.png
.. |image315| image:: images/matrix_checkmark.png
.. |image316| image:: images/matrix_checkmark.png
.. |image317| image:: images/matrix_checkmark.png
.. |image318| image:: images/matrix_checkmark.png
.. |image319| image:: images/matrix_checkmark.png
.. |image320| image:: images/matrix_checkmark.png
.. |image321| image:: images/matrix_checkmark.png
.. |image322| image:: images/matrix_checkmark.png
.. |image323| image:: images/matrix_checkmark.png
.. |image324| image:: images/matrix_checkmark.png
.. |image325| image:: images/matrix_checkmark.png
.. |image326| image:: images/matrix_checkmark.png
.. |image327| image:: images/matrix_checkmark.png
.. |image328| image:: images/matrix_checkmark.png
.. |image329| image:: images/matrix_checkmark.png
.. |image330| image:: images/matrix_checkmark.png
.. |image331| image:: images/matrix_checkmark.png
.. |image332| image:: images/matrix_checkmark.png
.. |image333| image:: images/matrix_transform.png
.. |image334| image:: images/matrix_sfcgal_enhanced.png
.. |image335| image:: images/matrix_checkmark.png
.. |image336| image:: images/matrix_checkmark.png
.. |image337| image:: images/matrix_sfcgal_enhanced.png
.. |image338| image:: images/matrix_checkmark.png
.. |image339| image:: images/matrix_checkmark.png
.. |image340| image:: images/matrix_checkmark.png
.. |image341| image:: images/matrix_checkmark.png
.. |image342| image:: images/matrix_checkmark.png
.. |image343| image:: images/matrix_checkmark.png
.. |image344| image:: images/matrix_checkmark.png
.. |image345| image:: images/matrix_checkmark.png
.. |image346| image:: images/matrix_checkmark.png
.. |image347| image:: images/matrix_checkmark.png
.. |image348| image:: images/matrix_checkmark.png
.. |image349| image:: images/matrix_checkmark.png
.. |image350| image:: images/matrix_sfcgal_required.png
.. |image351| image:: images/matrix_sfcgal_required.png
.. |image352| image:: images/matrix_sfcgal_required.png
.. |image353| image:: images/matrix_checkmark.png
.. |image354| image:: images/matrix_checkmark.png
.. |image355| image:: images/matrix_checkmark.png
.. |image356| image:: images/matrix_checkmark.png
.. |image357| image:: images/matrix_checkmark.png
.. |image358| image:: images/matrix_checkmark.png
.. |image359| image:: images/matrix_checkmark.png
.. |image360| image:: images/matrix_checkmark.png
.. |image361| image:: images/matrix_checkmark.png
.. |image362| image:: images/matrix_checkmark.png
.. |image363| image:: images/matrix_checkmark.png
.. |image364| image:: images/matrix_sfcgal_enhanced.png
.. |image365| image:: images/matrix_checkmark.png
.. |image366| image:: images/matrix_checkmark.png
.. |image367| image:: images/matrix_checkmark.png
.. |image368| image:: images/matrix_checkmark.png
.. |image369| image:: images/matrix_checkmark.png
.. |image370| image:: images/matrix_checkmark.png
.. |image371| image:: images/matrix_checkmark.png
.. |image372| image:: images/matrix_checkmark.png
.. |image373| image:: images/matrix_checkmark.png
.. |image374| image:: images/matrix_checkmark.png
.. |image375| image:: images/matrix_checkmark.png
.. |image376| image:: images/matrix_checkmark.png
.. |image377| image:: images/matrix_checkmark.png
.. |image378| image:: images/matrix_checkmark.png
.. |image379| image:: images/matrix_checkmark.png
.. |image380| image:: images/matrix_checkmark.png
.. |image381| image:: images/matrix_checkmark.png
.. |image382| image:: images/matrix_checkmark.png
.. |image383| image:: images/matrix_checkmark.png
.. |image384| image:: images/matrix_checkmark.png
.. |image385| image:: images/matrix_checkmark.png
.. |image386| image:: images/matrix_checkmark.png
.. |image387| image:: images/matrix_checkmark.png
.. |image388| image:: images/matrix_checkmark.png
.. |image389| image:: images/matrix_checkmark.png
.. |image390| image:: images/matrix_checkmark.png
.. |image391| image:: images/matrix_checkmark.png
.. |image392| image:: images/matrix_checkmark.png
.. |image393| image:: images/matrix_checkmark.png
.. |image394| image:: images/matrix_checkmark.png
.. |image395| image:: images/matrix_checkmark.png
.. |image396| image:: images/matrix_checkmark.png
.. |image397| image:: images/matrix_checkmark.png
.. |image398| image:: images/matrix_checkmark.png
.. |image399| image:: images/matrix_checkmark.png
.. |image400| image:: images/matrix_checkmark.png
.. |image401| image:: images/matrix_checkmark.png
.. |image402| image:: images/matrix_checkmark.png
.. |image403| image:: images/matrix_checkmark.png
.. |image404| image:: images/matrix_checkmark.png
.. |image405| image:: images/matrix_checkmark.png
.. |image406| image:: images/matrix_checkmark.png
.. |image407| image:: images/matrix_checkmark.png
.. |image408| image:: images/matrix_checkmark.png
.. |image409| image:: images/matrix_checkmark.png
.. |image410| image:: images/matrix_checkmark.png
.. |image411| image:: images/matrix_checkmark.png
.. |image412| image:: images/matrix_checkmark.png
.. |image413| image:: images/matrix_checkmark.png
.. |image414| image:: images/matrix_checkmark.png
.. |image415| image:: images/matrix_checkmark.png
.. |image416| image:: images/matrix_checkmark.png
.. |image417| image:: images/matrix_checkmark.png
.. |image418| image:: images/matrix_checkmark.png
.. |image419| image:: images/matrix_checkmark.png
.. |image420| image:: images/matrix_checkmark.png
.. |image421| image:: images/matrix_checkmark.png
.. |image422| image:: images/matrix_sfcgal_required.png
.. |image423| image:: images/matrix_sfcgal_required.png
.. |image424| image:: images/matrix_sfcgal_required.png
.. |image425| image:: images/matrix_checkmark.png
.. |image426| image:: images/matrix_checkmark.png
.. |image427| image:: images/matrix_checkmark.png
.. |image428| image:: images/matrix_checkmark.png
.. |image429| image:: images/matrix_checkmark.png
.. |image430| image:: images/matrix_checkmark.png
.. |image431| image:: images/matrix_checkmark.png
.. |image432| image:: images/matrix_checkmark.png
.. |image433| image:: images/matrix_checkmark.png
.. |image434| image:: images/matrix_checkmark.png
.. |image435| image:: images/matrix_checkmark.png
.. |image436| image:: images/matrix_checkmark.png
.. |image437| image:: images/matrix_checkmark.png
.. |image438| image:: images/matrix_checkmark.png
.. |image439| image:: images/matrix_checkmark.png
.. |image440| image:: images/matrix_checkmark.png
.. |image441| image:: images/matrix_checkmark.png
.. |image442| image:: images/matrix_checkmark.png
.. |image443| image:: images/matrix_checkmark.png
.. |image444| image:: images/matrix_checkmark.png
.. |image445| image:: images/matrix_checkmark.png
.. |image446| image:: images/matrix_checkmark.png
.. |image447| image:: images/matrix_checkmark.png
.. |image448| image:: images/matrix_checkmark.png
.. |image449| image:: images/matrix_checkmark.png
.. |image450| image:: images/matrix_checkmark.png
.. |image451| image:: images/matrix_checkmark.png
.. |image452| image:: images/matrix_checkmark.png
.. |image453| image:: images/matrix_checkmark.png
.. |image454| image:: images/matrix_checkmark.png
.. |image455| image:: images/matrix_checkmark.png
.. |image456| image:: images/matrix_sfcgal_required.png
.. |image457| image:: images/matrix_sfcgal_required.png
.. |image458| image:: images/matrix_sfcgal_required.png
.. |image459| image:: images/matrix_checkmark.png
.. |image460| image:: images/matrix_checkmark.png
.. |image461| image:: images/matrix_checkmark.png
.. |image462| image:: images/matrix_checkmark.png
.. |image463| image:: images/matrix_checkmark.png
.. |image464| image:: images/matrix_checkmark.png
.. |image465| image:: images/matrix_checkmark.png
.. |image466| image:: images/matrix_checkmark.png
.. |image467| image:: images/matrix_checkmark.png
.. |image468| image:: images/matrix_checkmark.png
.. |image469| image:: images/matrix_checkmark.png
.. |image470| image:: images/matrix_checkmark.png
.. |image471| image:: images/matrix_checkmark.png
.. |image472| image:: images/matrix_checkmark.png
.. |image473| image:: images/matrix_checkmark.png
.. |image474| image:: images/matrix_checkmark.png
.. |image475| image:: images/matrix_checkmark.png
.. |image476| image:: images/matrix_checkmark.png
.. |image477| image:: images/matrix_checkmark.png
.. |image478| image:: images/matrix_checkmark.png
.. |image479| image:: images/matrix_checkmark.png
.. |image480| image:: images/matrix_checkmark.png
.. |image481| image:: images/matrix_checkmark.png
.. |image482| image:: images/matrix_checkmark.png
.. |image483| image:: images/matrix_checkmark.png
.. |image484| image:: images/matrix_checkmark.png
.. |image485| image:: images/matrix_checkmark.png
.. |image486| image:: images/matrix_checkmark.png
.. |image487| image:: images/matrix_checkmark.png
.. |image488| image:: images/matrix_checkmark.png
.. |image489| image:: images/matrix_checkmark.png
.. |image490| image:: images/matrix_checkmark.png
.. |image491| image:: images/matrix_checkmark.png
.. |image492| image:: images/matrix_checkmark.png
.. |image493| image:: images/matrix_checkmark.png
.. |image494| image:: images/matrix_checkmark.png
.. |image495| image:: images/matrix_checkmark.png
.. |image496| image:: images/matrix_checkmark.png
.. |image497| image:: images/matrix_checkmark.png
.. |image498| image:: images/matrix_checkmark.png
.. |image499| image:: images/matrix_checkmark.png
.. |image500| image:: images/matrix_checkmark.png
.. |image501| image:: images/matrix_checkmark.png
.. |image502| image:: images/matrix_checkmark.png
.. |image503| image:: images/matrix_checkmark.png
.. |image504| image:: images/matrix_checkmark.png
.. |image505| image:: images/matrix_checkmark.png
.. |image506| image:: images/matrix_checkmark.png
.. |image507| image:: images/matrix_checkmark.png
.. |image508| image:: images/matrix_checkmark.png
.. |image509| image:: images/matrix_checkmark.png
.. |image510| image:: images/matrix_checkmark.png
.. |image511| image:: images/matrix_checkmark.png
.. |image512| image:: images/matrix_checkmark.png
.. |image513| image:: images/matrix_checkmark.png
.. |image514| image:: images/matrix_checkmark.png
.. |image515| image:: images/matrix_checkmark.png
.. |image516| image:: images/matrix_checkmark.png
.. |image517| image:: images/matrix_checkmark.png
.. |image518| image:: images/matrix_checkmark.png
.. |image519| image:: images/matrix_checkmark.png
.. |image520| image:: images/matrix_checkmark.png
.. |image521| image:: images/matrix_checkmark.png
.. |image522| image:: images/matrix_checkmark.png
.. |image523| image:: images/matrix_checkmark.png
.. |image524| image:: images/matrix_checkmark.png
.. |image525| image:: images/matrix_checkmark.png
.. |image526| image:: images/matrix_checkmark.png
.. |image527| image:: images/matrix_checkmark.png
.. |image528| image:: images/matrix_checkmark.png
.. |image529| image:: images/matrix_checkmark.png
.. |image530| image:: images/matrix_checkmark.png
.. |image531| image:: images/matrix_checkmark.png
.. |image532| image:: images/matrix_checkmark.png
.. |image533| image:: images/matrix_checkmark.png
.. |image534| image:: images/matrix_checkmark.png
.. |image535| image:: images/matrix_checkmark.png
.. |image536| image:: images/matrix_checkmark.png
.. |image537| image:: images/matrix_checkmark.png
.. |image538| image:: images/matrix_checkmark.png
.. |image539| image:: images/matrix_checkmark.png
.. |image540| image:: images/matrix_checkmark.png
.. |image541| image:: images/matrix_checkmark.png
.. |image542| image:: images/matrix_checkmark.png
.. |image543| image:: images/matrix_checkmark.png
.. |image544| image:: images/matrix_checkmark.png
.. |image545| image:: images/matrix_checkmark.png
.. |image546| image:: images/matrix_checkmark.png
.. |image547| image:: images/matrix_checkmark.png
.. |image548| image:: images/matrix_sfcgal_required.png
.. |image549| image:: images/matrix_sfcgal_required.png
.. |image550| image:: images/matrix_sfcgal_required.png
.. |image551| image:: images/matrix_checkmark.png
.. |image552| image:: images/matrix_checkmark.png
.. |image553| image:: images/matrix_checkmark.png
.. |image554| image:: images/matrix_checkmark.png
.. |image555| image:: images/matrix_checkmark.png
.. |image556| image:: images/matrix_checkmark.png
.. |image557| image:: images/matrix_sfcgal_required.png
.. |image558| image:: images/matrix_sfcgal_required.png
.. |image559| image:: images/matrix_sfcgal_required.png
.. |image560| image:: images/matrix_checkmark.png
.. |image561| image:: images/matrix_checkmark.png
.. |image562| image:: images/matrix_checkmark.png
.. |image563| image:: images/matrix_checkmark.png
.. |image564| image:: images/matrix_checkmark.png
.. |image565| image:: images/matrix_checkmark.png
.. |image566| image:: images/matrix_checkmark.png
.. |image567| image:: images/matrix_checkmark.png
.. |image568| image:: images/matrix_checkmark.png
.. |image569| image:: images/matrix_checkmark.png
.. |image570| image:: images/matrix_checkmark.png
.. |image571| image:: images/matrix_checkmark.png
.. |image572| image:: images/matrix_checkmark.png
.. |image573| image:: images/matrix_checkmark.png
.. |image574| image:: images/matrix_checkmark.png
.. |image575| image:: images/matrix_checkmark.png
.. |image576| image:: images/matrix_checkmark.png
.. |image577| image:: images/matrix_checkmark.png
.. |image578| image:: images/matrix_checkmark.png
.. |image579| image:: images/matrix_checkmark.png
.. |image580| image:: images/matrix_checkmark.png
.. |image581| image:: images/matrix_checkmark.png
.. |image582| image:: images/matrix_checkmark.png
.. |image583| image:: images/matrix_checkmark.png
.. |image584| image:: images/matrix_checkmark.png
.. |image585| image:: images/matrix_autocast.png
.. |image586| image:: images/matrix_checkmark.png
.. |image587| image:: images/matrix_checkmark.png
.. |image588| image:: images/matrix_autocast.png
.. |image589| image:: images/matrix_checkmark.png
.. |image590| image:: images/matrix_checkmark.png
.. |image591| image:: images/matrix_checkmark.png
.. |image592| image:: images/matrix_checkmark.png
.. |image593| image:: images/matrix_checkmark.png
.. |image594| image:: images/matrix_autocast.png
.. |image595| image:: images/matrix_checkmark.png
.. |image596| image:: images/matrix_checkmark.png
.. |image597| image:: images/matrix_autocast.png
.. |image598| image:: images/matrix_checkmark.png
.. |image599| image:: images/matrix_checkmark.png
.. |image600| image:: images/matrix_checkmark.png
.. |image601| image:: images/matrix_checkmark.png
.. |image602| image:: images/matrix_checkmark.png
.. |image603| image:: images/matrix_autocast.png
.. |image604| image:: images/matrix_checkmark.png
.. |image605| image:: images/matrix_checkmark.png
.. |image606| image:: images/matrix_autocast.png
.. |image607| image:: images/matrix_checkmark.png
.. |image608| image:: images/matrix_checkmark.png
.. |image609| image:: images/matrix_checkmark.png
.. |image610| image:: images/matrix_checkmark.png
.. |image611| image:: images/matrix_checkmark.png
.. |image612| image:: images/matrix_checkmark.png
.. |image613| image:: images/matrix_checkmark.png
.. |image614| image:: images/matrix_checkmark.png
.. |image615| image:: images/matrix_checkmark.png
.. |image616| image:: images/matrix_checkmark.png
.. |image617| image:: images/matrix_checkmark.png
.. |image618| image:: images/matrix_checkmark.png
.. |image619| image:: images/matrix_checkmark.png
.. |image620| image:: images/matrix_checkmark.png
.. |image621| image:: images/matrix_checkmark.png
.. |image622| image:: images/matrix_checkmark.png
.. |image623| image:: images/matrix_checkmark.png
.. |image624| image:: images/matrix_checkmark.png
.. |image625| image:: images/matrix_checkmark.png
.. |image626| image:: images/matrix_checkmark.png
.. |image627| image:: images/matrix_checkmark.png
.. |image628| image:: images/matrix_checkmark.png
.. |image629| image:: images/matrix_checkmark.png
.. |image630| image:: images/matrix_checkmark.png
.. |image631| image:: images/matrix_checkmark.png
.. |image632| image:: images/matrix_checkmark.png
.. |image633| image:: images/matrix_checkmark.png
.. |image634| image:: images/matrix_checkmark.png
.. |image635| image:: images/matrix_checkmark.png
.. |image636| image:: images/matrix_checkmark.png
.. |image637| image:: images/matrix_checkmark.png
.. |image638| image:: images/matrix_checkmark.png
.. |image639| image:: images/matrix_checkmark.png
.. |image640| image:: images/matrix_checkmark.png
.. |image641| image:: images/matrix_checkmark.png
.. |image642| image:: images/matrix_checkmark.png
.. |image643| image:: images/matrix_checkmark.png
.. |image644| image:: images/matrix_checkmark.png
.. |image645| image:: images/matrix_checkmark.png
.. |image646| image:: images/matrix_checkmark.png
.. |image647| image:: images/matrix_checkmark.png
.. |image648| image:: images/matrix_sfcgal_enhanced.png
.. |image649| image:: images/matrix_sfcgal_enhanced.png
.. |image650| image:: images/matrix_sfcgal_enhanced.png
.. |image651| image:: images/matrix_checkmark.png
.. |image652| image:: images/matrix_checkmark.png
.. |image653| image:: images/matrix_checkmark.png
.. |image654| image:: images/matrix_checkmark.png
.. |image655| image:: images/matrix_checkmark.png
.. |image656| image:: images/matrix_checkmark.png
.. |image657| image:: images/matrix_checkmark.png
.. |image658| image:: images/matrix_checkmark.png
.. |image659| image:: images/matrix_checkmark.png
.. |image660| image:: images/matrix_checkmark.png
.. |image661| image:: images/matrix_checkmark.png
.. |image662| image:: images/matrix_checkmark.png
.. |image663| image:: images/matrix_checkmark.png
.. |image664| image:: images/matrix_checkmark.png
.. |image665| image:: images/matrix_checkmark.png
.. |image666| image:: images/matrix_checkmark.png
.. |image667| image:: images/matrix_checkmark.png
.. |image668| image:: images/matrix_checkmark.png
.. |image669| image:: images/matrix_checkmark.png
.. |image670| image:: images/matrix_checkmark.png
.. |image671| image:: images/matrix_checkmark.png
.. |image672| image:: images/matrix_checkmark.png
.. |image673| image:: images/matrix_checkmark.png
.. |image674| image:: images/matrix_checkmark.png
.. |image675| image:: images/matrix_checkmark.png
.. |image676| image:: images/matrix_checkmark.png
.. |image677| image:: images/matrix_checkmark.png
.. |image678| image:: images/matrix_checkmark.png
.. |image679| image:: images/matrix_checkmark.png
.. |image680| image:: images/matrix_checkmark.png
.. |image681| image:: images/matrix_checkmark.png
.. |image682| image:: images/matrix_checkmark.png
.. |image683| image:: images/matrix_checkmark.png
.. |image684| image:: images/matrix_checkmark.png
.. |image685| image:: images/matrix_checkmark.png
.. |image686| image:: images/matrix_checkmark.png
.. |image687| image:: images/matrix_checkmark.png
.. |image688| image:: images/matrix_checkmark.png
.. |image689| image:: images/matrix_checkmark.png
.. |image690| image:: images/matrix_checkmark.png
.. |image691| image:: images/matrix_checkmark.png
.. |image692| image:: images/matrix_checkmark.png
.. |image693| image:: images/matrix_sfcgal_enhanced.png
.. |image694| image:: images/matrix_sfcgal_enhanced.png
.. |image695| image:: images/matrix_checkmark.png
.. |image696| image:: images/matrix_checkmark.png
.. |image697| image:: images/matrix_checkmark.png
.. |image698| image:: images/matrix_checkmark.png
.. |image699| image:: images/matrix_checkmark.png
.. |image700| image:: images/matrix_checkmark.png
.. |image701| image:: images/matrix_checkmark.png
.. |image702| image:: images/matrix_checkmark.png
.. |image703| image:: images/matrix_checkmark.png
.. |image704| image:: images/matrix_checkmark.png
.. |image705| image:: images/matrix_checkmark.png
.. |image706| image:: images/matrix_checkmark.png
.. |image707| image:: images/matrix_checkmark.png
.. |image708| image:: images/matrix_checkmark.png
.. |image709| image:: images/matrix_checkmark.png
.. |image710| image:: images/matrix_checkmark.png
.. |image711| image:: images/matrix_checkmark.png
.. |image712| image:: images/matrix_checkmark.png
.. |image713| image:: images/matrix_checkmark.png
.. |image714| image:: images/matrix_checkmark.png
.. |image715| image:: images/matrix_checkmark.png
.. |image716| image:: images/matrix_checkmark.png
.. |image717| image:: images/matrix_checkmark.png
.. |image718| image:: images/matrix_checkmark.png
.. |image719| image:: images/matrix_checkmark.png
.. |image720| image:: images/matrix_checkmark.png
.. |image721| image:: images/matrix_checkmark.png
.. |image722| image:: images/matrix_checkmark.png
.. |image723| image:: images/matrix_checkmark.png
.. |image724| image:: images/matrix_checkmark.png
.. |image725| image:: images/matrix_checkmark.png
.. |image726| image:: images/matrix_checkmark.png
.. |image727| image:: images/matrix_checkmark.png
.. |image728| image:: images/matrix_checkmark.png
.. |image729| image:: images/matrix_checkmark.png
.. |image730| image:: images/matrix_checkmark.png
.. |image731| image:: images/matrix_checkmark.png
.. |image732| image:: images/matrix_checkmark.png
.. |image733| image:: images/matrix_checkmark.png
.. |image734| image:: images/matrix_checkmark.png
.. |image735| image:: images/matrix_checkmark.png
.. |image736| image:: images/matrix_checkmark.png
.. |image737| image:: images/matrix_checkmark.png
.. |image738| image:: images/matrix_checkmark.png
.. |image739| image:: images/matrix_checkmark.png
.. |image740| image:: images/matrix_checkmark.png
.. |image741| image:: images/matrix_checkmark.png
.. |image742| image:: images/matrix_checkmark.png
.. |image743| image:: images/matrix_checkmark.png
.. |image744| image:: images/matrix_checkmark.png
.. |image745| image:: images/matrix_checkmark.png
.. |image746| image:: images/matrix_autocast.png
.. |image747| image:: images/matrix_checkmark.png
.. |image748| image:: images/matrix_transform.png
.. |image749| image:: images/matrix_checkmark.png
.. |image750| image:: images/matrix_checkmark.png
.. |image751| image:: images/matrix_checkmark.png
.. |image752| image:: images/matrix_checkmark.png
.. |image753| image:: images/matrix_checkmark.png
.. |image754| image:: images/matrix_checkmark.png
.. |image755| image:: images/matrix_checkmark.png
.. |image756| image:: images/matrix_checkmark.png
.. |image757| image:: images/matrix_checkmark.png
.. |image758| image:: images/matrix_checkmark.png
.. |image759| image:: images/matrix_checkmark.png
.. |image760| image:: images/matrix_checkmark.png
.. |image761| image:: images/matrix_checkmark.png
.. |image762| image:: images/matrix_checkmark.png
.. |image763| image:: images/matrix_checkmark.png
.. |image764| image:: images/matrix_checkmark.png
.. |image765| image:: images/matrix_checkmark.png
.. |image766| image:: images/matrix_checkmark.png
.. |image767| image:: images/matrix_checkmark.png
.. |image768| image:: images/matrix_checkmark.png
.. |image769| image:: images/matrix_checkmark.png
.. |image770| image:: images/matrix_checkmark.png
.. |image771| image:: images/matrix_checkmark.png
.. |image772| image:: images/matrix_checkmark.png
.. |image773| image:: images/matrix_checkmark.png
.. |image774| image:: images/matrix_checkmark.png
.. |image775| image:: images/matrix_checkmark.png
.. |image776| image:: images/matrix_checkmark.png
.. |image777| image:: images/matrix_checkmark.png
.. |image778| image:: images/matrix_checkmark.png
.. |image779| image:: images/matrix_checkmark.png
.. |image780| image:: images/matrix_checkmark.png
.. |image781| image:: images/matrix_checkmark.png
.. |image782| image:: images/matrix_checkmark.png
.. |image783| image:: images/matrix_checkmark.png
.. |image784| image:: images/matrix_checkmark.png
.. |image785| image:: images/matrix_checkmark.png
.. |image786| image:: images/matrix_checkmark.png
.. |image787| image:: images/matrix_checkmark.png
.. |image788| image:: images/matrix_checkmark.png
.. |image789| image:: images/matrix_checkmark.png
.. |image790| image:: images/matrix_checkmark.png
.. |image791| image:: images/matrix_checkmark.png
.. |image792| image:: images/matrix_checkmark.png
.. |image793| image:: images/matrix_checkmark.png
.. |image794| image:: images/matrix_checkmark.png
.. |image795| image:: images/matrix_checkmark.png
.. |image796| image:: images/matrix_checkmark.png
.. |image797| image:: images/matrix_checkmark.png
.. |image798| image:: images/matrix_checkmark.png
.. |image799| image:: images/matrix_checkmark.png
.. |image800| image:: images/matrix_sfcgal_enhanced.png
.. |image801| image:: images/matrix_checkmark.png
.. |image802| image:: images/matrix_checkmark.png
.. |image803| image:: images/matrix_checkmark.png
.. |image804| image:: images/matrix_checkmark.png
.. |image805| image:: images/matrix_checkmark.png
.. |image806| image:: images/matrix_checkmark.png
.. |image807| image:: images/matrix_checkmark.png
.. |image808| image:: images/matrix_checkmark.png
.. |image809| image:: images/matrix_checkmark.png
.. |image810| image:: images/matrix_checkmark.png
.. |image811| image:: images/matrix_checkmark.png
.. |image812| image:: images/matrix_checkmark.png
.. |image813| image:: images/matrix_checkmark.png
.. |image814| image:: images/matrix_checkmark.png
.. |image815| image:: images/matrix_checkmark.png
.. |image816| image:: images/matrix_checkmark.png
.. |image817| image:: images/matrix_checkmark.png
.. |image818| image:: images/matrix_checkmark.png
.. |image819| image:: images/matrix_checkmark.png
.. |image820| image:: images/matrix_checkmark.png
.. |image821| image:: images/matrix_checkmark.png
.. |image822| image:: images/matrix_autocast.png
.. |image823| image:: images/matrix_checkmark.png
.. |image824| image:: images/matrix_checkmark.png
.. |image825| image:: images/matrix_checkmark.png
.. |image826| image:: images/matrix_checkmark.png
.. |image827| image:: images/matrix_checkmark.png
.. |image828| image:: images/matrix_checkmark.png
.. |image829| image:: images/matrix_checkmark.png
.. |image830| image:: images/matrix_checkmark.png
.. |image831| image:: images/matrix_checkmark.png
.. |image832| image:: images/matrix_checkmark.png
.. |image833| image:: images/matrix_checkmark.png
.. |image834| image:: images/matrix_sfcgal_required.png
.. |image835| image:: images/matrix_sfcgal_required.png
.. |image836| image:: images/matrix_sfcgal_required.png
.. |image837| image:: images/matrix_checkmark.png
.. |image838| image:: images/matrix_checkmark.png
.. |image839| image:: images/matrix_checkmark.png
.. |image840| image:: images/matrix_checkmark.png
.. |image841| image:: images/matrix_checkmark.png
.. |image842| image:: images/matrix_checkmark.png
.. |image843| image:: images/matrix_sfcgal_required.png
.. |image844| image:: images/matrix_sfcgal_required.png
.. |image845| image:: images/matrix_sfcgal_required.png
.. |image846| image:: images/matrix_checkmark.png
.. |image847| image:: images/matrix_checkmark.png
.. |image848| image:: images/matrix_checkmark.png
.. |image849| image:: images/matrix_checkmark.png
.. |image850| image:: images/matrix_checkmark.png
.. |image851| image:: images/matrix_checkmark.png
.. |image852| image:: images/matrix_checkmark.png
.. |image853| image:: images/matrix_checkmark.png
.. |image854| image:: images/matrix_checkmark.png
.. |image855| image:: images/matrix_checkmark.png
.. |image856| image:: images/matrix_checkmark.png
.. |image857| image:: images/matrix_checkmark.png
.. |image858| image:: images/matrix_checkmark.png
.. |image859| image:: images/matrix_checkmark.png
.. |image860| image:: images/matrix_checkmark.png
.. |image861| image:: images/matrix_checkmark.png
.. |image862| image:: images/matrix_checkmark.png
.. |image863| image:: images/matrix_checkmark.png
.. |image864| image:: images/matrix_checkmark.png
.. |image865| image:: images/matrix_checkmark.png
.. |image866| image:: images/matrix_checkmark.png
.. |image867| image:: images/matrix_checkmark.png
.. |image868| image:: images/matrix_checkmark.png
.. |image869| image:: images/matrix_checkmark.png
.. |image870| image:: images/matrix_checkmark.png
.. |image871| image:: images/matrix_checkmark.png
.. |image872| image:: images/matrix_checkmark.png
.. |image873| image:: images/matrix_checkmark.png
.. |image874| image:: images/matrix_checkmark.png
.. |image875| image:: images/matrix_checkmark.png
.. |image876| image:: images/matrix_checkmark.png
.. |image877| image:: images/matrix_checkmark.png
.. |image878| image:: images/matrix_checkmark.png
.. |image879| image:: images/matrix_checkmark.png
.. |image880| image:: images/matrix_checkmark.png
.. |image881| image:: images/matrix_checkmark.png
.. |image882| image:: images/matrix_checkmark.png
.. |image883| image:: images/matrix_checkmark.png
.. |image884| image:: images/matrix_checkmark.png
.. |image885| image:: images/matrix_checkmark.png
.. |image886| image:: images/matrix_checkmark.png
.. |image887| image:: images/matrix_checkmark.png
.. |image888| image:: images/matrix_checkmark.png
.. |image889| image:: images/matrix_checkmark.png
.. |image890| image:: images/matrix_checkmark.png
.. |image891| image:: images/matrix_checkmark.png
.. |image892| image:: images/matrix_checkmark.png
.. |image893| image:: images/matrix_checkmark.png
.. |image894| image:: images/matrix_checkmark.png
.. |image895| image:: images/matrix_checkmark.png
.. |image896| image:: images/matrix_checkmark.png
.. |image897| image:: images/matrix_checkmark.png
.. |image898| image:: images/matrix_checkmark.png
.. |image899| image:: images/matrix_checkmark.png
.. |image900| image:: images/matrix_checkmark.png
.. |image901| image:: images/matrix_checkmark.png
.. |image902| image:: images/matrix_checkmark.png
.. |image903| image:: images/matrix_checkmark.png
.. |image904| image:: images/matrix_checkmark.png
.. |image905| image:: images/matrix_checkmark.png
.. |image906| image:: images/matrix_checkmark.png
.. |image907| image:: images/matrix_checkmark.png
.. |image908| image:: images/matrix_checkmark.png
.. |image909| image:: images/matrix_checkmark.png
.. |image910| image:: images/matrix_checkmark.png
.. |image911| image:: images/matrix_checkmark.png
.. |image912| image:: images/matrix_checkmark.png
.. |image913| image:: images/matrix_checkmark.png
.. |image914| image:: images/matrix_checkmark.png
.. |image915| image:: images/matrix_checkmark.png
.. |image916| image:: images/matrix_checkmark.png
.. |image917| image:: images/matrix_checkmark.png
.. |image918| image:: images/matrix_checkmark.png
.. |image919| image:: images/matrix_checkmark.png
.. |image920| image:: images/matrix_checkmark.png
.. |image921| image:: images/matrix_checkmark.png
.. |image922| image:: images/matrix_checkmark.png
.. |image923| image:: images/matrix_checkmark.png
.. |image924| image:: images/matrix_checkmark.png
.. |image925| image:: images/matrix_checkmark.png
.. |image926| image:: images/matrix_checkmark.png
.. |image927| image:: images/matrix_checkmark.png
.. |image928| image:: images/matrix_checkmark.png
.. |image929| image:: images/matrix_checkmark.png
.. |image930| image:: images/matrix_checkmark.png
.. |image931| image:: images/matrix_checkmark.png
.. |image932| image:: images/matrix_checkmark.png
.. |image933| image:: images/matrix_checkmark.png
.. |image934| image:: images/matrix_checkmark.png
.. |image935| image:: images/matrix_checkmark.png
.. |image936| image:: images/matrix_checkmark.png
.. |image937| image:: images/matrix_checkmark.png
.. |image938| image:: images/matrix_checkmark.png
.. |image939| image:: images/matrix_checkmark.png
.. |image940| image:: images/matrix_checkmark.png
.. |image941| image:: images/matrix_checkmark.png
.. |image942| image:: images/matrix_checkmark.png
.. |image943| image:: images/matrix_checkmark.png
.. |image944| image:: images/matrix_checkmark.png
.. |image945| image:: images/matrix_checkmark.png
.. |image946| image:: images/matrix_checkmark.png
.. |image947| image:: images/matrix_checkmark.png
.. |image948| image:: images/matrix_checkmark.png
.. |image949| image:: images/matrix_checkmark.png
.. |image950| image:: images/matrix_checkmark.png
.. |image951| image:: images/matrix_transform.png
.. |image952| image:: images/matrix_sfcgal_enhanced.png
.. |image953| image:: images/matrix_checkmark.png
.. |image954| image:: images/matrix_checkmark.png
.. |image955| image:: images/matrix_sfcgal_enhanced.png
.. |image956| image:: images/matrix_checkmark.png
.. |image957| image:: images/matrix_checkmark.png
.. |image958| image:: images/matrix_checkmark.png
.. |image959| image:: images/matrix_checkmark.png
.. |image960| image:: images/matrix_checkmark.png
.. |image961| image:: images/matrix_checkmark.png
.. |image962| image:: images/matrix_checkmark.png
.. |image963| image:: images/matrix_checkmark.png
.. |image964| image:: images/matrix_checkmark.png
.. |image965| image:: images/matrix_checkmark.png
.. |image966| image:: images/matrix_checkmark.png
.. |image967| image:: images/matrix_checkmark.png
.. |image968| image:: images/matrix_sfcgal_required.png
.. |image969| image:: images/matrix_sfcgal_required.png
.. |image970| image:: images/matrix_sfcgal_required.png
.. |image971| image:: images/matrix_checkmark.png
.. |image972| image:: images/matrix_checkmark.png
.. |image973| image:: images/matrix_checkmark.png
.. |image974| image:: images/matrix_checkmark.png
.. |image975| image:: images/matrix_checkmark.png
.. |image976| image:: images/matrix_checkmark.png
.. |image977| image:: images/matrix_checkmark.png
.. |image978| image:: images/matrix_checkmark.png
.. |image979| image:: images/matrix_checkmark.png
.. |image980| image:: images/matrix_checkmark.png
.. |image981| image:: images/matrix_checkmark.png
.. |image982| image:: images/matrix_sfcgal_enhanced.png
.. |image983| image:: images/matrix_checkmark.png
.. |image984| image:: images/matrix_checkmark.png
.. |image985| image:: images/matrix_checkmark.png
.. |image986| image:: images/matrix_checkmark.png
.. |image987| image:: images/matrix_checkmark.png
.. |image988| image:: images/matrix_checkmark.png
.. |image989| image:: images/matrix_checkmark.png
.. |image990| image:: images/matrix_checkmark.png
.. |image991| image:: images/matrix_checkmark.png
.. |image992| image:: images/matrix_checkmark.png
.. |image993| image:: images/matrix_checkmark.png
.. |image994| image:: images/matrix_checkmark.png
.. |image995| image:: images/matrix_checkmark.png
.. |image996| image:: images/matrix_checkmark.png
.. |image997| image:: images/matrix_checkmark.png
.. |image998| image:: images/matrix_checkmark.png
.. |image999| image:: images/matrix_checkmark.png
.. |image1000| image:: images/matrix_checkmark.png
.. |image1001| image:: images/matrix_checkmark.png
.. |image1002| image:: images/matrix_checkmark.png
.. |image1003| image:: images/matrix_checkmark.png
.. |image1004| image:: images/matrix_checkmark.png
.. |image1005| image:: images/matrix_checkmark.png
.. |image1006| image:: images/matrix_checkmark.png
.. |image1007| image:: images/matrix_checkmark.png
.. |image1008| image:: images/matrix_checkmark.png
.. |image1009| image:: images/matrix_checkmark.png
.. |image1010| image:: images/matrix_checkmark.png
.. |image1011| image:: images/matrix_checkmark.png
.. |image1012| image:: images/matrix_checkmark.png
.. |image1013| image:: images/matrix_checkmark.png
.. |image1014| image:: images/matrix_checkmark.png
.. |image1015| image:: images/matrix_checkmark.png
.. |image1016| image:: images/matrix_checkmark.png
.. |image1017| image:: images/matrix_checkmark.png
.. |image1018| image:: images/matrix_checkmark.png
.. |image1019| image:: images/matrix_checkmark.png
.. |image1020| image:: images/matrix_checkmark.png
.. |image1021| image:: images/matrix_checkmark.png
.. |image1022| image:: images/matrix_checkmark.png
.. |image1023| image:: images/matrix_checkmark.png
.. |image1024| image:: images/matrix_checkmark.png
.. |image1025| image:: images/matrix_checkmark.png
.. |image1026| image:: images/matrix_checkmark.png
.. |image1027| image:: images/matrix_checkmark.png
.. |image1028| image:: images/matrix_checkmark.png
.. |image1029| image:: images/matrix_checkmark.png
.. |image1030| image:: images/matrix_checkmark.png
.. |image1031| image:: images/matrix_checkmark.png
.. |image1032| image:: images/matrix_checkmark.png
.. |image1033| image:: images/matrix_checkmark.png
.. |image1034| image:: images/matrix_checkmark.png
.. |image1035| image:: images/matrix_checkmark.png
.. |image1036| image:: images/matrix_checkmark.png
.. |image1037| image:: images/matrix_checkmark.png
.. |image1038| image:: images/matrix_checkmark.png
.. |image1039| image:: images/matrix_checkmark.png
.. |image1040| image:: images/matrix_sfcgal_required.png
.. |image1041| image:: images/matrix_sfcgal_required.png
.. |image1042| image:: images/matrix_sfcgal_required.png
.. |image1043| image:: images/matrix_checkmark.png
.. |image1044| image:: images/matrix_checkmark.png
.. |image1045| image:: images/matrix_checkmark.png
.. |image1046| image:: images/matrix_checkmark.png
.. |image1047| image:: images/matrix_checkmark.png
.. |image1048| image:: images/matrix_checkmark.png
.. |image1049| image:: images/matrix_checkmark.png
.. |image1050| image:: images/matrix_checkmark.png
.. |image1051| image:: images/matrix_checkmark.png
.. |image1052| image:: images/matrix_checkmark.png
.. |image1053| image:: images/matrix_checkmark.png
.. |image1054| image:: images/matrix_checkmark.png
.. |image1055| image:: images/matrix_checkmark.png
.. |image1056| image:: images/matrix_checkmark.png
.. |image1057| image:: images/matrix_checkmark.png
.. |image1058| image:: images/matrix_checkmark.png
.. |image1059| image:: images/matrix_checkmark.png
.. |image1060| image:: images/matrix_checkmark.png
.. |image1061| image:: images/matrix_checkmark.png
.. |image1062| image:: images/matrix_checkmark.png
.. |image1063| image:: images/matrix_checkmark.png
.. |image1064| image:: images/matrix_checkmark.png
.. |image1065| image:: images/matrix_checkmark.png
.. |image1066| image:: images/matrix_checkmark.png
.. |image1067| image:: images/matrix_checkmark.png
.. |image1068| image:: images/matrix_checkmark.png
.. |image1069| image:: images/matrix_checkmark.png
.. |image1070| image:: images/matrix_checkmark.png
.. |image1071| image:: images/matrix_checkmark.png
.. |image1072| image:: images/matrix_checkmark.png
.. |image1073| image:: images/matrix_checkmark.png
.. |image1074| image:: images/matrix_sfcgal_required.png
.. |image1075| image:: images/matrix_sfcgal_required.png
.. |image1076| image:: images/matrix_sfcgal_required.png
.. |image1077| image:: images/matrix_checkmark.png
.. |image1078| image:: images/matrix_checkmark.png
.. |image1079| image:: images/matrix_checkmark.png
.. |image1080| image:: images/matrix_checkmark.png
.. |image1081| image:: images/matrix_checkmark.png
.. |image1082| image:: images/matrix_checkmark.png
.. |image1083| image:: images/matrix_checkmark.png
.. |image1084| image:: images/matrix_checkmark.png
.. |image1085| image:: images/matrix_checkmark.png
.. |image1086| image:: images/matrix_checkmark.png
.. |image1087| image:: images/matrix_checkmark.png
.. |image1088| image:: images/matrix_checkmark.png
.. |image1089| image:: images/matrix_checkmark.png
.. |image1090| image:: images/matrix_checkmark.png
.. |image1091| image:: images/matrix_checkmark.png
.. |image1092| image:: images/matrix_checkmark.png
.. |image1093| image:: images/matrix_checkmark.png
.. |image1094| image:: images/matrix_checkmark.png
.. |image1095| image:: images/matrix_checkmark.png
.. |image1096| image:: images/matrix_checkmark.png
.. |image1097| image:: images/matrix_checkmark.png
.. |image1098| image:: images/matrix_checkmark.png
.. |image1099| image:: images/matrix_checkmark.png
.. |image1100| image:: images/matrix_checkmark.png
.. |image1101| image:: images/matrix_checkmark.png
.. |image1102| image:: images/matrix_checkmark.png
.. |image1103| image:: images/matrix_checkmark.png
.. |image1104| image:: images/matrix_checkmark.png
.. |image1105| image:: images/matrix_checkmark.png
.. |image1106| image:: images/matrix_checkmark.png
.. |image1107| image:: images/matrix_checkmark.png
.. |image1108| image:: images/matrix_checkmark.png
.. |image1109| image:: images/matrix_checkmark.png
.. |image1110| image:: images/matrix_checkmark.png
.. |image1111| image:: images/matrix_checkmark.png
.. |image1112| image:: images/matrix_checkmark.png
.. |image1113| image:: images/matrix_checkmark.png
.. |image1114| image:: images/matrix_checkmark.png
.. |image1115| image:: images/matrix_checkmark.png
.. |image1116| image:: images/matrix_checkmark.png
.. |image1117| image:: images/matrix_checkmark.png
.. |image1118| image:: images/matrix_checkmark.png
.. |image1119| image:: images/matrix_checkmark.png
.. |image1120| image:: images/matrix_checkmark.png
.. |image1121| image:: images/matrix_checkmark.png
.. |image1122| image:: images/matrix_checkmark.png
.. |image1123| image:: images/matrix_checkmark.png
.. |image1124| image:: images/matrix_checkmark.png
.. |image1125| image:: images/matrix_checkmark.png
.. |image1126| image:: images/matrix_checkmark.png
.. |image1127| image:: images/matrix_checkmark.png
.. |image1128| image:: images/matrix_checkmark.png
.. |image1129| image:: images/matrix_checkmark.png
.. |image1130| image:: images/matrix_checkmark.png
.. |image1131| image:: images/matrix_checkmark.png
.. |image1132| image:: images/matrix_checkmark.png
.. |image1133| image:: images/matrix_checkmark.png
.. |image1134| image:: images/matrix_checkmark.png
.. |image1135| image:: images/matrix_checkmark.png
.. |image1136| image:: images/matrix_checkmark.png
.. |image1137| image:: images/matrix_checkmark.png
.. |image1138| image:: images/matrix_checkmark.png
.. |image1139| image:: images/matrix_checkmark.png
.. |image1140| image:: images/matrix_checkmark.png
.. |image1141| image:: images/matrix_checkmark.png
.. |image1142| image:: images/matrix_checkmark.png
.. |image1143| image:: images/matrix_checkmark.png
.. |image1144| image:: images/matrix_checkmark.png
.. |image1145| image:: images/matrix_checkmark.png
.. |image1146| image:: images/matrix_checkmark.png
.. |image1147| image:: images/matrix_checkmark.png
.. |image1148| image:: images/matrix_checkmark.png
.. |image1149| image:: images/matrix_checkmark.png
.. |image1150| image:: images/matrix_checkmark.png
.. |image1151| image:: images/matrix_checkmark.png
.. |image1152| image:: images/matrix_checkmark.png
.. |image1153| image:: images/matrix_checkmark.png
.. |image1154| image:: images/matrix_checkmark.png
.. |image1155| image:: images/matrix_checkmark.png
.. |image1156| image:: images/matrix_checkmark.png
.. |image1157| image:: images/matrix_checkmark.png
.. |image1158| image:: images/matrix_checkmark.png
.. |image1159| image:: images/matrix_checkmark.png
.. |image1160| image:: images/matrix_checkmark.png
.. |image1161| image:: images/matrix_checkmark.png
.. |image1162| image:: images/matrix_checkmark.png
.. |image1163| image:: images/matrix_checkmark.png
.. |image1164| image:: images/matrix_checkmark.png
.. |image1165| image:: images/matrix_checkmark.png
.. |image1166| image:: images/matrix_sfcgal_required.png
.. |image1167| image:: images/matrix_sfcgal_required.png
.. |image1168| image:: images/matrix_sfcgal_required.png
.. |image1169| image:: images/matrix_checkmark.png
.. |image1170| image:: images/matrix_checkmark.png
.. |image1171| image:: images/matrix_checkmark.png
.. |image1172| image:: images/matrix_checkmark.png
.. |image1173| image:: images/matrix_checkmark.png
.. |image1174| image:: images/matrix_checkmark.png
.. |image1175| image:: images/matrix_sfcgal_required.png
.. |image1176| image:: images/matrix_sfcgal_required.png
.. |image1177| image:: images/matrix_sfcgal_required.png
.. |image1178| image:: images/matrix_checkmark.png
.. |image1179| image:: images/matrix_checkmark.png
.. |image1180| image:: images/matrix_checkmark.png
.. |image1181| image:: images/matrix_checkmark.png
.. |image1182| image:: images/matrix_checkmark.png
.. |image1183| image:: images/matrix_checkmark.png
.. |image1184| image:: images/matrix_checkmark.png
.. |image1185| image:: images/matrix_checkmark.png
.. |image1186| image:: images/matrix_checkmark.png
.. |image1187| image:: images/matrix_checkmark.png
.. |image1188| image:: images/matrix_checkmark.png
.. |image1189| image:: images/matrix_checkmark.png
.. |image1190| image:: images/matrix_checkmark.png
.. |image1191| image:: images/matrix_checkmark.png
.. |image1192| image:: images/matrix_checkmark.png
.. |image1193| image:: images/matrix_checkmark.png
.. |image1194| image:: images/matrix_checkmark.png
.. |image1195| image:: images/matrix_checkmark.png
.. |image1196| image:: images/matrix_checkmark.png
.. |image1197| image:: images/matrix_checkmark.png
.. |image1198| image:: images/matrix_checkmark.png
.. |image1199| image:: images/matrix_checkmark.png
.. |image1200| image:: images/matrix_checkmark.png
.. |image1201| image:: images/matrix_checkmark.png
.. |image1202| image:: images/matrix_checkmark.png
.. |image1203| image:: images/matrix_autocast.png
.. |image1204| image:: images/matrix_checkmark.png
.. |image1205| image:: images/matrix_checkmark.png
.. |image1206| image:: images/matrix_autocast.png
.. |image1207| image:: images/matrix_checkmark.png
.. |image1208| image:: images/matrix_checkmark.png
.. |image1209| image:: images/matrix_checkmark.png
.. |image1210| image:: images/matrix_checkmark.png
.. |image1211| image:: images/matrix_checkmark.png
.. |image1212| image:: images/matrix_autocast.png
.. |image1213| image:: images/matrix_checkmark.png
.. |image1214| image:: images/matrix_checkmark.png
.. |image1215| image:: images/matrix_autocast.png
.. |image1216| image:: images/matrix_checkmark.png
.. |image1217| image:: images/matrix_checkmark.png
.. |image1218| image:: images/matrix_checkmark.png
.. |image1219| image:: images/matrix_checkmark.png
.. |image1220| image:: images/matrix_checkmark.png
.. |image1221| image:: images/matrix_autocast.png
.. |image1222| image:: images/matrix_checkmark.png
.. |image1223| image:: images/matrix_checkmark.png
.. |image1224| image:: images/matrix_autocast.png
.. |image1225| image:: images/matrix_checkmark.png
.. |image1226| image:: images/matrix_checkmark.png
.. |image1227| image:: images/matrix_checkmark.png
.. |image1228| image:: images/matrix_checkmark.png
.. |image1229| image:: images/matrix_checkmark.png
.. |image1230| image:: images/matrix_checkmark.png
.. |image1231| image:: images/matrix_checkmark.png
.. |image1232| image:: images/matrix_checkmark.png
.. |image1233| image:: images/matrix_checkmark.png
.. |image1234| image:: images/matrix_checkmark.png
.. |image1235| image:: images/matrix_checkmark.png
.. |image1236| image:: images/matrix_checkmark.png
.. |image1237| image:: images/matrix_checkmark.png
.. |image1238| image:: images/matrix_checkmark.png
.. |image1239| image:: images/matrix_checkmark.png
.. |image1240| image:: images/matrix_checkmark.png
