# Dockerfile for quickly getting locust running
FROM ubuntu:18.04

WORKDIR /root
ADD update_distro.sh install_locust.sh requirements.txt ./
RUN ./update_distro.sh && ./install_locust.sh

ADD run_locust.sh ./
ADD data data
ADD code code
EXPOSE 8089

ARG URL=http://localhost
ARG LAYERS=osm:osm
ARG BBOX_FILE=wms_256_tiles.csv

#ENV URL http://localhost
#ENV LOCUST_LAYERS osm:osm
ENV BBOX_FILE data/wms_256_tiles.csv

CMD ["sh","-c","./run_locust.sh --layers=${LAYERS} --url=${URL} --bbox-file=${BBOX_FILE}"]
