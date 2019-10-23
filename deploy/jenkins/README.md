# Stratus Jenkins Pipeline 

Stratus uses [Jenkins Pipeline](https://jenkins.io/doc/book/pipeline/) for CI.

This is configured by pipeline scripts, each of which correspond directly to a job in Jenkins:

* `pipeline-stratus-deploy.groovy`: Builds and deploys Stratus with the specified profile
* `pipeline-stratus-deploy-basic-all.groovy`: Builds and deploys the "BASIC" and "ALL" profiles of Stratus.
* `pipeline-stratus-release.groovy`: Tags, builds, and deploys a specific branch or commit of Stratus, and increments the version.
* `pipeline-stratus-deploy-terraform.groovy`: Builds Stratus, pushes the built image to a Terraform deployment of Stratus, and runs the JMeter load tests.
* `pipeline-stratus-k8s-tests.groovy`: Deploys an existing build of Stratus to a Kubernetes deployment, and runs the JMeter load tests.

In addition to the job specific Jenkins arguments (documented at the top of each groovy file), all of these jobs require the following environment variables:

* `DEPLOY_S3`= The S3 URL of the S3 bucket to push Stratus jars to.
* `DEPLOY_S3_HTTP`= The HTTP URL of the S3 bucket to push Stratus jars to.
* `DOCKER_USER`: The docker user used to deploy.
* `DOCKER_PASSWORD`: The password for `$DOCKER_USER`.
* `DOCKER_REPO`: The docker repository to deploy to (e.g. `docker.io`).

The k8s and terraform jobs also require the following environment variables for configuring the k8s deployment:

* `KUBE_CONTEXT`: The domain that k8s is deployed to.
* `KUBE_DB_URL`: The domain the k8s database is deployed to.
* `KUBE_DB_USERNAME`: The admin user for the k8s database.
* `KUBE_DB_PASSWORD`: The password for the database user.
* `KOPS_STATE_STORE`: THe S3 URL of the [kops](https://github.com/kubernetes/kops) state store for the k8s cluster.