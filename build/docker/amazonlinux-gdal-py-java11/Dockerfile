FROM amazoncorretto:11

RUN yum -y upgrade \
  && yum clean all \
  && yum -y install https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm \
  && yum -y install google-noto-serif-fonts google-noto-fonts-common google-noto-sans-fonts google-noto-sans-symbols-fonts google-noto-sans-ui-fonts \
  && yum -y install dejavu-sans-mono-fonts dejavu-sans-fonts dejavu-serif-fonts liberation-sans-fonts \
  && yum -y install kernel-headers gcc-c++ openjpeg openjpeg-devel proj-4.0.0 proj-devel-4.8.0 curl-devel make swig wget tar gzip \
  && mkdir /mnt/efs

ENV ANT_VERSION "1.10.5"
ENV GDAL_VERSION "2.3.2"
ENV LD_LIBRARY_PATH "/usr/local/lib:/usr/lib:/usr/lib64"

RUN wget http://download.osgeo.org/gdal/${GDAL_VERSION}/gdal-${GDAL_VERSION}.tar.gz -O /tmp/gdal.tar.gz \
  && tar xzf /tmp/gdal.tar.gz -C /tmp \
  && cd /tmp/gdal-${GDAL_VERSION} \
  && ./configure --with-java=$JAVA_HOME \
                --with-proj=/usr/bin/proj \
                --with-proj5-api=no \
                --with-curl \
                --with-openjpeg \
  && make \
  && make install \
  && cd /tmp \
  && wget https://archive.apache.org/dist/ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz -O /tmp/ant.tar.gz \
  && tar xzf /tmp/ant.tar.gz -C /tmp \
  && rm /tmp/ant.tar.gz \
  && ln -s /tmp/apache-ant-${ANT_VERSION}/bin/ant /usr/local/bin/ant \
  && cd /tmp/gdal-${GDAL_VERSION}/swig/java \
  && sed -i '/JAVA_HOME =/d' java.opt \
  && make \
  && make install \
  && cp ./.libs/*.so /usr/local/lib/ \
  && mv /tmp/gdal-${GDAL_VERSION}/swig/java/gdal.jar /usr/share/gdal.jar \
  && ln -s /usr/local/lib/libgdalalljni.so /usr/local/lib/libgdaljni.so \
  && yum -y remove kernel-headers gcc-c++ make openjpeg-devel proj-devel curl-devel wget tar gzip \
  && yum clean all \
  && rm -rf /var/cache/yum /var/tmp/* /tmp/* /usr/local/bin/ant

CMD ["/bin/bash"]
