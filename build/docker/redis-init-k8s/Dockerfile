FROM zvelo/redis-trib
MAINTAINER tingold
ADD setupCluster.sh /usr/local/bin
RUN chmod +x /usr/local/bin/setupCluster.sh
ENTRYPOINT ["/usr/local/bin/setupCluster.sh"]
