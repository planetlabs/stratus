import hudson.AbortException

node {
  /* (Required) The git branch to release from
   *
   * Defaults to "master"
   */
  env.GIT_BRANCH="$GIT_BRANCH"
  /* (Optional) The name of the git tag to publish for this release
   *
   * Defaults to "v$RELEASE_VERSION"
   */
  env.GIT_TAG="$GIT_TAG"
  /* (Optional) The mvn version to release under
   *
   * If the current maven version is 1.2.0-SNAPSHOT, defaults to "1.2.0".
   */
  env.RELEASE_VERSION="$RELEASE_VERSION"
  /* (Optional) The mvn version to push to master after the release is complete
   *
   * If the release version is 1.2.0, defaults to "1.2.1-SNAPSHOT".
   */
  env.DEVELOPMENT_VERSION="$DEVELOPMENT_VERSION"
  
  def repoName="stratus"

  def buildCmd="cd stratus; mvn release:prepare --batch-mode"

  def cleanCmd="cd stratus; mvn release:clean; git reset --hard HEAD"

  def dockerTag=""

  def releasePushed=false

  sh 'printenv'
  print "debug "+debug

  try {
    stage('Git') { 
        gitCheckout(repoName,env.GIT_BRANCH)
        version=sh (returnStdout: true, script: "cd "+repoName+"; mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -e '^[^\\[]'").trim()
        echo "Version is "+version
        env.PROJECT_VERSION=version

        gitCheckout('stratus','master')
    }
    stage('Setup') {

      if (env.GIT_TAG=="") {
        env.GIT_TAG = "v" + env.PROJECT_VERSION.replaceAll("-SNAPSHOT", "")
      }
      buildCmd = buildCmd + " -Dtag="+env.GIT_TAG

      if (env.GIT_TAG.startsWith("v")) {
        dockerTag = env.GIT_TAG.substring(1)
      } else {
        dockerTag = env.GIT_TAG
      }

      if (env.RELEASE_VERSION != "") {
        buildCmd = buildCmd + " -DreleaseVersion="+env.RELEASE_VERSION
      }

      if (env.DEVELOPMENT_VERSION != "") {
        buildCmd = buildCmd + " -DdevelopmentVersion="+env.DEVELOPMENT_VERSION
      }

      if (debug == true) {
        buildCmd = buildCmd + "  -DdryRun=true  -DpushChanges=false"
      }
    }
    stage('Mvn release') {
        sh buildCmd
        if (debug != true) {
          releasePushed=true
        }
    }
    stage('Notify') {
      def message=""
      if (env.RELEASE_VERSION == "") {
        message = "Released Stratus " + env.GIT_BRANCH + " as " + env.GIT_TAG + " in Git."
      } else {
        message = "Released Stratus " + env.RELEASE_VERSION + " as " + env.GIT_TAG + " in Git."
      }
      if (env.DEVELOPMENT_VERSION == "") {
        message = message + "\nIncremented mvn version for " + env.GIT_BRANCH + " branch."
      } else {
        message = message + "\nUpdated version for " + env.GIT_BRANCH + " branch to " + env.DEVELOPMENT_VERSION + " in Git."
      }
      echo message
    }
    stage('Cleanup') {
        sh cleanCmd
    }
    stage('Deploy') {
      /* Deploy is called after notify, because at this point even if the deploy fails the tags have already been pushed to git, 
       * so only the deploy would need to be rerun. In this case, the deploy job will provide its own failure message.
       */
      build job: 'stratus-deploy-all',
      parameters: [
        string(name: 'GIT_BRANCH', value: String.valueOf(env.GIT_TAG))
      ]
    }

  } catch (hudson.AbortException interruptEx ) {
    echo "Build aborted by Jenkins"
    currentBuild.result="ABORTED"
    
    if (releasePushed == true) {
      echo "Release already pushed to git, do not rerun the release job."
    }
    sh cleanCmd
  } catch (err) {
    print "Build failed: "+err.getClass()+" - "+err.getMessage()
    currentBuild.result = "FAILURE"
    
    if (releasePushed == true) {
      echo "Release already pushed to git, do not rerun the release job."
    }
    sh cleanCmd
  }
}

def gitCheckout(def repo, def branch){
  sh "rm -rf "+repo+"; git clone 'git@github.com:planetlabs/"+repo+".git'"
  sh "cd "+repo+"; git checkout "+branch
}



