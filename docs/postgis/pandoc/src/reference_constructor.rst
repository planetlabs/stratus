Geometry Constructors
=====================

ST\_BdPolyFromText
Construct a Polygon given an arbitrary collection of closed linestrings
as a MultiLineString Well-Known text representation.
geometry
ST\_BdPolyFromText
text
WKT
integer
srid
Description
-----------

Construct a Polygon given an arbitrary collection of closed linestrings
as a MultiLineString Well-Known text representation.

    **Note**

    Throws an error if WKT is not a MULTILINESTRING. Throws an error if
    output is a MULTIPOLYGON; use ST\_BdMPolyFromText in that case, or
    see ST\_BuildArea() for a postgis-specific approach.

SFS\_COMPLIANT s3.2.6.2

Availability: 1.1.0 - requires GEOS >= 2.1.0.

Examples
--------

::

    Forthcoming

See Also
--------

?, ?

ST\_BdMPolyFromText
Construct a MultiPolygon given an arbitrary collection of closed
linestrings as a MultiLineString text representation Well-Known text
representation.
geometry
ST\_BdMPolyFromText
text
WKT
integer
srid
Description
-----------

Construct a Polygon given an arbitrary collection of closed linestrings,
polygons, MultiLineStrings as Well-Known text representation.

    **Note**

    Throws an error if WKT is not a MULTILINESTRING. Forces MULTIPOLYGON
    output even when result is really only composed by a single POLYGON;
    use `ST\_BdPolyFromText <#ST_BdPolyFromText>`__ if you're sure a
    single POLYGON will result from operation, or see
    `ST\_BuildArea() <#ST_BuildArea>`__ for a postgis-specific approach.

SFS\_COMPLIANT s3.2.6.2

Availability: 1.1.0 - requires GEOS >= 2.1.0.

Examples
--------

::

    Forthcoming

See Also
--------

?, ?

ST\_Box2dFromGeoHash
Return a BOX2D from a GeoHash string.
box2d
ST\_Box2dFromGeoHash
text
geohash
integer
precision=full\_precision\_of\_geohash
Description
-----------

Return a BOX2D from a GeoHash string.

If no ``precision`` is specficified ST\_Box2dFromGeoHash returns a BOX2D
based on full precision of the input GeoHash string.

If ``precision`` is specified ST\_Box2dFromGeoHash will use that many
characters from the GeoHash to create the BOX2D. Lower precision values
results in larger BOX2Ds and larger values increase the precision.

Availability: 2.1.0

Examples
--------

::

    SELECT ST_Box2dFromGeoHash('9qqj7nmxncgyy4d0dbxqz0');

                    st_geomfromgeohash
    --------------------------------------------------
     BOX(-115.172816 36.114646,-115.172816 36.114646)

    SELECT ST_Box2dFromGeoHash('9qqj7nmxncgyy4d0dbxqz0', 0);

     st_box2dfromgeohash
    ----------------------
     BOX(-180 -90,180 90)

     SELECT ST_Box2dFromGeoHash('9qqj7nmxncgyy4d0dbxqz0', 10);
                                st_box2dfromgeohash
    ---------------------------------------------------------------------------
     BOX(-115.17282128334 36.1146408319473,-115.172810554504 36.1146461963654)
            
            

See Also
--------

?, ?, ?

ST\_GeogFromText
Return a specified geography value from Well-Known Text representation
or extended (WKT).
geography
ST\_GeogFromText
text
EWKT
Description
-----------

Returns a geography object from the well-known text or extended
well-known representation. SRID 4326 is assumed. This is an alias for
ST\_GeographyFromText. Points are always expressed in long lat form.

Examples
--------

::

    --- converting lon lat coords to geography
    ALTER TABLE sometable ADD COLUMN geog geography(POINT,4326);
    UPDATE sometable SET geog = ST_GeogFromText('SRID=4326;POINT(' || lon || ' ' || lat || ')');        
                

See Also
--------

?, ?

ST\_GeographyFromText
Return a specified geography value from Well-Known Text representation
or extended (WKT).
geography
ST\_GeographyFromText
text
EWKT
Description
-----------

Returns a geography object from the well-known text representation. SRID
4326 is assumed.

See Also
--------

?, ?

ST\_GeogFromWKB
Creates a geography instance from a Well-Known Binary geometry
representation (WKB) or extended Well Known Binary (EWKB).
geography
ST\_GeogFromWKB
bytea
geom
Description
-----------

The ``ST_GeogFromWKB`` function, takes a well-known binary
representation (WKB) of a geometry or PostGIS Extended WKB and creates
an instance of the appropriate geography type. This function plays the
role of the Geometry Factory in SQL.

If SRID is not specified, it defaults to 4326 (WGS 84 long lat).

CURVE\_SUPPORT

Examples
--------

::

    --Although bytea rep contains single \, these need to be escaped when inserting into a table
    SELECT ST_AsText(
    ST_GeogFromWKB(E'\\001\\002\\000\\000\\000\\002\\000\\000\\000\\037\\205\\353Q\\270~\\\\\\300\\323Mb\\020X\\231C@\\020X9\\264\\310~\\\\\\300)\\\\\\217\\302\\365\\230C@')
    );
                          st_astext
    ------------------------------------------------------
     LINESTRING(-113.98 39.198,-113.981 39.195)
    (1 row)

See Also
--------

?, ?

ST\_GeomCollFromText
Makes a collection Geometry from collection WKT with the given SRID. If
SRID is not give, it defaults to 0.
geometry
ST\_GeomCollFromText
text
WKT
integer
srid
geometry
ST\_GeomCollFromText
text
WKT
Description
-----------

Makes a collection Geometry from the Well-Known-Text (WKT)
representation with the given SRID. If SRID is not give, it defaults to
0.

OGC SPEC 3.2.6.2 - option SRID is from the conformance suite

Returns null if the WKT is not a GEOMETRYCOLLECTION

    **Note**

    If you are absolutely sure all your WKT geometries are collections,
    don't use this function. It is slower than ST\_GeomFromText since it
    adds an additional validation step.

SFS\_COMPLIANT s3.2.6.2

SQLMM\_COMPLIANT

Examples
--------

::

    SELECT ST_GeomCollFromText('GEOMETRYCOLLECTION(POINT(1 2),LINESTRING(1 2, 3 4))');

See Also
--------

?, ?

ST\_GeomFromEWKB
Return a specified ST\_Geometry value from Extended Well-Known Binary
representation (EWKB).
geometry
ST\_GeomFromEWKB
bytea
EWKB
Description
-----------

Constructs a PostGIS ST\_Geometry object from the OGC Extended
Well-Known binary (EWKT) representation.

    **Note**

    The EWKB format is not an OGC standard, but a PostGIS specific
    format that includes the spatial reference system (SRID) identifier

Enhanced: 2.0.0 support for Polyhedral surfaces and TIN was introduced.

Z\_SUPPORT

CURVE\_SUPPORT

P\_SUPPORT

T\_SUPPORT

Examples
--------

line string binary rep 0f LINESTRING(-71.160281 42.258729,-71.160837
42.259113,-71.161144 42.25932) in NAD 83 long lat (4269).

    **Note**

    NOTE: Even though byte arrays are delimited with \\ and may have ',
    we need to escape both out with \\ and '' if
    standard\_conforming\_strings is off. So it does not look exactly
    like its AsEWKB representation.

::

    SELECT ST_GeomFromEWKB(E'\\001\\002\\000\\000 \\255\\020\\000\\000\\003\\000\\000\\000\\344J=
    \\013B\\312Q\\300n\\303(\\010\\036!E@''\\277E''K
    \\312Q\\300\\366{b\\235*!E@\\225|\\354.P\\312Q
    \\300p\\231\\323e1!E@');

    **Note**

    In PostgreSQL 9.1+ - standard\_conforming\_strings is set to on by
    default, where as in past versions it was set to on. You can change
    defaults as needed for a single query or at the database or server
    level. Below is how you would do it with
    standard\_conforming\_strings = on. In this case we escape the '
    with standard ansi ', but slashes are not escaped

::

            set standard_conforming_strings = on;
    SELECT ST_GeomFromEWKB('\001\002\000\000 \255\020\000\000\003\000\000\000\344J=\012\013B
        \312Q\300n\303(\010\036!E@''\277E''K\012\312Q\300\366{b\235*!E@\225|\354.P\312Q\012\300p\231\323e1')

See Also
--------

?, ?, ?

ST\_GeomFromEWKT
Return a specified ST\_Geometry value from Extended Well-Known Text
representation (EWKT).
geometry
ST\_GeomFromEWKT
text
EWKT
Description
-----------

Constructs a PostGIS ST\_Geometry object from the OGC Extended
Well-Known text (EWKT) representation.

    **Note**

    The EWKT format is not an OGC standard, but an PostGIS specific
    format that includes the spatial reference system (SRID) identifier

Enhanced: 2.0.0 support for Polyhedral surfaces and TIN was introduced.

Z\_SUPPORT

CURVE\_SUPPORT

P\_SUPPORT

T\_SUPPORT

Examples
--------

::

    SELECT ST_GeomFromEWKT('SRID=4269;LINESTRING(-71.160281 42.258729,-71.160837 42.259113,-71.161144 42.25932)');
    SELECT ST_GeomFromEWKT('SRID=4269;MULTILINESTRING((-71.160281 42.258729,-71.160837 42.259113,-71.161144 42.25932))');

    SELECT ST_GeomFromEWKT('SRID=4269;POINT(-71.064544 42.28787)');

    SELECT ST_GeomFromEWKT('SRID=4269;POLYGON((-71.1776585052917 42.3902909739571,-71.1776820268866 42.3903701743239,
    -71.1776063012595 42.3903825660754,-71.1775826583081 42.3903033653531,-71.1776585052917 42.3902909739571))');

    SELECT ST_GeomFromEWKT('SRID=4269;MULTIPOLYGON(((-71.1031880899493 42.3152774590236,
    -71.1031627617667 42.3152960829043,-71.102923838298 42.3149156848307,
    -71.1023097974109 42.3151969047397,-71.1019285062273 42.3147384934248,
    -71.102505233663 42.3144722937587,-71.10277487471 42.3141658254797,
    -71.103113945163 42.3142739188902,-71.10324876416 42.31402489987,
    -71.1033002961013 42.3140393340215,-71.1033488797549 42.3139495090772,
    -71.103396240451 42.3138632439557,-71.1041521907712 42.3141153348029,
    -71.1041411411543 42.3141545014533,-71.1041287795912 42.3142114839058,
    -71.1041188134329 42.3142693656241,-71.1041112482575 42.3143272556118,
    -71.1041072845732 42.3143851580048,-71.1041057218871 42.3144430686681,
    -71.1041065602059 42.3145009876017,-71.1041097995362 42.3145589148055,
    -71.1041166403905 42.3146168544148,-71.1041258822717 42.3146748022936,
    -71.1041375307579 42.3147318674446,-71.1041492906949 42.3147711126569,
    -71.1041598612795 42.314808571739,-71.1042515013869 42.3151287620809,
    -71.1041173835118 42.3150739481917,-71.1040809891419 42.3151344119048,
    -71.1040438678912 42.3151191367447,-71.1040194562988 42.3151832057859,
    -71.1038734225584 42.3151140942995,-71.1038446938243 42.3151006300338,
    -71.1038315271889 42.315094347535,-71.1037393329282 42.315054824985,
    -71.1035447555574 42.3152608696313,-71.1033436658644 42.3151648370544,
    -71.1032580383161 42.3152269126061,-71.103223066939 42.3152517403219,
    -71.1031880899493 42.3152774590236)),
    ((-71.1043632495873 42.315113108546,-71.1043583974082 42.3151211109857,
    -71.1043443253471 42.3150676015829,-71.1043850704575 42.3150793250568,-71.1043632495873 42.315113108546)))');

::

    --3d circular string
    SELECT ST_GeomFromEWKT('CIRCULARSTRING(220268 150415 1,220227 150505 2,220227 150406 3)');

::

    --Polyhedral Surface example
    SELECT ST_GeomFromEWKT('POLYHEDRALSURFACE( 
        ((0 0 0, 0 0 1, 0 1 1, 0 1 0, 0 0 0)),  
        ((0 0 0, 0 1 0, 1 1 0, 1 0 0, 0 0 0)), 
        ((0 0 0, 1 0 0, 1 0 1, 0 0 1, 0 0 0)), 
        ((1 1 0, 1 1 1, 1 0 1, 1 0 0, 1 1 0)),  
        ((0 1 0, 0 1 1, 1 1 1, 1 1 0, 0 1 0)),  
        ((0 0 1, 1 0 1, 1 1 1, 0 1 1, 0 0 1)) 
    )');

See Also
--------

?, ?, ?

ST\_GeometryFromText
Return a specified ST\_Geometry value from Well-Known Text
representation (WKT). This is an alias name for ST\_GeomFromText
geometry
ST\_GeometryFromText
text
WKT
geometry
ST\_GeometryFromText
text
WKT
integer
srid
Description
-----------

SFS\_COMPLIANT

SQLMM\_COMPLIANT SQL-MM 3: 5.1.40

See Also
--------

?

ST\_GeomFromGeoHash
Return a geometry from a GeoHash string.
geometry
ST\_GeomFromGeoHash
text
geohash
integer
precision=full\_precision\_of\_geohash
Description
-----------

Return a geometry from a GeoHash string. The geometry will be a polygon
representing the GeoHash bounds.

If no ``precision`` is specficified ST\_GeomFromGeoHash returns a
polygon based on full precision of the input GeoHash string.

If ``precision`` is specified ST\_GeomFromGeoHash will use that many
characters from the GeoHash to create the polygon.

Availability: 2.1.0

Examples
--------

::

    SELECT ST_AsText(ST_GeomFromGeoHash('9qqj7nmxncgyy4d0dbxqz0'));
                                                            st_astext
    --------------------------------------------------------------------------------------------------------------------------
     POLYGON((-115.172816 36.114646,-115.172816 36.114646,-115.172816 36.114646,-115.172816 36.114646,-115.172816 36.114646))

    SELECT ST_AsText(ST_GeomFromGeoHash('9qqj7nmxncgyy4d0dbxqz0', 4));
                                                              st_astext
    ------------------------------------------------------------------------------------------------------------------------------
     POLYGON((-115.3125 36.03515625,-115.3125 36.2109375,-114.9609375 36.2109375,-114.9609375 36.03515625,-115.3125 36.03515625))

    SELECT ST_AsText(ST_GeomFromGeoHash('9qqj7nmxncgyy4d0dbxqz0', 10));
                                                                                           st_astext
    ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     POLYGON((-115.17282128334 36.1146408319473,-115.17282128334 36.1146461963654,-115.172810554504 36.1146461963654,-115.172810554504 36.1146408319473,-115.17282128334 36.1146408319473))
            
            

See Also
--------

?,?, ?

ST\_GeomFromGML
Takes as input GML representation of geometry and outputs a PostGIS
geometry object
geometry
ST\_GeomFromGML
text
geomgml
geometry
ST\_GeomFromGML
text
geomgml
integer
srid
Description
-----------

Constructs a PostGIS ST\_Geometry object from the OGC GML
representation.

ST\_GeomFromGML works only for GML Geometry fragments. It throws an
error if you try to use it on a whole GML document.

OGC GML versions supported:

-  GML 3.2.1 Namespace

-  GML 3.1.1 Simple Features profile SF-2 (with GML 3.1.0 and 3.0.0
   backward compatibility)

-  GML 2.1.2

OGC GML standards, cf: http://www.opengeospatial.org/standards/gml:

Availability: 1.5, requires libxml2 1.6+

Enhanced: 2.0.0 support for Polyhedral surfaces and TIN was introduced.

Enhanced: 2.0.0 default srid optional parameter added.

Z\_SUPPORT

P\_SUPPORT

T\_SUPPORT

GML allow mixed dimensions (2D and 3D inside the same MultiGeometry for
instance). As PostGIS geometries don't, ST\_GeomFromGML convert the
whole geometry to 2D if a missing Z dimension is found once.

GML support mixed SRS inside the same MultiGeometry. As PostGIS
geometries don't, ST\_GeomFromGML, in this case, reproject all
subgeometries to the SRS root node. If no srsName attribute available
for the GML root node, the function throw an error.

ST\_GeomFromGML function is not pedantic about an explicit GML
namespace. You could avoid to mention it explicitly for common usages.
But you need it if you want to use XLink feature inside GML.

    **Note**

    ST\_GeomFromGML function not support SQL/MM curves geometries.

Examples - A single geometry with srsName
-----------------------------------------

::

    SELECT ST_GeomFromGML('
            <gml:LineString srsName="EPSG:4269">
                <gml:coordinates>
                    -71.16028,42.258729 -71.160837,42.259112 -71.161143,42.25932
                </gml:coordinates>
            </gml:LineString>');
            

Examples - XLink usage
----------------------

::

    SELECT ST_GeomFromGML('
            <gml:LineString xmlns:gml="http://www.opengis.net/gml" 
                    xmlns:xlink="http://www.w3.org/1999/xlink"
                    srsName="urn:ogc:def:crs:EPSG::4269">
                <gml:pointProperty>
                    <gml:Point gml:id="p1"><gml:pos>42.258729 -71.16028</gml:pos></gml:Point>
                </gml:pointProperty>
                <gml:pos>42.259112 -71.160837</gml:pos>
                <gml:pointProperty>
                    <gml:Point xlink:type="simple" xlink:href="#p1"/>
                </gml:pointProperty>
            </gml:LineString>'););
            

Examples - Polyhedral Surface
-----------------------------

::

    SELECT ST_AsEWKT(ST_GeomFromGML('
    <gml:PolyhedralSurface>
    <gml:polygonPatches>
      <gml:PolygonPatch>
        <gml:exterior>
          <gml:LinearRing><gml:posList srsDimension="3">0 0 0 0 0 1 0 1 1 0 1 0 0 0 0</gml:posList></gml:LinearRing>
        </gml:exterior>
      </gml:PolygonPatch>
      <gml:PolygonPatch>
        <gml:exterior>
            <gml:LinearRing><gml:posList srsDimension="3">0 0 0 0 1 0 1 1 0 1 0 0 0 0 0</gml:posList></gml:LinearRing>
        </gml:exterior>
      </gml:PolygonPatch>
      <gml:PolygonPatch>
        <gml:exterior>
            <gml:LinearRing><gml:posList srsDimension="3">0 0 0 1 0 0 1 0 1 0 0 1 0 0 0</gml:posList></gml:LinearRing>
        </gml:exterior>
      </gml:PolygonPatch>
      <gml:PolygonPatch>
        <gml:exterior>
            <gml:LinearRing><gml:posList srsDimension="3">1 1 0 1 1 1 1 0 1 1 0 0 1 1 0</gml:posList></gml:LinearRing>
        </gml:exterior>
      </gml:PolygonPatch>
      <gml:PolygonPatch>
        <gml:exterior>
            <gml:LinearRing><gml:posList srsDimension="3">0 1 0 0 1 1 1 1 1 1 1 0 0 1 0</gml:posList></gml:LinearRing>
        </gml:exterior>
      </gml:PolygonPatch>
      <gml:PolygonPatch>
        <gml:exterior>
            <gml:LinearRing><gml:posList srsDimension="3">0 0 1 1 0 1 1 1 1 0 1 1 0 0 1</gml:posList></gml:LinearRing>
        </gml:exterior>
      </gml:PolygonPatch>
    </gml:polygonPatches>
    </gml:PolyhedralSurface>'));

    -- result --
     POLYHEDRALSURFACE(((0 0 0,0 0 1,0 1 1,0 1 0,0 0 0)),
     ((0 0 0,0 1 0,1 1 0,1 0 0,0 0 0)),
     ((0 0 0,1 0 0,1 0 1,0 0 1,0 0 0)),
     ((1 1 0,1 1 1,1 0 1,1 0 0,1 1 0)),
     ((0 1 0,0 1 1,1 1 1,1 1 0,0 1 0)),
     ((0 0 1,1 0 1,1 1 1,0 1 1,0 0 1)))
            

See Also
--------

?, ?, ?

ST\_GeomFromGeoJSON
Takes as input a geojson representation of a geometry and outputs a
PostGIS geometry object
geometry
ST\_GeomFromGeoJSON
text
geomjson
Description
-----------

Constructs a PostGIS geometry object from the GeoJSON representation.

ST\_GeomFromGeoJSON works only for JSON Geometry fragments. It throws an
error if you try to use it on a whole JSON document.

Availability: 2.0.0 requires - JSON-C >= 0.9

    **Note**

    If you do not have JSON-C enabled, support you will get an error
    notice instead of seeing an output. To enable JSON-C, run configure
    --with-jsondir=/path/to/json-c. See ? for details.

Z\_SUPPORT

Examples
--------

::

    SELECT ST_AsText(ST_GeomFromGeoJSON('{"type":"Point","coordinates":[-48.23456,20.12345]}')) As wkt;
    wkt
    ------
    POINT(-48.23456 20.12345)

::

    -- a 3D linestring
    SELECT ST_AsText(ST_GeomFromGeoJSON('{"type":"LineString","coordinates":[[1,2,3],[4,5,6],[7,8,9]]}')) As wkt;

    wkt
    -------------------
    LINESTRING(1 2,4 5,7 8)

See Also
--------

?, ?, ?

ST\_GeomFromKML
Takes as input KML representation of geometry and outputs a PostGIS
geometry object
geometry
ST\_GeomFromKML
text
geomkml
Description
-----------

Constructs a PostGIS ST\_Geometry object from the OGC KML
representation.

ST\_GeomFromKML works only for KML Geometry fragments. It throws an
error if you try to use it on a whole KML document.

OGC KML versions supported:

-  KML 2.2.0 Namespace

OGC KML standards, cf: http://www.opengeospatial.org/standards/kml:

Availability: 1.5,libxml2 2.6+

Z\_SUPPORT

    **Note**

    ST\_GeomFromKML function not support SQL/MM curves geometries.

Examples - A single geometry with srsName
-----------------------------------------

::

    SELECT ST_GeomFromKML('
            <LineString>
                <coordinates>-71.1663,42.2614 
                    -71.1667,42.2616</coordinates>
            </LineString>');
            

See Also
--------

?, ?

ST\_GMLToSQL
Return a specified ST\_Geometry value from GML representation. This is
an alias name for ST\_GeomFromGML
geometry
ST\_GMLToSQL
text
geomgml
geometry
ST\_GMLToSQL
text
geomgml
integer
srid
Description
-----------

SQLMM\_COMPLIANT SQL-MM 3: 5.1.50 (except for curves support).

Availability: 1.5, requires libxml2 1.6+

Enhanced: 2.0.0 support for Polyhedral surfaces and TIN was introduced.

Enhanced: 2.0.0 default srid optional parameter added.

See Also
--------

?, ?, ?

ST\_GeomFromText
Return a specified ST\_Geometry value from Well-Known Text
representation (WKT).
geometry
ST\_GeomFromText
text
WKT
geometry
ST\_GeomFromText
text
WKT
integer
srid
Description
-----------

Constructs a PostGIS ST\_Geometry object from the OGC Well-Known text
representation.

    **Note**

    There are 2 variants of ST\_GeomFromText function, the first takes
    no SRID and returns a geometry with no defined spatial reference
    system. The second takes a spatial reference id as the second
    argument and returns an ST\_Geometry that includes this srid as part
    of its meta-data. The srid must be defined in the spatial\_ref\_sys
    table.

SFS\_COMPLIANT s3.2.6.2 - option SRID is from the conformance suite.

SQLMM\_COMPLIANT SQL-MM 3: 5.1.40

CURVE\_SUPPORT

    **Warning**

    Changed: 2.0.0 In prior versions of PostGIS
    ST\_GeomFromText('GEOMETRYCOLLECTION(EMPTY)') was allowed. This is
    now illegal in PostGIS 2.0.0 to better conform with SQL/MM
    standards. This should now be written as
    ST\_GeomFromText('GEOMETRYCOLLECTION EMPTY')

Examples
--------

::

    SELECT ST_GeomFromText('LINESTRING(-71.160281 42.258729,-71.160837 42.259113,-71.161144 42.25932)');
    SELECT ST_GeomFromText('LINESTRING(-71.160281 42.258729,-71.160837 42.259113,-71.161144 42.25932)',4269);

    SELECT ST_GeomFromText('MULTILINESTRING((-71.160281 42.258729,-71.160837 42.259113,-71.161144 42.25932))');

    SELECT ST_GeomFromText('POINT(-71.064544 42.28787)');

    SELECT ST_GeomFromText('POLYGON((-71.1776585052917 42.3902909739571,-71.1776820268866 42.3903701743239,
    -71.1776063012595 42.3903825660754,-71.1775826583081 42.3903033653531,-71.1776585052917 42.3902909739571))');

    SELECT ST_GeomFromText('MULTIPOLYGON(((-71.1031880899493 42.3152774590236,
    -71.1031627617667 42.3152960829043,-71.102923838298 42.3149156848307,
    -71.1023097974109 42.3151969047397,-71.1019285062273 42.3147384934248,
    -71.102505233663 42.3144722937587,-71.10277487471 42.3141658254797,
    -71.103113945163 42.3142739188902,-71.10324876416 42.31402489987,
    -71.1033002961013 42.3140393340215,-71.1033488797549 42.3139495090772,
    -71.103396240451 42.3138632439557,-71.1041521907712 42.3141153348029,
    -71.1041411411543 42.3141545014533,-71.1041287795912 42.3142114839058,
    -71.1041188134329 42.3142693656241,-71.1041112482575 42.3143272556118,
    -71.1041072845732 42.3143851580048,-71.1041057218871 42.3144430686681,
    -71.1041065602059 42.3145009876017,-71.1041097995362 42.3145589148055,
    -71.1041166403905 42.3146168544148,-71.1041258822717 42.3146748022936,
    -71.1041375307579 42.3147318674446,-71.1041492906949 42.3147711126569,
    -71.1041598612795 42.314808571739,-71.1042515013869 42.3151287620809,
    -71.1041173835118 42.3150739481917,-71.1040809891419 42.3151344119048,
    -71.1040438678912 42.3151191367447,-71.1040194562988 42.3151832057859,
    -71.1038734225584 42.3151140942995,-71.1038446938243 42.3151006300338,
    -71.1038315271889 42.315094347535,-71.1037393329282 42.315054824985,
    -71.1035447555574 42.3152608696313,-71.1033436658644 42.3151648370544,
    -71.1032580383161 42.3152269126061,-71.103223066939 42.3152517403219,
    -71.1031880899493 42.3152774590236)),
    ((-71.1043632495873 42.315113108546,-71.1043583974082 42.3151211109857,
    -71.1043443253471 42.3150676015829,-71.1043850704575 42.3150793250568,-71.1043632495873 42.315113108546)))',4326);

    SELECT ST_GeomFromText('CIRCULARSTRING(220268 150415,220227 150505,220227 150406)');
        

See Also
--------

?, ?, ?

ST\_GeomFromWKB
Creates a geometry instance from a Well-Known Binary geometry
representation (WKB) and optional SRID.
geometry
ST\_GeomFromWKB
bytea
geom
geometry
ST\_GeomFromWKB
bytea
geom
integer
srid
Description
-----------

The ``ST_GeomFromWKB`` function, takes a well-known binary
representation of a geometry and a Spatial Reference System ID
(``SRID``) and creates an instance of the appropriate geometry type.
This function plays the role of the Geometry Factory in SQL. This is an
alternate name for ST\_WKBToSQL.

If SRID is not specified, it defaults to 0 (Unknown).

SFS\_COMPLIANT s3.2.7.2 - the optional SRID is from the conformance
suite

SQLMM\_COMPLIANT SQL-MM 3: 5.1.41

CURVE\_SUPPORT

Examples
--------

::

    --Although bytea rep contains single \, these need to be escaped when inserting into a table 
            -- unless standard_conforming_strings is set to on.
    SELECT ST_AsEWKT(
    ST_GeomFromWKB(E'\\001\\002\\000\\000\\000\\002\\000\\000\\000\\037\\205\\353Q\\270~\\\\\\300\\323Mb\\020X\\231C@\\020X9\\264\\310~\\\\\\300)\\\\\\217\\302\\365\\230C@',4326)
    );
                          st_asewkt
    ------------------------------------------------------
     SRID=4326;LINESTRING(-113.98 39.198,-113.981 39.195)
    (1 row)

    SELECT
      ST_AsText(
        ST_GeomFromWKB(
          ST_AsEWKB('POINT(2 5)'::geometry)
        )
      );
     st_astext
    ------------
     POINT(2 5)
    (1 row)

See Also
--------

?, ?, ?

ST\_LineFromMultiPoint
Creates a LineString from a MultiPoint geometry.
geometry
ST\_LineFromMultiPoint
geometry
aMultiPoint
Description
-----------

Creates a LineString from a MultiPoint geometry.

Z\_SUPPORT

Examples
--------

::

    --Create a 3d line string from a 3d multipoint
    SELECT ST_AsEWKT(ST_LineFromMultiPoint(ST_GeomFromEWKT('MULTIPOINT(1 2 3, 4 5 6, 7 8 9)')));
    --result--
    LINESTRING(1 2 3,4 5 6,7 8 9)
            

See Also
--------

?, ?, ?

ST\_LineFromText
Makes a Geometry from WKT representation with the given SRID. If SRID is
not given, it defaults to 0.
geometry
ST\_LineFromText
text
WKT
geometry
ST\_LineFromText
text
WKT
integer
srid
Description
-----------

Makes a Geometry from WKT with the given SRID. If SRID is not give, it
defaults to 0. If WKT passed in is not a LINESTRING, then null is
returned.

    **Note**

    OGC SPEC 3.2.6.2 - option SRID is from the conformance suite.

    **Note**

    If you know all your geometries are LINESTRINGS, its more efficient
    to just use ST\_GeomFromText. This just calls ST\_GeomFromText and
    adds additional validation that it returns a linestring.

SFS\_COMPLIANT s3.2.6.2

SQLMM\_COMPLIANT SQL-MM 3: 7.2.8

Examples
--------

::

    SELECT ST_LineFromText('LINESTRING(1 2, 3 4)') AS aline, ST_LineFromText('POINT(1 2)') AS null_return;
    aline                            | null_return
    ------------------------------------------------
    010200000002000000000000000000F ... | t
            

See Also
--------

?

ST\_LineFromWKB
Makes a
LINESTRING
from WKB with the given SRID
geometry
ST\_LineFromWKB
bytea
WKB
geometry
ST\_LineFromWKB
bytea
WKB
integer
srid
Description
-----------

The ``ST_LineFromWKB`` function, takes a well-known binary
representation of geometry and a Spatial Reference System ID (``SRID``)
and creates an instance of the appropriate geometry type - in this case,
a ``LINESTRING`` geometry. This function plays the role of the Geometry
Factory in SQL.

If an SRID is not specified, it defaults to 0. ``NULL`` is returned if
the input ``bytea`` does not represent a ``LINESTRING``.

    **Note**

    OGC SPEC 3.2.6.2 - option SRID is from the conformance suite.

    **Note**

    If you know all your geometries are ``LINESTRING``\ s, its more
    efficient to just use ?. This function just calls ? and adds
    additional validation that it returns a linestring.

SFS\_COMPLIANT s3.2.6.2

SQLMM\_COMPLIANT SQL-MM 3: 7.2.9

Examples
--------

::

    SELECT ST_LineFromWKB(ST_AsBinary(ST_GeomFromText('LINESTRING(1 2, 3 4)'))) AS aline,
            ST_LineFromWKB(ST_AsBinary(ST_GeomFromText('POINT(1 2)'))) IS NULL AS null_return;
    aline                            | null_return
    ------------------------------------------------
    010200000002000000000000000000F ... | t
            

See Also
--------

?, ?

ST\_LinestringFromWKB
Makes a geometry from WKB with the given SRID.
geometry
ST\_LinestringFromWKB
bytea
WKB
geometry
ST\_LinestringFromWKB
bytea
WKB
integer
srid
Description
-----------

The ``ST_LinestringFromWKB`` function, takes a well-known binary
representation of geometry and a Spatial Reference System ID (``SRID``)
and creates an instance of the appropriate geometry type - in this case,
a ``LINESTRING`` geometry. This function plays the role of the Geometry
Factory in SQL.

If an SRID is not specified, it defaults to 0. ``NULL`` is returned if
the input ``bytea`` does not represent a ``LINESTRING`` geometry. This
an alias for ?.

    **Note**

    OGC SPEC 3.2.6.2 - optional SRID is from the conformance suite.

    **Note**

    If you know all your geometries are ``LINESTRING``\ s, it's more
    efficient to just use ?. This function just calls ? and adds
    additional validation that it returns a ``LINESTRING``.

SFS\_COMPLIANT s3.2.6.2

SQLMM\_COMPLIANT SQL-MM 3: 7.2.9

Examples
--------

::

    SELECT
      ST_LineStringFromWKB(
        ST_AsBinary(ST_GeomFromText('LINESTRING(1 2, 3 4)'))
      ) AS aline,
      ST_LinestringFromWKB(
        ST_AsBinary(ST_GeomFromText('POINT(1 2)'))
      ) IS NULL AS null_return;
       aline                            | null_return
    ------------------------------------------------
    010200000002000000000000000000F ... | t

See Also
--------

?, ?

ST\_MakeBox2D
Creates a BOX2D defined by the given point geometries.
box2d
ST\_MakeBox2D
geometry
pointLowLeft
geometry
pointUpRight
Description
-----------

Creates a BOX2D defined by the given point geometries. This is useful
for doing range queries

Examples
--------

::

    --Return all features that fall reside or partly reside in a US national atlas coordinate bounding box
    --It is assumed here that the geometries are stored with SRID = 2163 (US National atlas equal area)
    SELECT feature_id, feature_name, the_geom
    FROM features
    WHERE the_geom && ST_SetSRID(ST_MakeBox2D(ST_Point(-989502.1875, 528439.5625),
        ST_Point(-987121.375 ,529933.1875)),2163)

See Also
--------

?, ?, ?, ?

ST\_3DMakeBox
Creates a BOX3D defined by the given 3d point geometries.
box3d
ST\_3DMakeBox
geometry
point3DLowLeftBottom
geometry
point3DUpRightTop
Description
-----------

Creates a BOX3D defined by the given 2 3D point geometries.

|image0| This function supports 3d and will not drop the z-index.

Changed: 2.0.0 In prior versions this used to be called ST\_MakeBox3D

Examples
--------

::

    SELECT ST_3DMakeBox(ST_MakePoint(-989502.1875, 528439.5625, 10),
        ST_MakePoint(-987121.375 ,529933.1875, 10)) As abb3d

    --bb3d--
    --------
    BOX3D(-989502.1875 528439.5625 10,-987121.375 529933.1875 10)
        

See Also
--------

?, ?, ?

ST\_MakeLine
Creates a Linestring from point or line geometries.
geometry
ST\_MakeLine
geometry set
geoms
geometry
ST\_MakeLine
geometry
geom1
geometry
geom2
geometry
ST\_MakeLine
geometry[]
geoms\_array
Description
-----------

ST\_MakeLine comes in 3 forms: a spatial aggregate that takes rows of
point-or-line geometries and returns a line string, a function that
takes an array of point-or-lines, and a regular function that takes two
point-or-line geometries. You might want to use a subselect to order
points before feeding them to the aggregate version of this function.

When adding line components a common node is removed from the output.

Z\_SUPPORT

Availability: 1.4.0 - ST\_MakeLine(geomarray) was introduced.
ST\_MakeLine aggregate functions was enhanced to handle more points
faster.

Availability: 2.0.0 - Support for linestring input elements was
introduced

Examples: Spatial Aggregate version
-----------------------------------

This example takes a sequence of GPS points and creates one record for
each gps travel where the geometry field is a line string composed of
the gps points in the order of the travel.

::

    -- For pre-PostgreSQL 9.0 - this usually works, 
    -- but the planner may on occasion choose not to respect the order of the subquery
    SELECT gps.gps_track, ST_MakeLine(gps.the_geom) As newgeom
        FROM (SELECT gps_track,gps_time, the_geom
                FROM gps_points ORDER BY gps_track, gps_time) As gps
        GROUP BY gps.gps_track;

::

    -- If you are using PostgreSQL 9.0+ 
    -- (you can use the new ORDER BY support for aggregates)
    -- this is a guaranteed way to get a correctly ordered linestring
    -- Your order by part can order by more than one column if needed
    SELECT gps.gps_track, ST_MakeLine(gps.the_geom ORDER BY gps_time) As newgeom
        FROM gps_points As gps
        GROUP BY gps.gps_track;

Examples: Non-Spatial Aggregate version
---------------------------------------

First example is a simple one off line string composed of 2 points. The
second formulates line strings from 2 points a user draws. The third is
a one-off that joins 2 3d points to create a line in 3d space.

::

    SELECT ST_AsText(ST_MakeLine(ST_MakePoint(1,2), ST_MakePoint(3,4)));
          st_astext
    ---------------------
     LINESTRING(1 2,3 4)

    SELECT userpoints.id, ST_MakeLine(startpoint, endpoint) As drawn_line
        FROM userpoints ;

    SELECT ST_AsEWKT(ST_MakeLine(ST_MakePoint(1,2,3), ST_MakePoint(3,4,5)));
            st_asewkt
    -------------------------
     LINESTRING(1 2 3,3 4 5)
                

Examples: Using Array version
-----------------------------

::

    SELECT ST_MakeLine(ARRAY(SELECT ST_Centroid(the_geom) FROM visit_locations ORDER BY visit_time));

    --Making a 3d line with 3 3-d points
    SELECT ST_AsEWKT(ST_MakeLine(ARRAY[ST_MakePoint(1,2,3),
                    ST_MakePoint(3,4,5), ST_MakePoint(6,6,6)]));
            st_asewkt
    -------------------------
    LINESTRING(1 2 3,3 4 5,6 6 6)
                

See Also
--------

?, ?, ?, ?

ST\_MakeEnvelope
Creates a rectangular Polygon formed from the given minimums and
maximums. Input values must be in SRS specified by the SRID.
geometry
ST\_MakeEnvelope
double precision
xmin
double precision
ymin
double precision
xmax
double precision
ymax
integer
srid=unknown
Description
-----------

Creates a rectangular Polygon formed from the minima and maxima. by the
given shell. Input values must be in SRS specified by the SRID. If no
SRID is specified the unknown spatial reference system is assumed

Availability: 1.5

Enhanced: 2.0: Ability to specify an envelope without specifying an SRID
was introduced.

Example: Building a bounding box polygon
----------------------------------------

::

    SELECT ST_AsText(ST_MakeEnvelope(10, 10, 11, 11, 4326));

    st_asewkt
    -----------
    POLYGON((10 10, 10 11, 11 11, 11 10, 10 10))
                  

See Also
--------

?, ?, ?

ST\_MakePolygon
Creates a Polygon formed by the given shell. Input geometries must be
closed LINESTRINGS.
geometry
ST\_MakePolygon
geometry
linestring
geometry
ST\_MakePolygon
geometry
outerlinestring
geometry[]
interiorlinestrings
Description
-----------

Creates a Polygon formed by the given shell. Input geometries must be
closed LINESTRINGS. Comes in 2 variants.

Variant 1: takes one closed linestring.

Variant 2: Creates a Polygon formed by the given shell and array of
holes. You can construct a geometry array using ST\_Accum or the
PostgreSQL ARRAY[] and ARRAY() constructs. Input geometries must be
closed LINESTRINGS.

    **Note**

    This function will not accept a MULTILINESTRING. Use ? or ? to
    generate line strings.

Z\_SUPPORT

Examples: Single closed LINESTRING
----------------------------------

::

    --2d line
    SELECT ST_MakePolygon(ST_GeomFromText('LINESTRING(75.15 29.53,77 29,77.6 29.5, 75.15 29.53)'));
    --If linestring is not closed
    --you can add the start point to close it
    SELECT ST_MakePolygon(ST_AddPoint(foo.open_line, ST_StartPoint(foo.open_line)))
    FROM (
    SELECT ST_GeomFromText('LINESTRING(75.15 29.53,77 29,77.6 29.5)') As open_line) As foo;

    --3d closed line
    SELECT ST_MakePolygon(ST_GeomFromText('LINESTRING(75.15 29.53 1,77 29 1,77.6 29.5 1, 75.15 29.53 1)'));

    st_asewkt
    -----------
    POLYGON((75.15 29.53 1,77 29 1,77.6 29.5 1,75.15 29.53 1))

    --measured line --
    SELECT ST_MakePolygon(ST_GeomFromText('LINESTRINGM(75.15 29.53 1,77 29 1,77.6 29.5 2, 75.15 29.53 2)'));

    st_asewkt
    ----------
    POLYGONM((75.15 29.53 1,77 29 1,77.6 29.5 2,75.15 29.53 2))
                  

Examples: Outter shell with inner shells
----------------------------------------

Build a donut with an ant hole

::

    SELECT ST_MakePolygon(
            ST_ExteriorRing(ST_Buffer(foo.line,10)),
        ARRAY[ST_Translate(foo.line,1,1),
            ST_ExteriorRing(ST_Buffer(ST_MakePoint(20,20),1)) ]
        )
    FROM
        (SELECT ST_ExteriorRing(ST_Buffer(ST_MakePoint(10,10),10,10))
            As line )
            As foo;
            

Build province boundaries with holes representing lakes in the province
from a set of province polygons/multipolygons and water line strings
this is an example of using PostGIS ST\_Accum

    **Note**

    The use of CASE because feeding a null array into ST\_MakePolygon
    results in NULL

    **Note**

    the use of left join to guarantee we get all provinces back even if
    they have no lakes

::

        SELECT p.gid, p.province_name,
            CASE WHEN
                ST_Accum(w.the_geom) IS NULL THEN p.the_geom
            ELSE  ST_MakePolygon(ST_LineMerge(ST_Boundary(p.the_geom)), ST_Accum(w.the_geom)) END
        FROM
            provinces p LEFT JOIN waterlines w
                ON (ST_Within(w.the_geom, p.the_geom) AND ST_IsClosed(w.the_geom))
        GROUP BY p.gid, p.province_name, p.the_geom;

        --Same example above but utilizing a correlated subquery
        --and PostgreSQL built-in ARRAY() function that converts a row set to an array

        SELECT p.gid,  p.province_name, CASE WHEN
            EXISTS(SELECT w.the_geom
                FROM waterlines w
                WHERE ST_Within(w.the_geom, p.the_geom)
                AND ST_IsClosed(w.the_geom))
            THEN
            ST_MakePolygon(ST_LineMerge(ST_Boundary(p.the_geom)),
                ARRAY(SELECT w.the_geom
                    FROM waterlines w
                    WHERE ST_Within(w.the_geom, p.the_geom)
                    AND ST_IsClosed(w.the_geom)))
            ELSE p.the_geom END As the_geom
        FROM
            provinces p;
                  

See Also
--------

?, ?, ?, ?, ?, ?

ST\_MakePoint
Creates a 2D,3DZ or 4D point geometry.
geometry
ST\_MakePoint
double precision
x
double precision
y
geometry
ST\_MakePoint
double precision
x
double precision
y
double precision
z
geometry
ST\_MakePoint
double precision
x
double precision
y
double precision
z
double precision
m
Description
-----------

Creates a 2D,3DZ or 4D point geometry (geometry with measure).
``ST_MakePoint`` while not being OGC compliant is generally faster and
more precise than ? and ?. It is also easier to use if you have raw
coordinates rather than WKT.

    **Note**

    Note x is longitude and y is latitude

    **Note**

    Use ? if you need to make a point with x,y,m.

Z\_SUPPORT

Examples
--------

::

    --Return point with unknown SRID
    SELECT ST_MakePoint(-71.1043443253471, 42.3150676015829);

    --Return point marked as WGS 84 long lat
    SELECT ST_SetSRID(ST_MakePoint(-71.1043443253471, 42.3150676015829),4326);

    --Return a 3D point (e.g. has altitude)
    SELECT ST_MakePoint(1, 2,1.5);

    --Get z of point
    SELECT ST_Z(ST_MakePoint(1, 2,1.5));
    result
    -------
    1.5

See Also
--------

?, ?, ?, ?

ST\_MakePointM
Creates a point geometry with an x y and m coordinate.
geometry
ST\_MakePointM
float
x
float
y
float
m
Description
-----------

Creates a point with x, y and measure coordinates.

    **Note**

    Note x is longitude and y is latitude.

Examples
--------

We use ST\_AsEWKT in these examples to show the text representation
instead of ST\_AsText because ST\_AsText does not support returning M.

::

    --Return EWKT representation of point with unknown SRID
    SELECT ST_AsEWKT(ST_MakePointM(-71.1043443253471, 42.3150676015829, 10));

    --result
                       st_asewkt
    -----------------------------------------------
     POINTM(-71.1043443253471 42.3150676015829 10)

    --Return EWKT representation of point with measure marked as WGS 84 long lat
    SELECT ST_AsEWKT(ST_SetSRID(ST_MakePointM(-71.1043443253471, 42.3150676015829,10),4326));

                            st_asewkt
    ---------------------------------------------------------
    SRID=4326;POINTM(-71.1043443253471 42.3150676015829 10)

    --Return a 3d point (e.g. has altitude)
    SELECT ST_MakePoint(1, 2,1.5);

    --Get m of point
    SELECT ST_M(ST_MakePointM(-71.1043443253471, 42.3150676015829,10));
    result
    -------
    10
                  

See Also
--------

?, ?, ?

ST\_MLineFromText
Return a specified ST\_MultiLineString value from WKT representation.
geometry
ST\_MLineFromText
text
WKT
integer
srid
geometry
ST\_MLineFromText
text
WKT
Description
-----------

Makes a Geometry from Well-Known-Text (WKT) with the given SRID. If SRID
is not give, it defaults to 0.

OGC SPEC 3.2.6.2 - option SRID is from the conformance suite

Returns null if the WKT is not a MULTILINESTRING

    **Note**

    If you are absolutely sure all your WKT geometries are points, don't
    use this function. It is slower than ST\_GeomFromText since it adds
    an additional validation step.

SFS\_COMPLIANT s3.2.6.2

SQLMM\_COMPLIANTSQL-MM 3: 9.4.4

Examples
--------

::

    SELECT ST_MLineFromText('MULTILINESTRING((1 2, 3 4), (4 5, 6 7))');

See Also
--------

?

ST\_MPointFromText
Makes a Geometry from WKT with the given SRID. If SRID is not give, it
defaults to 0.
geometry
ST\_MPointFromText
text
WKT
integer
srid
geometry
ST\_MPointFromText
text
WKT
Description
-----------

Makes a Geometry from WKT with the given SRID. If SRID is not give, it
defaults to 0.

OGC SPEC 3.2.6.2 - option SRID is from the conformance suite

Returns null if the WKT is not a MULTIPOINT

    **Note**

    If you are absolutely sure all your WKT geometries are points, don't
    use this function. It is slower than ST\_GeomFromText since it adds
    an additional validation step.

SFS\_COMPLIANT 3.2.6.2

SQLMM\_COMPLIANT SQL-MM 3: 9.2.4

Examples
--------

::

    SELECT ST_MPointFromText('MULTIPOINT(1 2, 3 4)');
    SELECT ST_MPointFromText('MULTIPOINT(-70.9590 42.1180, -70.9611 42.1223)', 4326);

See Also
--------

?

ST\_MPolyFromText
Makes a MultiPolygon Geometry from WKT with the given SRID. If SRID is
not give, it defaults to 0.
geometry
ST\_MPolyFromText
text
WKT
integer
srid
geometry
ST\_MPolyFromText
text
WKT
Description
-----------

Makes a MultiPolygon from WKT with the given SRID. If SRID is not give,
it defaults to 0.

OGC SPEC 3.2.6.2 - option SRID is from the conformance suite

Throws an error if the WKT is not a MULTIPOLYGON

    **Note**

    If you are absolutely sure all your WKT geometries are
    multipolygons, don't use this function. It is slower than
    ST\_GeomFromText since it adds an additional validation step.

SFS\_COMPLIANT s3.2.6.2

SQLMM\_COMPLIANT SQL-MM 3: 9.6.4

Examples
--------

::

    SELECT ST_MPolyFromText('MULTIPOLYGON(((0 0 1,20 0 1,20 20 1,0 20 1,0 0 1),(5 5 3,5 7 3,7 7 3,7 5 3,5 5 3)))');
    SELECt ST_MPolyFromText('MULTIPOLYGON(((-70.916 42.1002,-70.9468 42.0946,-70.9765 42.0872,-70.9754 42.0875,-70.9749 42.0879,-70.9752 42.0881,-70.9754 42.0891,-70.9758 42.0894,-70.9759 42.0897,-70.9759 42.0899,-70.9754 42.0902,-70.9756 42.0906,-70.9753 42.0907,-70.9753 42.0917,-70.9757 42.0924,-70.9755 42.0928,-70.9755 42.0942,-70.9751 42.0948,-70.9755 42.0953,-70.9751 42.0958,-70.9751 42.0962,-70.9759 42.0983,-70.9767 42.0987,-70.9768 42.0991,-70.9771 42.0997,-70.9771 42.1003,-70.9768 42.1005,-70.977 42.1011,-70.9766 42.1019,-70.9768 42.1026,-70.9769 42.1033,-70.9775 42.1042,-70.9773 42.1043,-70.9776 42.1043,-70.9778 42.1048,-70.9773 42.1058,-70.9774 42.1061,-70.9779 42.1065,-70.9782 42.1078,-70.9788 42.1085,-70.9798 42.1087,-70.9806 42.109,-70.9807 42.1093,-70.9806 42.1099,-70.9809 42.1109,-70.9808 42.1112,-70.9798 42.1116,-70.9792 42.1127,-70.979 42.1129,-70.9787 42.1134,-70.979 42.1139,-70.9791 42.1141,-70.9987 42.1116,-71.0022 42.1273,
        -70.9408 42.1513,-70.9315 42.1165,-70.916 42.1002)))',4326);

See Also
--------

?, ?

ST\_Point
Returns an ST\_Point with the given coordinate values. OGC alias for
ST\_MakePoint.
geometry
ST\_Point
float
x\_lon
float
y\_lat
Description
-----------

Returns an ST\_Point with the given coordinate values. MM compliant
alias for ST\_MakePoint that takes just an x and y.

SQLMM\_COMPLIANT SQL-MM 3: 6.1.2

Examples: Geometry
------------------

::

    SELECT ST_SetSRID(ST_Point(-71.1043443253471, 42.3150676015829),4326)

Examples: Geography
-------------------

::

    SELECT CAST(ST_SetSRID(ST_Point(-71.1043443253471, 42.3150676015829),4326) As geography);

::

    -- the :: is PostgreSQL short-hand for casting.
    SELECT ST_SetSRID(ST_Point(-71.1043443253471, 42.3150676015829),4326)::geography;

::

    --If your point coordinates are in a different spatial reference from WGS-84 long lat, then you need to transform before casting
    -- This example we convert a point in Pennsylvania State Plane feet to WGS 84 and then geography
    SELECT ST_Transform(ST_SetSRID(ST_Point(3637510, 3014852),2273),4326)::geography;

See Also
--------

?, ?, ?, ?

ST\_PointFromGeoHash
Return a point from a GeoHash string.
point
ST\_PointFromGeoHash
text
geohash
integer
precision=full\_precision\_of\_geohash
Description
-----------

Return a point from a GeoHash string. The point represents the center
point of the GeoHash.

If no ``precision`` is specficified ST\_PointFromGeoHash returns a point
based on full precision of the input GeoHash string.

If ``precision`` is specified ST\_PointFromGeoHash will use that many
characters from the GeoHash to create the point.

Availability: 2.1.0

Examples
--------

::

    SELECT ST_AsText(ST_PointFromGeoHash('9qqj7nmxncgyy4d0dbxqz0'));
              st_astext
    ------------------------------
     POINT(-115.172816 36.114646)

    SELECT ST_AsText(ST_PointFromGeoHash('9qqj7nmxncgyy4d0dbxqz0', 4));
                 st_astext
    -----------------------------------
     POINT(-115.13671875 36.123046875)

    SELECT ST_AsText(ST_PointFromGeoHash('9qqj7nmxncgyy4d0dbxqz0', 10));
                     st_astext
    -------------------------------------------
     POINT(-115.172815918922 36.1146435141563)
            
            

See Also
--------

?, ?, ?

ST\_PointFromText
Makes a point Geometry from WKT with the given SRID. If SRID is not
given, it defaults to unknown.
geometry
ST\_PointFromText
text
WKT
geometry
ST\_PointFromText
text
WKT
integer
srid
Description
-----------

Constructs a PostGIS ST\_Geometry point object from the OGC Well-Known
text representation. If SRID is not give, it defaults to unknown
(currently 0). If geometry is not a WKT point representation, returns
null. If completely invalid WKT, then throws an error.

    **Note**

    There are 2 variants of ST\_PointFromText function, the first takes
    no SRID and returns a geometry with no defined spatial reference
    system. The second takes a spatial reference id as the second
    argument and returns an ST\_Geometry that includes this srid as part
    of its meta-data. The srid must be defined in the spatial\_ref\_sys
    table.

    **Note**

    If you are absolutely sure all your WKT geometries are points, don't
    use this function. It is slower than ST\_GeomFromText since it adds
    an additional validation step. If you are building points from long
    lat coordinates and care more about performance and accuracy than
    OGC compliance, use ? or OGC compliant alias ?.

SFS\_COMPLIANT s3.2.6.2 - option SRID is from the conformance suite.

SQLMM\_COMPLIANT SQL-MM 3: 6.1.8

Examples
--------

::

    SELECT ST_PointFromText('POINT(-71.064544 42.28787)');
    SELECT ST_PointFromText('POINT(-71.064544 42.28787)', 4326);
        

See Also
--------

?, ?, ?, ?

ST\_PointFromWKB
Makes a geometry from WKB with the given SRID
geometry
ST\_GeomFromWKB
bytea
geom
geometry
ST\_GeomFromWKB
bytea
geom
integer
srid
Description
-----------

The ``ST_PointFromWKB`` function, takes a well-known binary
representation of geometry and a Spatial Reference System ID (``SRID``)
and creates an instance of the appropriate geometry type - in this case,
a ``POINT`` geometry. This function plays the role of the Geometry
Factory in SQL.

If an SRID is not specified, it defaults to 0. ``NULL`` is returned if
the input ``bytea`` does not represent a ``POINT`` geometry.

SFS\_COMPLIANT s3.2.7.2

SQLMM\_COMPLIANT SQL-MM 3: 6.1.9

Z\_SUPPORT

CURVE\_SUPPORT

Examples
--------

::

    SELECT
      ST_AsText(
        ST_PointFromWKB(
          ST_AsEWKB('POINT(2 5)'::geometry)
        )
      );
     st_astext
    ------------
     POINT(2 5)
    (1 row)

    SELECT
      ST_AsText(
        ST_PointFromWKB(
          ST_AsEWKB('LINESTRING(2 5, 2 6)'::geometry)
        )
      );
     st_astext
    -----------

    (1 row)

See Also
--------

?, ?

ST\_Polygon
Returns a polygon built from the specified linestring and SRID.
geometry
ST\_Polygon
geometry
aLineString
integer
srid
Description
-----------

Returns a polygon built from the specified linestring and SRID.

    **Note**

    ST\_Polygon is similar to first version oST\_MakePolygon except it
    also sets the spatial ref sys (SRID) of the polygon. Will not work
    with MULTILINESTRINGS so use LineMerge to merge multilines. Also
    does not create polygons with holes. Use ST\_MakePolygon for that.

SFS\_COMPLIANT

SQLMM\_COMPLIANT SQL-MM 3: 8.3.2

Z\_SUPPORT

Examples
--------

::

    --a 2d polygon
    SELECT ST_Polygon(ST_GeomFromText('LINESTRING(75.15 29.53,77 29,77.6 29.5, 75.15 29.53)'), 4326);

    --result--
    POLYGON((75.15 29.53,77 29,77.6 29.5,75.15 29.53))
    --a 3d polygon
    SELECT ST_AsEWKT(ST_Polygon(ST_GeomFromEWKT('LINESTRING(75.15 29.53 1,77 29 1,77.6 29.5 1, 75.15 29.53 1)'), 4326));

    result
    ------
    SRID=4326;POLYGON((75.15 29.53 1,77 29 1,77.6 29.5 1,75.15 29.53 1))
                

See Also
--------

?, ?, ?, ?, ?, ?

ST\_PolygonFromText
Makes a Geometry from WKT with the given SRID. If SRID is not give, it
defaults to 0.
geometry
ST\_PolygonFromText
text
WKT
geometry
ST\_PolygonFromText
text
WKT
integer
srid
Description
-----------

Makes a Geometry from WKT with the given SRID. If SRID is not give, it
defaults to 0. Returns null if WKT is not a polygon.

OGC SPEC 3.2.6.2 - option SRID is from the conformance suite

    **Note**

    If you are absolutely sure all your WKT geometries are polygons,
    don't use this function. It is slower than ST\_GeomFromText since it
    adds an additional validation step.

SFS\_COMPLIANT s3.2.6.2

SQLMM\_COMPLIANT SQL-MM 3: 8.3.6

Examples
--------

::

    SELECT ST_PolygonFromText('POLYGON((-71.1776585052917 42.3902909739571,-71.1776820268866 42.3903701743239,
    -71.1776063012595 42.3903825660754,-71.1775826583081 42.3903033653531,-71.1776585052917 42.3902909739571))');
    st_polygonfromtext
    ------------------
    010300000001000000050000006...


    SELECT ST_PolygonFromText('POINT(1 2)') IS NULL as point_is_notpoly;

    point_is_not_poly
    ----------
    t

See Also
--------

?

ST\_WKBToSQL
Return a specified ST\_Geometry value from Well-Known Binary
representation (WKB). This is an alias name for ST\_GeomFromWKB that
takes no srid
geometry
ST\_WKBToSQL
bytea
WKB
Description
-----------

SQLMM\_COMPLIANT SQL-MM 3: 5.1.36

See Also
--------

?

ST\_WKTToSQL
Return a specified ST\_Geometry value from Well-Known Text
representation (WKT). This is an alias name for ST\_GeomFromText
geometry
ST\_WKTToSQL
text
WKT
Description
-----------

SQLMM\_COMPLIANT SQL-MM 3: 5.1.34

See Also
--------

?

.. |image0| image:: images/check.png
