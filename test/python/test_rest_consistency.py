#!/usr/bin/python

import sys, math, time, subprocess
from optparse import OptionParser
import psycopg2
from psycopg2.extensions import AsIs, ISOLATION_LEVEL_AUTOCOMMIT
from geoserver_db_utils import GeoserverDBUtil

'''
REST Consistency Test

Creates a workspace, creates a store, creates a layer.
Gathers statistics on how long it takes for this to be successful.
'''
def main():
  parser = OptionParser()
  parser.add_option("--geoserver-host",  dest="geoserver_host", default="localhost",
                help="geoserver host (e.g., 45f7c706595539e802d375bb8e139587-738985581.us-east-1.elb.amazonaws.com")
  parser.add_option("--geoserver-port",  dest="geoserver_port", default=8080,
                help="geoserver port")
  parser.add_option("--geoserver-username",  dest="geoserver_username", default="admin",
                help="geoserver username")
  parser.add_option("--geoserver-password",  dest="geoserver_password", default="geoserver",
                help="geoserver password")
  parser.add_option("--geoserver-db-host",  dest="geoserver_db_host", default="localhost",
                      help="database host used by geoserver")
  parser.add_option("--geoserver-db-port",  dest="geoserver_db_port", default=5432,
                help="postgis port used by geoserver")
  parser.add_option("--ws-name",  dest="ws_name", default="test_rest",
                  help="The workspace used for test data")
  parser.add_option("--debug",  dest="debug", default="false",
                  help="Show debug logging")
  parser.add_option("--iterations",  dest="iterations", default=10,
                  help="Number of test iterations to run")
  parser.add_option("--sleep-interval",  dest="sleep_interval", default=10,
                  help="Number of milliseconds to sleep after a failed request")
  parser.add_option("--max-failed-requests",  dest="max_failed_requests", default=10,
                  help="Maximum number of requests that can fail before the test fails")
  GeoserverDBUtil.add_options(parser)

  (options, args) = parser.parse_args()

  debug=False
  if (options.debug != "false"):
    debug=True

  # initialize utils
  geoserver_db_util=GeoserverDBUtil(
        db_host=options.db_host, 
        db_port=options.db_port, 
        db_username=options.db_username, 
        db_password=options.db_password)

  test_runner = RESTConsistencyTest(
        geoserver_db_util=geoserver_db_util,
        geoserver_host=options.geoserver_host, 
        geoserver_port=options.geoserver_port, 
        geoserver_username=options.geoserver_username, 
        geoserver_password=options.geoserver_password, 
        db_host=options.geoserver_db_host, 
        db_port=options.geoserver_db_port, 
        db_username=options.db_username, 
        db_password=options.db_password,
        ws_name=options.ws_name,
        debug=debug,
        iterations=int(options.iterations),
        sleep_interval=int(options.sleep_interval),
        max_failed_requests=int(options.max_failed_requests))

  test_runner.run()

class RESTConsistencyTest:

  def __init__(self, 
            geoserver_db_util,
            geoserver_host="localhost", 
            geoserver_port="8080", 
            geoserver_username="admin", 
            geoserver_password="geoserver",
            db_host="localhost", 
            db_port="5432", 
            db_username="docker", 
            db_password="docker",
            ws_name="test_rest",
            debug=False,
            iterations=10,
            sleep_interval=10,
            max_failed_requests=10):
    self.geoserver_db_util=geoserver_db_util
    self.geoserver_host=geoserver_host
    self.geoserver_port=geoserver_port
    self.geoserver_host_url='http://'+geoserver_host+':'+str(geoserver_port)
    self.geoserver_username=geoserver_username
    self.geoserver_password=geoserver_password
    self.db_host=db_host
    self.db_username=db_username
    self.db_password=db_password
    self.db_port=db_port
    self.ws_name=ws_name
    self.debug=debug
    self.iterations=iterations
    self.sleep_interval=sleep_interval
    self.max_failed_requests=max_failed_requests

  def run(self):
    print "Initializing Database..."

    geoserver_db_util = self.geoserver_db_util
    # Create DB
    ws_name=self.ws_name
    db_name="test_rest_consistency"
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

    

    geoserver_db_util.create_table(conn, table_name, "Polygon")
    # Create simple dummy feature
    geoserver_db_util.insert_feature(conn, table_name, wkt="POLYGON ((-22.5 -22.5, 22.5 -22.5, 22.5 22.5, -22.5 22.5, -22.5 -22.5))")

    print "Database initialized"

    #clean up
    self.delete_workspace(ws_name)

    total_ft_failures=0
    total_ds_failures=0
    total_ws_failures=0

    total_ft_time=0
    total_ds_time=0
    total_ws_time=0

    min_ft_failures=sys.maxint
    min_ds_failures=sys.maxint
    min_ws_failures=sys.maxint

    min_ft_time=sys.float_info.max
    min_ds_time=sys.float_info.max
    min_ws_time=sys.float_info.max

    max_ft_failures=0
    max_ds_failures=0
    max_ws_failures=0

    max_ft_time=0
    max_ds_time=0
    max_ws_time=0

    for i in range(1, self.iterations+1):
      print ""
      print "Test run %d of %d" % (i, self.iterations)
      
      ws=0
      ws_start_time= time.time()
      consistent = False
      print "Testing workspace creation"
      while not consistent and ws < self.max_failed_requests:
        ws=ws+1
        result = self.create_workspace(ws_name)
        if (self.debug):
          print result
        consistent = (result == "201")
        if not consistent:
          time.sleep(float(self.sleep_interval)/1000)
      
      ws_time = time.time() - ws_start_time
      if not consistent:
        print "Error: Failed to create workspace after %d attempts" % ws
        return

      ds=0
      ds_start_time= time.time()
      consistent = False
      print "Testing datastore creation"
      while not consistent and ds < self.max_failed_requests:
        ds=ds+1
        result = self.create_datastore(ws_name, db_name, db_name)
        if (self.debug):
          print result
        consistent = (result == "201")
        if not consistent:
          time.sleep(float(self.sleep_interval)/1000)

      ds_time = time.time() - ds_start_time
      if not consistent:
        print "Error: Failed to create datastore after %d attempts" % ds
        return

      ft=0
      ft_start_time= time.time()
      consistent = False
      print "Testing featuretype creation"
      while not consistent and ft < self.max_failed_requests:
        ft=ft+1
        result = self.create_featuretype(ws_name, db_name, table_name)
        if (self.debug):
          print result
        consistent = (result == "201")
        if not consistent:
          time.sleep(float(self.sleep_interval)/1000)

      ft_time = time.time() - ft_start_time
      if not consistent:
        print "Error: Failed to create featuretype after %d attempts" % ft
        return

      print "Success: Test iteration %d complete" % i
      print "Created workspace in %0.4fs with %d failed attempts" %(ws_time, ws-1)
      print "Created datastore in %0.4fs with %d failed attempts" %(ds_time, ds-1)
      print "Created featuretype in %0.4fs with %d failed attempts" %(ft_time, ft-1)

      total_ft_failures=total_ft_failures+ft-1
      total_ds_failures=total_ds_failures+ds-1
      total_ws_failures=total_ws_failures+ws-1

      total_ft_time=total_ft_time+ft_time
      total_ds_time=total_ds_time+ds_time
      total_ws_time=total_ws_time+ws_time

      min_ft_failures=min(min_ft_failures,ft-1)
      min_ds_failures=min(min_ds_failures,ds-1)
      min_ws_failures=min(min_ws_failures,ws-1)

      min_ft_time=min(min_ft_time,ft_time)
      min_ds_time=min(min_ds_time,ds_time)
      min_ws_time=min(min_ws_time,ws_time)

      max_ft_failures=max(max_ft_failures,ft-1)
      max_ds_failures=max(max_ds_failures,ds-1)
      max_ws_failures=max(max_ws_failures,ws-1)

      max_ft_time=max(max_ft_time,ft_time)
      max_ds_time=max(max_ds_time,ds_time)
      max_ws_time=max(max_ws_time,ws_time)

      #clean up
      self.delete_workspace(ws_name)

    print "Test Complete"
    # print report
    if self.iterations > 1:
      print ""
      print "All %d test runs complete." % self.iterations
      print ""
      print "Results:"
      print "+------------------------------+------------------+------------------+------------------+"
      print "|                              | %-16s | %-16s | %-16s |" % ("Average", "Minimum", "Maximum")
      print "+------------------------------+------------------+------------------+------------------+"
      print "| Workspace creation time      | %-16s | %-16s | %-16s |" % ("%0.4f" % (total_ws_time/self.iterations),"%0.4f" % min_ws_time,"%0.4f" % max_ws_time)
      print "| Workspace creation failures  | %-16s | %-16s | %-16s |" % (str(total_ws_failures/self.iterations),str(min_ws_failures),str(max_ws_failures))
      print "| Datastore creation time      | %-16s | %-16s | %-16s |" % ("%0.4f" % (total_ds_time/self.iterations),"%0.4f" % min_ds_time,"%0.4f" % max_ds_time)
      print "| Datastore creation failures  | %-16s | %-16s | %-16s |" % (str(total_ds_failures/self.iterations),str(min_ds_failures),str(max_ds_failures))
      print "| Featuretype creation time    | %-16s | %-16s | %-16s |" % ("%0.4f" % (total_ft_time/self.iterations),"%0.4f" % min_ft_time,"%0.4f" % max_ft_time)
      print "| Feauretype creation failures | %-16s | %-16s | %-16s |" % (str(total_ft_failures/self.iterations),str(min_ft_failures),str(max_ft_failures))
      print "+------------------------------+------------------+------------------+------------------+"
      
    elif self.iterations == 1:
      print ""
      print "Only one test itertation, skipping summary report"

  def delete_workspace(self, workspace_name):
        wd_curl = 'curl -sS -o /dev/null -w "%{http_code}" -u '+self.geoserver_username+':'+self.geoserver_password+' -XDELETE "'+self.geoserver_host_url+'/geoserver/rest/workspaces/'+workspace_name+'?recurse=true"'
        if (self.debug):
          print wd_curl
        output = subprocess.check_output(['bash','-c', wd_curl])

        return output

  '''
  Variant of GeoserverUtil.create_workspace that does not use curl retries, and returns the HTTP response code
  '''
  def create_workspace(self, workspace_name):
        ws_curl = 'curl -sS -o /dev/null -w "%{http_code}" -u '+self.geoserver_username+':'+self.geoserver_password+' -XPOST -H "Content-type: text/xml" -d "<workspace><name>'+workspace_name
        ws_curl = ws_curl+'</name></workspace>" '+self.geoserver_host_url+'/geoserver/rest/workspaces'
        if (self.debug):
          print ws_curl
        output = subprocess.check_output(['bash','-c', ws_curl])

        return output

  '''
  Variant of GeoserverUtil.create_datastore that does not use curl retries, and returns the HTTP response code
  '''
  def create_datastore(self, workspace_name, database_name, datastore_name):
      st_curl = 'curl -sS -o /dev/null -w "%{http_code}" -u '+self.geoserver_username+':'+self.geoserver_password+' -XPOST -H "Content-type: text/xml" -d "<dataStore><name>'+datastore_name
      st_curl = st_curl+'</name><connectionParameters><host>'+self.db_host+'</host><port>'+str(self.db_port)+'</port><database>'+database_name+'</database><user>'+self.db_username+'</user><passwd>'+self.db_password+'</passwd>'
      st_curl = st_curl+'<dbtype>postgis</dbtype></connectionParameters></dataStore>" '+self.geoserver_host_url+'/geoserver/rest/workspaces/'+workspace_name+'/datastores'


      if (self.debug):
        print st_curl
      output = subprocess.check_output(['bash','-c', st_curl])

      return output

  '''
  Variant of GeoserverUtil.create_featuretype that does not use curl retries, and returns the HTTP response code
  '''
  def create_featuretype(self, workspace_name,datastore_name, featuretype_name):

      l_curl = 'curl -sS -o /dev/null -w "%{http_code}" -u  '+self.geoserver_username+':'+self.geoserver_password+' -H "Content-type: text/xml" -d "'
      l_curl = l_curl + '<featureType><name>'+featuretype_name+'</name></featureType>'
      l_curl = l_curl + '" '+self.geoserver_host_url+'/geoserver/rest/workspaces/'+workspace_name+'/datastores/'+datastore_name+'/featuretypes'
      if (self.debug):
        print l_curl
      output = subprocess.check_output(['bash','-c', l_curl])

      return output


  
if __name__ == "__main__":
  main()