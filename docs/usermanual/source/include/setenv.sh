#! /bin/sh
export JAVA_OPTS="$JAVA_OPTS -Xms=256m -Xmx=756m"
export JAVA_OPTS="$JAVA_OPTS -XX:SoftRefLRUPolicyMSPerMB=36000"
export JAVA_OPTS="$JAVA_OPTS -XX:-UsePerfData"

# oracle compatibility
export JAVA_OPTS="$JAVA_OPTS -Duser.timezone=GMT"

# geoserver settings
export JAVA_OPTS="$JAVA_OPTS -Dorg.geotools.referencing.forceXY=true"
export JAVA_OPTS="$JAVA_OPTS -Dorg.geotoools.render.lite.scale.unitCompensation=true" 
