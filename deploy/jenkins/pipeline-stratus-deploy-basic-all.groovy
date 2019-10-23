import hudson.AbortException

node {
  /* The git branch (or tag) to build from
   *
   * Defaults to "master"
   */
  env.GIT_BRANCH="$GIT_BRANCH"

  sh 'printenv'

  try {
    stage('Deploy BASIC') { 
      deployTag('BASIC')
    }
    stage('Deploy ALL') { 
      deployTag('ALL')
    }
  } catch (hudson.AbortException interruptEx ) {
    echo "Build aborted by Jenkins"
    currentBuild.result="ABORTED"
  } catch (err) {
    print "Build failed: "+err.getClass()+" - "+err.getMessage()
    currentBuild.result = "FAILURE"
  }
}

def deployTag(def tag){
  build job: 'stratus-deploy',
    parameters: [
      string(name: 'GIT_BRANCH', value: String.valueOf(env.GIT_BRANCH)),
      booleanParam(name: 'SKIP_TESTS', value: false),
      booleanParam(name: 'DEBUG', value: false),
      string(name: 'DOCKER_TAG_NAME', value: ''),
      string(name: 'BUILD_PROFILE', value: String.valueOf(tag)),
    ]
}
