Tiger Geocoder
==============

There are a couple other open source geocoders for PostGIS, that unlike
tiger geocoder have the advantage of multi-country geocoding support

-  `Nominatim <http://wiki.openstreetmap.org/wiki/Nominatim>`__ and uses
   OpenStreetMap gazeteer formatted data. It requires osm2pgsql for
   loading the data, PostgreSQL 8.4+ and PostGIS 1.5+ to function. It is
   packaged as a webservice interface and seems designed to be called as
   a webservice. Just like the tiger geocoder, it has both a geocoder
   and a reverse geocoder component. From the documentation, it is
   unclear if it has a pure SQL interface like the tiger geocoder, or if
   a good deal of the logic is implemented in the web interface.

-  `GIS Graphy <http://www.gisgraphy.com/>`__ also utilizes PostGIS and
   like Nominatim works with OpenStreetMap (OSM) data. It comes with a
   loader to load OSM data and similar to Nominatim is capable of
   geocoding not just US. Much like Nominatim, it runs as a webservice
   and relies on Java 1.5, Servlet apps, Solr. GisGraphy is
   cross-platform and also has a reverse geocoder among some other neat
   features.

Drop\_Indexes\_Generate\_Script
Generates a script that drops all non-primary key and non-unique indexes
on tiger schema and user specified schema. Defaults schema to
tiger\_data
if no schema is specified.
text
Drop\_Indexes\_Generate\_Script
text
param\_schema=tiger\_data
Description
-----------

Generates a script that drops all non-primary key and non-unique indexes
on tiger schema and user specified schema. Defaults schema to
``tiger_data`` if no schema is specified.

This is useful for minimizing index bloat that may confuse the query
planner or take up unnecessary space. Use in combination with ? to add
just the indexes used by the geocoder.

Availability: 2.0.0

Examples
--------

::

    SELECT drop_indexes_generate_script() As actionsql;
    actionsql
    ---------------------------------------------------------
    DROP INDEX tiger.idx_tiger_countysub_lookup_lower_name;
    DROP INDEX tiger.idx_tiger_edges_countyfp;
    DROP INDEX tiger.idx_tiger_faces_countyfp;
    DROP INDEX tiger.tiger_place_the_geom_gist;
    DROP INDEX tiger.tiger_edges_the_geom_gist;
    DROP INDEX tiger.tiger_state_the_geom_gist;
    DROP INDEX tiger.idx_tiger_addr_least_address;
    DROP INDEX tiger.idx_tiger_addr_tlid;
    DROP INDEX tiger.idx_tiger_addr_zip;
    DROP INDEX tiger.idx_tiger_county_countyfp;
    DROP INDEX tiger.idx_tiger_county_lookup_lower_name;
    DROP INDEX tiger.idx_tiger_county_lookup_snd_name;
    DROP INDEX tiger.idx_tiger_county_lower_name;
    DROP INDEX tiger.idx_tiger_county_snd_name;
    DROP INDEX tiger.idx_tiger_county_the_geom_gist;
    DROP INDEX tiger.idx_tiger_countysub_lookup_snd_name;
    DROP INDEX tiger.idx_tiger_cousub_countyfp;
    DROP INDEX tiger.idx_tiger_cousub_cousubfp;
    DROP INDEX tiger.idx_tiger_cousub_lower_name;
    DROP INDEX tiger.idx_tiger_cousub_snd_name;
    DROP INDEX tiger.idx_tiger_cousub_the_geom_gist;
    DROP INDEX tiger_data.idx_tiger_data_ma_addr_least_address;
    DROP INDEX tiger_data.idx_tiger_data_ma_addr_tlid;
    DROP INDEX tiger_data.idx_tiger_data_ma_addr_zip;
    DROP INDEX tiger_data.idx_tiger_data_ma_county_countyfp;
    DROP INDEX tiger_data.idx_tiger_data_ma_county_lookup_lower_name;
    DROP INDEX tiger_data.idx_tiger_data_ma_county_lookup_snd_name;
    DROP INDEX tiger_data.idx_tiger_data_ma_county_lower_name;
    DROP INDEX tiger_data.idx_tiger_data_ma_county_snd_name;
    :
    :

See Also
--------

?, ?

Drop\_Nation\_Tables\_Generate\_Script
Generates a script that drops all tables in the specified schema that
start with
county\_all
,
state\_all
or stae code followed by
county
or
state
.
text
Drop\_Nation\_Tables\_Generate\_Script
text
param\_schema=tiger\_data
Description
-----------

Generates a script that drops all tables in the specified schema that
start with ``county_all``, ``state_all`` or stae code followed by
``county`` or ``state``. This is needed if you are upgrading from
``tiger_2010`` to ``tiger_2011`` data.

Availability: 2.1.0

Examples
--------

::

    SELECT drop_nation_tables_generate_script();
    DROP TABLE tiger_data.county_all;
    DROP TABLE tiger_data.county_all_lookup;
    DROP TABLE tiger_data.state_all;
    DROP TABLE tiger_data.ma_county;
    DROP TABLE tiger_data.ma_state;

See Also
--------

?

Drop\_State\_Tables\_Generate\_Script
Generates a script that drops all tables in the specified schema that
are prefixed with the state abbreviation. Defaults schema to
tiger\_data
if no schema is specified.
text
Drop\_State\_Tables\_Generate\_Script
text
param\_state
text
param\_schema=tiger\_data
Description
-----------

Generates a script that drops all tables in the specified schema that
are prefixed with the state abbreviation. Defaults schema to
``tiger_data`` if no schema is specified. This function is useful for
dropping tables of a state just before you reload a state in case
something went wrong during your previous load.

Availability: 2.0.0

Examples
--------

::

    SELECT drop_state_tables_generate_script('PA');
    DROP TABLE tiger_data.pa_addr;
    DROP TABLE tiger_data.pa_county;
    DROP TABLE tiger_data.pa_county_lookup;
    DROP TABLE tiger_data.pa_cousub;
    DROP TABLE tiger_data.pa_edges;
    DROP TABLE tiger_data.pa_faces;
    DROP TABLE tiger_data.pa_featnames;
    DROP TABLE tiger_data.pa_place;
    DROP TABLE tiger_data.pa_state;
    DROP TABLE tiger_data.pa_zip_lookup_base;
    DROP TABLE tiger_data.pa_zip_state;
    DROP TABLE tiger_data.pa_zip_state_loc;
            

See Also
--------

?

Geocode
Takes in an address as a string (or other normalized address) and
outputs a set of possible locations which include a point geometry in
NAD 83 long lat, a normalized address for each, and the rating. The
lower the rating the more likely the match. Results are sorted by lowest
rating first. Can optionally pass in maximum results, defaults to 10,
and restrict\_region (defaults to NULL)
setof record
geocode
varchar
address
integer
max\_results=10
geometry
restrict\_region=NULL
norm\_addy
OUT addy
geometry
OUT geomout
integer
OUT rating
setof record
geocode
norm\_addy
in\_addy
integer
max\_results=10
geometry
restrict\_region=NULL
norm\_addy
OUT addy
geometry
OUT geomout
integer
OUT rating
Description
-----------

Takes in an address as a string (or already normalized address) and
outputs a set of possible locations which include a point geometry in
NAD 83 long lat, a ``normalized_address`` (addy) for each, and the
rating. The lower the rating the more likely the match. Results are
sorted by lowest rating first. Uses Tiger data (edges,faces,addr),
PostgreSQL fuzzy string matching (soundex,levenshtein) and PostGIS line
interpolation functions to interpolate address along the Tiger edges.
The higher the rating the less likely the geocode is right. The geocoded
point is defaulted to offset 10 meters from center-line off to side
(L/R) of street address is located on.

Enhanced: 2.0.0 to support Tiger 2010 structured data and revised some
logic to improve speed, accuracy of geocoding, and to offset point from
centerline to side of street address is located on. New parameter
max\_results useful for specifying ot just return the best result.

Examples: Basic
---------------

The below examples timings are on a 3.0 GHZ single processor Windows 7
machine with 2GB ram running PostgreSQL 9.1rc1/PostGIS 2.0 loaded with
all of MA,MN,CA, RI state Tiger data loaded.

Exact matches are faster to compute (61ms)

::

    SELECT g.rating, ST_X(g.geomout) As lon, ST_Y(g.geomout) As lat, 
        (addy).address As stno, (addy).streetname As street, 
        (addy).streettypeabbrev As styp, (addy).location As city, (addy).stateabbrev As st,(addy).zip 
        FROM geocode('75 State Street, Boston MA 02109') As g;  
     rating |        lon        |       lat        | stno | street | styp |  city  | st |  zip  
    --------+-------------------+------------------+------+--------+------+--------+----+-------
          0 | -71.0556722990239 | 42.3589914927049 |   75 | State  | St   | Boston | MA | 02109

Even if zip is not passed in the geocoder can guess (took about 122-150
ms)

::

    SELECT g.rating, ST_AsText(ST_SnapToGrid(g.geomout,0.00001)) As wktlonlat, 
        (addy).address As stno, (addy).streetname As street, 
        (addy).streettypeabbrev As styp, (addy).location As city, (addy).stateabbrev As st,(addy).zip 
        FROM geocode('226 Hanover Street, Boston, MA',1) As g;  
     rating |         wktlonlat         | stno | street  | styp |  city  | st |  zip  
    --------+---------------------------+------+---------+------+--------+----+-------
          1 | POINT(-71.05528 42.36316) |  226 | Hanover | St   | Boston | MA | 02113

Can handle misspellings and provides more than one possible solution
with ratings and takes longer (500ms).

::

    SELECT g.rating, ST_AsText(ST_SnapToGrid(g.geomout,0.00001)) As wktlonlat, 
        (addy).address As stno, (addy).streetname As street, 
        (addy).streettypeabbrev As styp, (addy).location As city, (addy).stateabbrev As st,(addy).zip 
        FROM geocode('31 - 37 Stewart Street, Boston, MA 02116') As g; 
     rating |         wktlonlat         | stno | street | styp |  city  | st |  zip  
    --------+---------------------------+------+--------+------+--------+----+-------
         70 | POINT(-71.06459 42.35113) |   31 | Stuart | St   | Boston | MA | 02116
        

Using to do a batch geocode of addresses. Easiest is to set
``max_results=1``. Only process those not yet geocoded (have no rating).

::

    CREATE TABLE addresses_to_geocode(addid serial PRIMARY KEY, address text,
            lon numeric, lat numeric, new_address text, rating integer);

    INSERT INTO addresses_to_geocode(address)
    VALUES ('529 Main Street, Boston MA, 02129'),
     ('77 Massachusetts Avenue, Cambridge, MA 02139'),
     ('25 Wizard of Oz, Walaford, KS 99912323'),
     ('26 Capen Street, Medford, MA'),
     ('124 Mount Auburn St, Cambridge, Massachusetts 02138'),
     ('950 Main Street, Worcester, MA 01610');
     
    -- only update the first 3 addresses (323-704 ms -  there are caching and shared memory effects so first geocode you do is always slower) --
    -- for large numbers of addresses you don't want to update all at once
    -- since the whole geocode must commit at once 
    -- For this example we rejoin with LEFT JOIN 
    -- and set to rating to -1 rating if no match 
    -- to ensure we don't regeocode a bad address 
    UPDATE addresses_to_geocode
      SET  (rating, new_address, lon, lat) 
        = ( COALESCE((g.geo).rating,-1), pprint_addy((g.geo).addy),
           ST_X((g.geo).geomout)::numeric(8,5), ST_Y((g.geo).geomout)::numeric(8,5) )
    FROM (SELECT addid 
        FROM addresses_to_geocode 
        WHERE rating IS NULL ORDER BY addid LIMIT 3) As a
        LEFT JOIN (SELECT addid, (geocode(address,1)) As geo
        FROM addresses_to_geocode As ag
        WHERE ag.rating IS NULL ORDER BY addid LIMIT 3) As g ON a.addid = g.addid
    WHERE a.addid = addresses_to_geocode.addid;

    result
    -----
    Query returned successfully: 3 rows affected, 480 ms execution time.

    SELECT * FROM addresses_to_geocode WHERE rating is not null;

     addid |                   address                    |    lon    |   lat    |                new_address                | rating 
    -------+----------------------------------------------+-----------+----------+-------------------------------------------+--------
         1 | 529 Main Street, Boston MA, 02129            | -71.07181 | 42.38359 | 529 Main St, Boston, MA 02129             |      0
         2 | 77 Massachusetts Avenue, Cambridge, MA 02139 | -71.09428 | 42.35988 | 77 Massachusetts Ave, Cambridge, MA 02139 |      0
         3 | 25 Wizard of Oz, Walaford, KS 99912323       |           |          |                                           |     -1

Examples: Using Geometry filter
-------------------------------

::

    SELECT g.rating, ST_AsText(ST_SnapToGrid(g.geomout,0.00001)) As wktlonlat, 
        (addy).address As stno, (addy).streetname As street, 
        (addy).streettypeabbrev As styp, 
        (addy).location As city, (addy).stateabbrev As st,(addy).zip 
      FROM geocode('100 Federal Street, MA',
            3, 
            (SELECT ST_Union(the_geom) 
                FROM place WHERE statefp = '25' AND name = 'Lynn')::geometry
            ) As g;

     rating |        wktlonlat         | stno | street  | styp | city | st |  zip
    --------+--------------------------+------+---------+------+------+----+-------
          8 | POINT(-70.96796 42.4659) |  100 | Federal | St   | Lynn | MA | 01905
    Total query runtime: 245 ms.
              

See Also
--------

?, ?, ?, ?, ?, ?

Geocode\_Intersection
Takes in 2 streets that intersect and a state, city, zip, and outputs a
set of possible locations on the first cross street that is at the
intersection, also includes a point geometry in NAD 83 long lat, a
normalized address for each location, and the rating. The lower the
rating the more likely the match. Results are sorted by lowest rating
first. Can optionally pass in maximum results, defaults to 10
setof record
geocode\_intersection
text
roadway1
text
roadway2
text
in\_state
text
in\_city
text
in\_zip
integer
max\_results=10
norm\_addy
OUT addy
geometry
OUT geomout
integer
OUT rating
Description
-----------

Takes in 2 streets that intersect and a state, city, zip, and outputs a
set of possible locations on the first cross street that is at the
intersection, also includes a point geometry in NAD 83 long lat, a
normalized address for each location, and the rating. The lower the
rating the more likely the match. Results are sorted by lowest rating
first. Can optionally pass in maximum results, defaults to 10. Returns
``normalized_address`` (addy) for each, geomout as the point location in
nad 83 long lat, and the rating. The lower the rating the more likely
the match. Results are sorted by lowest rating first. Uses Tiger data
(edges,faces,addr), PostgreSQL fuzzy string matching
(soundex,levenshtein)

Availability: 2.0.0

Examples: Basic
---------------

The below examples timings are on a 3.0 GHZ single processor Windows 7
machine with 2GB ram running PostgreSQL 9.0/PostGIS 1.5 loaded with all
of MA state Tiger data loaded. Currently a bit slow (3000 ms)

Testing on Windows 2003 64-bit 8GB on PostGIS 2.0 PostgreSQL 64-bit
Tiger 2011 data loaded -- (41ms)

::

    SELECT pprint_addy(addy), st_astext(geomout),rating 
                FROM geocode_intersection( 'Haverford St','Germania St', 'MA', 'Boston', '02130',1); 
               pprint_addy            |         st_astext          | rating
    ----------------------------------+----------------------------+--------
    98 Haverford St, Boston, MA 02130 | POINT(-71.101375 42.31376) |      0

Even if zip is not passed in the geocoder can guess (took about 3500 ms
on the windows 7 box), on the windows 2003 64-bit 741 ms

::

    SELECT pprint_addy(addy), st_astext(geomout),rating 
                    FROM geocode_intersection('Weld', 'School', 'MA', 'Boston');
              pprint_addy          |        st_astext         | rating
    -------------------------------+--------------------------+--------
     98 Weld Ave, Boston, MA 02119 | POINT(-71.099 42.314234) |      3
     99 Weld Ave, Boston, MA 02119 | POINT(-71.099 42.314234) |      3

See Also
--------

?, ?, ?

Get\_Geocode\_Setting
Returns value of specific setting stored in tiger.geocode\_settings
table.
text
Get\_Geocode\_Setting
text
setting\_name
Description
-----------

Returns value of specific setting stored in tiger.geocode\_settings
table. Settings allow you to toggle debugging of functions. Later plans
will be to control rating with settings. Current list of settings are as
follows:

::

                  name              | setting |  unit   | category  |                                                             short_desc
    --------------------------------+---------+---------+-----------+------------------------------------------------------------------------------------------------------
     debug_geocode_address          | false   | boolean | debug     | outputs debug information in notice log such as queries when geocode_addresss is called if true
     debug_geocode_intersection     | false   | boolean | debug     | outputs debug information in notice log such as queries when geocode_intersection is called if true
     debug_normalize_address        | false   | boolean | debug     | outputs debug information in notice log such as queries 
                                    |         |         |           |   and intermediate expressions when normalize_address is called if true
     debug_reverse_geocode          | false   | boolean | debug     | if true, outputs debug information in notice log such as queries 
                                                                    |  and intermediate expressions when reverse_geocode
     reverse_geocode_numbered_roads | 0       | integer | rating    | For state and county highways, 0 - no preference in name
                                    |         |         |           |  , 1 - prefer the numbered highway name, 2 - prefer local state/county name
     use_pagc_address_parser        | false   | boolean | normalize | If set to true, will try to use the pagc_address normalizer instead of tiger built one    

Availability: 2.1.0

Example return debugging setting
--------------------------------

::

    SELECT get_geocode_setting('debug_geocode_address) As result;
    result
    ---------
    false
            

See Also
--------

?

Get\_Tract
Returns census tract or field from tract table of where the geometry is
located. Default to returning short name of tract.
text
get\_tract
geometry
loc\_geom
text
output\_field=name
Description
-----------

Given a geometry will return the census tract location of that geometry.
NAD 83 long lat is assumed if no spatial ref sys is specified.

Availability: 2.0.0

Examples: Basic
---------------

::

    SELECT get_tract(ST_Point(-71.101375, 42.31376) ) As tract_name;
    tract_name
    ---------
    1203.01
            

::

    --this one returns the tiger geoid
    SELECT get_tract(ST_Point(-71.101375, 42.31376), 'tract_id' ) As tract_id;
    tract_id
    ---------
    25025120301

See Also
--------

?>

Install\_Missing\_Indexes
Finds all tables with key columns used in geocoder joins and filter
conditions that are missing used indexes on those columns and will add
them.
boolean
Install\_Missing\_Indexes
Description
-----------

Finds all tables in ``tiger`` and ``tiger_data`` schemas with key
columns used in geocoder joins and filters that are missing indexes on
those columns and will output the SQL DDL to define the index for those
tables and then execute the generated script. This is a helper function
that adds new indexes needed to make queries faster that may have been
missing during the load process. This function is a companion to ? that
in addition to generating the create index script, also executes it. It
is called as part of the ``update_geocode.sql`` upgrade script.

Availability: 2.0.0

Examples
--------

::

    SELECT install_missing_indexes();
             install_missing_indexes
    -------------------------
     t
            

See Also
--------

?, ?

Loader\_Generate\_Census\_Script
Generates a shell script for the specified platform for the specified
states that will download Tiger census state tract, bg, and tabblocks
data tables, stage and load into
tiger\_data
schema. Each state script is returned as a separate record.
setof text
loader\_generate\_census\_script
text[]
param\_states
text
os
Description
-----------

Generates a shell script for the specified platform for the specified
states that will download Tiger data census state ``tract``, block
groups ``bg``, and ``tabblocks`` data tables, stage and load into
``tiger_data`` schema. Each state script is returned as a separate
record.

It uses unzip on Linux (7-zip on Windows by default) and wget to do the
downloading. It uses ? to load in the data. Note the smallest unit it
does is a whole state. It will only process the files in the staging and
temp folders.

It uses the following control tables to control the process and
different OS shell syntax variations.

1. ``loader_variables`` keeps track of various variables such as census
   site, year, data and staging schemas

2. ``loader_platform`` profiles of various platforms and where the
   various executables are located. Comes with windows and linux. More
   can be added.

3. ``loader_lookuptables`` each record defines a kind of table (state,
   county), whether to process records in it and how to load them in.
   Defines the steps to import data, stage data, add, removes columns,
   indexes, and constraints for each. Each table is prefixed with the
   state and inherits from a table in the tiger schema. e.g. creates
   ``tiger_data.ma_faces`` which inherits from ``tiger.faces``

Availability: 2.0.0

    **Note**

    ? includes this logic, but if you installed tiger geocoder prior to
    PostGIS 2.0.0 alpha5, you'll need to run this on the states you have
    already done to get these additional tables.

Examples
--------

Generate script to load up data for select states in Windows shell
script format.

::

    SELECT loader_generate_census_script(ARRAY['MA'], 'windows');
    -- result --
    set STATEDIR="\gisdata\www2.census.gov\geo\pvs\tiger2010st\25_Massachusetts"
    set TMPDIR=\gisdata\temp\
    set UNZIPTOOL="C:\Program Files\7-Zip\7z.exe"
    set WGETTOOL="C:\wget\wget.exe"
    set PGBIN=C:\projects\pg\pg91win\bin\
    set PGPORT=5432
    set PGHOST=localhost
    set PGUSER=postgres
    set PGPASSWORD=yourpasswordhere
    set PGDATABASE=tiger_postgis20
    set PSQL="%PGBIN%psql"
    set SHP2PGSQL="%PGBIN%shp2pgsql"
    cd \gisdata

    %WGETTOOL% http://www2.census.gov/geo/pvs/tiger2010st/25_Massachusetts/25/ --no-parent --relative --accept=*bg10.zip,*tract10.zip,*tabblock10.zip --mirror --reject=html
    del %TMPDIR%\*.* /Q
    %PSQL% -c "DROP SCHEMA tiger_staging CASCADE;"
    %PSQL% -c "CREATE SCHEMA tiger_staging;"
    cd %STATEDIR%
    for /r %%z in (*.zip) do %UNZIPTOOL% e %%z  -o%TMPDIR% 
    cd %TMPDIR% 
    %PSQL% -c "CREATE TABLE tiger_data.MA_tract(CONSTRAINT pk_MA_tract PRIMARY KEY (tract_id) ) INHERITS(tiger.tract); " 
    %SHP2PGSQL% -c -s 4269 -g the_geom   -W "latin1" tl_2010_25_tract10.dbf tiger_staging.ma_tract10 | %PSQL%
    %PSQL% -c "ALTER TABLE tiger_staging.MA_tract10 RENAME geoid10 TO tract_id;  SELECT loader_load_staged_data(lower('MA_tract10'), lower('MA_tract')); "
    %PSQL% -c "CREATE INDEX tiger_data_MA_tract_the_geom_gist ON tiger_data.MA_tract USING gist(the_geom);"
    %PSQL% -c "VACUUM ANALYZE tiger_data.MA_tract;"
    %PSQL% -c "ALTER TABLE tiger_data.MA_tract ADD CONSTRAINT chk_statefp CHECK (statefp = '25');"
    : 

Generate sh script

::

    STATEDIR="/gisdata/www2.census.gov/geo/pvs/tiger2010st/25_Massachusetts" 
    TMPDIR="/gisdata/temp/"
    UNZIPTOOL=unzip
    WGETTOOL="/usr/bin/wget"
    export PGBIN=/usr/pgsql-9.0/bin
    export PGPORT=5432
    export PGHOST=localhost
    export PGUSER=postgres
    export PGPASSWORD=yourpasswordhere
    export PGDATABASE=geocoder
    PSQL=${PGBIN}/psql
    SHP2PGSQL=${PGBIN}/shp2pgsql
    cd /gisdata

    wget http://www2.census.gov/geo/pvs/tiger2010st/25_Massachusetts/25/ --no-parent --relative --accept=*bg10.zip,*tract10.zip,*tabblock10.zip --mirror --reject=html
    rm -f ${TMPDIR}/*.*
    ${PSQL} -c "DROP SCHEMA tiger_staging CASCADE;"
    ${PSQL} -c "CREATE SCHEMA tiger_staging;"
    cd $STATEDIR
    for z in *.zip; do $UNZIPTOOL -o -d $TMPDIR $z; done
    :
    : 

See Also
--------

?

Loader\_Generate\_Script
Generates a shell script for the specified platform for the specified
states that will download Tiger data, stage and load into
tiger\_data
schema. Each state script is returned as a separate record. Latest
version supports Tiger 2010 structural changes and also loads census
tract, block groups, and blocks tables.
setof text
loader\_generate\_script
text[]
param\_states
text
os
Description
-----------

Generates a shell script for the specified platform for the specified
states that will download Tiger data, stage and load into ``tiger_data``
schema. Each state script is returned as a separate record.

It uses unzip on Linux (7-zip on Windows by default) and wget to do the
downloading. It uses ? to load in the data. Note the smallest unit it
does is a whole state, but you can overwrite this by downloading the
files yourself. It will only process the files in the staging and temp
folders.

It uses the following control tables to control the process and
different OS shell syntax variations.

1. ``loader_variables`` keeps track of various variables such as census
   site, year, data and staging schemas

2. ``loader_platform`` profiles of various platforms and where the
   various executables are located. Comes with windows and linux. More
   can be added.

3. ``loader_lookuptables`` each record defines a kind of table (state,
   county), whether to process records in it and how to load them in.
   Defines the steps to import data, stage data, add, removes columns,
   indexes, and constraints for each. Each table is prefixed with the
   state and inherits from a table in the tiger schema. e.g. creates
   ``tiger_data.ma_faces`` which inherits from ``tiger.faces``

Availability: 2.0.0 to support Tiger 2010 structured data and load
census tract (tract), block groups (bg), and blocks (tabblocks) tables .

Examples
--------

Generate script to load up data for 2 states in Windows shell script
format.

::

    SELECT loader_generate_script(ARRAY['MA','RI'], 'windows') AS result;
    -- result --
    set STATEDIR="\gisdata\www2.census.gov\geo\pvs\tiger2010st\44_Rhode_Island"
    set TMPDIR=\gisdata\temp\
    set UNZIPTOOL="C:\Program Files\7-Zip\7z.exe"
    set WGETTOOL="C:\wget\wget.exe"
    set PGBIN=C:\Program Files\PostgreSQL\8.4\bin\
    set PGPORT=5432
    set PGHOST=localhost
    set PGUSER=postgres
    set PGPASSWORD=yourpasswordhere
    set PGDATABASE=geocoder
    set PSQL="%PGBIN%psql"
    set SHP2PGSQL="%PGBIN%shp2pgsql"

    %WGETTOOL% http://www2.census.gov/geo/pvs/tiger2010st/44_Rhode_Island/ --no-parent --relative --recursive --level=2 --accept=zip,txt --mirror --reject=html
    :
    :

Generate sh script

::

    SELECT loader_generate_script(ARRAY['MA','RI'], 'sh') AS result;
    -- result --
    STATEDIR="/gisdata/www2.census.gov/geo/pvs/tiger2010st/44_Rhode_Island" 
    TMPDIR="/gisdata/temp/"
    UNZIPTOOL=unzip
    PGPORT=5432
    PGHOST=localhost
    PGUSER=postgres
    PGPASSWORD=yourpasswordhere
    PGDATABASE=geocoder
    PSQL=psql
    SHP2PGSQ=shp2pgsql

    wget http://www2.census.gov/geo/pvs/tiger2010st/44_Rhode_Island/ --no-parent --relative --recursive --level=2 --accept=zip,txt --mirror --reject=html
    :
    :

See Also
--------

Loader\_Generate\_Nation\_Script
Generates a shell script for the specified platform that loads in the
county and state lookup tables.
text
loader\_generate\_nation\_script
text
os
Description
-----------

Generates a shell script for the specified platform that loads in the
``county_all``, ``county_all_lookup``, ``state_all`` tables into
``tiger_data`` schema. These inherit respectively from the ``county``,
``county_lookup``, ``state`` tables in ``tiger`` schema.

It uses unzip on Linux (7-zip on Windows by default) and wget to do the
downloading. It uses ? to load in the data.

It uses the following control tables ``tiger.loader_platform``,
``tiger.loader_variables``, and ``tiger.loader_lookuptables`` to control
the process and different OS shell syntax variations.

1. ``loader_variables`` keeps track of various variables such as census
   site, year, data and staging schemas

2. ``loader_platform`` profiles of various platforms and where the
   various executables are located. Comes with windows and linux/unix.
   More can be added.

3. ``loader_lookuptables`` each record defines a kind of table (state,
   county), whether to process records in it and how to load them in.
   Defines the steps to import data, stage data, add, removes columns,
   indexes, and constraints for each. Each table is prefixed with the
   state and inherits from a table in the tiger schema. e.g. creates
   ``tiger_data.ma_faces`` which inherits from ``tiger.faces``

Availability: 2.1.0

    **Note**

    If you were running ``tiger_2010`` version and you want to reload as
    state with ``tiger_2011``, you'll need to for the very first load
    generate and run drop statements ? before you run this script.

Examples
--------

Generate script script to load nation data Windows.

::

    SELECT loader_generate_nation_script('windows'); 

Generate script to load up data for Linux/Unix systems.

::

    SELECT loader_generate_nation_script('sh'); 

See Also
--------

?

Missing\_Indexes\_Generate\_Script
Finds all tables with key columns used in geocoder joins that are
missing indexes on those columns and will output the SQL DDL to define
the index for those tables.
text
Missing\_Indexes\_Generate\_Script
Description
-----------

Finds all tables in ``tiger`` and ``tiger_data`` schemas with key
columns used in geocoder joins that are missing indexes on those columns
and will output the SQL DDL to define the index for those tables. This
is a helper function that adds new indexes needed to make queries faster
that may have been missing during the load process. As the geocoder is
improved, this function will be updated to accommodate new indexes being
used. If this function outputs nothing, it means all your tables have
what we think are the key indexes already in place.

Availability: 2.0.0

Examples
--------

::

    SELECT missing_indexes_generate_script();
    -- output: This was run on a database that was created before many corrections were made to the loading script ---
    CREATE INDEX idx_tiger_county_countyfp ON tiger.county USING btree(countyfp);
    CREATE INDEX idx_tiger_cousub_countyfp ON tiger.cousub USING btree(countyfp);
    CREATE INDEX idx_tiger_edges_tfidr ON tiger.edges USING btree(tfidr);
    CREATE INDEX idx_tiger_edges_tfidl ON tiger.edges USING btree(tfidl);
    CREATE INDEX idx_tiger_zip_lookup_all_zip ON tiger.zip_lookup_all USING btree(zip);
    CREATE INDEX idx_tiger_data_ma_county_countyfp ON tiger_data.ma_county USING btree(countyfp);
    CREATE INDEX idx_tiger_data_ma_cousub_countyfp ON tiger_data.ma_cousub USING btree(countyfp);
    CREATE INDEX idx_tiger_data_ma_edges_countyfp ON tiger_data.ma_edges USING btree(countyfp);
    CREATE INDEX idx_tiger_data_ma_faces_countyfp ON tiger_data.ma_faces USING btree(countyfp);
            

See Also
--------

?, ?

Normalize\_Address
Given a textual street address, returns a composite
norm\_addy
type that has road suffix, prefix and type standardized, street,
streetname etc. broken into separate fields. This function will work
with just the lookup data packaged with the tiger\_geocoder (no need for
tiger census data).
norm\_addy
normalize\_address
varchar
in\_address
Description
-----------

Given a textual street address, returns a composite ``norm_addy`` type
that has road suffix, prefix and type standardized, street, streetname
etc. broken into separate fields. This is the first step in the
geocoding process to get all addresses into normalized postal form. No
other data is required aside from what is packaged with the geocoder.

This function just uses the various direction/state/suffix lookup tables
preloaded with the tiger\_geocoder and located in the ``tiger`` schema,
so it doesn't need you to download tiger census data or any other
additional data to make use of it. You may find the need to add more
abbreviations or alternative namings to the various lookup tables in the
``tiger`` schema.

It uses various control lookup tables located in ``tiger`` schema to
normalize the input address.

Fields in the ``norm_addy`` type object returned by this function in
this order where () indicates a field required by the geocoder, []
indicates an optional field:

(address) [predirAbbrev] (streetName) [streetTypeAbbrev] [postdirAbbrev]
[internal] [location] [stateAbbrev] [zip]

1.  ``address`` is an integer: The street number

2.  ``predirAbbrev`` is varchar: Directional prefix of road such as N,
    S, E, W etc. These are controlled using the ``direction_lookup``
    table.

3.  ``streetName`` varchar

4.  ``streetTypeAbbrev`` varchar abbreviated version of street type:
    e.g. St, Ave, Cir. These are controlled using the
    ``street_type_lookup`` table.

5.  ``postdirAbbrev`` varchar abbreviated directional suffice of road N,
    S, E, W etc. These are controlled using the ``direction_lookup``
    table.

6.  ``internal`` varchar internal address such as an apartment or suite
    number.

7.  ``location`` varchar usually a city or governing province.

8.  ``stateAbbrev`` varchar two character US State. e.g MA, NY, MI.
    These are controlled by the ``state_lookup`` table.

9.  ``zip`` varchar 5-digit zipcode. e.g. 02109.

10. ``parsed`` boolean - denotes if addess was formed from normalize
    process. The normalize\_address function sets this to true before
    returning the address.

Examples
--------

Output select fields. Use ? if you want a pretty textual output.

::

    SELECT address As orig, (g.na).streetname, (g.na).streettypeabbrev
     FROM (SELECT address, normalize_address(address) As na
            FROM addresses_to_geocode) As g;
            
                            orig                         |  streetname   | streettypeabbrev 
    -----------------------------------------------------+---------------+------------------
     28 Capen Street, Medford, MA                        | Capen         | St
     124 Mount Auburn St, Cambridge, Massachusetts 02138 | Mount Auburn  | St
     950 Main Street, Worcester, MA 01610                | Main          | St
     529 Main Street, Boston MA, 02129                   | Main          | St
     77 Massachusetts Avenue, Cambridge, MA 02139        | Massachusetts | Ave
     25 Wizard of Oz, Walaford, KS 99912323              | Wizard of Oz  | 
            

See Also
--------

?, ?

Pagc\_Normalize\_Address
Given a textual street address, returns a composite
norm\_addy
type that has road suffix, prefix and type standardized, street,
streetname etc. broken into separate fields. This function will work
with just the lookup data packaged with the tiger\_geocoder (no need for
tiger census data). Requires address\_standardizer extension.
norm\_addy
pagc\_normalize\_address
varchar
in\_address
Description
-----------

Given a textual street address, returns a composite ``norm_addy`` type
that has road suffix, prefix and type standardized, street, streetname
etc. broken into separate fields. This is the first step in the
geocoding process to get all addresses into normalized postal form. No
other data is required aside from what is packaged with the geocoder.

This function just uses the various pagc\_\* lookup tables preloaded
with the tiger\_geocoder and located in the ``tiger`` schema, so it
doesn't need you to download tiger census data or any other additional
data to make use of it. You may find the need to add more abbreviations
or alternative namings to the various lookup tables in the ``tiger``
schema.

It uses various control lookup tables located in ``tiger`` schema to
normalize the input address.

Fields in the ``norm_addy`` type object returned by this function in
this order where () indicates a field required by the geocoder, []
indicates an optional field:

This version uses the PAGC address standardizer C extension which you
can download. There are slight variations in casing and formatting and
also provides a richer breakout.

Availability: 2.1.0

(address) [predirAbbrev] (streetName) [streetTypeAbbrev] [postdirAbbrev]
[internal] [location] [stateAbbrev] [zip]

The native standardaddr of address\_standardizer extension is at this
time a bit richer than norm\_addy since its designed to support
international addresses (including country). standardaddr equivalent
fields are:

house\_num,predir, name, suftype, sufdir, unit, city, state, postcode

1.  ``address`` is an integer: The street number

2.  ``predirAbbrev`` is varchar: Directional prefix of road such as N,
    S, E, W etc. These are controlled using the ``direction_lookup``
    table.

3.  ``streetName`` varchar

4.  ``streetTypeAbbrev`` varchar abbreviated version of street type:
    e.g. St, Ave, Cir. These are controlled using the
    ``street_type_lookup`` table.

5.  ``postdirAbbrev`` varchar abbreviated directional suffice of road N,
    S, E, W etc. These are controlled using the ``direction_lookup``
    table.

6.  ``internal`` varchar internal address such as an apartment or suite
    number.

7.  ``location`` varchar usually a city or governing province.

8.  ``stateAbbrev`` varchar two character US State. e.g MA, NY, MI.
    These are controlled by the ``state_lookup`` table.

9.  ``zip`` varchar 5-digit zipcode. e.g. 02109.

10. ``parsed`` boolean - denotes if addess was formed from normalize
    process. The normalize\_address function sets this to true before
    returning the address.

Examples
--------

Single call example

::

    SELECT addy.*
    FROM pagc_normalize_address('9000 E ROO ST STE 999, Springfield, CO') AS addy;

            
     address | predirabbrev | streetname | streettypeabbrev | postdirabbrev | internal  |  location   | stateabbrev | zip | parsed
    ---------+--------------+------------+------------------+---------------+-----------+-------------+-------------+-----+--------
        9000 | E            | ROO        | ST               |               | SUITE 999 | SPRINGFIELD | CO          |     | t

Batch call. There are currently speed issues with the way
postgis\_tiger\_geocoder wraps the address\_standardizer. These will
hopefully be resolved in later editions. To work around them, if you
need speed for batch geocoding to call generate a normaddy in batch
mode, you are encouraged to directly call the address\_standardizer
standardize\_address function as shown below which is similar exercise
to what we did in ? that uses data created in ?.

::

    WITH g AS (SELECT address, ROW((sa).house_num, (sa).predir, (sa).name
      , (sa).suftype, (sa).sufdir, (sa).unit , (sa).city, (sa).state, (sa).postcode, true)::norm_addy As na
     FROM (SELECT address, standardize_address('tiger.pagc_lex'
           , 'tiger.pagc_gaz'
           , 'tiger.pagc_rules', address) As sa
            FROM addresses_to_geocode) As g)
    SELECT address As orig, (g.na).streetname, (g.na).streettypeabbrev
     FROM  g;
     
     orig                                                |  streetname   | streettypeabbrev
    -----------------------------------------------------+---------------+------------------
     529 Main Street, Boston MA, 02129                   | MAIN          | ST
     77 Massachusetts Avenue, Cambridge, MA 02139        | MASSACHUSETTS | AVE
     25 Wizard of Oz, Walaford, KS 99912323              | WIZARD OF     |
     26 Capen Street, Medford, MA                        | CAPEN         | ST
     124 Mount Auburn St, Cambridge, Massachusetts 02138 | MOUNT AUBURN  | ST
     950 Main Street, Worcester, MA 01610                | MAIN          | ST

See Also
--------

?, ?

Pprint\_Addy
Given a
norm\_addy
composite type object, returns a pretty print representation of it.
Usually used in conjunction with normalize\_address.
varchar
pprint\_addy
norm\_addy
in\_addy
Description
-----------

Given a ``norm_addy`` composite type object, returns a pretty print
representation of it. No other data is required aside from what is
packaged with the geocoder.

Usually used in conjunction with ?.

Examples
--------

Pretty print a single address

::

    SELECT pprint_addy(normalize_address('202 East Fremont Street, Las Vegas, Nevada 89101')) As pretty_address;
                pretty_address
    ---------------------------------------
     202 E Fremont St, Las Vegas, NV 89101
            

Pretty print address a table of addresses

::

    SELECT address As orig, pprint_addy(normalize_address(address)) As pretty_address
            FROM addresses_to_geocode;
            
                            orig                         |              pretty_address
    -----------------------------------------------------+-------------------------------------------
     529 Main Street, Boston MA, 02129                   | 529 Main St, Boston MA, 02129
     77 Massachusetts Avenue, Cambridge, MA 02139        | 77 Massachusetts Ave, Cambridge, MA 02139
     28 Capen Street, Medford, MA                        | 28 Capen St, Medford, MA
     124 Mount Auburn St, Cambridge, Massachusetts 02138 | 124 Mount Auburn St, Cambridge, MA 02138
     950 Main Street, Worcester, MA 01610                | 950 Main St, Worcester, MA 01610

See Also
--------

?

Reverse\_Geocode
Takes a geometry point in a known spatial ref sys and returns a record
containing an array of theoretically possible addresses and an array of
cross streets. If include\_strnum\_range = true, includes the street
range in the cross streets.
record
Reverse\_Geocode
geometry
pt
boolean
include\_strnum\_range=false
geometry[]
OUT intpt
norm\_addy[]
OUT addy
varchar[]
OUT street
Description
-----------

Takes a geometry point in a known spatial ref and returns a record
containing an array of theoretically possible addresses and an array of
cross streets. If include\_strnum\_range = true, includes the street
range in the cross streets. include\_strnum\_range defaults to false if
not passed in. Addresses are sorted according to which road a point is
closest to so first address is most likely the right one.

Why do we say theoretical instead of actual addresses. The Tiger data
doesn't have real addresses, but just street ranges. As such the
theoretical address is an interpolated address based on the street
ranges. Like for example interpolating one of my addresses returns a 26
Court St. and 26 Court Sq., though there is no such place as 26 Court
Sq. This is because a point may be at a corner of 2 streets and thus the
logic interpolates along both streets. The logic also assumes addresses
are equally spaced along a street, which of course is wrong since you
can have a municipal building taking up a good chunk of the street range
and the rest of the buildings are clustered at the end.

Note: Hmm this function relies on Tiger data. If you have not loaded
data covering the region of this point, then hmm you will get a record
filled with NULLS.

Returned elements of the record are as follows:

1. ``intpt`` is an array of points: These are the center line points on
   the street closest to the input point. There are as many points as
   there are addresses.

2. ``addy`` is an array of norm\_addy (normalized addresses): These are
   an array of possible addresses that fit the input point. The first
   one in the array is most likely. Generally there should be only one,
   except in the case when a point is at the corner of 2 or 3 streets,
   or the point is somewhere on the road and not off to the side.

3. ``street`` an array of varchar: These are cross streets (or the
   street) (streets that intersect or are the street the point is
   projected to be on).

Availability: 2.0.0

Examples
--------

Example of a point at the corner of two streets, but closest to one.
This is approximate location of MIT: 77 Massachusetts Ave, Cambridge, MA
02139 Note that although we don't have 3 streets, PostgreSQL will just
return null for entries above our upper bound so safe to use. This
includes street ranges

::

    SELECT pprint_addy(r.addy[1]) As st1, pprint_addy(r.addy[2]) As st2, pprint_addy(r.addy[3]) As st3, 
                array_to_string(r.street, ',') As cross_streets 
            FROM reverse_geocode(ST_GeomFromText('POINT(-71.093902 42.359446)',4269),true) As r;
           
     result
     ------
          st1                                  | st2 | st3 |               cross_streets
    -------------------------------------------+-----+-----+----------------------------------------------
     67 Massachusetts Ave, Cambridge, MA 02139 |     |     | 67 - 127 Massachusetts Ave,32 - 88 Vassar St

Here we choose not to include the address ranges for the cross streets
and picked a location really really close to a corner of 2 streets thus
could be known by two different addresses.

::

    SELECT pprint_addy(r.addy[1]) As st1, pprint_addy(r.addy[2]) As st2, 
    pprint_addy(r.addy[3]) As st3, array_to_string(r.street, ',') As cross_str
    FROM reverse_geocode(ST_GeomFromText('POINT(-71.06941 42.34225)',4269)) As r;

    result
    --------
                   st1               |               st2               | st3 | cross_str
    ---------------------------------+---------------------------------+-----+------------------------
     5 Bradford St, Boston, MA 02118 | 49 Waltham St, Boston, MA 02118 |     | Waltham St

For this one we reuse our geocoded example from ? and we only want the
primary address and at most 2 cross streets.

::

    SELECT actual_addr, lon, lat, pprint_addy((rg).addy[1]) As int_addr1, 
        (rg).street[1] As cross1, (rg).street[2] As cross2
    FROM (SELECT address As actual_addr, lon, lat,
        reverse_geocode( ST_SetSRID(ST_Point(lon,lat),4326) ) As rg
        FROM addresses_to_geocode WHERE rating > -1) As foo;

                         actual_addr                     |    lon    |   lat    |                 int_addr1                 |     cross1      |   cross2   
    -----------------------------------------------------+-----------+----------+-------------------------------------------+-----------------+------------
     529 Main Street, Boston MA, 02129                   | -71.07181 | 42.38359 | 527 Main St, Boston, MA 02129             | Medford St      | 
     77 Massachusetts Avenue, Cambridge, MA 02139        | -71.09428 | 42.35988 | 77 Massachusetts Ave, Cambridge, MA 02139 | Vassar St       | 
     26 Capen Street, Medford, MA                        | -71.12377 | 42.41101 | 9 Edison Ave, Medford, MA 02155           | Capen St        | Tesla Ave
     124 Mount Auburn St, Cambridge, Massachusetts 02138 | -71.12304 | 42.37328 | 3 University Rd, Cambridge, MA 02138      | Mount Auburn St | 
     950 Main Street, Worcester, MA 01610                | -71.82368 | 42.24956 | 3 Maywood St, Worcester, MA 01603         | Main St         | Maywood Pl

See Also
--------

?, ?

Topology\_Load\_Tiger
Loads a defined region of tiger data into a PostGIS Topology and
transforming the tiger data to spatial reference of the topology and
snapping to the precision tolerance of the topology.
text
Topology\_Load\_Tiger
varchar
topo\_name
varchar
region\_type
varchar
region\_id
Description
-----------

Loads a defined region of tiger data into a PostGIS Topology. The faces,
nodes and edges are transformed to the spatial reference system of the
target topology and points are snapped to the tolerance of the target
topology. The created faces, nodes, edges maintain the same ids as the
original Tiger data faces, nodes, edges so that datasets can be in the
future be more easily reconciled with tiger data. Returns summary
details about the process.

This would be useful for example for redistricting data where you
require the newly formed polygons to follow the center lines of streets
and for the resulting polygons not to overlap.

    **Note**

    This function relies on Tiger data as well as the installation of
    the PostGIS topology module. For more information, refer to ? and ?.
    If you have not loaded data covering the region of interest, then no
    topology records will be created. This function will also fail if
    you have not created a topology using the topology functions.

    **Note**

    Most topology validation errors are a result of tolerance issues
    where after transformation the edges points don't quite line up or
    overlap. To remedy the situation you may want to increase or lower
    the precision if you get topology validation failures.

Required arguments:

1. ``topo_name`` The name of an existing PostGIS topology to load data
   into.

2. ``region_type`` The type of bounding region. Currently only ``place``
   and ``county`` are supported. Plan is to have several more. This is
   the table to look into to define the region bounds. e.g
   ``tiger.place``, ``tiger.county``

3. ``region_id`` This is what TIGER calls the geoid. It is the unique
   identifier of the region in the table. For place it is the
   ``plcidfp`` column in ``tiger.place``. For county it is the
   ``cntyidfp`` column in ``tiger.county``

Availability: 2.0.0

Example: Boston, Massachusetts Topology
---------------------------------------

Create a topology for Boston, Massachusetts in Mass State Plane Feet
(2249) with tolerance 0.25 feet and then load in Boston city tiger
faces, edges, nodes.

::

    SELECT topology.CreateTopology('topo_boston', 2249, 0.25);
    createtopology
    --------------
       15
    -- 60,902 ms ~ 1 minute on windows 7 desktop running 9.1 (with 5 states tiger data loaded) 
    SELECT tiger.topology_load_tiger('topo_boston', 'place', '2507000'); 
    -- topology_loader_tiger --
    29722 edges holding in temporary. 11108 faces added. 1875 edges of faces added.  20576 nodes added.  
    19962 nodes contained in a face.  0 edge start end corrected.  31597 edges added. 
     
    -- 41 ms --
    SELECT topology.TopologySummary('topo_boston');
     -- topologysummary--
    Topology topo_boston (15), SRID 2249, precision 0.25
    20576 nodes, 31597 edges, 11109 faces, 0 topogeoms in 0 layers

    -- 28,797 ms to validate yeh returned no errors --
    SELECT * FROM 
        topology.ValidateTopology('topo_boston'); 
        
           error       |   id1    |    id2
    -------------------+----------+-----------
          

Example: Suffolk, Massachusetts Topology
----------------------------------------

Create a topology for Suffolk, Massachusetts in Mass State Plane Meters
(26986) with tolerance 0.25 meters and then load in Suffolk county tiger
faces, edges, nodes.

::

    SELECT topology.CreateTopology('topo_suffolk', 26986, 0.25);
    -- this took 56,275 ms ~ 1 minute on Windows 7 32-bit with 5 states of tiger loaded
    -- must have been warmed up after loading boston
    SELECT tiger.topology_load_tiger('topo_suffolk', 'county', '25025');  
    -- topology_loader_tiger --
     36003 edges holding in temporary. 13518 faces added. 2172 edges of faces added. 
     24761 nodes added.  24075 nodes contained in a face.  0 edge start end corrected.  38175 edges added. 
    -- 31 ms --
    SELECT topology.TopologySummary('topo_suffolk');
     -- topologysummary--
     Topology topo_suffolk (14), SRID 26986, precision 0.25
    24761 nodes, 38175 edges, 13519 faces, 0 topogeoms in 0 layers

    -- 33,606 ms to validate --
    SELECT * FROM 
        topology.ValidateTopology('topo_suffolk'); 
        
           error       |   id1    |    id2
    -------------------+----------+-----------
     coincident nodes  | 81045651 |  81064553
     edge crosses node | 81045651 |  85737793
     edge crosses node | 81045651 |  85742215
     edge crosses node | 81045651 | 620628939
     edge crosses node | 81064553 |  85697815
     edge crosses node | 81064553 |  85728168
     edge crosses node | 81064553 |  85733413
          

See Also
--------

?, ?, ?, ?

Set\_Geocode\_Setting
Sets a setting that affects behavior of geocoder functions.
text
Set\_Geocode\_Setting
text
setting\_name
text
setting\_value
Description
-----------

Sets value of specific setting stored in ``tiger.geocode_settings``
table. Settings allow you to toggle debugging of functions. Later plans
will be to control rating with settings. Current list of settings are
listed in ?.

Availability: 2.1.0

Example return debugging setting
--------------------------------

If you run ? when this function is true, the NOTICE log will output
timing and queries.

::

    SELECT set_geocode_setting('debug_geocode_address', 'true') As result;
    result
    ---------
    true

See Also
--------

?
