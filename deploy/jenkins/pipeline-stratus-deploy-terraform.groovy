import hudson.AbortException

node {

  env.GIT_BRANCH="$GIT_BRANCH"
  def skipTests="$SKIP_TESTS"
  def debug="$DEBUG"

  env.KUBE_CONTEXT="$KUBE_CONTEXT"
  env.KUBE_NAMESPACE="$KUBE_NAMESPACE"
  
  def repoName="stratus"
  def dbUrl="$KUBE_DB_URL"

  def numWorkspaces=5
  def numFeatureTypes=5
  def numTestThreads=5
  def numTestIterations=100

  sh 'printenv'
  print "debug "+debug

  try {
    stage('Git') { 
      if (debug!=true){
        gitCheckout(repoName,env.GIT_BRANCH)
        version=sh (returnStdout: true, script: "cd "+repoName+"; mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -e '^[^\\[]'").trim()
        echo "Version is "+version
        env.PROJECT_VERSION=version

        gitCheckout('stratus','master')
      } else {
        print "DEBUG - skipping"
      }
    }
    
    stage('Mvn build') {
      if (debug!=true){
        if (skipTests){
          sh "cd stratus; mvn clean install -U -DskipTests"
        } else {
          sh "cd stratus; mvn clean install -U"
        }
      }
    }
    
    stage('Docker build') {
      if (debug!=true){
        sh "cd gsstratus/stratus-application; mvn docker:build"
      }
    }
    
    stage('Docker push') {
      if (debug!=true){
        sh "docker tag gsstratus/stratus:latest $DOCKER_REPO/gsstratus/stratus:"+env.PROJECT_VERSION
        sh "docker login -u=\"$DOCKER_USER\" -p=\"$DOCKER_PASSWORD\" $DOCKER_REPO && docker push $DOCKER_REPO/gsstratus/stratus:"+env.PROJECT_VERSION
      }
    }
    stage('S3 push'){
      if (debug!=true){
        sh "aws s3 cp gsstratus/stratus-application/target/stratus-application-"+env.PROJECT_VERSION+"-exec.jar $DEPLOY_S3/"
        sh "aws s3 cp gsstratus/stratus-application/target/stratus-application-"+env.PROJECT_VERSION+".jar $DEPLOY_S3/"
      }
    }

    stage('TF Apply') {
      sh "cd stratus/deploy/terraform/aws/stratus; terraform init -reconfigure -backend=true; terraform apply -var db_create_instance=true"
    }

    stage('Load/Test Catalog') {
      if (debug!=true){
        geoserverUrl=getGeoserverUrl()
        dbUrl=getDatabaseUrl()
        dbUsername=getDatabaseUsername()
        dbPassword=getDatabasePassword()
        loadGeoserver(geoserverUrl,dbUrl,dbUsername,dbPassword)
        testJmeter("vector-scale-test-wfs.jmx", numWorkspaces, numFeatureTypes, numTestThreads, numTestIterations, geoserverUrl)
        testJmeter("vector-scale-test-wms.jmx", numWorkspaces, numFeatureTypes, numTestThreads, numTestIterations, geoserverUrl)
      }
    }

    stage('TF Destroy') {
      sh "cd deploy/terraform/aws/stratus; terraform destroy -force"
    }

  } catch (hudson.AbortException interruptEx ) {
    echo "Build aborted by Jenkins"
    currentBuild.result="ABORTED"
  } catch (err) {
    print "Build failed: "+err.getClass()+" - "+err.getMessage()
    currentBuild.result = "FAILURE"
  }
  finally {
      // sh "cd deploy/terraform/aws/stratus; terraform destroy -lock=false"
  }
}

def getGeoserverUrl() {
  geoserverUrl=sh (returnStdout: true, script: "cd stratus/deploy/terraform/aws/stratus; terraform show | grep alb_dns_name | cut -f2 -d=").trim()
  return geoserverUrl
}
def getDatabaseUrl() {
  databaseUrl=sh (returnStdout: true, script: "cd stratus/deploy/terraform/aws/stratus; terraform show | grep rds_dns_name | cut -f2 -d=").trim()
  return databaseUrl
}
def getDatabaseUsername() {
  line=sh (returnStdout: true, script: "cd stratus/deploy/terraform/aws/stratus; terraform show | grep -ni db_username variables.tf | cut -f1 -d:").trim()
  line2=line+2
  username=sh(returnStdout: true, script: "sed -n "+line+","+line2+"p stratus/deploy/terraform/aws/stratus/variables.tf | grep default| cut -f2 -d\"")
  return username
}
def getDatabasePassword() {
  line=sh (returnStdout: true, script: "cd stratus/deploy/terraform/aws/stratus; terraform show | grep -ni db_password variables.tf | cut -f1 -d:").trim()
  line2=line+2
  password=sh(returnStdout: true, script: "sed -n "+line+","+line2+"p stratus/deploy/terraform/aws/stratus/variables.tf | grep default| cut -f2 -d\"")
  return password
}

def gitCheckout(def repo, def branch){
  sh "rm -rf "+repo+"; git clone 'git@github.com:planetlabs/"+repo+".git'"
  sh "cd "+repo+"; git checkout "+branch
}

def loadGeoserver(def geoserverUrl, def dbUrl, def dbUsername, def dbPassword) {
  def cmd= "cd stratus/test/python; python generate_points.py --load-gs --workspaces 1 --layers 1 --geoserver-host "+geoserverUrl+" --geoserver-port 80 --db-host "+dbUrl+" --db-port 5432 --db-username "+dbUsername+" --db-password "+dbPassword+" --workspace-prefix ws --datastore-prefix ds --table-prefix ft --db-format 0000 --datastore-format 0000 --workspace-format 0000 --featuretype-format 0000 --curl-retries=1"
  echo cmd
  sh cmd
}

def testJmeter(def jmeterFile, def numWorkspaces, def numFeatureTypes, def numTestThreads, def numTestIterations, def geoserverUrl){
  def cmd="cd stratus/test/jmeter; jmeter -n -t "+jmeterFile+" -JTHREADS="+numTestThreads+" -JITERATIONS="+numTestIterations+" -JGEOSERVER_HOST="+geoserverUrl+" -JGEOSERVER_PORT=80 -JWORKSPACES="+numWorkspaces+" -JFEATURETYPES="+numFeatureTypes+" -JWORKSPACE_FORMAT=\"0000\" -JFEATURETYPE_FORMAT=\"0000\" -JFEATURETYPE_PREFIX=ft -JWORKSPACE_PREFIX=ws -JDB_PREFIX=db -JDB_FORMAT=\"0000\""
  echo cmd
  sh cmd
}


