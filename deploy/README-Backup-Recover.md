# Local backup/restore to/from redis
## Backup with appendonly yes
docker run -d --name redis-catalog -p 6379:6379 -e "SERVICE_NAME=redis-catalog" redis redis-server --appendonly yes
...
Create your catalog through geoserver/web UI and/or geoserver/rest API
docker cp fbde40f89edd:/data/appendonly.aof $BACKUP_DIR/$BACKUP_FILENAME

## Restore with appendonly yes
docker run -d --name redis-catalog -p 6379:6379 -e "SERVICE_NAME=redis-catalog" -v $BACKUP_DIR/$BACKUP_FILENAME:/data/appendonly.aof redis redis-server --appendonly yes

# Kubernetes backup/restore to/from redis with AOF
## Backup (with appendonly yes)

kubectl get po --namespace=${NAMESPACE}
Look for redis-master
kubectl cp default/${REDIS_MSATER_POD_NAME}:/redis-master-data/appendonly.aof /backups/appendonly.aof

This provides a copy of the AOF file that you can use for future restore. It represents a "replay" that will generate the current state of the redis catalog.

## Restore (with AOF file and appendonly yes)
Redis should NOT be running at this point.

I have the EFS shared as an nfs mount from an EC2 instance (34.206.255.185) with the root path of the EFS mounted as "/efs" on the instance. For this example, I'm copying my backup file to the EFS and will then bring Redis up pointing to this file. 

scp /backups/appendonly.aof ec2-user@34.206.255.185:/efs/redis-osm/appendonly.aof

Now, bring redis up normally and the catalog will be populated with the backup you made previously.

*** Note that the instructions for AOF are identival if appendonly is disabled but the filenames are "dump.rdb" rather than "appendonly.aof" ***

# Kubernetes backup/restore to/from redis RDB with AOF on
## Backup (with appendonly no)

Find the redis-master pod name (a redis slave should be up-to-date if sentinel is choosing masters)
kubectl get po --namespace=${NAMESPACE}

Look for redis-master
Trigger a snapshot to be dumped to disk:
kubectl exec ${REDIS_MASTER_POD_NAME} -- redis-cli -c SAVE

Copy backup to a local directory
kubectl cp default/${REDIS_MASTER_POD_NAME}:/redis-master-data/dump.rdb /backups/dump.rdb

This provides a copy of the RDB file that you can use for future restore. It represents a "snapshot" of the current state of the catalog. It is saved at set intervals based on the `SAVE` command and can be triggered.

## Restore (with RDB file and appendonly off)
Redis should NOT be running at this point. We are going to bring Redis up with `appendonly no`, which will cause redis to read the RDB file. Once redis is running we can turn `appendonly yes` and have an AOF generated.

### EFS example
I have the EFS shared as an nfs mount from an EC2 instance (34.206.255.185) with the root path of the EFS mounted as "/efs" on the instance. For this example, I'm copying my backup file to the EFS and will then bring Redis up pointing to this file. 

scp /backups/dump.rdb ec2-user@34.206.255.185:/efs/redis-osm/appendonly.aof

Now, bring redis up normally and the catalog will be populated with the backup you made previously.

# Anatomy of an automated restore with an RDB file (with Redis running AOF)
Provided the RDB is accessible to redis and redis is configured appropriately for persistent volumes with AWS (EFS/EBS) or GCE (
Bring up redis in `appendonly no` mode

==
==
==
==
==
Backing up redis catalog store
==
Redis backups run in one of two modes: `appendonly on` and `appendonly off`. By default, the container we have been using (kubernetes/redis:v1) has appendonly enabled, which adds a slight complication for restoring.

Access `redis-cli`. To access redis-cli from a kubernetes proxy (assuming your namespace is `ec` in the examples below):
```bash
kubectl --namespace=ec get pods
```
will return something like
```
mac1:kubernetes aaryno$ kubectl --namespace=ec get pods
NAME                            READY     STATUS    RESTARTS   AGE
consul-0                        1/1       Running   0          21h
consul-1                        1/1       Running   0          21h
consul-2                        1/1       Running   0          21h
ec-deployment-774053996-4mmtt   1/1       Running   0          1h
ec-deployment-774053996-4pp7p   1/1       Running   0          33m
redis-kcvgl                     1/1       Running   0          38m
redis-lrnsr                     1/1       Running   0          38m
redis-master                    2/2       Running   0          1h
redis-sentinel-0n7gl            1/1       Running   0          1h
redis-sentinel-3h9bl            1/1       Running   0          38m
```
Any of the `redis` pods (not `redis-sentinel`) will do. Set up port forwarding to access one of these:
```bash
kubectl --namespace=ec port-forward redis-master 6379&
```
This maps your port 6379 to the pod's exposed port 6379. Now you can run `redis-cli` to get a backup. Here I'm saving a snapshot to my local directory:
```bash
redis-cli --rdb ~/dump.rdb
```
That `~/dump.rdb` is your snapshot.

Restoring the redis catalog store
==
To restore the redis backup from the RDB file we need to do two things: 
1) Turn off `appendonly`. This is done by creating a new `redis.conf` file and applying it to the `redis` container using `kubernetes configmap`.
2) Put the `dump.rdb` we saved previously on a persistent volume accessible to the container.

## Turn off `appendonly`
```bash
cd ${STRATUS}/deploy/kubernetes
kubectl --namespace=ec create configmap redis-conf --from-file=redis/redis.conf
```
We need to tell redis about this file, but we'll simultaneously add the persistent volume so that one set of edits is required to make the changes for both appendonly and for the persistent volume.

### Edit `redis/redis-master.yml` and `redis-controller.yml`
Under `spec.containers[name:master]` add this `volumeMount` for the `redis.conf` config:
```yml
      volumeMounts:
        - name: config
          mountPath: /redis-master
```
Under `spec.volumes:` (if it doesn't exist, create it), add both the persistentVolume Claim and the configmapped redis-conf:
```yml
  volumes:
    - name: data
      persistentVolumeClaim:
        claimName: efs
    - name: config
      configMap:
        name: redis-conf
```
At this point the config should mostly work for the redis-config but it should also be applied to `redis-controller.yml`. Go ahead and change this file as well, adding the same entries as above.
### Setup the persistent volume
There are a number of sources for persistent volumes. I had an EFS persistent volume previously set up. See [Setting up EFS](efs/README.md) for details on that setup.

The important part of the persistent volume here is that you put the `dump.rdb` file in the location that `redis` expects to find it. So use sftp to push that file into place and when you start up redis, use redis-cli to verify your previously saved keys are in place.

### Starting up redis
Once the persistent volume has been set up and the config map has been set you can start up redis.
```bash
kubectl --namespace=ec apply -f redis/redis-service.yml
kubectl --namespace=ec apply -f redis/redis-sentinel-service.yml
kubectl --namespace=ec apply -f redis/redis-master.yml
kubectl --namespace=ec apply -f redis/redis-sentinel-controller.yml
kubectl --namespace=ec apply -f redis/redis-controller.yml
```
You can use `port-forward` again to re-connect to redis via `redis-cli` and check on keys (e.g., `keys *` to list them all).



