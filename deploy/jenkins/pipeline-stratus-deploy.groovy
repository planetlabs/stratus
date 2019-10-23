import hudson.AbortException

node {
  /* The set of maven profiles to run the build with.
   *
   * This should be a comma-seperated list of mvn profile names, or either BASIC or ALL which are interpreted as:
   * - BASIC: webadmin
   * - ALL: webadmin,grib,mongo,netcdf,oracle,sqlserver
   *
   * Defaults to "BASIC".
   */
  env.BUILD_PROFILE="$BUILD_PROFILE"
  /* The name of the docker image to publish for this build
   * 
   * If "latest", will also publish an image tagged with the project version.
   * If blank, will be computed from the maven profile and the project version.
   *
   * Defaults to "latest"
   */
  env.DOCKER_TAG_NAME="$DOCKER_TAG_NAME"
  /* The git branch (or tag) to build from
   *
   * Defaults to "master"
   */
  env.GIT_BRANCH="$GIT_BRANCH"


  def skipTests="$SKIP_TESTS"
  
  def repoName="stratus"

  def buildCmd="cd stratus; mvn clean install -U"

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
    stage('Setup') {
      if (skipTests){
        buildCmd = buildCmd + " -DskipTests"
      }
      if (env.BUILD_PROFILE=="BASIC") {
        buildCmd = buildCmd + " -Pwebadmin"
      } else if (env.BUILD_PROFILE=="ALL") {
        buildCmd = buildCmd + " -Pwebadmin,grib,mongo,netcdf,oracle,sqlserver"
      } else if (env.BUILD_PROFILE!="") {
        buildCmd = buildCmd + " -P"+ env.BUILD_PROFILE
      }

      if (env.DOCKER_TAG_NAME=="") {
        env.DOCKER_TAG_NAME=env.PROJECT_VERSION+"-"+env.BUILD_PROFILE.replaceAll(',','-');
      }
    }
    stage('Mvn build') {
      if (debug!=true){
        sh buildCmd
      }
    }
    
    stage('S3 push'){
      if (debug!=true){
        if (env.DOCKER_TAG_NAME=="latest") {
          sh "curl --request PUT --upload-file gsstratus/stratus-application/target/stratus-application-"+env.PROJECT_VERSION+"-exec.jar $DEPLOY_S3_HTTP/stratus-application-"+env.PROJECT_VERSION+"-exec.jar"
          sh "curl --request PUT --upload-file gsstratus/stratus-application/target/stratus-application-"+env.PROJECT_VERSION+".jar $DEPLOY_S3_HTTP/stratus-application-"+env.PROJECT_VERSION+".jar"
        } else {
          sh "curl --request PUT --upload-file gsstratus/stratus-application/target/stratus-application-"+env.PROJECT_VERSION+"-exec.jar $DEPLOY_S3_HTTP/stratus-application-"+env.DOCKER_TAG_NAME+"-exec.jar"
          sh "curl --request PUT --upload-file gsstratus/stratus-application/target/stratus-application-"+env.PROJECT_VERSION+".jar $DEPLOY_S3_HTTP/stratus-application-"+env.DOCKER_TAG_NAME+".jar"
        }
      }
    }
    
    
    stage('Docker build') {
      if (debug!=true){
        sh "cd gsstratus/stratus-application; docker login -u=\"$DOCKER_USER\" -p=\"$DOCKER_PASSWORD\" $DOCKER_REPO && mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=docker.baseImage | grep -Ev '(^\\[|Download\\w+:)' | xargs docker pull"
        sh "cd gsstratus/stratus-application; mvn docker:build"
      }
    }
 
    stage('Docker push') {
      if (debug!=true){
        if (env.DOCKER_TAG_NAME=="latest") {
          sh "docker tag gsstratus/stratus:latest $DOCKER_REPO/gsstratus/stratus:"+env.PROJECT_VERSION
          sh "docker login -u=\"$DOCKER_USER\" -p=\"$DOCKER_PASSWORD\" $DOCKER_REPO && docker push $DOCKER_REPO/gsstratus/stratus:"+env.PROJECT_VERSION
        }
        sh "docker tag gsstratus/stratus:latest $DOCKER_REPO/gsstratus/stratus:"+env.DOCKER_TAG_NAME
        sh "docker login -u=\"$DOCKER_USER\" -p=\"$DOCKER_PASSWORD\" $DOCKER_REPO && docker push $DOCKER_REPO/gsstratus/stratus:"+env.DOCKER_TAG_NAME
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

def gitCheckout(def repo, def branch){
  sh "rm -rf "+repo+"; git clone 'git@github.com:planetlabs/"+repo+".git'"
  sh "cd "+repo+"; git checkout "+branch
}


