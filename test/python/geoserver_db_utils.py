#!/usr/bin/python 
import psycopg2
from psycopg2.extensions import AsIs, ISOLATION_LEVEL_AUTOCOMMIT
from optparse import OptionParser

"""
Utility class for populating and querying a spatially-enabled PostgreSQL Database
"""
class GeoserverDBUtil:

    '''
    Add options required by GeoserverDBUtil to the provided OptionParser
    '''
    @staticmethod
    def add_options(parser):
        parser.add_option("--db-host",  dest="db_host", default="localhost",
                      help="database host ")
        parser.add_option("--db-username",  dest="db_username", default="docker",
                      help="database username")
        parser.add_option("--db-password",  dest="db_password", default="docker",
                      help="db_password")
        parser.add_option("--db-port",  dest="db_port", default=5432,
                      help="postgis port")

    def __init__(self, 
            db_host="localhost", 
            db_port="5432", 
            db_username="docker", 
            db_password="docker"):
        self.db_host=db_host
        self.db_username=db_username
        self.db_password=db_password
        self.db_port=db_port


    def get_database_connection(self, db_name):
      conn_string = "host='%s' port='%s' dbname='%s' user='%s' password='%s'" % (self.db_host, self.db_port, db_name, self.db_username, self.db_password)
      print conn_string
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

    def create_table(self, conn, table_name, geometry_type="Point", srs=4326):
      try:
        cursor = conn.cursor()
        cursor.execute("DROP TABLE IF EXISTS %s", (AsIs(table_name),))    
        cursor.execute("CREATE TABLE %s ( fid serial NOT NULL, geom geometry(%s,%s), description varchar, CONSTRAINT %s PRIMARY KEY (fid) )", (AsIs(table_name), AsIs(geometry_type), AsIs(srs), AsIs(table_name+"_pkey")))
        conn.commit()
      except Exception, e:
        print e
        conn.rollback()

    def insert_feature(self, conn, table_name, wkt, srs=4326, description="desc"):
      try:
        cursor = conn.cursor()
        geom = "ST_GeomFromText('%s',%d)" % (wkt, srs)
        cursor.execute("INSERT INTO %s (geom, description) VALUES (%s,'%s')", (AsIs(table_name), AsIs(geom), AsIs(description)))
        conn.commit()
      except Exception, e:
        print e
        conn.rollback()
