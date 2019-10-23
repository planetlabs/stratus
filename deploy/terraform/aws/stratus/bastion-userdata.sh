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
  PIP='command -v pip-3.6'
  PYTHON=python3.6
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
  `$PIP` install awscli
fi

PIP_LIST="$($PIP) list --format legacy"
BOTO3_INSTALLED=$($PIP_LIST | grep boto3)

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

  # aws ec2 create-tags --resources $INSTANCE_ID --tags Key=Name,Value=$NODE_NAME
fi

echo ${deploy_id} > /opt/deploy_id

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

for pkg in gdal java-1.8.0-openjdk-devel postgresql96 git docker
do
  if [ -z `rpm -qa $pkg` ];then
    yum -y install $pkg
  fi
done

if [ -f /usr/lib/jvm/jre-1.8.0-openjdk.x86_64/bin/java ];then
  alternatives --set java /usr/lib/jvm/jre-1.8.0-openjdk.x86_64/bin/java
fi

export ARTIFACTS_BASE_DIR=/opt/stratus/artifacts

if [ ! -d "$ARTIFACTS_BASE_DIR" ];then
  /bin/mkdir -p $ARTIFACTS_BASE_DIR
fi

for f in elasticache_info.json efs_info.json rds_info.json
do
  if [ ! -f "$ARTIFACTS_BASE_DIR/$f" ];then
    ls_file=`$AWSCLI s3 ls s3://${logging_bucket_name}/$f`
    if [ ! -z "$ls_file" ];then
      $AWSCLI s3 cp s3://${logging_bucket_name}/$f $ARTIFACTS_BASE_DIR/$f
    fi
  fi
done

efs_dns_name=''
if [ -f "$ARTIFACTS_BASE_DIR/efs_info.json" ];then
  efs_dns_name=`$PYTHON -c "import json;import os;data=open('{dir}/efs_info.json'.format(dir=os.environ['ARTIFACTS_BASE_DIR'])).read();print(json.loads(data)['dns_name'])"`
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
fi

userdata_dir="/var/lib/cloud/instance"

if ! grep "^/bin/bash $userdata_dir/user-data.txt$" /etc/rc.d/rc.local > /dev/null;then
  echo "/bin/bash $userdata_dir/user-data.txt" >> /etc/rc.d/rc.local
fi

if [ "$PLATFORM" == centos ];then
  chmod 755 /etc/rc.d/rc.local
fi
