#!/bin/sh


if [ -z "$2" ]; then
  echo "Usage: test_upgrade.sh OLD_VER NEW_VER";
else
  OLD_VER=$1;
  NEW_VER=$2;

  #load env vars
  source env.sh

  #init redis
  $DEPLOY_DIR/redis.sh

  #init postgis and load data
  $DEPLOY_DIR/postgis.sh

  echo "Loading data into postgis..."
  sleep 10
  cd ../
  ./na_roads_postgis.sh
  cd upgrade

  #load redis ip
  source env.sh


  #Start Stratus
  ./stratus.sh $OLD_VER "stratus-old.log"

  #Load Catalog
  ./load_catalog.sh

  #Upgrade
  ./stratus.sh $NEW_VER "stratus-new.log"

  #Teardown
  #$DEPLOY_DIR/kill-env.sh
fi
