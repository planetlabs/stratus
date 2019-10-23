#!/usr/bin/python
import psycopg2
from psycopg2.extensions import AsIs, ISOLATION_LEVEL_AUTOCOMMIT
import sys, math, time, random, subprocess
from optparse import OptionParser
from geoserver_name_utils import GeoserverNameUtil
from geoserver_utils import GeoserverUtil
from geoserver_doer_facade import GeoserverDoerFacade

'''
generate_points.py

Loads a number of workspaces into GeoServer, each containing some number of vector layers, where each layer contains a random distribution of points

Used for testing the performance effects of different numbers of workspaces, layers, and features on the GeoServer catalog and services.

For usage, run generate_points.py --help
'''

def main():

  parser = OptionParser()
  parser.add_option("-d", "--load-pg", dest="load_pg", action="store_true", default=False, 
                  help="write postgis database")
  parser.add_option("-g", "--load-gs",  dest="load_gs", action="store_true", default=False, 
                  help="write geoserver layers")
  parser.add_option("-w", "--workspaces",  dest="workspaces", default=1,
                  help="number of workspaces to generate")
  parser.add_option("-l", "--layers",  dest="layers", default=10,
                  help="number of layers per workspace to generate")
  parser.add_option("-p", "--points",  dest="points", default=100,
                  help="number of points per layer to generate")

  GeoserverUtil.add_options(parser)


  parser.add_option("--db-prefix",  dest="db_prefix", default="db",
                  help="database prefix")
  parser.add_option("--workspace-prefix",  dest="workspace_prefix", default="ws",
                  help="workspace prefix")
  parser.add_option("--datastore-prefix",  dest="datastore_prefix", default="ds",
                  help="datastore prefix")
  parser.add_option("--table-prefix",  dest="table_prefix", default="ft",
                  help="table prefix")

  parser.add_option("--db-format",  dest="db_format", default="0000",
                  help="database numbering format (e.g., 0000 refers to 4 digits for each number)")
  parser.add_option("--datastore-format",  dest="datastore_format", default="0000",
                  help="datastore numbering format (e.g., 0000 refers to 4 digits for each number)")
  parser.add_option("--workspace-format",  dest="workspace_format", default="0000",
                  help="workspace numbering format (e.g., 0000 refers to 4 digits for each number)")
  parser.add_option("--featuretype-format",  dest="featuretype_format", default="0000",
                  help="featuretype numbering format (e.g., 0000 refers to 4 digits for each number)")

  

  parser.add_option("--skip-ws",  dest="skip_ws", default="1",
                  help="don't create the first few workspaces")
  parser.add_option("--skip-ds",  dest="skip_ds", default="1",
                  help="don't create the first few datastores")
  parser.add_option("--skip-ft",  dest="skip_ft", default="1",
                  help="don't create the first few workspaces")

  # parser.add_option("--delete-first",  dest="delete_first", action="store_true", default=False,
  #                 help="Delete workspaces, etc, first?")

  (options, args) = parser.parse_args()

  workspaces=int(options.workspaces)
  layers=int(options.layers)
  points=int(options.points)
  geoserver_host=options.geoserver_host
  geoserver_port=options.geoserver_port
  geoserver_username=options.geoserver_username
  geoserver_password=options.geoserver_password
  db_host=options.db_host
  db_port=options.db_port
  db_username=options.db_username
  db_password=options.db_password
  load_pg=options.load_pg
  load_gs=options.load_gs

  featuretype_format=options.featuretype_format
  workspace_format=options.workspace_format
  datastore_format=options.datastore_format
  db_format=options.db_format

  db_prefix=options.db_prefix
  workspace_prefix=options.workspace_prefix
  table_prefix=options.table_prefix
  datastore_prefix=options.datastore_prefix

  curl_retries=options.curl_retries
  curl_max_time=options.curl_max_time

  if (options.skip_ws):
    skip_ws=int(options.skip_ws)
  else:
    skip_ws=1
  if (options.skip_ds):
    skip_ds=int(options.skip_ds)
  else:
    skip_ds=1
  if (options.skip_ft):
    skip_ft=int(options.skip_ft)
  else:
    skip_ft=1

  geoserver_util=GeoserverUtil(
        geoserver_host=geoserver_host, 
        geoserver_port=geoserver_port, 
        geoserver_username=geoserver_username, 
        geoserver_password=geoserver_password, 
        db_host=db_host, 
        db_port=db_port, 
        db_username=db_username, 
        db_password=db_password,
        curl_retries=curl_retries,
        curl_max_time=curl_max_time)

  geoserver_name_util=GeoserverNameUtil(
        workspace_prefix=workspace_prefix, 
        table_prefix=table_prefix, 
        database_prefix=db_prefix, 
        datastore_prefix=datastore_prefix, 
        database_name_pad=len(db_format),
        table_name_pad=len(featuretype_format),
        workspace_name_pad=len(workspace_format),
        datastore_name_pad=len(datastore_format))


  doer_facade=GeoserverDoerFacade(geoserver_util, geoserver_name_util)

  print "Connection parameters: \n  user: "+db_username+"\n  password: "+db_password+"\n  host: "+db_host+"\n"
  
  if load_pg:
    print "Adding Postgis layers: \n  workspaces="+str(workspaces)+"\n  layers="+str(layers)+"\n  points="+str(points)+"\n"
    doer_facade.addlayers_pg(skip_ds, workspaces, layers, points)

  if load_gs:
    print "Adding Geoserver layers\n  workspaces="+str(workspaces)+"\n  layers="+str(layers)+"\n  points="+str(points)+"\n"

    doer_facade.addlayers_gs(skip_ws, skip_ds, skip_ft, geoserver_util, workspaces, layers, points)

 
if __name__ == "__main__":
  main()
