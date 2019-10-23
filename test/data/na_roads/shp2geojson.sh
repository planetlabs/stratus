ogr2ogr -f geoJSON roads.json ne_10m_roads_north_america.shp
sed 's/,$//' roads.json > roads_no_comma.json
# also need to manually remove the opening curly brace, "type", and '"feautres": [' lines from the beginning of the json file, and the last two braces "]}" from the end of the file

