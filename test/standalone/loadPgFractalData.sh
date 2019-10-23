TIMEFORMAT=' --> Workspace created in %R seconds.'
time {
curl -v -u admin:geoserver -XPOST -H "Content-type: text/xml" -d "<workspace><name>acme</name></workspace>" http://localhost:8080/geoserver/rest/workspaces
printf "\n\n"
}

printf "\n******************************************\n"

TIMEFORMAT=' --> Data store created in %R seconds.'
time {
curl -v -u admin:geoserver -XPOST -T ../data/pgFractalDataStore.xml -H "Content-type: text/xml" http://localhost:8080/geoserver/rest/workspaces/acme/datastores
printf "\n\n"
}

printf "\n******************************************\n"

TIMEFORMAT=' --> Layer created in %R seconds.'
time {
curl -v -u admin:geoserver -H "Content-type: text/xml" -d "<featureType><name>sierpinski_carpet</name></featureType>" http://localhost:8080/geoserver/rest/workspaces/acme/datastores/sierpinski/featuretypes
printf "\n\n"
}
