#!/bin/sh

NUM_NODES="$(($1-1))"
REPLICAS=$2
NAMESPACE=$3
NODES=""

for i in $(seq 0 $NUM_NODES); do
  redis-cli -h redis-$i.redis.$NAMESPACE -p 6379 flushall
  redis-cli -h redis-$i.redis.$NAMESPACE -p 6379 cluster reset soft
  IP=$(getent hosts redis-$i.redis.$NAMESPACE | awk '{ print $1 }')
  NODES="$NODES $IP:6379"
done;

