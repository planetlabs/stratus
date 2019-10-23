#!/bin/sh

#Init

    export HOST=localhost
    export PORT=8080

#Upload legend png

    curl -v -u admin:geoserver -XPUT -T legend.png -H "Content-type: image/png" http://"$HOST":"$PORT"/geoserver/rest/resource/styles/legend.png

#Upload line style

    curl -u admin:geoserver -v -XPOST -d @mbstyle_line_borderedline.json -H "Content-Type: application/vnd.geoserver.mbstyle+json" "$HOST":"$PORT"/geoserver/rest/styles?name=mbstyle_line

#Update style config

    curl -u admin:geoserver -v -XPUT -d @config/mbstyle_line.xml -H "Content-Type: application/xml" "$HOST":"$PORT"/geoserver/rest/styles/mbstyle_line

###

#Create new workspace

    curl -v -u admin:geoserver -XPOST -H "Content-type: text/xml" -d "<workspace><name>test</name></workspace>" http://"$HOST":"$PORT"/geoserver/rest/workspaces

#Create default blobstore

curl -v -u admin:geoserver -XPUT -H "Content-type: text/xml" -d @config/blobStore.xml http://"$HOST":"$PORT"/geoserver/gwc/rest/blobstores/default

#Create PostGIS store

    #curl -v -u admin:geoserver -XPOST -d @config/vectorStore.xml -H "Content-type: text/xml" http://"$HOST":"$PORT"/geoserver/rest/workspaces/test/datastores

    # amend postgis ip
    source env.sh
    VECTOR_STORE="<dataStore>
                    <name>roads</name>
                    <type>PostGIS</type>
                    <enabled>true</enabled>
                    <workspace>
                      <name>test</name>
                    </workspace>
                    <connectionParameters>
                      <entry key=\"Estimated extends\">false</entry>
                      <entry key=\"encode functions\">false</entry>
                      <entry key=\"Support on the fly geometry simplification\">false</entry>
                      <entry key=\"Expose primary keys\">false</entry>
                      <entry key=\"validate connections\">false</entry>
                      <entry key=\"create database\">false</entry>
                      <entry key=\"preparedStatements\">false</entry>
                      <entry key=\"database\">na_roads</entry>
                      <entry key=\"passwd\">docker</entry>
                      <entry key=\"port\">5432</entry>
                      <entry key=\"dbtype\">postgis</entry>
                      <entry key=\"namespace\">http://test</entry>
                      <entry key=\"host\">$POSTGIS_IP</entry>
                      <entry key=\"Loose bbox\">false</entry>
                      <entry key=\"Test while idle\">false</entry>
                      <entry key=\"user\">docker</entry>
                    </connectionParameters>
                  </dataStore>"
    curl -v -u admin:geoserver -XPOST -d "$VECTOR_STORE" -H "Content-type: text/xml" http://"$HOST":"$PORT"/geoserver/rest/workspaces/test/datastores

#Publish roads

    curl -v -u admin:geoserver -XPOST -d @config/vectorLayer.xml -H "Content-type: text/xml" http://"$HOST":"$PORT"/geoserver/rest/workspaces/test/datastores/roads/featuretypes

#Create mosaic store

    curl -v -u admin:geoserver -XPOST -d @config/mosaicStore.xml -H "Content-type: text/xml" http://"$HOST":"$PORT"/geoserver/rest/workspaces/test/coveragestores

#Publish mosiac

    curl -v -u admin:geoserver -XPOST -d @config/mosaicLayer.xml -H "Content-type: text/xml" http://"$HOST":"$PORT"/geoserver/rest/workspaces/test/coveragestores/mosaic/coverages

#Create wms store

    curl -v -u admin:geoserver -XPOST -d @config/wmsStore.xml -H "Content-type: text/xml" http://"$HOST":"$PORT"/geoserver/rest/workspaces/test/wmsstores

#Publish wms layer

    curl -v -u admin:geoserver -XPOST -d @config/wmsLayer.xml -H "Content-type: text/xml"  http://"$HOST":"$PORT"/geoserver/rest/workspaces/test/wmsstores/mosaicCascade/wmslayers

#Create wmts store

    curl -v -u admin:geoserver -XPOST -d @config/wmtsStore.xml -H "Content-type: text/xml" http://"$HOST":"$PORT"/geoserver/rest/workspaces/test/wmtsstores

#Publish wmts layer

    curl -v -u admin:geoserver -XPOST -H "Content-type: text/xml" -d @config/wmtsLayer.xml http://"$HOST":"$PORT"/geoserver/rest/workspaces/test/wmtsstores/vectorCascade/layers

###

# Update global settings

    curl -v -u admin:geoserver -XPUT -d @config/globalSettings.xml -H "Content-type: text/xml" http://"$HOST":"$PORT"/geoserver/rest/settings

# Update contact settings

    curl -v -u admin:geoserver -XPUT -d @config/contactSettings.xml -H "Content-type: text/xml" http://"$HOST":"$PORT"/geoserver/rest/settings/contact

# TODO: Update logging settings - Is there a rest endpoint for this?

# Update WMS Settings

    curl -v -u admin:geoserver -XPUT -d @config/wmsSettings.xml -H "Content-type: text/xml" http://"$HOST":"$PORT"/geoserver/rest/services/wms/settings

# Update WFS Settings

    curl -v -u admin:geoserver -XPUT -d @config/wfsSettings.xml -H "Content-type: text/xml" http://"$HOST":"$PORT"/geoserver/rest/services/wfs/settings

# Update WCS Settings

    curl -v -u admin:geoserver -XPUT -d @config/wcsSettings.xml -H "Content-type: text/xml" http://"$HOST":"$PORT"/geoserver/rest/services/wcs/settings

# Add local WMS Settings

    curl -v -u admin:geoserver -XPUT -d @config/testWmsSettings.xml -H "Content-type: text/xml" http://"$HOST":"$PORT"/geoserver/rest/services/wms/workspaces/test/settings

