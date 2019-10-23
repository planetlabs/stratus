# Load testing Stratus with locust

This configuration utilizes a parameterized docker container to conduct a locust load test against a Stratus (or community geoserver) 
instance. 
## Quick start
### Quick start (simple)
`$URL` refers to the url of your Stratus instance in the format 'http://webserver:port' 
Assuming your layers here is `osm:osm`; change in the Dockerfile as needed. The `BBOX_FILE` is specific 
to the `osm:osm` dataset

```
docker build -t stratus-tests .
docker run -e "URL=$URL" -e "LAYERS=osm:osm" -e "BBOX_FILE=data/wms_256_tiles.csv" \
    -p 8089:8089 -it stratus-tests
```

### _Quick start (variable load) - Needs updating_
To run a test where load changes over time, edit the usage_load.csv file to add periods of specific load. The file has two columns: [Number of Users], [Time in minutes].

```
python3 simulate_variable_usage.py
```
TODO: parameterize and dockerize ^

## Building the docker image
If not pulling from the repo: See [Building_locust.md](Building_locust.md).

## Prepping Stratus (Loading data/catalog)
A number of test datasets are available. Please refer to [../test/data/README.md](../test/data/README.md) for details.

## Generating bbox CSV for test
To generate a full set of bboxes for tiles ranging from one pyramid level to another, utilize the `mercantile_gen.py` utility script. Optionally randomize the bboxes. Otherwise, the order is systematic across each pyramid level at a time.

```python3 mercantile_gen.py minx miny maxx maxy min_level max_level output_csv_file [-r]```

`-r` randomizes the lines

## Manual Test:
### Launching the test UI
```
docker run -e "URL=$URL" -e "LAYERS=$LAYERS" -e "BBOX_FILE=data/wms_256_tiles.csv" \
    -p 8089:8089 -it stratus-tests
```
Note that the BBOX data must be baked into the docker file (data/wms_256_tiles.csv, a  is already present) or specified with `-v` as in:
```
docker run -e "URL=$URL" -e "LAYERS=$LAYERS" -e "BBOX_FILE=/data/bbox.csv" \
    -v ~/bbox.csv:/data/bbox.csv -p 8089:8089 -it stratus-tests
```
where the data resides locally on `~/bbox.csv` and is in the form `minx,miny,maxx,maxy`; e.g., 
```
-78.75,37.718590325588146,-77.34375,38.82259097617711
-77.34375,38.82259097617711,-75.9375,39.90973623453718
```
## Running/monitoring the test
Locust is running exposed on port 8089 on the client machine. Assuming localhost, visit http://localhost:8089 to see the locust UI and launch a test. You can watch stdout on the docker container for debugging info and/or change the verbosity of logging on the docker container itself. See [Building_locust.md](Building_locust.md).

## _Variable load Test  - Needs updating_:
_TODO_
Simulated load
To run a test where load changes over time, edit the usage_load.csv file to add periods of specific load. The file has two columns: [Number of Users], [Time in minutes].

python3 simulate_variable_usage.py
