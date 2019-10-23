# Standalone tests

Standalone test scripts for ad-hoc testing.
Depends upon the test data in [test/data](../data).
See also: [deploy/standalone](../../deploy/standalone) for scripts to set up a test environment that is compatible with these scripts.

* [createWorkspace.sh](./createWorkspace.sh) - Creates a workspace `acme` in the local GeoServer
* [fractal_postgis.py](./fractal_postgis.py) - Creates a single PostGIS table containing a number of iterations of the sierpinski carpet fractal. This can be used to generate large datasets for load-testing purposes.
* [getExternalGraphic.sh](./getExternalGraphic.sh) - Downloads the external graphic for the `acme:roads_10m_north_america` layer as `externalGraphic.png`
* [getLegendGraphic.sh](./getLegendGraphic.sh) - Downloads `geoserver/styles/smileyface.png` as `legendGraphic.png`
* [loadGeotiff.sh](./loadGeotiff.sh) - Loads the FAS Brazil tif to the `acme:tiff` coverage store.
* [loadPgData.sh](./loadPgData.sh) - Creates the `acme` workspace, `na_roads` datastore, and `ne_10m_roads_north_america` layer. Run [na_roads_postgis.sh](./na_roads_postgis.sh) first to create the required database.
* [loadPgFractalData.sh](./loadPgFractalData.sh) - Creates the `acme` workspace, `sierpinski` datastore, and `sierpinski_carpet` layer. Run [fractal_postgis.py](./fractal_postgis.py) first to create the required database.
* [na_roads_postgis.sh](./na_roads_postgis.sh) - Loads the na_roads shapefile into postgis
* [setExternalGraphic.sh](./setExternalGraphic.sh) - Sets `smileyface.png` as the external graphic for the global `points` layer, and sets this style as the default for the `acme:ne_10m_roads_north_america` layer.
* [setLegendGraphic.sh](./setLegendGraphic.sh) - Sets `smileyface.png` as the external graphic for the global `lines` layer, and sets this style as the default for the `acme:ne_10m_roads_north_america` layer.

## Upgrade

A self-contained Stratus upgrade test. See [upgrade/README.md](./upgrade/README.md) for details.
