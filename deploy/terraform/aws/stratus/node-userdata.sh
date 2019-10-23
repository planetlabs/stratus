#!/bin/bash
if [ ! -z `rpm -qa \*yum | grep amzn1` ];then
  PLATFORM=amazon
  PLATFORM_VERSION=1
fi

if [ ! -z `rpm -qa \*yum | grep amzn2` ];then
  PLATFORM=amazon
  PLATFORM_VERSION=2
fi

if [ ! -z `rpm -qa \*yum | grep el7.centos` ];then
  PLATFORM=centos
  PLATFORM_VERSION=7
fi

AWSCLI='command -v aws'
if [ "$PLATFORM" == amazon ];then
  if [ "$PLATFORM_VERSION" -eq 1 ];then
      for pkg in python36 python36-pip
      do
        if [ -z `rpm -qa $pkg` ];then
          yum -y install $pkg
        fi
      done
  elif [ "$PLATFORM_VERSION" -eq 2 ];then
    if [ -z `rpm -qa python3` ];then
      amazon-linux-extras install python3
    fi

    if [ -z `rpm -qa python3-pip` ];then
      yum -y install python3-pip
    fi
  fi
  PIP='command -v pip-3'
  PYTHON=python3
else
  PIP='command -v pip'
  PYTHON=python
fi

if ! $PIP > /dev/null;then
  if [ "$PLATFORM" == amazon ];then
    if [ "$PLATFORM_VERSION" -eq 1 ];then
      yum -y install python27-pip
    elif [ "$PLATFORM_VERSION" -eq 2 ];then
      yum -y install python2-pip
    fi
  else
    /usr/bin/curl -o get-pip.py https://bootstrap.pypa.io/get-pip.py
    $PYTHON get-pip.py
    rm -f get-pip.py
  fi
  PIP='command -v pip'
fi

if ! $AWSCLI > /dev/null;then
  `$PIP` install awscli yaml
fi

PIP_LIST="$($PIP) list --format legacy"
PYAML_INSTALLED=$($PIP_LIST | grep pyaml)
BOTO3_INSTALLED=$($PIP_LIST | grep boto3)

if [ -z "$PYAML_INSTALLED" ];then
`$PIP` install pyaml
fi

if [ -z "$BOTO3_INSTALLED" ];then
  `$PIP` install boto3
fi

if [ -f /usr/bin/aws ];then
  AWSCLI=/usr/bin/aws
elif [ -f /usr/local/bin/aws ];then
  AWSCLI=/usr/local/bin/aws
else
  AWSCLI=/bin/aws
fi

if [ -z `rpm -qa jq` ];then
  yum -y install jq
fi

export AWS_DEFAULT_REGION=`curl -s http://169.254.169.254/latest/dynamic/instance-identity/document | jq .region -r`
INSTANCE_ID=`curl -s http://169.254.169.254/latest/meta-data/instance-id`

HOST_NAME_SET=`/bin/hostname | grep "${node_name_prefix}"`
if [ -z "$HOST_NAME_SET" ];then
  NODE_NAME=`$PYTHON -c "import uuid;name='${node_name_prefix}-{s}'.format(s=str(uuid.uuid4())[:4]);print(name.upper())"`
  /bin/hostname $NODE_NAME
  sed -i "s/HOSTNAME.*/HOSTNAME=$NODE_NAME/" /etc/sysconfig/network

  if ([ "$PLATFORM" == amazon ] && [ "$PLATFORM_VERSION" -eq 2 ]) || ([ "$PLATFORM" == centos ] && [ "$PLATFORM_VERSION" -eq 7 ]);then
    hostnamectl set-hostname $NODE_NAME --static && systemctl restart systemd-hostnamed
  fi

  aws ec2 create-tags --resources $INSTANCE_ID --tags Key=Name,Value=$NODE_NAME
fi

echo ${deploy_id} > /opt/deploy_id
export GEOSERVER_BASE_DIR=/opt/stratus

if [ ! -d "$GEOSERVER_BASE_DIR/lib" ];then
  /bin/mkdir -p $GEOSERVER_BASE_DIR/lib
fi

if [ ! -f "$GEOSERVER_BASE_DIR/stratus.jar" ];then
  $AWSCLI s3 cp s3://${artifacts_bucket_name}/${jar_bucket_path} $GEOSERVER_BASE_DIR/stratus.jar || $AWSCLI s3 cp s3://${failover_artifacts_bucket_name}/${jar_bucket_path} $GEOSERVER_BASE_DIR/stratus.jar
fi

if [ ! -f "$GEOSERVER_BASE_DIR/lib/marlin.jar" ];then
  $AWSCLI s3 cp s3://${artifacts_bucket_name}/marlin/marlin-0.8.2-Unsafe.jar $GEOSERVER_BASE_DIR/lib/marlin.jar || $AWSCLI s3 cp s3://${failover_artifacts_bucket_name}/marlin/marlin-0.8.2-Unsafe.jar $GEOSERVER_BASE_DIR/lib/marlin.jar
fi

if [ ! -f "$GEOSERVER_BASE_DIR/lib/marlin-sun-java2d.jar" ];then
  $AWSCLI s3 cp s3://${artifacts_bucket_name}/marlin/marlin-0.8.2-Unsafe-sun-java2d.jar $GEOSERVER_BASE_DIR/lib/marlin-sun-java2d.jar || $AWSCLI s3 cp s3://${failover_artifacts_bucket_name}/marlin/marlin-0.8.2-Unsafe-sun-java2d.jar $GEOSERVER_BASE_DIR/lib/marlin-sun-java2d.jar
fi

MSTTCOREFONTS_INSTALLED=`rpm -qa msttcorefonts`
if [ -z "$MSTTCOREFONTS_INSTALLED" ];then
  $AWSCLI s3 cp s3://${artifacts_bucket_name}/msttcorefonts-2.5-1.noarch.rpm /tmp/msttcorefonts-2.5-1.noarch.rpm || $AWSCLI s3 cp s3://${failover_artifacts_bucket_name}/msttcorefonts-2.5-1.noarch.rpm /tmp/msttcorefonts-2.5-1.noarch.rpm
  yum -y install /tmp/msttcorefonts-2.5-1.noarch.rpm
fi

if [ "$PLATFORM" == amazon ];then
  if [ "$PLATFORM_VERSION" -eq 1 ];then
    PGDG_REPO=`rpm -qa pgdg-ami201503-96`
    PGDG_RPM=https://yum.postgresql.org/9.6/redhat/rhel-6-x86_64/pgdg-ami201503-96-9.6-2.noarch.rpm
    
  elif [ "$PLATFORM_VERSION" -eq 2 ];then
    PGDG_REPO=`rpm -qa pgdg-ami201503-96`
    PGDG_RPM=https://yum.postgresql.org/9.6/redhat/rhel-6-x86_64/pgdg-ami201503-96-9.6-2.noarch.rpm
  fi
else
  PGDG_REPO=`rpm -qa pgdg-centos96`
  PGDG_RPM=https://yum.postgresql.org/9.6/redhat/rhel-7-x86_64/pgdg-centos96-9.6-3.noarch.rpm
fi

if [ -z "$PGDG_REPO" ];then
  yum -y install $PGDG_RPM
fi

if [ "$PLATFORM" == amazon ] && [ "$PLATFORM_VERSION" -eq 2 ];then
  sed -i 's/rhel-6/rhel-7/g' /etc/yum.repos.d/pgdg-96-ami201503.repo
  yum clean all > /dev/null
fi

if [ "$PLATFORM" == amazon ] && [ "$PLATFORM_VERSION" -eq 1 ];then
  yum-config-manager --enable epel
elif [ -z `rpm -qa epel-release` ];then
  yum -y install epel-release
else 
  yum -y install https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm
fi

for pkg in  gdal dejavu-sans-mono-fonts dejavu-sans-fonts dejavu-serif-fonts java-1.8.0-openjdk-devel postgresql96
do
  if [ -z `rpm -qa $pkg` ];then
    yum -y install $pkg
  fi
done

if [ -f /usr/lib/jvm/jre-1.8.0-openjdk.x86_64/bin/java ];then
  alternatives --set java /usr/lib/jvm/jre-1.8.0-openjdk.x86_64/bin/java
fi

for f in elasticache_info.json efs_info.json rds_info.json
do
  if [ ! -f "$GEOSERVER_BASE_DIR/$f" ];then
    ls_file=`$AWSCLI s3 ls s3://${logging_bucket_name}/$f`
    if [ ! -z "$ls_file" ];then
      $AWSCLI s3 cp s3://${logging_bucket_name}/$f $GEOSERVER_BASE_DIR/$f
    fi
  fi
done

NODE_TYPE=${node_type}
GWC_DIR=$GEOSERVER_BASE_DIR/gwc
ELASTICACHE_DNS_NAME=`$PYTHON -c "import json;import os;data=open('{dir}/elasticache_info.json'.format(dir=os.environ['GEOSERVER_BASE_DIR'])).read();print(json.loads(data)['primary_endpoint'])"`

if [ "$NODE_TYPE" == 'reader' ];then
  export ELASTICACHE_ID=`$PYTHON -c "import json;import os;data=open('{dir}/elasticache_info.json'.format(dir=os.environ['GEOSERVER_BASE_DIR'])).read();obj=json.loads(data);print(obj['id']) if 'id' in obj else ''"`
  if [ ! -z "$ELASTICACHE_ID" ];then
    ELASTICACHE_DNS_NAME=`$PYTHON -c "import os;import boto3;import random;elasticache=boto3.client('elasticache');resp=elasticache.describe_replication_groups(ReplicationGroupId='{id}'.format(id=os.environ['ELASTICACHE_ID']));
nodes=resp['ReplicationGroups'][0]['NodeGroups'][0]['NodeGroupMembers'];read_endpoints=[x['ReadEndpoint']['Address'] for x in nodes];print(random.choice(read_endpoints))"`
  fi
fi

efs_dns_name=''
if [ -f "$GEOSERVER_BASE_DIR/efs_info.json" ];then
  efs_dns_name=`$PYTHON -c "import json;import os;data=open('{dir}/efs_info.json'.format(dir=os.environ['GEOSERVER_BASE_DIR'])).read();print(json.loads(data)['dns_name'])"`
fi

if [ ! -z "$efs_dns_name" ];then
  EFS_BASE_DIR=/efs/stratus
  if [ ! -d "$EFS_BASE_DIR" ];then
    /bin/mkdir -p $EFS_BASE_DIR
  fi

  if [ -z "`grep $efs_dns_name /etc/fstab`" ];then
    echo "$efs_dns_name:/ $EFS_BASE_DIR nfs4 nfsvers=4.1,rsize=1048576,wsize=1048576,hard,timeo=600,retrans=2 0 2" >> /etc/fstab
    mount -a
  fi

  GWC_DIR=$EFS_BASE_DIR/gwc
fi

if [ -f "$GEOSERVER_BASE_DIR/rds_info.json" ];then
  db_type=`jq .type -r $GEOSERVER_BASE_DIR/rds_info.json`
  db_username=`jq .username -r $GEOSERVER_BASE_DIR/rds_info.json`
  db_password=`jq .password -r $GEOSERVER_BASE_DIR/rds_info.json`
  db_name=`jq .db_name -r $GEOSERVER_BASE_DIR/rds_info.json`
  
  if [[ "$db_type" = rds ]];then
    db_host=`jq .endpoint -r $GEOSERVER_BASE_DIR/rds_info.json`
  else
    db_host=`jq .write_endpoint -r $GEOSERVER_BASE_DIR/rds_info.json`
  fi

  db_exist=`PGPASSWORD="$db_password" psql -h "$db_host" -U "$db_username" -d postgres -c "SELECT datname FROM pg_database WHERE datname='$db_name'"`
  if [ -z `echo "$db_exist" | grep "$db_name"` ];then
    PGPASSWORD="$db_password" psql -h "$db_host" -U "$db_username" -d postgres -c "create database $db_name"
  fi

  postgis_exist=`PGPASSWORD="$db_password" psql -h "$db_host" -U "$db_username" -d "$db_name" -c "select name from pg_available_extensions where installed_version is not null and name = 'postgis'"`
  if [ -z `echo "$postgis_exist" | grep postgis` ];then
    PGPASSWORD="$db_password" psql -h "$db_host" -U "$db_username" -d "$db_name" -c "create extension postgis"
  fi
fi

if [ ! -d "$GWC_DIR" ];then
  /bin/mkdir -p $GWC_DIR
fi

EXTERNAL_SCRIPT=${external_script}
if [ ! -z $EXTERNAL_SCRIPT ];then
  if [[ $EXTERNAL_SCRIPT = 'http'* ]];then
    curl -O -L $EXTERNAL_SCRIPT
  fi

  if [[ $EXTERNAL_SCRIPT = 's3://'* ]];then
    aws s3 cp $EXTERNAL_SCRIPT .
  fi

  fname=$(basename "$EXTERNAL_SCRIPT")
  if [[ $fname = *py ]];then
    $PYTHON $fname
  else
    bash $fname
  fi
fi

base_java_opts="-Xbootclasspath/a:$GEOSERVER_BASE_DIR/lib/marlin.jar -Xbootclasspath/p:$GEOSERVER_BASE_DIR/lib/marlin-sun-java2d.jar \
-Dsun.java2d.renderer=org.marlin.pisces.MarlinRenderingEngine -Dloader.path=$GEOSERVER_BASE_DIR/lib -DGEOWEBCACHE_CACHE_DIR=$GWC_DIR \
-Dstratus.catalog.redis.manual.host=$ELASTICACHE_DNS_NAME -Dstratus.catalog.redis.manual.port=6379"

if [ -f "$GEOSERVER_BASE_DIR/application.yml" ];then
  base_java_opts+=" -Dspring.config.location=file:$GEOSERVER_BASE_DIR/application.yml"
fi

PID=$(ps aux | grep $GEOSERVER_BASE_DIR/stratus.jar | grep -v grep | awk '{print $2}')
if [ -z "$PID" ];then
  nohup java  $base_java_opts ${java_opts} -jar $GEOSERVER_BASE_DIR/stratus.jar >> $GEOSERVER_BASE_DIR/geoserver.log 2>&1 &
fi

userdata_dir="/var/lib/cloud/instance"

if ! grep "^/bin/bash $userdata_dir/user-data.txt$" /etc/rc.d/rc.local > /dev/null;then
  echo "/bin/bash $userdata_dir/user-data.txt" >> /etc/rc.d/rc.local
fi

if [ "$PLATFORM" == centos ];then
  chmod 755 /etc/rc.d/rc.local
fi
