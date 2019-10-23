# Python test framework

Python libraries and scripts for general Stratus testing

## Generate Points

[generate_points.py](./generate_points.py) is a utility for loading bulk data into GeoServer. It creates a configurable number of workspaces in GeoServer, each containing some number of vector layers, where each layer contains a random distribution of points.

It is primarily used by other tests (such as [jmeter](../jmeter) for testing the performance effects of different numbers of workspaces, layers, and features on the GeoServer catalog and services, but can also be used to set up a catalog for manual testing.

## Consistency Tests

These tests are intended to be run against a Stratus instance with multiple nodes, and are used to determine how long it takes all nodes to return a consistent result after a catalog change on one node.

Note: Consistency is determined by querying the GeoServer endpoint multiple times - if each response matches the expected value. Therefore, if your load balancer is configured to asign a single node to any given client, this test will not produce meaningful results.

### OWS Consistency Test

[test_ows_consistency.py](./test_ows_consistency.py)

Test infrastructure for verifying that changes to a layer or style in 
geoserver are (promptly) reflected across all nodes and services.

Tests: WFS, WMS, WMTS

Two test modes are supported:

layer (default): Tests adding a feature to a layer
style: Tests changing the style of a layer

Test structure:

1. Initialize spatial database
2. Initialize test data
3. Verify initial state
4. Alter the layer
5. Query the layer until expected results are consistently obtained. Collect statistics on failures.
6. Repeat steps 2-5 for the number of iterations requested. Aggregate statistics
7. Clear test data
8. Print aggregate report


### REST Consistency Test

[test_rest_consistency.py](./test_rest_consistency.py)

Creates a workspace, creates a store, creates a layer.
Gathers statistics on how long it takes for this to be successful.

## Test libraries

The rest of the python files are utilities or libraries used by one or more of the test scripts described above.

* [geoserver_db_utils.py](./geoserver_db_utils.py) - Utility class for populating and querying a spatially-enabled PostgreSQL Database.
* [geoserver_doer_facade.py](./geoserver_doer_facade.py) - Utility class for generating random points, workspaces, and layers.
* [geoserver_name_utils.py](.geoserver_name_utils.py) - Utility class for generating PostGIS database and table names, and GeoServer 
workspace, store, and layer names, based on certain proviced parameters.
* [geoserver_utils.py](./geoserver_utils.py) - Utility class for creating workspaces, stores, and layers in GeoServer using
the REST API.

