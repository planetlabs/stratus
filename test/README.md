# Testing Stratus

## Local

### Upgrade 

Test catalog consistency when upgrading from one version of Stratus to another.

```
cd ./standalone/upgrade
./test_upgrade.sh $OLD_VER $NEW_VER
```

### Ad-hoc

Prerequesites:

* A locally running [postgis container](../deploy/standalone/postgis.sh)
* A locally running Stratus instance.

#### 1. Load data - na_roads

* na_roads
Load na_roads into 
```
./standalone/na_roads_postgis.sh # if prompted, it is for a password. Enter 'docker' up to 3 times.
```
* Geoserver
Create workspace, datastore, and featuretype accessible via http://localhost:8080/geoserver/rest/layers/acme:ne_10m_roads_north_america
```
./standalone/loadPgData.sh
```

#### 2. Load data - geotiff

Assumes acme workspace is created already. If not, then 
```
./standalone/createWorkspace.sh
```
then
```
./standalone/loadGeotiff.sh
```

#### 3. Load data - Sierpinski carpet

Assumes acme workspace is created already. If not, then 
```
./standalone/createWorkspace.sh
```
then
```
./standalone/loadPgFractalData.sh
```
#### 4. Load simulated points data

First, create the points in your database
```
python ./python/generate_points.py --load-pg --workspaces 100 --layers 10 --points 1000 --db-host localhost --db-port 5432 --db-username docker --db-password docker
```
Next, create the geoserver workspaces, datastores, and featuretypes
```
python ./python/generate_points.py --load-gs --workspaces 100 --layers 10 --points 1000 --geoserver-host localhost --geoserver-port 8080 --db-host localhost --db-port 5432 --db-username docker --db-password docker
```

## Jmeter

Test simulated points data
* `vector-scale-test-wfs.jmx`, * `vector-scale-test-wms.jmx`

## Locust

See [locust/README.md](./locust/README.md) and [locust/Building_locust.md](./locust/Building_locust.md) on locust install

* Load test 
Start locust. In this case, it's a wms png test but you could configure locust to run other tests under the varying load as well.
```
locust -f code/wms_tester.py -n1000 -c1 --host=localhost:8080
```
You can start/stop loads from the UI: http://localhost:8089. Of course, it's more helpful to run the load test on a remotely deployed instance but localhost:8080 is provided above for a simple start.

* Simulate scale up and scale down of users via csv file of load over time
** Edit a csv file (usage_load.csv is given as a starter) and set the prescribed load
```
vi usage_load.csv
```
** Start locust as above. 
Now visit http://localhost:8089 to see the locust web UI. The simulation will utilize the rest interface to start and stop specific tests based on the usage_load.csv
** Run the simulated load tests
```
simulate_variable_usage.py
```
## Test latencies for catalog updates
[locust/post_latency.py](locust/post_latency.py)
e.g., 
```
python3 post_latency.py $STRATUS_HOST  \
    --database-host $DB_HOST \
    --database-port $DB_PORT \
    --database-name $DB_NAME \
    --database-user $DB_USER \
    --database-password $DB_PASSWORD \
    --test-count 2 --delay 0.01 --max-fails 10
```

