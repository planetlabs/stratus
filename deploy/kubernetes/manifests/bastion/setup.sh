#!bin/bash

apt-get install software-properties-common python-software-properties apt-transport-https

add-apt-repository "deb http://apt.postgresql.org/pub/repos/apt/ $(lsb_release -sc)-pgdg main"
add-apt-repository ppa:git-core/ppa

apt-get install postgresql-9.6 git python-pscopg2 curl

git clone https://github.com/gsstratus/stratus.git

#python generate_points.py --load-pg --workspaces 1 --layers 1 --db-host $DB_HOST --db-port 5432 --db-username postgres --db-password postgres --workspace-prefix ws --datastore-prefix ds --table-prefix ft --db-format 0000 --datastore-format 0000 --workspace-format 0000 --featuretype-format 0000 
