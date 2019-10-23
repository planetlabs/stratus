#!/usr/bin/python
import psycopg2
from psycopg2.extensions import AsIs, ISOLATION_LEVEL_AUTOCOMMIT
import sys, math, time

#fractal.py [iterations]
#Creates a single table containing a number of iterations of the sierpinski carpet fractal.
#This can be used to generate large datasets for load-testing purposes.
#Each successive iteration of the fractal is 9 times the size of the previous iterations.
#WARNING: 9 iterations takes a few hours to run and is about 12 GB in PostGIS.
def main():
  #default to 7 iterations of the fractal. Allow different values from args.
  iterations = 7
  
  if len(sys.argv) > 1:
    if "help" == str(sys.argv[1]):
      print "\nUsage: \n    python fractal_postgis.py [i]\n"
      print "Creates an i-level sierpinski carpet in a new postgis database named sierpinski. Default is i=7."
      print "Each succesive level is larger by a factor of 9.\n"
      quit()
      
    iterations = int(sys.argv[1])
    
  width = 180
  table = "sierpinski_carpet"
  
  print "Creating sierpinski carpet with " + str(iterations) + " levels"
  
  #create database
  conn_string = "host='localhost' dbname='postgres' user='docker' password='docker'"
  conn = psycopg2.connect(conn_string)
  conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
  cursor = conn.cursor()
  try:
    cursor.execute("DROP DATABASE sierpinski ;")
  except Exception, e:
    print 'Could not "DROP DATABASE sierpnski ;" (ignore this message if the database doesn\'t exist):'
    print e
  cursor.execute("CREATE DATABASE sierpinski ;")
  cursor.close()
  
  #Define our connection string
  conn_string = "host='localhost' dbname='sierpinski' user='docker' password='docker'"
  conn = psycopg2.connect(conn_string)
  cursor = conn.cursor()
  
  try:
    cursor.execute("CREATE EXTENSION postgis ;")
    conn.commit()
  except Exception, e:
    print "Failed to 'CREATE EXTENSION postgis'"
    conn.rollback()
    quit()
  
  #Fill one table with the full fractal
  try:
    #create db
    cursor.execute("DROP TABLE %s", (AsIs(table),))
    conn.commit()
  except Exception, e:
    conn.rollback()
  try:
    #create db
    cursor.execute("CREATE TABLE %s ( fid serial NOT NULL, geom geometry(Polygon,4326), iteration integer, description varchar, CONSTRAINT %s PRIMARY KEY (fid) )", (AsIs(table), AsIs(table+"_pkey")))
    conn.commit()
    cursor.close()
    t0 = time.time();
    tnet = 0;
    pnet = 0
    
    for i in range(1, iterations+1):
      sys.stdout.write(time.strftime("%Y-%m-%d %H:%M:%S") + " - Populating level "+str(i)+" of sierpinski carpet ... ")
      sys.stdout.flush() 
      sierpinski_carpet(i, width, conn, table)
      conn.commit()
      td = time.time() - t0;
      pd = math.pow(9,i-1)
      tnet = tnet + td
      pnet = pnet + pd
      
      sys.stdout.write("Created %.4g polygons in %.4gs\n" % (float(pd), float(td)))
      sys.stdout.flush()
      t0 = time.time();

  finally:
    conn.close()

  sys.stdout.write("Complete. Created "+str(iterations)+" levels of carpet in %.4gs\n" % float(tnet))

def sierpinski_carpet(iteration, width, conn, table):
  cursor = conn.cursor()

  rows = int(math.pow(3, iteration-1))
  gap = width/math.pow(3, iteration)

  for j in range(1, rows+1):
    for k in range(1, rows+1):
      l=(gap*(3.0*j-2.0))-(width/2.0)
      r=l+gap
      t=(gap*(3.0*k-2.0))-(width/2.0)
      b=t+gap

      geom = "ST_GeomFromText('POLYGON(("+str(l)+" "+str(t)+","+str(r)+" "+str(t)+","+str(r)+" "+str(b)+","+str(l)+" "+str(b)+","+str(l)+" "+str(t)+"))', 4326)"
      description = "("+str(j)+","+str(k)+")"


      cursor.execute("INSERT INTO %s (geom, iteration, description) VALUES (%s, %s,%s)", (AsIs(table), AsIs(geom), iteration, description))
  cursor.close()
 
if __name__ == "__main__":
  main()
