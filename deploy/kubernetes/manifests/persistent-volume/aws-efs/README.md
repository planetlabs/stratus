# Create Coverage with EFS-backed imagery

## Create EC2 instance 
It doesn't have to be large -- just something to mount the EFS instance via nfs, and then sftp imagery to. I used a t2.micro. You're going to need to note its security group for EFS access.
Normally at this point, you might want to add an `Incoming` `Security Group` rule to this instance so you can access this from your own IP but that is covered below under "Allow NFS incoming access from EFS to the EC2 instance".

### Associate it with an Elastic IP
Go to the AWS `VPC` page, select the VPC associated with the kubernetes cluster, select `Elastic IPs` and then click the `Allocate New Address` button at the top. In the wizard popup, select the `VPC` radio button and then click `Allocate`. You will see the newly allocated Elastic IP, not associated with any instance. 

Select the Unallocated Elastic IP you want to use, and use the `Actions` drop-down to select `Associate address`. 
In the wizard that comes up, select the EC2 isntance you just created. Note the IP so you can use ssh and sftp to access this machine from your workstation. 

## Create EFS instance

EFS setup requires two availability zones and, of course, it should be on the same VPC as the kubernetes cluster.
Under "File System Access", add the security groups for the kubernetes master and nodes and for the EC2 instance created above. 

Note that, in addition to the default security group created for this instance, I have added another one which matches the EC2 security group, as well as the security groups for both the kubernetes nodes and masters.

## Allow NFS incoming access from EFS to the EC2 instance
From the EC2 instance configuration, click on the security group to bring up incoming and outgoing access rules. This is where we can create access rules for NFS (so we can mount the EFS volume on our tiny EC2) as well as SSH (so we can access this tiny EC2 from elsewhere, probably our workstation). Add two new Incoming rules:

    Type: NFS, Source: Custom (Type the security group for the EFS instance in the text box)
    Type: SSH, Source: My IP (should auto-populate)

## Kubernetes Setup
Kubernetes deployments can access EFS file systems via NFS using [Persistent Volumes](https://kubernetes.io/docs/user-guide/persistent-volumes/). A Persistent Volume references the network path of the EFS instance and the mount point.  

    apiVersion: v1
    kind: PersistentVolume
    metadata:
      name: efs-pv
    spec:
      capacity:
        storage: 100Gi
      accessModes:
        - ReadWriteMany
      nfs:
        server: fs-0384ad7f.efs.us-east-1.amazonaws.com
        path: "/"

In order to access the PersistentVolume, we need to establish a Persistent Volume Claim. This is done with two configurations, the first a PersistentVolumeClaim:

    kind: PersistentVolumeClaim
    apiVersion: v1
    metadata:
      name: efs-pv
      namespace: ec
    spec:
      accessModes:
        - ReadWriteMany
      resources:
        requests:
          storage: 10Gi
          
And from within the deployment spec (Note that the yml below has been largely stripped to show the level of hierarchy at which the persistent volume claim resides at):

    apiVersion: extensions/v1beta1
    kind: Deployment
    metadata:
      name: ec-deployment
      namespace: ec
    spec:
      replicas: 2
      template:
        metadata:
          namespace: ec
        spec:
          containers:
            - name: stratus
              ...
              volumeMounts:
                - mountPath: "/data"
                  name: datavol
          volumes:
            - name: datavol
              persistentVolumeClaim:
                claimName: efs-pv

When you deploy your `ec-deployment` in this case, it will have a volume accessible locally in the path `/data`. For us, this corresponds with the `/` directory on `fs-0384ad7f.efs.us-east-1.amazonaws.com`. 

## Getting data to EFS
Anything stored on "/" in our EFS file system will be accessible to the EC application but we still need to push data. The easiest way is to mount the EFS volume on our tiny EC2 instance and use sftp or curl to copy the data onto the mount. From AWS there is a handy link for "Amazon EC2 mount instructions" which will bring up instructions on how to do it, populated with the mount options and the DNS entry for your specific EFS. For this example:

    ssh -i my-private-key.pem 
    sudo yum install -y nfs-utils
    sudo mkdir /efs
    sudo mount -t nfs4 -o nfsvers=4.1,rsize=1048576,wsize=1048576,hard,timeo=600,retrans=2 fs-0384ad7f.efs.us-east-1.amazonaws.com:/ efs

After that I was able to use sftp from my local workstation to transfer files to the remote EC2 machine.

    sftp -i my-private-key.pem ec2-user@34.206.255.185  local-directory/landsat.tif /efs/landsat.tif

This puts `landsat.tif` at the path `/landsat.tif` on the EFS volume. Since the kubernetes deployment uses a volume path of `/data`, the deployment should have access to this as `/data/landsat.tif`

## Creating EFS-backed Coverages
The process for creating coverages is the same as with traditional geoserver from here, but you just need to know the path to the EFS file. Here, obviously, the geoserver-url is taken from your kubernetes deployment (see the advertised url for the `ec-lb` Service). Yours will obviously be different but I've left this url in to retain the flavor of how this really looks in practice. The examples below use a sample workspace `landsat_ws`, CoverageStore `landsat_cs`, and Coverage `landsat_cov`

### Create Workspace
    curl -v -u admin:geoserver -XPOST -H "Content-type: text/xml" -d "<workspace><name>landsat_ws</name></workspace>" http://<host>.us-east-1.elb.amazonaws.com/geoserver/rest/workspaces'
    
### Create CoverageStore
    curl -v -u admin:geoserver -v -XPOST -H "Content-type: text/xml" -d \
        "<coverageStore> \
          <name>landsat_cs</name> \
          <workspace><name>'+workspace_name+'</name></workspace> \
          <enabled>true</enabled> \
          <type>GeoTIFF</type> \
          <url>/data/landsat.tif</url> \
        </coverageStore>" \
        http://<host>.us-east-1.elb.amazonaws.com/geoserver/rest/workspaces/landsat_ws/coveragestores?configure=all
    
### Create Coverage
This is obviously going to vary depending on the specifics of your coverage. This is from band 1 of a UTM zone 12 Landsat-8 scene.

    curl -v -u admin:geoserver -v -XPOST -H "Content-type: text/xml" -d \
        "<coverage> \
            <name>landsat_cov</name> \
            <title>Landsat Cov'</title> \
            <nativeCRS> \
                PROJCS[&quot;WGS 84 / UTM zone 12N&quot;, \
                GEOGCS[&quot;WGS 84&quot;, \
                    DATUM[&quot;WGS_1984&quot;, \
                        SPHEROID[&quot;WGS 84&quot;,6378137,298.257223563, \
                            AUTHORITY[&quot;EPSG&quot;,&quot;7030&quot;]], \
                        AUTHORITY[&quot;EPSG&quot;,&quot;6326&quot;]], \
                    PRIMEM[&quot;Greenwich&quot;,0, \
                        AUTHORITY[&quot;EPSG&quot;,&quot;8901&quot;]], \
                    UNIT[&quot;degree&quot;,0.0174532925199433, \
                        AUTHORITY[&quot;EPSG&quot;,&quot;9122&quot;]], \
                    AUTHORITY[&quot;EPSG&quot;,&quot;4326&quot;]], \
                PROJECTION[&quot;Transverse_Mercator&quot;], \
                PARAMETER[&quot;latitude_of_origin&quot;,0], \
                PARAMETER[&quot;central_meridian&quot;,-111], \
                PARAMETER[&quot;scale_factor&quot;,0.9996], \
                PARAMETER[&quot;false_easting&quot;,500000], \
                PARAMETER[&quot;false_northing&quot;,0], \
                UNIT[&quot;metre&quot;,1, \
                    AUTHORITY[&quot;EPSG&quot;,&quot;9001&quot;]], \
                AXIS[&quot;Easting&quot;,EAST], \
                AXIS[&quot;Northing&quot;,NORTH], \
                AUTHORITY[&quot;EPSG&quot;,&quot;32612&quot;]] \
            </nativeCRS> \
            <srs>EPSG:4326</srs> \
            <latLonBoundingBox><minx>-112.588130</minx><maxx>-110.124059</maxx><miny>30.668436</miny><maxy>32.799997</maxy><crs>EPSG:4326</crs></latLonBoundingBox> \
        </coverage>" \
         http://<host>.us-east-1.elb.amazonaws.com/geoserver/rest/workspaces/landsat_ws/coveragestores/landsat_cs/coverages
         
## Test the coverage:
    http://<host>.us-east-1.elb.amazonaws.com/geoserver/wms?service=WMS&version=1.3.0&request=GetMap&layers=landsat_ws:landsat_cov&styles=&format=image/png&bbox=32.683439,-111.935673,32.724837,-110.944479&srs=EPSG:4326&width=400&height=300
    
