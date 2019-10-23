## Installation
Please download the latest version of Terraform from the [download website](https://www.terraform.io/downloads.html). 
To make you it easier, please make sure the executable is in your path, either by extracting to a known location, or by symlinking. 
**NOTE**: The minimum supported Terraform version is 0.10.8, and the minimum supported AWS provider version is 1.7.0

Once extracted, you should set up your [AWS credentials](http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html). You may also use an EC2 instance with an IAM role associated with it instead of configuring and managing your AWS credentials.  
However, at a minimum, you should make sure to, not open any ports in the security group to the public. Open SSH only to your IP.

## Architecture
The following architecture is what the Terraform scripting will create:

![AWS Architecture](STRATUS_terraform_aws_alb.png)

## Configuration
When working in production, you should store your state file in S3 instead of locally.
This will allow you, or a different person to work/update the deployment, and can provide a way to restore a corrupt/deleted state.  
The S3 bucket, should, at a minimum be versioned, and if you need/want to be able to work when one AWS region becomes unavailable, you should configure replication to a secondary region.  
See [backend](aws/stratus/backend.tf) for an example on how to configure an S3 backend. Terraform documentation is [here](https://www.terraform.io/docs/backends/types/s3.html).  

Once the backend is configured, cd to the ```deploy/terraform/aws/stratus``` directory, or by providing the path when running the `terraform init` command below: 
Once the backend is configured you will have to run 
```
terraform init -reconfigure -backend=true
``` 

When using an S3 backend, you should also create a workspace, instead of working with the default workspace.  
This can be done by running 
```
terraform workspace new internal
```
from within the ```deploy/terraform/aws/stratus directory```  
For more information, see [Terraform Documentaion](https://www.terraform.io/docs/state/workspaces.html)

## Create AWS resources / Stratus cluster
The simplest way to get started, would be to run 
```
terraform apply -var key_name=ssh_key_name
```  
Where ```ssh_key_name``` is an existing key pair in the region the resources will be created in.  
This will create a working environment, however the sizing of the AWS resources will only be appropriate for basic testing, or low load cases. 

To see all the available variables you could change, look at the [variables](aws/stratus/variables.tf) file.  
You should not make any changes directly to this file, instead, you should either override the variables via the command line, or by providing an override file 

### Command line parameters

Variables can be specified by command line, although the development of profiles with ```.tfvars``` file is recommended for Stratus
```
terraform apply -var lb_https_only=true \
    -var lb_iam_cert_name=iam_cert_name \
    -var key_name=ssh_key_name \
    -var domain_name=route53_domain_name \
    -var dns_name_prefix=stratus-test \
    -var ogc_instance_type=m4.large \
    -var cache_node_count=2 \
    -var db_create_instance=true \
    -var efs_create_file_system=true
```

Where:  
```iam_cert_name``` is the HTTPS server certificate imported to IAM  
```ssh_key_name``` is an existing key pair in the region the resources will be created in  
```route53_domain_name``` is the public/private route 53 domain you would like to create a DNS record in to point at the ALB  

### Parameter override file

Instead of specifying variables as command line options it is also possible to use an external file to store the variables, and use that file as input
As an example, you could create a file named ```/opt/var-override.tfvars``` 

And add the following variables: 

```
lb_https_only=true
lb_iam_cert_name="iam_cert_name"
key_name="ssh_key_name"
domain_name="route53_domain_name"
dns_name_prefix="stratus-test"
ogc_instance_type="m4.large"
cache_node_count=2
db_create_instance=true
efs_create_file_system=true
``` 
Which can be deployed with ```terraform apply -var-file=/opt/var-override.tfvars``` 

### Getting started with select profiles
As we develop profiles with specific configurations, we will stash these profiles here. Currently we have:

* [profile_default.tfvars](aws/stratus/overrides/profile_default.tfvars) - A snapshot of default variable values to use as a starting point
* [profile_jenkins_deploy.tfvars](aws/stratus/overrides/profile_jenkins_deploy.tfvars) - A profile used by the Stratus jenkins deployment
* [profile_minimum.tfvars](aws/stratus/overrides/profile_minimum.tfvars) - A profile using the minimum system requirements
