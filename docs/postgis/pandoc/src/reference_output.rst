Geometry Outputs
================

ST\_AsBinary
Return the Well-Known Binary (WKB) representation of the
geometry/geography without SRID meta data.
bytea
ST\_AsBinary
geometry
g1
bytea
ST\_AsBinary
geometry
g1
text
NDR\_or\_XDR
bytea
ST\_AsBinary
geography
g1
bytea
ST\_AsBinary
geography
g1
text
NDR\_or\_XDR
Description
-----------

Returns the Well-Known Binary representation of the geometry. There are
2 variants of the function. The first variant takes no endian encoding
parameter and defaults to server machine endian. The second variant
takes a second argument denoting the encoding - using little-endian
('NDR') or big-endian ('XDR') encoding.

This is useful in binary cursors to pull data out of the database
without converting it to a string representation.

    **Note**

    The WKB spec does not include the SRID. To get the WKB with SRID
    format use ST\_AsEWKB

    **Note**

    ST\_AsBinary is the reverse of ? for geometry. Use ? to convert to a
    postgis geometry from ST\_AsBinary representation.

    **Note**

    The default behavior in PostgreSQL 9.0 has been changed to output
    bytea in hex encoding. ST\_AsBinary is the reverse of ? for
    geometry. If your GUI tools require the old behavior, then SET
    bytea\_output='escape' in your database.

Enhanced: 2.0.0 support for Polyhedral surfaces, Triangles and TIN was
introduced.

Enhanced: 2.0.0 support for higher coordinate dimensions was introduced.

Enhanced: 2.0.0 support for specifying endian with geography was
introduced.

Availability: 1.5.0 geography support was introduced.

Changed: 2.0.0 Inputs to this function can not be unknown -- must be
geometry. Constructs such as ``ST_AsBinary('POINT(1 2)')`` are no longer
valid and you will get an
``n st_asbinary(unknown)  is not unique error``. Code like that needs to
be changed to ``ST_AsBinary('POINT(1 2)'::geometry);``. If that is not
possible, then install ``legacy.sql``.

SFS\_COMPLIANT s2.1.1.1

SQLMM\_COMPLIANT SQL-MM 3: 5.1.37

CURVE\_SUPPORT

P\_SUPPORT

T\_SUPPORT

Z\_SUPPORT

Examples
--------

::

    SELECT ST_AsBinary(ST_GeomFromText('POLYGON((0 0,0 1,1 1,1 0,0 0))',4326));

               st_asbinary
    --------------------------------
    \001\003\000\000\000\001\000\000\000\005
    \000\000\000\000\000\000\000\000\000\000
    \000\000\000\000\000\000\000\000\000\000
    \000\000\000\000\000\000\000\000\000\000
    \000\000\000\360?\000\000\000\000\000\000
    \360?\000\000\000\000\000\000\360?\000\000
    \000\000\000\000\360?\000\000\000\000\000
    \000\000\000\000\000\000\000\000\000\000\000
    \000\000\000\000\000\000\000\000
    (1 row)

::

    SELECT ST_AsBinary(ST_GeomFromText('POLYGON((0 0,0 1,1 1,1 0,0 0))',4326), 'XDR');
               st_asbinary
    --------------------------------
    \000\000\000\000\003\000\000\000\001\000\000\000\005\000\000\000\000\000
    \000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000
    \000?\360\000\000\000\000\000\000?\360\000\000\000\000\000\000?\360\000\000
    \000\000\000\000?\360\000\000\000\000\000\000\000\000\000\000\000\000\000\000
    \000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000
    (1 row)

See Also
--------

? ?, ?,

ST\_AsEWKB
Return the Well-Known Binary (WKB) representation of the geometry with
SRID meta data.
bytea
ST\_AsEWKB
geometry
g1
bytea
ST\_AsEWKB
geometry
g1
text
NDR\_or\_XDR
Description
-----------

Returns the Well-Known Binary representation of the geometry with SRID
metadata. There are 2 variants of the function. The first variant takes
no endian encoding parameter and defaults to little endian. The second
variant takes a second argument denoting the encoding - using
little-endian ('NDR') or big-endian ('XDR') encoding.

This is useful in binary cursors to pull data out of the database
without converting it to a string representation.

    **Note**

    The WKB spec does not include the SRID. To get the OGC WKB format
    use ST\_AsBinary

    **Note**

    ST\_AsEWKB is the reverse of ST\_GeomFromEWKB. Use ST\_GeomFromEWKB
    to convert to a postgis geometry from ST\_AsEWKB representation.

Enhanced: 2.0.0 support for Polyhedral surfaces, Triangles and TIN was
introduced.

Z\_SUPPORT

CURVE\_SUPPORT

P\_SUPPORT

T\_SUPPORT

Examples
--------

::

    SELECT ST_AsEWKB(ST_GeomFromText('POLYGON((0 0,0 1,1 1,1 0,0 0))',4326));

               st_asewkb
    --------------------------------
    \001\003\000\000 \346\020\000\000\001\000
    \000\000\005\000\000\000\000
    \000\000\000\000\000\000\000\000
    \000\000\000\000\000\000\000\000\000
    \000\000\000\000\000\000\000\000\000\000
    \000\000\360?\000\000\000\000\000\000\360?
    \000\000\000\000\000\000\360?\000\000\000\000\000
    \000\360?\000\000\000\000\000\000\000\000\000\000\000
    \000\000\000\000\000\000\000\000\000\000\000\000\000
    (1 row)

::

                SELECT ST_AsEWKB(ST_GeomFromText('POLYGON((0 0,0 1,1 1,1 0,0 0))',4326), 'XDR');
               st_asewkb
    --------------------------------
    \000 \000\000\003\000\000\020\346\000\000\000\001\000\000\000\005\000\000\000\000\
    000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000?
    \360\000\000\000\000\000\000?\360\000\000\000\000\000\000?\360\000\000\000\000
    \000\000?\360\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000
    \000\000\000\000\000\000\000\000\000\000\000\000\000
            

See Also
--------

?, ?, ?, ?, ?

ST\_AsEWKT
Return the Well-Known Text (WKT) representation of the geometry with
SRID meta data.
text
ST\_AsEWKT
geometry
g1
text
ST\_AsEWKT
geography
g1
Description
-----------

Returns the Well-Known Text representation of the geometry prefixed with
the SRID.

    **Note**

    The WKT spec does not include the SRID. To get the OGC WKT format
    use ST\_AsText

WKT format does not maintain precision so to prevent floating
truncation, use ST\_AsBinary or ST\_AsEWKB format for transport.

    **Note**

    ST\_AsEWKT is the reverse of ?. Use ? to convert to a postgis
    geometry from ST\_AsEWKT representation.

Enhanced: 2.0.0 support for Geography, Polyhedral surfaces, Triangles
and TIN was introduced.

Z\_SUPPORT

CURVE\_SUPPORT

P\_SUPPORT

T\_SUPPORT

Examples
--------

::

    SELECT ST_AsEWKT('0103000020E61000000100000005000000000000
                000000000000000000000000000000000000000000000000000000
                F03F000000000000F03F000000000000F03F000000000000F03
                F000000000000000000000000000000000000000000000000'::geometry);

               st_asewkt
    --------------------------------
    SRID=4326;POLYGON((0 0,0 1,1 1,1 0,0 0))
    (1 row)

    SELECT ST_AsEWKT('0108000080030000000000000060E30A4100000000785C0241000000000000F03F0000000018
    E20A4100000000485F024100000000000000400000000018
    E20A4100000000305C02410000000000000840')

    --st_asewkt---
    CIRCULARSTRING(220268 150415 1,220227 150505 2,220227 150406 3)

See Also
--------

???, ?

ST\_AsGeoJSON
Return the geometry as a GeoJSON element.
text
ST\_AsGeoJSON
geometry
geom
integer
maxdecimaldigits=15
integer
options=0
text
ST\_AsGeoJSON
geography
geog
integer
maxdecimaldigits=15
integer
options=0
text
ST\_AsGeoJSON
integer
gj\_version
geometry
geom
integer
maxdecimaldigits=15
integer
options=0
text
ST\_AsGeoJSON
integer
gj\_version
geography
geog
integer
maxdecimaldigits=15
integer
options=0
Description
-----------

Return the geometry as a Geometry Javascript Object Notation (GeoJSON)
element. (Cf `GeoJSON specifications
1.0 <http://geojson.org/geojson-spec.html>`__). 2D and 3D Geometries are
both supported. GeoJSON only support SFS 1.1 geometry type (no curve
support for example).

The gj\_version parameter is the major version of the GeoJSON spec. If
specified, must be 1. This represents the spec version of GeoJSON.

The third argument may be used to reduce the maximum number of decimal
places used in output (defaults to 15).

The last 'options' argument could be used to add Bbox or Crs in GeoJSON
output:

-  0: means no option (default value)

-  1: GeoJSON Bbox

-  2: GeoJSON Short CRS (e.g EPSG:4326)

-  4: GeoJSON Long CRS (e.g urn:ogc:def:crs:EPSG::4326)

Version 1: ST\_AsGeoJSON(geom) / precision=15 version=1 options=0

Version 2: ST\_AsGeoJSON(geom, precision) / version=1 options=0

Version 3: ST\_AsGeoJSON(geom, precision, options) / version=1

Version 4: ST\_AsGeoJSON(gj\_version, geom) / precision=15 options=0

Version 5: ST\_AsGeoJSON(gj\_version, geom, precision) /options=0

Version 6: ST\_AsGeoJSON(gj\_version, geom, precision,options)

Availability: 1.3.4

Availability: 1.5.0 geography support was introduced.

Changed: 2.0.0 support default args and named args.

Z\_SUPPORT

Examples
--------

GeoJSON format is generally more efficient than other formats for use in
ajax mapping. One popular javascript client that supports this is Open
Layers. Example of its use is `OpenLayers GeoJSON
Example <http://openlayers.org/dev/examples/vector-formats.html>`__

::

    SELECT ST_AsGeoJSON(the_geom) from fe_edges limit 1;
                           st_asgeojson
    -----------------------------------------------------------------------------------------------------------

    {"type":"MultiLineString","coordinates":[[[-89.734634999999997,31.492072000000000],
    [-89.734955999999997,31.492237999999997]]]}
    (1 row)
    --3d point
    SELECT ST_AsGeoJSON('LINESTRING(1 2 3, 4 5 6)');

    st_asgeojson
    -----------------------------------------------------------------------------------------
     {"type":"LineString","coordinates":[[1,2,3],[4,5,6]]}

ST\_AsGML
Return the geometry as a GML version 2 or 3 element.
text
ST\_AsGML
integer
version
geometry
geom
integer
maxdecimaldigits=15
integer
options=0
text
nprefix=null
text
id=null
text
ST\_AsGML
integer
version
geography
geog
integer
maxdecimaldigits=15
integer
options=0
text
nprefix=null
text
id=null
Description
-----------

Return the geometry as a Geography Markup Language (GML) element. The
version parameter, if specified, may be either 2 or 3. If no version
parameter is specified then the default is assumed to be 2. The
precision argument may be used to reduce the maximum number of decimal
places (``maxdecimaldigits``) used in output (defaults to 15).

GML 2 refer to 2.1.2 version, GML 3 to 3.1.1 version

The 'options' argument is a bitfield. It could be used to define CRS
output type in GML output, and to declare data as lat/lon:

-  0: GML Short CRS (e.g EPSG:4326), default value

-  1: GML Long CRS (e.g urn:ogc:def:crs:EPSG::4326)

-  2: For GML 3 only, remove srsDimension attribute from output.

-  4: For GML 3 only, use <LineString> rather than <Curve> tag for
   lines.

-  16: Declare that datas are lat/lon (e.g srid=4326). Default is to
   assume that data are planars. This option is useful for GML 3.1.1
   output only, related to axis order. So if you set it, it will swap
   the coordinates so order is lat lon instead of database lon lat.

-  32: Output the box of the geometry (envelope).

The 'namespace prefix' argument may be used to specify a custom
namespace prefix or no prefix (if empty). If null or omitted 'gml'
prefix is used

Availability: 1.3.2

Availability: 1.5.0 geography support was introduced.

Enhanced: 2.0.0 prefix support was introduced. Option 4 for GML3 was
introduced to allow using LineString instead of Curve tag for lines.
GML3 Support for Polyhedral surfaces and TINS was introduced. Option 32
was introduced to output the box.

Changed: 2.0.0 use default named args

Enhanced: 2.1.0 id support was introduced, for GML 3.

    **Note**

    Only version 3+ of ST\_AsGML supports Polyhedral Surfaces and TINS.

Z\_SUPPORT

P\_SUPPORT

T\_SUPPORT

Examples: Version 2
-------------------

::

    SELECT ST_AsGML(ST_GeomFromText('POLYGON((0 0,0 1,1 1,1 0,0 0))',4326));
            st_asgml
            --------
            <gml:Polygon srsName="EPSG:4326"><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>0,0 0,1 1,1 1,0 0,0</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs></gml:Polygon>
                

Examples: Version 3
-------------------

::

    -- Flip coordinates and output extended EPSG (16 | 1)--
    SELECT ST_AsGML(3, ST_GeomFromText('POINT(5.234234233242 6.34534534534)',4326), 5, 17);
                st_asgml
                --------
            <gml:Point srsName="urn:ogc:def:crs:EPSG::4326"><gml:pos>6.34535 5.23423</gml:pos></gml:Point>
                

::

    -- Output the envelope (32) --
    SELECT ST_AsGML(3, ST_GeomFromText('LINESTRING(1 2, 3 4, 10 20)',4326), 5, 32);
            st_asgml
            --------
        <gml:Envelope srsName="EPSG:4326">
            <gml:lowerCorner>1 2</gml:lowerCorner>
            <gml:upperCorner>10 20</gml:upperCorner>
        </gml:Envelope>
                

::

    -- Output the envelope (32) , reverse (lat lon instead of lon lat) (16), long srs (1)= 32 | 16 | 1 = 49 --
    SELECT ST_AsGML(3, ST_GeomFromText('LINESTRING(1 2, 3 4, 10 20)',4326), 5, 49);
        st_asgml
        --------
    <gml:Envelope srsName="urn:ogc:def:crs:EPSG::4326">
        <gml:lowerCorner>2 1</gml:lowerCorner>
        <gml:upperCorner>20 10</gml:upperCorner>
    </gml:Envelope>
                

::

    -- Polyhedral Example --
    SELECT ST_AsGML(3, ST_GeomFromEWKT('POLYHEDRALSURFACE( ((0 0 0, 0 0 1, 0 1 1, 0 1 0, 0 0 0)), 
    ((0 0 0, 0 1 0, 1 1 0, 1 0 0, 0 0 0)), ((0 0 0, 1 0 0, 1 0 1, 0 0 1, 0 0 0)), 
    ((1 1 0, 1 1 1, 1 0 1, 1 0 0, 1 1 0)), 
    ((0 1 0, 0 1 1, 1 1 1, 1 1 0, 0 1 0)), ((0 0 1, 1 0 1, 1 1 1, 0 1 1, 0 0 1)) )'));
        st_asgml
        --------
     <gml:PolyhedralSurface>
    <gml:polygonPatches>
       <gml:PolygonPatch>
            <gml:exterior>
                  <gml:LinearRing>
                       <gml:posList srsDimension="3">0 0 0 0 0 1 0 1 1 0 1 0 0 0 0</gml:posList>
                  </gml:LinearRing>
            </gml:exterior>
       </gml:PolygonPatch>
       <gml:PolygonPatch>
            <gml:exterior>
                  <gml:LinearRing>
                       <gml:posList srsDimension="3">0 0 0 0 1 0 1 1 0 1 0 0 0 0 0</gml:posList>
                  </gml:LinearRing>
            </gml:exterior>
       </gml:PolygonPatch>
       <gml:PolygonPatch>
            <gml:exterior>
                  <gml:LinearRing>
                       <gml:posList srsDimension="3">0 0 0 1 0 0 1 0 1 0 0 1 0 0 0</gml:posList>
                  </gml:LinearRing>
            </gml:exterior>
       </gml:PolygonPatch>
       <gml:PolygonPatch>
            <gml:exterior>
                  <gml:LinearRing>
                       <gml:posList srsDimension="3">1 1 0 1 1 1 1 0 1 1 0 0 1 1 0</gml:posList>
                  </gml:LinearRing>
            </gml:exterior>
       </gml:PolygonPatch>
       <gml:PolygonPatch>
            <gml:exterior>
                  <gml:LinearRing>
                       <gml:posList srsDimension="3">0 1 0 0 1 1 1 1 1 1 1 0 0 1 0</gml:posList>
                  </gml:LinearRing>
            </gml:exterior>
       </gml:PolygonPatch>
       <gml:PolygonPatch>
            <gml:exterior>
                  <gml:LinearRing>
                       <gml:posList srsDimension="3">0 0 1 1 0 1 1 1 1 0 1 1 0 0 1</gml:posList>
                  </gml:LinearRing>
            </gml:exterior>
       </gml:PolygonPatch>
    </gml:polygonPatches>
    </gml:PolyhedralSurface>
                

See Also
--------

?

ST\_AsHEXEWKB
Returns a Geometry in HEXEWKB format (as text) using either
little-endian (NDR) or big-endian (XDR) encoding.
text
ST\_AsHEXEWKB
geometry
g1
text
NDRorXDR
text
ST\_AsHEXEWKB
geometry
g1
Description
-----------

Returns a Geometry in HEXEWKB format (as text) using either
little-endian (NDR) or big-endian (XDR) encoding. If no encoding is
specified, then NDR is used.

    **Note**

    Availability: 1.2.2

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    SELECT ST_AsHEXEWKB(ST_GeomFromText('POLYGON((0 0,0 1,1 1,1 0,0 0))',4326));
            which gives same answer as

            SELECT ST_GeomFromText('POLYGON((0 0,0 1,1 1,1 0,0 0))',4326)::text;

            st_ashexewkb
            --------
            0103000020E6100000010000000500
            00000000000000000000000000000000
            00000000000000000000000000000000F03F
            000000000000F03F000000000000F03F000000000000F03
            F000000000000000000000000000000000000000000000000

ST\_AsKML
Return the geometry as a KML element. Several variants. Default
version=2, default precision=15
text
ST\_AsKML
geometry
geom
integer
maxdecimaldigits=15
text
ST\_AsKML
geography
geog
integer
maxdecimaldigits=15
text
ST\_AsKML
integer
version
geometry
geom
integer
maxdecimaldigits=15
text
nprefix=NULL
text
ST\_AsKML
integer
version
geography
geog
integer
maxdecimaldigits=15
text
nprefix=NULL
Description
-----------

Return the geometry as a Keyhole Markup Language (KML) element. There
are several variants of this function. maximum number of decimal places
used in output (defaults to 15), version default to 2 and default
namespace is no prefix.

Version 1: ST\_AsKML(geom\_or\_geog, maxdecimaldigits) / version=2 /
maxdecimaldigits=15

Version 2: ST\_AsKML(version, geom\_or\_geog, maxdecimaldigits, nprefix)
maxdecimaldigits=15 / nprefix=NULL

    **Note**

    Requires PostGIS be compiled with Proj support. Use ? to confirm you
    have proj support compiled in.

    **Note**

    Availability: 1.2.2 - later variants that include version param came
    in 1.3.2

    **Note**

    Enhanced: 2.0.0 - Add prefix namespace. Default is no prefix

    **Note**

    Changed: 2.0.0 - uses default args and supports named args

    **Note**

    AsKML output will not work with geometries that do not have an SRID

Z\_SUPPORT

Examples
--------

::

    SELECT ST_AsKML(ST_GeomFromText('POLYGON((0 0,0 1,1 1,1 0,0 0))',4326));

            st_askml
            --------
            <Polygon><outerBoundaryIs><LinearRing><coordinates>0,0 0,1 1,1 1,0 0,0</coordinates></LinearRing></outerBoundaryIs></Polygon>

            --3d linestring
            SELECT ST_AsKML('SRID=4326;LINESTRING(1 2 3, 4 5 6)');
            <LineString><coordinates>1,2,3 4,5,6</coordinates></LineString>
            
            

See Also
--------

?, ?

ST\_AsSVG
Returns a Geometry in SVG path data given a geometry or geography
object.
text
ST\_AsSVG
geometry
geom
integer
rel=0
integer
maxdecimaldigits=15
text
ST\_AsSVG
geography
geog
integer
rel=0
integer
maxdecimaldigits=15
Description
-----------

Return the geometry as Scalar Vector Graphics (SVG) path data. Use 1 as
second argument to have the path data implemented in terms of relative
moves, the default (or 0) uses absolute moves. Third argument may be
used to reduce the maximum number of decimal digits used in output
(defaults to 15). Point geometries will be rendered as cx/cy when 'rel'
arg is 0, x/y when 'rel' is 1. Multipoint geometries are delimited by
commas (","), GeometryCollection geometries are delimited by semicolons
(";").

    **Note**

    Availability: 1.2.2. Availability: 1.4.0 Changed in PostGIS 1.4.0 to
    include L command in absolute path to conform to
    http://www.w3.org/TR/SVG/paths.html#PathDataBNF

Changed: 2.0.0 to use default args and support named args

Examples
--------

::

    SELECT ST_AsSVG(ST_GeomFromText('POLYGON((0 0,0 1,1 1,1 0,0 0))',4326));

            st_assvg
            --------
            M 0 0 L 0 -1 1 -1 1 0 Z

ST\_AsX3D
Returns a Geometry in X3D xml node element format:
ISO-IEC-19776-1.2-X3DEncodings-XML
text
ST\_AsX3D
geometry
g1
integer
maxdecimaldigits=15
integer
options=0
Description
-----------

Returns a geometry as an X3D xml formatted node element
http://web3d.org/x3d/specifications/ISO-IEC-19776-1.2-X3DEncodings-XML/Part01/EncodingOfNodes.html.
If ``maxdecimaldigits`` (precision) is not specified then defaults to
15.

    **Note**

    There are various options for translating PostGIS geometries to X3D
    since X3D geometry types don't map directly to PostGIS geometry
    types and some newer X3D types that might be better mappings we ahve
    avoided since most rendering tools don't currently support them.
    These are the mappings we have settled on. Feel free to post a bug
    ticket if you have thoughts on the idea or ways we can allow people
    to denote their preferred mappings.

    Below is how we currently map PostGIS 2D/3D types to X3D types

+--------------------------------------+--------------------------------------------+--------------------------------------------------------------------+
| PostGIS Type                         | 2D X3D Type                                | 3D X3D Type                                                        |
+======================================+============================================+====================================================================+
| LINESTRING                           | not yet implemented - will be PolyLine2D   | LineSet                                                            |
+--------------------------------------+--------------------------------------------+--------------------------------------------------------------------+
| MULTILINESTRING                      | not yet implemented - will be PolyLine2D   | IndexedLineSet                                                     |
+--------------------------------------+--------------------------------------------+--------------------------------------------------------------------+
| MULTIPOINT                           | Polypoint2D                                | PointSet                                                           |
+--------------------------------------+--------------------------------------------+--------------------------------------------------------------------+
| POINT                                | outputs the space delimited coordinates    | outputs the space delimited coordinates                            |
+--------------------------------------+--------------------------------------------+--------------------------------------------------------------------+
| (MULTI) POLYGON, POLYHEDRALSURFACE   | Invalid X3D markup                         | IndexedFaceSet (inner rings currently output as another faceset)   |
+--------------------------------------+--------------------------------------------+--------------------------------------------------------------------+
| TIN                                  | TriangleSet2D (Not Yet Implemented)        | IndexedTriangleSet                                                 |
+--------------------------------------+--------------------------------------------+--------------------------------------------------------------------+

    **Note**

    2D geometry support not yet complete. Inner rings currently just
    drawn as separate polygons. We are working on these.

Lots of advancements happening in 3D space particularly with `X3D
Integration with
HTML5 <http://www.web3d.org/x3d/wiki/index.php/X3D_and_HTML5#Goals:_X3D_and_HTML5>`__

There is also a nice open source X3D viewer you can use to view rendered
geometries. Free Wrl http://freewrl.sourceforge.net/ binaries available
for Mac, Linux, and Windows. Use the FreeWRL\_Launcher packaged to view
the geometries.

Availability: 2.0.0: ISO-IEC-19776-1.2-X3DEncodings-XML

Z\_SUPPORT

P\_SUPPORT

T\_SUPPORT

Example: Create a fully functional X3D document - This will generate a cube that is viewable in FreeWrl and other X3D viewers.
------------------------------------------------------------------------------------------------------------------------------

::

    SELECT '<?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE X3D PUBLIC "ISO//Web3D//DTD X3D 3.0//EN" "http://www.web3d.org/specifications/x3d-3.0.dtd">
    <X3D>
      <Scene>
        <Transform>
          <Shape>
           <Appearance>
                <Material emissiveColor=''0 0 1''/>   
           </Appearance> ' || 
           ST_AsX3D( ST_GeomFromEWKT('POLYHEDRALSURFACE( ((0 0 0, 0 0 1, 0 1 1, 0 1 0, 0 0 0)), 
    ((0 0 0, 0 1 0, 1 1 0, 1 0 0, 0 0 0)), ((0 0 0, 1 0 0, 1 0 1, 0 0 1, 0 0 0)), 
    ((1 1 0, 1 1 1, 1 0 1, 1 0 0, 1 1 0)), 
    ((0 1 0, 0 1 1, 1 1 1, 1 1 0, 0 1 0)), ((0 0 1, 1 0 1, 1 1 1, 0 1 1, 0 0 1)) )')) ||
          '</Shape>
        </Transform>
      </Scene>
    </X3D>' As x3ddoc;

            x3ddoc
            --------
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE X3D PUBLIC "ISO//Web3D//DTD X3D 3.0//EN" "http://www.web3d.org/specifications/x3d-3.0.dtd">
    <X3D>
      <Scene>
        <Transform>
          <Shape>
           <Appearance>
                <Material emissiveColor='0 0 1'/>   
           </Appearance> 
           <IndexedFaceSet  coordIndex='0 1 2 3 -1 4 5 6 7 -1 8 9 10 11 -1 12 13 14 15 -1 16 17 18 19 -1 20 21 22 23'>
                <Coordinate point='0 0 0 0 0 1 0 1 1 0 1 0 0 0 0 0 1 0 1 1 0 1 0 0 0 0 0 1 0 0 1 0 1 0 0 1 1 1 0 1 1 1 1 0 1 1 0 0 0 1 0 0 1 1 1 1 1 1 1 0 0 0 1 1 0 1 1 1 1 0 1 1' />
          </IndexedFaceSet>
          </Shape>
        </Transform>
      </Scene>
    </X3D>

Example: An Octagon elevated 3 Units and decimal precision of 6
---------------------------------------------------------------

::

    SELECT ST_AsX3D(
    ST_Translate(
        ST_Force_3d(
            ST_Buffer(ST_Point(10,10),5, 'quad_segs=2')), 0,0,
        3)
      ,6) As x3dfrag;

    x3dfrag
    --------
    <IndexedFaceSet coordIndex="0 1 2 3 4 5 6 7">
        <Coordinate point="15 10 3 13.535534 6.464466 3 10 5 3 6.464466 6.464466 3 5 10 3 6.464466 13.535534 3 10 15 3 13.535534 13.535534 3 " />
    </IndexedFaceSet>

Example: TIN
------------

::

    SELECT ST_AsX3D(ST_GeomFromEWKT('TIN (((
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
                )')) As x3dfrag;

            x3dfrag
            --------
    <IndexedTriangleSet  index='0 1 2 3 4 5'><Coordinate point='0 0 0 0 0 1 0 1 0 0 0 0 0 1 0 1 1 0'/></IndexedTriangleSet>

Example: Closed multilinestring (the boundary of a polygon with holes)
----------------------------------------------------------------------

::

    SELECT ST_AsX3D(
                ST_GeomFromEWKT('MULTILINESTRING((20 0 10,16 -12 10,0 -16 10,-12 -12 10,-20 0 10,-12 16 10,0 24 10,16 16 10,20 0 10),
      (12 0 10,8 8 10,0 12 10,-8 8 10,-8 0 10,-8 -4 10,0 -8 10,8 -4 10,12 0 10))') 
    ) As x3dfrag;

            x3dfrag
            --------
    <IndexedLineSet  coordIndex='0 1 2 3 4 5 6 7 0 -1 8 9 10 11 12 13 14 15 8'>
        <Coordinate point='20 0 10 16 -12 10 0 -16 10 -12 -12 10 -20 0 10 -12 16 10 0 24 10 16 16 10 12 0 10 8 8 10 0 12 10 -8 8 10 -8 0 10 -8 -4 10 0 -8 10 8 -4 10 ' />
     </IndexedLineSet>

ST\_GeoHash
Return a GeoHash representation of the geometry.
text
ST\_GeoHash
geometry
geom
integer
maxchars=full\_precision\_of\_point
Description
-----------

Return a GeoHash representation (http://en.wikipedia.org/wiki/Geohash)
of the geometry. A GeoHash encodes a point into a text form that is
sortable and searchable based on prefixing. A shorter GeoHash is a less
precise representation of a point. It can also be thought of as a box,
that contains the actual point.

If no ``maxchars`` is specficified ST\_GeoHash returns a GeoHash based
on full precision of the input geometry type. Points return a GeoHash
with 20 characters of precision (about enough to hold the full double
precision of the input). Other types return a GeoHash with a variable
amount of precision, based on the size of the feature. Larger features
are represented with less precision, smaller features with more
precision. The idea is that the box implied by the GeoHash will always
contain the input feature.

If ``maxchars`` is specified ST\_GeoHash returns a GeoHash with at most
that many characters so a possibly lower precision representation of the
input geometry. For non-points, the starting point of the calculation is
the center of the bounding box of the geometry.

Availability: 1.4.0

    **Note**

    ST\_GeoHash will not work with geometries that are not in geographic
    (lon/lat) coordinates.

CURVE\_SUPPORT

Examples
--------

::

    SELECT ST_GeoHash(ST_SetSRID(ST_MakePoint(-126,48),4326));

         st_geohash
    ----------------------
     c0w3hf1s70w3hf1s70w3

    SELECT ST_GeoHash(ST_SetSRID(ST_MakePoint(-126,48),4326),5);

     st_geohash
    ------------
     c0w3h
            
            

See Also
--------

?

ST\_AsText
Return the Well-Known Text (WKT) representation of the
geometry/geography without SRID metadata.
text
ST\_AsText
geometry
g1
text
ST\_AsText
geography
g1
Description
-----------

Returns the Well-Known Text representation of the geometry/geography.

    **Note**

    The WKT spec does not include the SRID. To get the SRID as part of
    the data, use the non-standard PostGIS ?

WKT format does not maintain precision so to prevent floating
truncation, use ST\_AsBinary or ST\_AsEWKB format for transport.

    **Note**

    ST\_AsText is the reverse of ?. Use ? to convert to a postgis
    geometry from ST\_AsText representation.

Availability: 1.5 - support for geography was introduced.

SFS\_COMPLIANT s2.1.1.1

SQLMM\_COMPLIANT SQL-MM 3: 5.1.25

CURVE\_SUPPORT

Examples
--------

::

    SELECT ST_AsText('01030000000100000005000000000000000000
    000000000000000000000000000000000000000000000000
    F03F000000000000F03F000000000000F03F000000000000F03
    F000000000000000000000000000000000000000000000000');

               st_astext
    --------------------------------
     POLYGON((0 0,0 1,1 1,1 0,0 0))
    (1 row)

See Also
--------

?, ?, ?, ?

ST\_AsLatLonText
Return the Degrees, Minutes, Seconds representation of the given point.
text
ST\_AsLatLonText
geometry
pt
text
ST\_AsLatLonText
geometry
pt
text
format
Description
-----------

Returns the Degrees, Minutes, Seconds representation of the point.

    **Note**

    It is assumed the point is in a lat/lon projection. The X (lon) and
    Y (lat) coordinates are normalized in the output to the "normal"
    range (-180 to +180 for lon, -90 to +90 for lat).

The text parameter is a format string containing the format for the
resulting text, similar to a date format string. Valid tokens are "D"
for degrees, "M" for minutes, "S" for seconds, and "C" for cardinal
direction (NSEW). DMS tokens may be repeated to indicate desired width
and precision ("SSS.SSSS" means " 1.0023").

"M", "S", and "C" are optional. If "C" is omitted, degrees are shown
with a "-" sign if south or west. If "S" is omitted, minutes will be
shown as decimal with as many digits of precision as you specify. If "M"
is also omitted, degrees are shown as decimal with as many digits
precision as you specify.

If the format string is omitted (or zero-length) a default format will
be used.

Availability: 2.0

Examples
--------

Default format.

::

    SELECT (ST_AsLatLonText('POINT (-3.2342342 -2.32498)'));
          st_aslatlontext       
    ----------------------------
     2°19'29.928"S 3°14'3.243"W

Providing a format (same as the default).

::

    SELECT (ST_AsLatLonText('POINT (-3.2342342 -2.32498)', 'D°M''S.SSS"C'));
          st_aslatlontext       
    ----------------------------
     2°19'29.928"S 3°14'3.243"W

Characters other than D, M, S, C and . are just passed through.

::

    SELECT (ST_AsLatLonText('POINT (-3.2342342 -2.32498)', 'D degrees, M minutes, S seconds to the C'));
                                       st_aslatlontext                                    
    --------------------------------------------------------------------------------------
     2 degrees, 19 minutes, 30 seconds to the S 3 degrees, 14 minutes, 3 seconds to the W

Signed degrees instead of cardinal directions.

::

    SELECT (ST_AsLatLonText('POINT (-3.2342342 -2.32498)', 'D°M''S.SSS"'));
          st_aslatlontext       
    ----------------------------
     -2°19'29.928" -3°14'3.243"

Decimal degrees.

::

    SELECT (ST_AsLatLonText('POINT (-3.2342342 -2.32498)', 'D.DDDD degrees C'));
              st_aslatlontext          
    -----------------------------------
     2.3250 degrees S 3.2342 degrees W

Excessively large values are normalized.

::

    SELECT (ST_AsLatLonText('POINT (-302.2342342 -792.32498)'));
            st_aslatlontext        
    -------------------------------
     72°19'29.928"S 57°45'56.757"E

