History Tracking
================

    **Note**

    The ``history_table`` was also packaged in PostGIS 1.5, but added to
    the documentation in PostGIS 2.0. This package is written in plpgsql
    and located in the ``extras/history_table`` of PostGIS source tar
    balls and source repository.

If you have a table 'roads', this module will maintain a
'roads\_history' side table, which contains all the columns of the
parent table, and the following additional columns:

::

    history_id      | integer                     | not null default 
     date_added      | timestamp without time zone | not null default now()
     date_deleted    | timestamp without time zone | 
     last_operation  | character varying(30)       | not null
     active_user     | character varying(90)       | not null default "current_user"()
     current_version | text                        | not null

1. When you insert a new record into 'roads' a record is automatically
   inserted into 'roads\_history', with the 'date\_added' filled in the
   'date\_deleted' set to NULL, a unique 'history\_id', a
   'last\_operation' of 'INSERT' and 'active\_user' set.

2. When you delete a record in 'roads', the record in the history table
   is \*not\* deleted, but the 'date\_deleted' is set to the current
   date.

3. When you update a record in 'roads', the current record has
   'date\_deleted' filled in and a new record is created with the
   'date\_added' set and 'date\_deleted' NULL.

With this information maintained, it is possible to retrieve the history
of any record in the roads table:

::

    SELECT * FROM roads_history WHERE roads_pk = 111;

Or, to retrieve a view of the roads table at any point in the past:

::

    SELECT * FROM roads_history 
        WHERE date_added < 'January 1, 2001' AND 
            ( date_deleted >= 'January 1, 2001' OR date_deleted IS NULL );

Postgis\_Install\_History
Creates a table that will hold some interesting values for managing
history tables.
void
Postgis\_Install\_History
Description
-----------

Creates a table that will hold some interesting values for managing
history tables. Creates a table called ``historic_information``

Availability: 1.5.0

Examples
--------

::

    SELECT postgis_install_history();

See Also
--------

Postgis\_Enable\_History
Registers a tablein the history\_information table for tracking and also
adds in side line history table and insert, update, delete rules on the
table.
boolean
Postgis\_Enable\_History
text
p\_schema
text
p\_table
Description
-----------

Registers a table in the history\_information table for tracking and
also adds in side line history table with same name as table but
prefixed with ``history`` in the same schema as the original table. Puts
in insert, update, delete rules on the table. Any
inserts,updates,deletes of the geometry are recorded in the history
table.

    **Note**

    This function currently relies on a geometry column being registered
    in ``geometry_columns`` and fails if the geometry column is not
    present in ``geometry_columns`` table.

Availability: 1.5.0

Examples
--------

::

    CREATE TABLE roads(gid SERIAL PRIMARY KEY, road_name varchar(150));
    SELECT AddGeometryColumn('roads', 'geom', 26986, 'LINESTRING', 2);
                    
    SELECT postgis_enable_history('public', 'roads', 'geom') As register_table;
    register_table
    --------------
    t

    INSERT INTO roads(road_name, geom) 
      VALUES('Test Street', ST_GeomFromText('LINESTRING(231660.5 832170,231647 832202,231627.5 832250.5)',26986));

    -- check transaction detail --
    SELECT date_added, last_operation, current_version 
    FROM roads_history 
    WHERE road_name = 'Test Street' ORDER BY date_added DESC;

           date_added       | last_operation | current_version
    ------------------------+----------------+-----------------
     2011-02-07 12:44:36.92 | INSERT         | 2

See Also
--------

