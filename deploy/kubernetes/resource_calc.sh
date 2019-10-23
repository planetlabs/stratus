#!/bin/bash

if [ "$#" -ne "2" ]
then
	echo "Usage: $0 <nodes> <node cpu limit>"
	exit 1
fi

nodes=$1
totcpu=$2

cpu=`echo "0.4375 * $totcpu" | bc -l`
maxcpu=`echo "0.875 * $totcpu" | bc -l`
mem="2.0G"
maxmem=`echo "$totcpu * 1.2" | bc -l`
rediscpu=`echo "$nodes * $totcpu * 25" | bc -l`

export CPU=$cpu
export MAX_CPU=$maxcpu
export MEM=\"2.0G\"
export MAX_MEM=\"${maxmem}G\"
export REDIS_CPU=\"${rediscpu}m\"

echo "
stratus.ogc.resources.cpu: $cpu
stratus.ogc.resources.maxCpu: $maxcpu
stratus.ogc.resources.mem: \"2.0G\"
stratus.ogc.resources.maxMem: \"${maxmem}G\"
redis.resources.maxCpu: \"${rediscpu}m\"
"
