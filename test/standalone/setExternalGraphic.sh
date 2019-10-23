#!/bin/bash
#Upload external graphic
curl -u admin:geoserver -XPUT -H "Content-type: image/png" --data-binary @styles/smileyface.png "http://localhost:8080/geoserver/rest/resource/styles/smileyface.png"
#Modifies the default point style to use an external graphic
curl -u admin:geoserver -XPUT -H "Content-type: text/xml" -d@styles/externalgraphic.sld http://localhost:8080/geoserver/rest/styles/point
#Assigns the modified point style as the defaul style of the acme:ne_10m_roads_north_america layer
curl -u admin:geoserver -XPUT -H "Content-type: application/xml" -d '<layer><defaultStyle><name>point</name><atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/styles/point.xml" type="application/xml"/></defaultStyle></layer>' "http://localhost:8080/geoserver/rest/layers/ne_10m_roads_north_america"
