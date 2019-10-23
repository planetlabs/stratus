cwd=$(pwd)

if [ ! -f FAS_Brazil1.2013363.aqua.ndvi.2km.tif ]; then
	wget http://www.qgistutorials.com/downloads/FAS_Brazil1.2013363.aqua.ndvi.2km.tif
fi

echo "file:$cwd/land_shallow_topo.tif"
curl -v -u admin:geoserver -XPUT -H "Content-type: text/plain" -d "file:$cwd/FAS_Brazil1.2013363.aqua.ndvi.2km.tif" "http://localhost:8080/geoserver/rest/workspaces/acme/coveragestores/tiff/external.geotiff"

