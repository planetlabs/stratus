# Random Points Test
This test generates random points around the world in a configurable number of tables and databases. Designed to test 
Stratus against a very large catalog.

**Note**: This test is best run against a real Stratus instance (as opposed to a local Stratus vm). The `*.jmx` test files are configured with `host` and `db_host` (the Stratus and database hosts, respectively) set to `localhost` for portability - you should update these to the name of your remote instances as needed.

## Prerequisites
- VM or machine external to Stratus with python, python-psycopg2, git, and jmeter

## Initial Ubuntu setup
```
apt-get install software-properties-common python-software-properties apt-transport-https
add-apt-repository "deb http://apt.postgresql.org/pub/repos/apt/ $(lsb_release -sc)-pgdg main"
add-apt-repository "deb https://apt.postgresql.org/pub/repos/apt/ trusty-pgdg main"
add-apt-repository ppa:git-core/ppa
apt-get install postgresql-9.6 git python-psycopg2

git clone https://github.com/gsstratus/stratus.git

cd stratus/test/python; 
```
And install jmeter :-)
## Filling the database with databases, tables, and points
```
DB_HOST=
DB_PORT=
DB_USER=
DB_PASS=
GS_HOST=
GS_PORT=
NUM_WORKSPACES=1000
NUM_LAYERS_PER_WS=100
NUM_POINTS_PER_TABLE=1000000

python generate_points.py --load-pg --workspaces $NUM_WORKSPACES --layers $NUM_LAYERS_PER_WS --db-host $DB_HOST --db-port $DB_PORT --db-username $DB_USER --db-password $DB_PASS --workspace-prefix ws --geoserver-host $GS_HOST --geoserver-port $GS_PORT --datastore-prefix ds --table-prefix ft --db-format 0000 --datastore-format 0000 --workspace-format 0000 --featuretype-format 0000 --points $NUM_POINTS_PER_TABLE
```
Depending how many points are being generated, the above could take a long time. It may be worthwhile to use database `COPY` to generate identical tables from existing ones, but with different names.

## Filling the catalog
Similar to the above, but with the `--load-gs` switch
```
python generate_points.py --load-pg --workspaces 1 --layers 1 --db-host $DB_HOST --db-port $DB_PORT --db-username $DB_USER --db-password $DB_PASS --workspace-prefix ws --geoserver-host $GS_HOST --geoserver-port $GS_PORT --datastore-prefix ds --table-prefix ft --db-format 0000 --datastore-format 0000 --workspace-format 0000 --featuretype-format 0000 --points 1000000
```
Now you are ready to load test against layers named `ws0001:ft0001`, `ws0001:ft0002`,..., `ws1000:ft0100`, etc., depending on your choice of number of workspaces and layers (`ft`)

## Testing with jmeter
Alter the number of threads (simultaneous users) and iterations below:
```
THREADS=10
ITERATIONS=1000
jmeter -n -t jmeter/vector-scale-test-wfs.jmx.jmx
            -JGEOSERVER_HOST=$GS_HOST
            -JGEOSERVER_PORT=$GS_PORT
            -JFEATURETYPE_FORMAT=0000
            -JWORKSPACE_FORMAT=0000
            -JFEATURETYPES=$NUM_LAYERS_PER_WS
            -JWORKSPACES=$NUM_WORKSPACES
            -JTHREADS=$THREADS
            -JITERATIONS=$ITERATIONS
            -JDB_FORMAT=0000
            -JFEATURETYPE_PREFIX=ft
            -JWORKSPACE_PREFIX=ws
            -JDB_PREFIX=ds"
```
