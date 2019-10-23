# Kubernetes Deploy of Stratus on Amazon Web Services

## Prerequisites

1. AWS account with administrator access (http://docs.aws.amazon.com/IAM/latest/UserGuide/id_users_create.html)
2. AWS CLI installed (http://docs.aws.amazon.com/cli/latest/userguide/installing.html)
3. AWS CLI configured (http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html)
4. SSH keypair setup in default location (~/.ssh/id_rsa & ~/.ssh/id_rsa.pub) (https://wiki.osuosl.org/howtos/ssh_key_tutorial.html)
5. Stratus repo cloned (https://github.com/gsstratus/stratus)

## Setup Kubernetes CLI

1. Kubectl CLI installed locally (Reference: https://kubernetes.io/docs/getting-started-guides/kubectl/)

   *Note*- Locking version to 1.5.1 at this time. Latest version as found here: https://console.cloud.google.com/storage/browser/kubernetes-release/release/?pli=1

   MacOS:  
   ```bash
   wget https://storage.googleapis.com/kubernetes-release/release/v1.5.1/bin/darwin/amd64/kubectl
   chmod +x kubectl
   mv kubectl /usr/local/bin/kubectl
   ```

   Linux:  
   ```bash
   wget https://storage.googleapis.com/kubernetes-release/release/v1.5.1/bin/linux/amd64/kubectl
   chmod +x kubectl
   mv kubectl /usr/local/bin/kubectl
   ```

2. Kops CLI installed locally (Reference: https://kubernetes.io/docs/getting-started-guides/kops/)

   *Note*- Substitute latest version as found here: https://github.com/kubernetes/kops/releases/latest
   
   MacOS:  
   ```bash
   wget https://github.com/kubernetes/kops/releases/download/v1.4.4/kops-darwin-amd64
   chmod +x kops-darwin-amd64
   mv kops-darwin-amd64 /usr/local/bin/kops
   ```

   Linux:  
   ```bash
   wget https://github.com/kubernetes/kops/releases/download/v1.4.4/kops-linux-amd64
   chmod +x kops-linux-amd64
   mv kops-linux-amd64 /usr/local/bin/kops
   ```
   
## Setup AWS resources

1. Create Route 53 Hosted Zone

   *Note*- Hosted Zone Name and caller reference must be unique!

   Caller reference:  
   > A unique string that identifies the request and that allows failed create-hosted-zone requests to be retried without the risk of executing the operation twice. You must use a unique CallerReference string every time you create a hosted zone. 
   
   ```bash
   aws route53 create-hosted-zone --name HOSTED-ZONE-NAME.stratus.com --caller-reference 1
   ```
   
2. Capture and submit output from previous step and send to administrator (Chris D.) to add to organization DNS records.

   *Note*- Do not proceed until confirmation has been received back!

3. Create S3 bucket

   *Note*- Substitute HOSTED-ZONE-NAME as needed.

   ```bash
   aws s3 mb s3://clusters.HOSTED-ZONE-NAME.stratus.com
   ```
   
4. Define S3 Store for Kops

   *Note*- Substitute HOSTED-ZONE-NAME as needed.

   ```bash
   export KOPS_STATE_STORE=s3://clusters.HOSTED-ZONE-NAME.stratus.com
   ```
   
## Create Kubernetes Cluster

1. Create cluster configuration

   *Note*- Substitute HOSTED-ZONE-NAME and EC2 zone as needed.

   ```bash
   kops create cluster --zones=us-east-1a HOSTED-ZONE-NAME.stratus.com
   ```
   
2. Build cluster

   *Note*- Substitute HOSTED-ZONE-NAME as needed.

   ```bash
   kops update cluster HOSTED-ZONE-NAME.stratus.com --yes
   ```

   *Note* - This step can take  up to 15 minutes to fully complete.
   
3. Verify cluster online

   ```bash
   kubectl get nodes --show-labels
   ```
   
   Expected Output:  
   >NAME                            STATUS    AGE       LABELS
   >ip-172-20-37-248.ec2.internal   Ready     3m        beta.kubernetes.io/arch=amd64,beta.kubernetes.io/instance-type=    t2.medium,beta.kubernetes.io/os=linux,failure-domain.beta.kubernetes.io/region=us-east-1,failure-domain.beta.kubern    etes.io/zone=us-east-1a,kubernetes.io/hostname=ip-172-20-37-248.ec2.internal
   >ip-172-20-58-164.ec2.internal   Ready     6m        beta.kubernetes.io/arch=amd64,beta.kubernetes.io/instance-type=    m3.medium,beta.kubernetes.io/os=linux,failure-domain.beta.kubernetes.io/region=us-east-1,failure-domain.beta.kubern    etes.io/zone=us-east-1a,kubernetes.io/hostname=ip-172-20-58-164.ec2.internal,kubernetes.io/role=master
   >ip-172-20-63-72.ec2.internal    Ready     3m        beta.kubernetes.io/arch=amd64,beta.kubernetes.io/instance-type=    t2.medium,beta.kubernetes.io/os=linux,failure-domain.beta.kubernetes.io/region=us-east-1,failure-domain.beta.kubern    etes.io/zone=us-east-1a,kubernetes.io/hostname=ip-172-20-63-72.ec2.internal

4. Upgrade cluster to latest version.
   
   *Note*- Substitute HOSTED-ZONE-NAME as needed.
   
   ```bash
   kops edit cluster HOSTED-ZONE-NAME.stratus.com
   ```
   
   Update `kubernetesVersion` to version 1.5.1.
   
   ```bash
   kops update cluster HOSTED-ZONE-NAME.stratus.com --yes
   kops rolling-update cluster HOSTED-ZONE-NAME.stratus.com --yes
   ```
   
   *Note* - This step can take  up to 15 minutes to fully complete. Do NOT interrupt!
   
5. Verify cluster available

   ```bash
   kubectl get nodes --show-labels
   ```
   
   Expected Output:  
   >NAME                            STATUS    AGE       LABELS
   >ip-172-20-37-248.ec2.internal   Ready     3m        beta.kubernetes.io/arch=amd64,beta.kubernetes.io/instance-type=    t2.medium,beta.kubernetes.io/os=linux,failure-domain.beta.kubernetes.io/region=us-east-1,failure-domain.beta.kubern    etes.io/zone=us-east-1a,kubernetes.io/hostname=ip-172-20-37-248.ec2.internal  
   >ip-172-20-58-164.ec2.internal   Ready     6m        beta.kubernetes.io/arch=amd64,beta.kubernetes.io/instance-type=    m3.medium,beta.kubernetes.io/os=linux,failure-domain.beta.kubernetes.io/region=us-east-1,failure-domain.beta.kubern    etes.io/zone=us-east-1a,kubernetes.io/hostname=ip-172-20-58-164.ec2.internal,kubernetes.io/role=master  
   >ip-172-20-63-72.ec2.internal    Ready     3m        beta.kubernetes.io/arch=amd64,beta.kubernetes.io/instance-type=    t2.medium,beta.kubernetes.io/os=linux,failure-domain.beta.kubernetes.io/region=us-east-1,failure-domain.beta.kubern    etes.io/zone=us-east-1a,kubernetes.io/hostname=ip-172-20-63-72.ec2.internal

6. Verify version deployed

   ```bash
   kubectl version
   ```
   
## Create Kubernetes Namespace

Reference found here: https://kubernetes.io/docs/user-guide/namespaces/

> Kubernetes supports multiple virtual clusters backed by the same physical cluster. These virtual clusters are called namespaces.

1. Show namespaces in use

   ```bash 
   kubectl get namespaces
   ```
   
2. Set up unique namespace

   *Note*- Substitute NAMESPACE-ID with a unique namespace name.
   
   ```bash
   kubectl create namespace NAMESPACE-ID
   ```
   
## Setup Kubernetes Dashboard

1. Install Dashboard

   ```bash
   kubectl create -f https://rawgit.com/kubernetes/dashboard/master/src/deploy/kubernetes-dashboard.yaml
   ```
   
2. Verify deployment

   ```bash
   kubectl get pods --all-namespaces | grep dashboard
   ```
   
3. Start proxy

   ```bash
   kubectl proxy &
   ```
  
4. Clone Heapster repo (https://github.com/kubernetes/heapster)

   ```bash
   git clone https://github.com/kubernetes/heapster.git
   cd heapster/
   ```

5. Install Heapster & InfluxDB

   ```bash
   kubectl create -f deploy/kube-config/influxdb/
   ```   

6. Login to Dashboard (https://127.0.0.1:8001)

   Login: admin  
   Password found in `~/.kube/config`
   
# Deploy using helm
See [../helm/README.md](helm) for deployment
