echo 'Removing old postgis instance'
docker rm -f postgis

echo 'Starting Postgis'
docker run --name postgis -p 5432:5432 -d -t kartoza/postgis
sleep 2
docker stop postgis
docker cp ./files/pg_hba.conf postgis:/etc/postgresql/9.5/main/pg_hba.conf
docker start postgis
docker exec -d postgis chown postgres:postgres /etc/postgresql/9.5/main/pg_hba.conf
docker exec -d postgis service postgresql start
