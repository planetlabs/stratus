import hudson.AbortException

node {

  env.KUBE_CONTEXT="$KUBE_CONTEXT"
  env.KUBE_NAMESPACE="$KUBE_NAMESPACE"
  env.DOCKER_TAG_NAME="$DOCKER_TAG_NAME"

  def geoserverUrl=getGeoserverUrl()
  def dbUsername="$KUBE_DB_USERNAME"
  def dbPassword="$KUBE_DB_PASSWORD"
  def dbUrl="$KUBE_DB_URL"
  
  def repoName="stratus"

  def numWorkspaces=5
  def numFeatureTypes=5
  def numTestThreads=5
  def numTestIterations=100

  sh 'printenv'
  print "debug "+debug

  try {
// LETTUCE MANUAL
    stage('Deploy k8s lettuce-manual') {
      if (debug!=true){
        deployStratus('lettuce','manual')
      }
    }
    stage('Catalog lettuce-manual') {
      if (debug!=true){
        loadGeoserver(geoserverUrl,dbUrl,dbUsername,dbPassword)
        testJmeter("vector-scale-test-wfs.jmx", numWorkspaces, numFeatureTypes, numTestThreads, numTestIterations, geoserverUrl)
        testJmeter("vector-scale-test-wms.jmx", numWorkspaces, numFeatureTypes, numTestThreads, numTestIterations, geoserverUrl)
      }
    }
      
    // JEDIS MANUAL
    stage('Deploy k8s jedis-manual') {
      if (debug!=true){
        deployStratus('jedis','manual')
      }
    }
    stage('Catalog jedis-manual') {
      if (debug!=true){
        loadGeoserver(geoserverUrl,dbUrl,dbUsername,dbPassword)
        testJmeter("vector-scale-test-wfs.jmx", numWorkspaces, numFeatureTypes, numTestThreads, numTestIterations, geoserverUrl)
        testJmeter("vector-scale-test-wms.jmx", numWorkspaces, numFeatureTypes, numTestThreads, numTestIterations, geoserverUrl)
      }
    }
      
    // LETTUCE SENTINEL
    stage('Deploy k8s lettuce-sentinel') {
      if (debug!=true){
        deployStratus('lettuce','manual')
      }
    }
    stage('Catalog lettuce-sentinel') {
      if (debug!=true){
        loadGeoserver(geoserverUrl,dbUrl,dbUsername,dbPassword)      
        testJmeter("vector-scale-test-wfs.jmx", numWorkspaces, numFeatureTypes, numTestThreads, numTestIterations, geoserverUrl)
        testJmeter("vector-scale-test-wms.jmx", numWorkspaces, numFeatureTypes, numTestThreads, numTestIterations, geoserverUrl)
      }
    }
      
    // JEDIS SENTINEL
    stage('Deploy k8s jedis-sentinel') {
      if (debug!=true){
        deployStratus('jedis','manual')
      }
    }
    stage('Catalog jedis-sentinel') {
      if (debug!=true){
        geoserverUrl=getGeoserverUrl()
        loadGeoserver(geoserverUrl,dbUrl,dbUsername,dbPassword)
        testJmeter("vector-scale-test-wfs.jmx", numWorkspaces, numFeatureTypes, numTestThreads, numTestIterations, geoserverUrl)
        testJmeter("vector-scale-test-wms.jmx", numWorkspaces, numFeatureTypes, numTestThreads, numTestIterations, geoserverUrl)
      }
    }

  } catch (hudson.AbortException interruptEx ) {
    echo "Build aborted by Jenkins"
    currentBuild.result="ABORTED"
  } catch (err) {
    print "Build failed: "+err.getClass()+" - "+err.getMessage()
    currentBuild.result = "FAILURE"
  }
}

def getGeoserverUrl() {
  geoserverUrl=sh (returnStdout: true, script: "kubectl --namespace="+env.KUBE_NAMESPACE+" --context="+env.KUBE_CONTEXT+" describe svc stratus-ogc-lb | grep Ingress |cut -f2 -d: | cut -f2").trim()
  return geoserverUrl
}

def loadGeoserver(def geoserverUrl, def dbUrl, def dbUsername, def dbPassword) {
  def cmd= "cd stratus/test/python; python generate_points.py --load-gs --workspaces 1 --layers 1 --geoserver-host "+geoserverUrl+" --geoserver-port 80 --db-host "+dbUrl+" --db-port 5432 --db-username "+dbUsername+" --db-password "+dbPassword+" --workspace-prefix ws --datastore-prefix ds --table-prefix ft --db-format 0000 --datastore-format 0000 --workspace-format 0000 --featuretype-format 0000 --curl-retries=1"
  echo cmd
  sh cmd
}

def deployStratus(def redisImplementation, def redisType) { 
  def cmd="cd stratus/deploy/kubernetes/helm; bash ./deploy.sh --namespace="+env.KUBE_NAMESPACE+" --kops-state-store=$KOPS_STATE_STORE --context="+env.KUBE_CONTEXT+" --reset-redis --docker-tag-name="+env.DOCKER_TAG_NAME+" --docker-image-name=stratus --docker-repo=$DOCKER_REPO/stratus --redis-type="+redisType+"  --redis-implementation="+redisImplementation
  echo cmd
  sh cmd
}

def testJmeter(def jmeterFile, def numWorkspaces, def numFeatureTypes, def numTestThreads, def numTestIterations, def geoserverUrl){
  def cmd="cd stratus/test/jmeter; jmeter -n -t "+jmeterFile+" -JTHREADS="+numTestThreads+" -JITERATIONS="+numTestIterations+" -JGEOSERVER_HOST="+geoserverUrl+" -JGEOSERVER_PORT=80 -JWORKSPACES="+numWorkspaces+" -JFEATURETYPES="+numFeatureTypes+" -JWORKSPACE_FORMAT=\"0000\" -JFEATURETYPE_FORMAT=\"0000\" -JFEATURETYPE_PREFIX=ft -JWORKSPACE_PREFIX=ws -JDB_PREFIX=db -JDB_FORMAT=\"0000\""
  echo cmd
  sh cmd
}


