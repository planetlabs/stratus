PORT=$1
if [ -z "$PORT" ]
then
    PORT="8080"
fi

TIMEFORMAT=' --> Workspace created in %R seconds.'
time {
curl -v -u admin:geoserver -XPOST -H "Content-type: text/xml" -d "<workspace><name>acme</name></workspace>" http://localhost:"$PORT"/geoserver/rest/workspaces
printf "\n\n"
}

printf "\n******************************************\n"

TIMEFORMAT=' --> Data store created in %R seconds.'
time {
curl -v -u admin:geoserver -XPOST -T ../data/pgDataStore.xml -H "Content-type: text/xml" http://localhost:"$PORT"/geoserver/rest/workspaces/acme/datastores
printf "\n\n"
}

printf "\n******************************************\n"

TIMEFORMAT=' --> Layer created in %R seconds.'
time {
curl -v -u admin:geoserver -H "Content-type: text/xml" -d "<featureType><name>ne_10m_roads_north_america</name></featureType>" http://localhost:"$PORT"/geoserver/rest/workspaces/acme/datastores/na_roads/featuretypes
printf "\n\n"
}
