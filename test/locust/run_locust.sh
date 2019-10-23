#!/bin/bash


while [[ $# -gt 0 ]]
do
    arg="$1"
    key=`echo $arg | cut -f1 -d\=`
    val=`echo $arg | cut -f2 -d\=`
    case $key in
        --url)
        URL=$val
        ;;
        --layers)
        LAYERS=$val
        ;;
        --bbox-file)
        BBOX_FILE=$val
        ;;
    esac
    shift # past argument or value
done

if [ -z "$URL" ]; then
    echo "please specify a URL"
    exit 1
fi
echo "URL='$URL'"

echo "LAYERS='$LAYERS'"
echo "BBOX_FILE='$BBOX_FILE'"

if [ -z "$LAYERS" ]; then
    echo "please specify LAYERS (e.g., osm:osm)"
    exit 1
fi

# if it's already on path, guess we should use that
if [ -n "$(command -v locust)" ]; then
    LOCUST=locust

# otherwise look for ./locust_env
elif [ -d locust_env ] && [ -x "locust_env/bin/locust" ]; then
    LOCUST=locust_env/bin/locust

else
    echo "Can't find a locust to use"
    exit 1
fi

# build data file if it is not present
if [ ! -f "./data/wms_256_tiles.csv" ]; then
    ./code/mercantile_gen.py
fi

$LOCUST --version

LOGLEVEL=INFO
echo "Starting locust: $LOCUST (log level $LOGLEVEL)"
export LOCUST_LAYERS=$layers
export URL=$URL
export BBOX_FILE=$BBOX_FILE

env $LOCUST -f ./code/wms_tester.py --loglevel="$LOGLEVEL" --host="$URL"
