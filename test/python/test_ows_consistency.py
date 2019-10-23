#!/usr/bin/python

import sys, math, time, subprocess
from optparse import OptionParser
from geoserver_utils import GeoserverUtil
from geoserver_db_utils import GeoserverDBUtil
import psycopg2
from psycopg2.extensions import AsIs, ISOLATION_LEVEL_AUTOCOMMIT
from PIL import Image

'''
OWS Consistency Test

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

'''
def main():
  print "Running OWS Consistency Test"

  parser = OptionParser()
  

  parser.add_option("--mode",  dest="mode", default="layer",
                  help="test type: \"layer\" or \"style\"")
  parser.add_option("--max-requests",  dest="max_requests", default=100,
                  help="maximum number of service requests to send in a single iteration")
  parser.add_option("--request-increment",  dest="request_increment", default=10,
                  help="number of successful service requests necessary to confirm consistency")
  parser.add_option("--iterations",  dest="iterations", default=1,
                  help="how many times to run the test")
  parser.add_option("--ws-name",  dest="ws_name", default="test_ows",
                  help="The workspace used for test data")
  parser.add_option("--debug",  dest="debug", default="false",
                  help="Show debug logging")
  parser.add_option("--geoserver-db-host",  dest="geoserver_db_host", default="localhost",
                      help="database host used by geoserver")
  parser.add_option("--geoserver-db-port",  dest="geoserver_db_port", default=5432,
                help="postgis port used by geoserver")

  GeoserverUtil.add_options(parser)
  

  (options, args) = parser.parse_args()

  # validate options
  debug=False
  if (options.debug != "false"):
    debug=True

  mode=""
  if (options.mode == "layer"):
    mode = "layer"
  elif (options.mode == "style"):
    mode = "style"
  else:
    print "Invalid mode: \""+options.mode+"\". Defaulting to \""+layer+"\""
    mode = "layer"

  max_requests = int(options.max_requests)
  request_increment = int(options.request_increment)
  iterations = int(options.iterations)


  # initialize utils
  geoserver_util=GeoserverUtil(
        geoserver_host=options.geoserver_host, 
        geoserver_port=options.geoserver_port, 
        geoserver_username=options.geoserver_username, 
        geoserver_password=options.geoserver_password, 
        db_host=options.geoserver_db_host, 
        db_port=options.geoserver_db_port, 
        db_username=options.db_username, 
        db_password=options.db_password,
        curl_retries=options.curl_retries,
        curl_max_time=options.curl_max_time,
        debug=debug)

  geoserver_db_util=GeoserverDBUtil(
        db_host=options.db_host, 
        db_port=options.db_port, 
        db_username=options.db_username, 
        db_password=options.db_password)

  test_runner = OWSConsistencyTest(mode, max_requests, request_increment, iterations, options.ws_name, debug, geoserver_util, geoserver_db_util)
  test_runner.run()

class OWSConsistencyTest:

  def __init__(self, mode, max_requests, request_increment, iterations, ws_name, debug, geoserver_util, geoserver_db_util):
    self.mode = mode
    self.max_requests = max_requests
    self.request_increment = request_increment
    self.iterations = iterations
    self.ws_name = ws_name
    self.debug=debug
    self.geoserver_util = geoserver_util
    self.geoserver_db_util = geoserver_db_util

  '''
  Runs a test, using the settings provided to the constructor
  '''
  def run(self):
    print "Test settings: Mode: %s; Max Requests: %s; Request Increment: %s; Iterations: %s" % (self.mode, self.max_requests, self.request_increment, self.iterations)

    print "Initializing Database..."

    geoserver_db_util = self.geoserver_db_util
    geoserver_util = self.geoserver_util
    ## Create DB
    ws_name=self.ws_name
    db_name="test_ows_consistency"
    table_name="features"

    layer_name = ws_name+":"+table_name

    conn = geoserver_db_util.get_database_connection("postgres")
    conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
    geoserver_db_util.create_database(conn,db_name)
    conn.commit()
    conn.cursor().close()
    conn.close()

    conn=geoserver_db_util.get_database_connection(db_name)
    geoserver_db_util.enable_postgis_database(conn, db_name)
    conn.close

    print "Database initialized"

    # Aggregate statistics
    wfs_total_requests=0
    wfs_total_failed_requests=0
    wfs_min_total_requests=sys.maxint
    wfs_min_total_failed_requests=sys.maxint
    wfs_max_total_requests=0
    wfs_max_total_failed_requests=0

    wms_total_requests=0
    wms_total_failed_requests=0
    wms_min_total_requests=sys.maxint
    wms_min_total_failed_requests=sys.maxint
    wms_max_total_requests=0
    wms_max_total_failed_requests=0

    wmts_total_requests=0
    wmts_total_failed_requests=0
    wmts_total_cache_misses=0
    wmts_min_total_requests=sys.maxint
    wmts_min_total_failed_requests=sys.maxint
    wmts_min_total_cache_misses=sys.maxint
    wmts_max_total_requests=0
    wmts_max_total_failed_requests=0
    wmts_max_total_cache_misses=0

    total_time=0
    min_time=sys.float_info.max
    max_time=0

    style_name="red_poly"
    
    for i in range(1, self.iterations+1):

      print ""
      print "Test run %d of %d" % (i, self.iterations)
      print "Initializing data..."
      self.init_data(db_name, ws_name, table_name)

      print "Verifying intitial state..."
      # Initial consistency checks
      if not self.TestFeature(table_name, layer_name, False): #id=0 should return
        print "Error: TestFeature failed"
        return -1

      if not self.TestMap(layer_name, False):     #should only show one feature
        print "Error: TestMap failed"
        return -1

      (tile, cache) = self.TestTile(layer_name, False)   #same as get map, should be cache miss
      if (not tile):
        print "Error: TestTile failed"
        return -1
      
      if self.debug:
        print "cache: %s" % cache
      if (cache != "MISS"):
        print "Error: Expected cache MISS, but was: \"%s\"" % cache
        return -1

      
      time.sleep(1)
      (tile, cache) = self.TestTile(layer_name, False)   #should be cache hit
      if (not tile):
        print "Error: Second TestTile failed"
        return -1

      if self.debug:
        print "cache: %s" % cache
      if (cache != "HIT"):
        print "Error: Expected cache HIT, but was: \"%s\"" % cache
        return -1
      
      print "Initial state verified."

      # make a change

      if (self.mode == "layer"):
        print "Inserting feature..."
        self.WFS_Insert(ws_name, layer_name, "-22.5,22.5 -22.5,67.5 -67.5,67.5 -67.5,22.5 -22.5,22.5", "added")
      elif (self.mode == "style"):
        print "Changing style..."
        self.POST_Style(style_name, "#FF0000")
        self.POST_SetStyle(style_name, layer_name)

      print "Executing test..."
      #Number of requests
      j=0
      #Check to see if the change is picked up
      consistent=False
      #start time
      start_time= time.time()
      #request stats
      wfs_failures = 0
      wms_failures = 0
      wmts_failures = 0
      wmts_misses = 0
      while not consistent and j < self.max_requests:

        consistent=True
        for k in range(1, self.request_increment+1):
        
          j=j+1

          wfs = self.TestFeature(table_name, layer_name, True)
          wms = self.TestMap(layer_name, True)
          (wmts, cache) = self.TestTile(layer_name, True)
          if self.debug:
            print "cache: %s" % cache
          if (cache == "MISS"):
            wmts_misses=wmts_misses+1
          

          if not wfs:
            wfs_failures=wfs_failures+1
          if not wms:
            wms_failures=wms_failures+1
          if not wmts:
            wmts_failures=wmts_failures+1

          #update consistent flag
          consistent = consistent and wfs and wms and wmts;

      delta_time = time.time() - start_time
      print "Test run %d complete after %0.4f seconds" % (i,delta_time)
      print "Results:"

      if consistent:
        print "SUCCESS: Reached consistency after %d requests (minimum for verification is %d)" % (j, self.request_increment)
      else:
        print "FAILURE: Failed to reach consistency after %d requests" % j

      print "WFS GetFeature failed to return the expected result in: %d of %d requests" % (wfs_failures, j)
      print "WMS GetMap failed to return the expected result in:     %d of %d requests" % (wms_failures, j)
      print "WMTS GetTile failed to return the expected result in:   %d of %d requests" % (wmts_failures, j)
      print "WMTS GetTile returned %d cache miss(es) (expected 1)" % wmts_misses

      # update statistics
      #total requests
      wfs_total_requests = wfs_total_requests+j
      wms_total_requests = wms_total_requests+j
      wmts_total_requests = wmts_total_requests+j
      #total failures
      wfs_total_failed_requests = wfs_total_failed_requests+wfs_failures
      wms_total_failed_requests = wms_total_failed_requests+wms_failures
      wmts_total_failed_requests = wmts_total_failed_requests+wmts_failures
      #total time
      total_time=total_time+delta_time
      #total misses
      wmts_total_cache_misses=wmts_total_cache_misses+wmts_misses

      #min requests
      wfs_min_total_requests = min(wfs_min_total_requests,j)
      wms_min_total_requests = min(wms_min_total_requests,j)
      wmts_min_total_requests = min(wmts_min_total_requests,j)
      #min failures
      wfs_min_total_failed_requests = min(wfs_min_total_failed_requests,wfs_failures)
      wms_min_total_failed_requests = min(wms_min_total_failed_requests,wms_failures)
      wmts_min_total_failed_requests = min(wmts_min_total_failed_requests,wmts_failures)
      #min time
      min_time=min(min_time,delta_time)
      #min misses
      wmts_min_total_cache_misses=min(wmts_min_total_cache_misses,wmts_misses)

      #max requests
      wfs_max_total_requests = max(wfs_max_total_requests,j)
      wms_max_total_requests = max(wms_max_total_requests,j)
      wmts_max_total_requests = max(wmts_max_total_requests,j)
      #max failures
      wfs_max_total_failed_requests = max(wfs_max_total_failed_requests,wfs_failures)
      wms_max_total_failed_requests = max(wms_max_total_failed_requests,wms_failures)
      wmts_max_total_failed_requests = max(wmts_max_total_failed_requests,wmts_failures)
      #max time
      max_time=max(max_time,delta_time)
      #max misses
      wmts_max_total_cache_misses=max(wmts_max_total_cache_misses,wmts_misses)
      

    # clean up
    self.geoserver_util.delete_workspace(ws_name)
    self.DELETE_Style(style_name)

    # print report
    if self.iterations > 1:
      print ""
      print "All %d test runs complete." % self.iterations
      print ""
      print "Results:"
      print "+------------------------------------+------------------+------------------+------------------+"
      print "|                                    | %-16s | %-16s | %-16s |" % ("Average", "Minimum", "Maximum")
      print "+------------------------------------+------------------+------------------+------------------+"
      print "| Test Time                          | %-16s | %-16s | %-16s |" % ("%0.4f" % (total_time/self.iterations),"%0.4f" % min_time,"%0.4f" % max_time)
      print "| Requests to completion (min. %-4d) | %-16s | %-16s | %-16s |" % (self.request_increment, str(wfs_total_requests/self.iterations),str(wfs_min_total_requests),str(wfs_max_total_requests))
      print "| Failed WFS Requests                | %-16s | %-16s | %-16s |" % (str(wfs_total_failed_requests/self.iterations),str(wfs_min_total_failed_requests),str(wfs_max_total_failed_requests))
      print "| Failed WMS Requests                | %-16s | %-16s | %-16s |" % (str(wms_total_failed_requests/self.iterations),str(wms_min_total_failed_requests),str(wms_max_total_failed_requests))
      print "| Failed WMTS Requests               | %-16s | %-16s | %-16s |" % (str(wmts_total_failed_requests/self.iterations),str(wmts_min_total_failed_requests),str(wmts_max_total_failed_requests))
      print "| WMTS Cache Misses (expect 1)       | %-16s | %-16s | %-16s |" % (str(wmts_total_cache_misses/self.iterations),str(wmts_min_total_cache_misses),str(wmts_max_total_cache_misses))
      print "+------------------------------------+------------------+------------------+------------------+"
      
    elif self.iterations == 1:
      print ""
      print "Only one test itertation, skipping summary report"


  '''
  Initializes the test environment:

  In the datanase: Creates a new table containing a single 45x45 square centered at the origin.

  In GeoServer: Creates a new workspace, a store linking to the database, and a layer publishing the features in the table
  '''
  def init_data(self, db_name, ws_name, table_name):
    conn=self.geoserver_db_util.get_database_connection(db_name)
    ## Create table
    self.geoserver_db_util.create_table(conn, table_name, "Polygon")

    ##Insert feature 0
    self.geoserver_db_util.insert_feature(conn, table_name, wkt="POLYGON ((-22.5 -22.5, 22.5 -22.5, 22.5 22.5, -22.5 22.5, -22.5 -22.5))")

    #Remove old workspace, if it exists
    self.geoserver_util.delete_workspace(ws_name)
    ## Create workpace
    self.geoserver_util.create_workspace(ws_name)
    ## Create store
    self.geoserver_util.create_datastore(ws_name, db_name, db_name)
    ## Create layer
    self.geoserver_util.create_featuretype(ws_name, db_name, table_name)

    conn.close

  '''
  Sends a WFS Transaction Insert request to geoserver, adding a new Polygon feature to the named layer
  ws_name      the name of the workspace
  layer_name   the prefixed name of the layer
  coords       the coordinates of the new feature, of the form "y1,x1 y2,x2 ..."
  description  the description of the layer
  '''
  def WFS_Insert(self, ws_name, layer_name, coords, description):
    geoserver_util = self.geoserver_util
    wfs_insert = '<wfs:Transaction service="WFS" version="1.0.0" xmlns:wfs="http://www.opengis.net/wfs" xmlns:%s="http://%s" xmlns:gml="http://www.opengis.net/gml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-transaction.xsd http://%s %s/geoserver/wfs/DescribeFeatureType?typename=%s"><wfs:Insert><%s><%s:geom><gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates decimal="." cs="," ts=" ">%s</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs></gml:Polygon></%s:geom><%s:description>%s</%s:description></%s></wfs:Insert></wfs:Transaction>' % (ws_name, ws_name, ws_name, geoserver_util.geoserver_host_url, layer_name, layer_name, ws_name, coords, ws_name, ws_name, description, ws_name, layer_name)
    if (self.debug):
      print wfs_insert

    wfs_curl = 'curl -sS -retry '+geoserver_util.curl_retries+' --max-time '+geoserver_util.curl_max_time+' -u '+geoserver_util.geoserver_username+':'+geoserver_util.geoserver_password+' -XPOST -H "Content-Type: application/xml" -d \''+wfs_insert+'\' "'+geoserver_util.geoserver_host_url+'/geoserver/wfs?service=WFS&version=1.0.0&request=Transaction"'
    
    if (self.debug):
      print wfs_curl
    output = subprocess.check_output(['bash','-c', wfs_curl])

    return output

  '''
  Using the GeoServer REST API, creates a new simple polygon style (mbstyle format)
  style_name  the name of the new style
  color       the polygon color
  '''
  def POST_Style(self, style_name, color):
    geoserver_util = self.geoserver_util
    style_curl = 'curl -sS -retry '+geoserver_util.curl_retries+' --max-time '+geoserver_util.curl_max_time+' -u '+geoserver_util.geoserver_username+':'+geoserver_util.geoserver_password+' -XPOST -H "Content-Type: application/vnd.geoserver.mbstyle+json" -d \'{"version":8,"name":"simple-polygon","layers":[{"id":"polygon","type":"fill","paint": {"fill-color": "'+color+'"} }]}\' "'+geoserver_util.geoserver_host_url+'/geoserver/rest/styles?name='+style_name+'"'
    output = subprocess.check_output(['bash','-c', style_curl])

    return output

  '''
  Using the GeoServer REST API, assignes an existing style to an existing layer
  style_name  The name of the style
  layer_name  The name of the layer
  '''
  def POST_SetStyle(self, style_name, layer_name):
    geoserver_util = self.geoserver_util
    setstyle_curl = 'curl -sS -retry '+geoserver_util.curl_retries+' --max-time '+geoserver_util.curl_max_time+' -u '+geoserver_util.geoserver_username+':'+geoserver_util.geoserver_password+' -XPOST -H "Content-Type: application/json" -d \'{"style":{"name":"'+style_name+'"} }\' "'+geoserver_util.geoserver_host_url+'/geoserver/rest/layers/'+layer_name+'/styles?default=true"'
    output = subprocess.check_output(['bash','-c', setstyle_curl])

    return output

  '''
  Using the GeoServer REST API, deletes a style
  style_name  The name of the style
  '''
  def DELETE_Style(self, style_name):
    geoserver_util = self.geoserver_util
    style_curl = 'curl -sS -u '+geoserver_util.geoserver_username+':'+geoserver_util.geoserver_password+' -XDELETE "'+geoserver_util.geoserver_host_url+'/geoserver/rest/styles/'+style_name+'"'
    output = subprocess.check_output(['bash','-c', style_curl])

    return output

  '''
  Runs the WFS test against the feature, verifying that the expected features exist and unexpected features do not exist
  type_name.  The name of the feature type of the layer (no prefix)
  layer_name  The name of the layer to test (including prefix)
  updated     Determines the expectations of the test. If True, <type_name>.1 and <type_name>.2 should exist. Otherwise, only <type_name>.1 should exist

  Returns True for Pass and False for Fail
  '''
  def TestFeature(self, type_name, layer_name, updated):
    geoserver_util = self.geoserver_util
    if (self.mode == "layer"):
      
      self.WFS_GetFeature(layer_name)
      wfs = open('wfs.xml','r');

      # getFeature(1) should succeed
      fid1_found=False
      fid2_found=False

      for line in wfs:
        if ("<%s fid=\"%s.1\">" % (layer_name, type_name)) in line:
          fid1_found = True
        if ("<%s fid=\"%s.2\">" % (layer_name, type_name)) in line:
          fid2_found = True

      if (updated):
        # getFeature(2) should succeed
        return fid1_found and fid2_found
      else:
        # getFeature(2) should fail
        return fid1_found and not fid2_found

    #TestFeature not relevant for mode="style"
    return True

  '''
  Runs the WMS test against the layer
  layer_name  The name of the layer to test (including prefix)
  updated     Determines the expectations of the test, in conjunction with self.mode:
              mode="layer",updated=False - Bottom-right gray square
              mode="layer",updated=True - Bottom-right and top-left gray square
              mode="style",updated=False - Bottom-right gray square
              mode="style",updated=True - Bottom-right red square

  Returns True for Pass and False for Fail
  '''
  def TestMap(self, layer_name, updated):
    geoserver_util = self.geoserver_util
    self.WMS_GetMap(layer_name)
    return self.TestImage(updated,'wms.png')

  '''
  Runs the WMTS test against the layer
  layer_name  The name of the layer to test (including prefix)
  updated     Determines the expectations of the test, in conjunction with self.mode:
              mode="layer",updated=False - Bottom-right gray square
              mode="layer",updated=True - Bottom-right and top-left gray square
              mode="style",updated=False - Bottom-right gray square
              mode="style",updated=True - Bottom-right red square

  Returns a tuple indicating the test result (True for Pass and False for Fail) and if it was a cache HIT or MISS
  '''
  def TestTile(self, layer_name, updated):
    geoserver_util = self.geoserver_util
    self.WMTS_GetTile(layer_name)

    headers = open('wmts_headers.txt','r');

    geowebcache_cache_result=""
    for line in headers:
      if "geowebcache-cache-result" in line:
        geowebcache_cache_result=line[26:].strip()

    return (self.TestImage(updated,'wmts.png'),geowebcache_cache_result)

  '''
  Tests WMS and WMTS images for correctness
  updated     Determines the expectations of the test, in conjunction with self.mode:
              mode="layer",updated=False - Bottom-right gray square
              mode="layer",updated=True - Bottom-right and top-left gray square
              mode="style",updated=False - Bottom-right gray square
              mode="style",updated=True - Bottom-right red square
  filename    the name of the file to test

  Returns True for Pass and False for Fail
  '''
  def TestImage(self, updated, filename):
    wms = Image.open(filename, 'r')
    pixels = list(wms.getdata())

    #test 63,63 and 191,191

    top_left = pixels[(256*63 + 63)]
    bottom_right = pixels[(256*191 + 191)]

    #trim alpha, if applicable
    top_left = top_left[0:3]
    bottom_right = bottom_right[0:3]


    if self.debug:
      print top_left
      print bottom_right

    if (self.mode == "layer"):
      if (updated):
        return (top_left == (170,170,170) and bottom_right == (170,170,170))
      else:
        return (top_left == (255,255,255) and bottom_right == (170,170,170))
    elif (self.mode == "style"):
      if (updated):
        return (top_left == (255,255,255) and bottom_right == (255,0,0))
      else:
        return (top_left == (255,255,255) and bottom_right == (170,170,170))

    return True


  # OWS Operations
  '''
  Performs a WFS GetFeature request
  type_name  The feature type to query
  outputfile The file to save the results to
  '''
  def WFS_GetFeature(self, type_name, outputfile="wfs.xml"):
    geoserver_util = self.geoserver_util
    wfs_curl = "curl -sS -o %s \"%s/geoserver/wfs?service=WFS&version=1.0.0&request=GetFeature&typeName=%s\"" % (outputfile, geoserver_util.geoserver_host_url,type_name)
    if self.debug:
      print wfs_curl
    output = subprocess.check_output(['bash','-c', wfs_curl])

    return output

  '''
  Performs a WMS GetMap request
  layers     The layer(s) to query
  styles     The style(s) associated with the layer(s)
  bbox       The bbox of the request
  outputfile The file to save the results to
  '''
  def WMS_GetMap(self, layers, styles="", bbox="-45,0,0,45", outputfile="wms.png"):
    geoserver_util = self.geoserver_util
    wms_curl = "curl -sS -o %s \"%s/geoserver/wms?service=WMS&version=1.1.0&request=GetMap&layers=%s&styles=%s&bbox=%s&width=256&height=256&srs=EPSG:4326&format=image/png\"" % (outputfile, geoserver_util.geoserver_host_url,layers,styles,bbox)
    if self.debug:
      print wms_curl
    output = subprocess.check_output(['bash','-c', wms_curl])

    return output

  '''
  Performs a WMTS GetTile request
  layer        The layer to query
  style        The style associated with the layer
  tile_matrix  SRS:Level
  tile_col     Tile column in the matrix
  tile_row     Tile row in the matrix
  outputfile   The file to save the results to
  headerfile   The file to save the response headers to
  '''
  def WMTS_GetTile(self,layer,style="",tile_matrix="EPSG:4326:2",tile_col=3,tile_row=1, outputfile="wmts.png", headerfile="wmts_headers.txt"):
    geoserver_util = self.geoserver_util
    wmts_curl = "curl -sS -D %s -o %s \"%s/geoserver/gwc/service/wmts?Service=WMTS&Request=GetTile&Version=1.0.0&layer=%s&style=%s&tilematrixset=EPSG:4326&TileMatrix=%s&TileCol=%d&TileRow=%d&Format=image/png\"" % (headerfile, outputfile, geoserver_util.geoserver_host_url,layer,style,tile_matrix,tile_col,tile_row)
    if self.debug:
      print wmts_curl
    output = subprocess.check_output(['bash','-c', wmts_curl])

    return output


if __name__ == "__main__":
  main()
