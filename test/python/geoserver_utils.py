#!/usr/bin/python 

import sys, math, time, random, subprocess
from optparse import OptionParser

"""
Utility class for creating workspaces, stores, and layers in GeoServer using
the REST API.

cURL is used to send the requests, and must be installed and on the PATH.
"""
class GeoserverUtil:

    '''
    Add options required by GeoserverUtil to the provided OptionParser
    '''
    @staticmethod
    def add_options(parser):
        parser.add_option("--geoserver-host",  dest="geoserver_host", default="localhost",
                  help="geoserver host (e.g., 45f7c706595539e802d375bb8e139587-738985581.us-east-1.elb.amazonaws.com")
        parser.add_option("--geoserver-port",  dest="geoserver_port", default=8080,
                      help="geoserver port")
        parser.add_option("--geoserver-username",  dest="geoserver_username", default="admin",
                      help="geoserver username")
        parser.add_option("--geoserver-password",  dest="geoserver_password", default="geoserver",
                      help="geoserver password")
        parser.add_option("--db-host",  dest="db_host", default="localhost",
                      help="database host ")
        parser.add_option("--db-username",  dest="db_username", default="docker",
                      help="database username")
        parser.add_option("--db-password",  dest="db_password", default="docker",
                      help="db_password")
        parser.add_option("--db-port",  dest="db_port", default=5432,
                      help="postgis port")
        parser.add_option("--curl-retries",  dest="curl_retries", default="10",
                  help="Number of curl retries")
        parser.add_option("--curl-max-time",  dest="curl_max_time", default="600",
                      help="curl max time")


    def __init__(self, 
            geoserver_host="localhost", 
            geoserver_port="8080", 
            geoserver_username="admin", 
            geoserver_password="geoserver", \
            db_host="localhost", 
            db_port="5432", 
            db_username="docker", 
            db_password="docker",
            curl_retries=20,
            curl_max_time=600,
            debug=True):
        self.geoserver_host=geoserver_host
        self.geoserver_port=geoserver_port
        self.geoserver_host_url='http://'+geoserver_host+':'+str(geoserver_port)
        self.geoserver_username=geoserver_username
        self.geoserver_password=geoserver_password
        self.db_host=db_host
        self.db_username=db_username
        self.db_password=db_password
        self.db_port=db_port
        self.curl_retries=curl_retries
        self.curl_max_time=curl_max_time
        self.debug=debug


    def delete_workspace(self, workspace_name):
        wd_curl = 'curl -sS -u '+self.geoserver_username+':'+self.geoserver_password+' -XDELETE "'+self.geoserver_host_url+'/geoserver/rest/workspaces/'+workspace_name+'?recurse=true"'
        if (self.debug):
          print wd_curl
        output = subprocess.check_output(['bash','-c', wd_curl])

    def create_workspace(self, workspace_name):
        ws_curl = 'curl -sS --retry '+self.curl_retries+' --max-time '+self.curl_max_time+' -u '+self.geoserver_username+':'+self.geoserver_password+' -XPOST -H "Content-type: text/xml" -d "<workspace><name>'+workspace_name
        ws_curl = ws_curl+'</name></workspace>" '+self.geoserver_host_url+'/geoserver/rest/workspaces'
        if (self.debug):
          print ws_curl
        output = subprocess.check_output(['bash','-c', ws_curl])

    def create_datastore(self, workspace_name, database_name, datastore_name):
        st_curl = 'curl -sS --retry '+self.curl_retries+' --max-time '+self.curl_max_time+' -u '+self.geoserver_username+':'+self.geoserver_password+' -XPOST -H "Content-type: text/xml" -d "<dataStore><name>'+datastore_name
        st_curl = st_curl+'</name><connectionParameters><host>'+self.db_host+'</host><port>'+str(self.db_port)+'</port><database>'+database_name+'</database><user>'+self.db_username+'</user><passwd>'+self.db_password+'</passwd>'
        st_curl = st_curl+'<dbtype>postgis</dbtype></connectionParameters></dataStore>" '+self.geoserver_host_url+'/geoserver/rest/workspaces/'+workspace_name+'/datastores'


        if (self.debug):
          print st_curl
        output = subprocess.check_output(['bash','-c', st_curl])

    def create_featuretype(self, workspace_name,datastore_name, featuretype_name):

        l_curl = 'curl -sS --retry '+self.curl_retries+' --max-time '+self.curl_max_time+' -u  '+self.geoserver_username+':'+self.geoserver_password+' -H "Content-type: text/xml" -d "'
        l_curl = l_curl + '<featureType><name>'+featuretype_name+'</name></featureType>'
        l_curl = l_curl + '" '+self.geoserver_host_url+'/geoserver/rest/workspaces/'+workspace_name+'/datastores/'+datastore_name+'/featuretypes'
        if (self.debug):
          print l_curl
        output = subprocess.check_output(['bash','-c', l_curl])


    def create_coveragestore(self, workspace_name, coverage_store_name, img_url):
        coveragestore_delete_curl = 'curl -sS --retry '+self.curl_retries+' --max-time '+self.curl_max_time+' -u '+self.geoserver_username+':'+self.geoserver_password+' -XDELETE -H "Accept: text/xml" '+self.geoserver_host_url+'/geoserver/rest/workspaces/'+workspace_name+'/'+coveragestore_name+'?recurse=true'
        if (self.debug):
          print coveragestore_delete_curl
        output = subprocess.check_output(['bash','-c', coveragestore_delete_curl])
        coveragestore_post_curl = 'curl -sS --retry '+self.curl_retries+' --max-time '+self.curl_max_time+' -u '+self.geoserver_username+':'+self.geoserver_password+' -v -XPOST -H "Content-type: text/xml" -d '+\
            '"<coverageStore>'+\
            '  <name>'+coveragestore_name+'</name>'+\
            '  <workspace><name>'+workspace_name+'</name></workspace>'+\
            '  <enabled>true</enabled>'+\
            '  <type>GeoTIFF</type>'+\
            '  <url>'+img_url+'</url>'+\
            '</coverageStore>" ' +\
            'http://'+self.geoserver_host_url+'/geoserver/rest/workspaces/'+workspace_name+'/coveragestores?configure=all'
        if (self.debug):
          print coveragestore_post_curl
        output = subprocess.check_output(['bash','-c', coveragestore_post_curl])

    def create_coverage(self, workspace_name, coverage_store_name, coverage_name, coverage_title, native_crs, srs, minx, maxx, miny, maxy):
        coverage_delete_curl = 'curl -sS --retry '+self.curl_retries+' --max-time '+self.curl_max_time+' -u '+self.geoserver_username+':'+self.geoserver_password+' -XDELETE -H "Accept: text/xml" '+self.geoserver_host_url+'/geoserver/rest/workspaces/'+workspace_name+'/'+coverage_store_name+'?recurse=true'
        if (self.debug):
          print coverage_delete_curl
        output = subprocess.check_output(['bash','-c', coverage_delete_curl])
        coverage_post_curl = 'curl -sS -u '+self.geoserver_username+':'+self.geoserver_password+' -v -XPOST -H "Content-type: text/xml" -d '+\
        '"<coverage>'+\
            '<name>'+coverage_name+'</name>'+\
            '<title>'+coverage_title+'</title>'+\
            '<nativeCRS>'+native_crs+'</nativeCRS>'+\
            '<srs>'+srs+'</srs>'+\
            '<latLonBoundingBox><minx>-'+minx+'</minx><maxx>'+maxx+'</maxx><miny>'+miny+'</miny><maxy>'+maxy+'</maxy><crs>EPSG:4326</crs></latLonBoundingBox>'+\
        '</coverage>"'+\
        ' http://'+self.geoserver_host_url+'/geoserver/rest/workspaces/'+workspace_name+'/coveragestores/'+coveragestore_name+'/coverages'
        if (self.debug):
          print coverage_post_curl
        output = subprocess.check_output(['bash','-c', coverage_post_curl])

