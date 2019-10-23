import sys, math, time, random, subprocess
from optparse import OptionParser
from geoserver_name_utils import GeoserverNameUtil
from geoserver_utils import GeoserverUtil
import psycopg2
from psycopg2.extensions import AsIs, ISOLATION_LEVEL_AUTOCOMMIT
from time import sleep

class GeoserverDoerFacade:

  def __init__(self, geoserver_util, geoserver_name_util):
      self.geoserver_util=geoserver_util
      self.geoserver_name_util=geoserver_name_util

  def prep_random_name_padding(self,workspaces,layers):
    workspace_name_pad=str(len(workspaces))
    featuretype_name_pad=str(len(layers))
    coverage_store_name_pad=str(len(workspaces))

  def get_database_connection(self, db_name):
    conn_string = "host='%s' dbname='%s' user='%s' password='%s'" % (self.geoserver_util.db_host, db_name, self.geoserver_util.db_username, self.geoserver_util.db_password)
    conn = psycopg2.connect(conn_string)
    return conn;

  def create_database(self, conn, db_name):
    cursor=conn.cursor()
    try:
      cursor.execute("SELECT pg_terminate_backend(pg_stat_activity.pid) "+\
          " FROM pg_stat_activity"+\
          " WHERE pg_stat_activity.datname = '%s'"+\
          "  AND pid <> pg_backend_pid();",(AsIs(db_name),))
      cursor.execute("DROP DATABASE IF EXISTS %s ;", (AsIs(db_name),))
    except Exception, e:
      print e
      conn.rollback()
    try:
      cursor.execute("CREATE DATABASE %s ;", (AsIs(db_name),))
      conn.commit()
    except Exception, e:
      print e
      conn.rollback()
      cursor.close()
      quit()

  def enable_postgis_database(self, conn, db_name):
    try:
      cursor = conn.cursor()
      cursor.execute("CREATE EXTENSION if not exists postgis ;")
      conn.commit()
    except Exception, e:
      print "Failed to 'CREATE EXTENSION postgis'"
      print e
      conn.rollback()
      quit()

  def create_table(self, conn, table_name):
    try:
      cursor = conn.cursor()
      cursor.execute("DROP TABLE IF EXISTS %s", (AsIs(table_name),))    
      cursor.execute("CREATE TABLE %s ( fid serial NOT NULL, geom geometry(Point,4326), iteration integer, description varchar, CONSTRAINT %s PRIMARY KEY (fid) )", (AsIs(table_name), AsIs(table_name+"_pkey")))
      conn.commit()
    except Exception, e:
      print e
      conn.rollback()


  def create_gen_workspaces(self, num_workspaces):
      for i in range(1, num_workspaces):
          workspace_name=self.get_workspace_name(i,num_workspaces)
          self.create_workspace(num_workspaces)

  def populate_table_with_random_points(self, conn, table_name, num_points):
    try:
      cursor = conn.cursor()
      for k in range(1, num_points):
        kstring = str(k)
        kstring = kstring.zfill(len(str(num_points)))
        x = random.uniform(-180, 180)
        y = random.uniform(-90,90)
        geom = "ST_SetSRID(ST_MakePoint("+str(x)+","+str(y)+"), 4326)"
        description = "pt "+kstring
        cursor.execute("INSERT INTO %s (geom, iteration, description) VALUES (%s,%s,%s)", (AsIs(table_name), AsIs(geom), kstring, description))
      conn.commit()
    except Exception, e:
      print e
      conn.rollback()

  def addlayers_pg(self, skip, workspaces, layers, points): 
    print "-------------------------------------------------"
    print "Creating "+str(workspaces)+" databases, each with "+str(layers)+" tables, each with "+str(points)+" rows"
    print "-------------------------------------------------"
    
    #create databases

    for i in range(skip, workspaces+1):
      db_name=self.geoserver_name_util.get_gen_database_name(i)
      print "Creating database "+db_name+" of "+str(workspaces)+"."
      conn = self.get_database_connection("postgres")
      conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
      self.create_database(conn,db_name)
      conn.commit()
      conn.cursor().close()
      conn.close()

      conn=self.get_database_connection(db_name)
      self.enable_postgis_database(conn,db_name)
      for j in range(1, layers+1):
        table_name = self.geoserver_name_util.get_gen_table_name(j)
        print "Creating table "+table_name+" of "+str(layers)+"."
        self.create_table(conn, table_name)
        self.populate_table_with_random_points(conn, table_name, points)
      conn.commit()
      conn.cursor().close()
      conn.close()

    print "-------------------------------------------------"
    print "Database setup complete."
    print "-------------------------------------------------"

  def addlayers_gs(self, skip_ws, skip_ds, skip_ft, geoserver_util, workspaces, layers, points):
    print "-------------------------------------------------"
    print "Adding data to geoserver."
    print "-------------------------------------------------"
    for i in range(skip_ws, workspaces+1):
    # for i in range(workspaces+1, skip, -1):

      print "-------------------------------------------------"
      print "Creating geoserver: Workspace "+str(i)
      print "-------------------------------------------------"
      workspace_name=self.geoserver_name_util.get_gen_workspace_name(i)
      #self.geoserver_util.delete_workspace(workspace_name)
      #sleep(0.50)
      self.geoserver_util.create_workspace(workspace_name)

    for i in range(skip_ds, workspaces+1):
      workspace_name=self.geoserver_name_util.get_gen_workspace_name(i)
      datastore_name=self.geoserver_name_util.get_gen_datastore_name(i)
      db_name=self.geoserver_name_util.get_gen_database_name(1)
      print "-------------------------------------------------"
      print "Creating geoserver datastore: Workspace "+str(i)
      print "-------------------------------------------------"
      self.geoserver_util.create_datastore(workspace_name, db_name, datastore_name)
      
    for i in range(skip_ft, workspaces+1):
      workspace_name=self.geoserver_name_util.get_gen_workspace_name(i)
      datastore_name=self.geoserver_name_util.get_gen_datastore_name(i)
      print "-------------------------------------------------"
      print "Creating geoserver featuretype Workspace "+str(i)
      print "-------------------------------------------------"
      for j in range(1, layers+1):
        #sleep(0.50)
        table_name = self.geoserver_name_util.get_gen_table_name(1)
        self.geoserver_util.create_featuretype(workspace_name, datastore_name, table_name)
    print "Complete!"

  def deletelayers_gs(self,geoserver_util, workspaces):
    for i in range(1, workspaces+1):
      workspace_name=self.geoserver_name_util.get_gen_workspace_name(i)
      self.geoserver_util.delete_workspace(workspace_name)
