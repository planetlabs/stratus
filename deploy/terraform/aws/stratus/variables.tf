variable "aws_region" {
  description = "The AWS region to create resources in"
  default     = "us-east-1"
}

variable "availability_zones" {
  default     = ["us-east-1a", "us-east-1b", "us-east-1c", "us-east-1d", "us-east-1e", "us-east-1f"]
  type        = "list"
  description = "List of availability zones"
}

variable "vpc_cidr" {
  default     = "10.12.0.0/16"
  description = "List of availability zones"
}

variable "key_name" {
  description = "Name of AWS key pair"
}

variable "ui_instance_type" {
  default     = "t2.medium"
  description = "Instance type to used for Geoserver REST/UI configuration access"
}

variable "ogc_instance_type" {
  default     = "t2.medium"
  description = "Instance type used for Geoserver OGC/Services access"
}

variable "min_instance_count" {
  description = "Min numbers of instances in the OGC/Services Autoscale group"
  default     = "2"
}

variable "max_instance_count" {
  description = "Max numbers of instances in the OGC/Services Autoscale group"
  default     = "20"
}

variable "lb_access_log_bucket_prefix" {
  description = "The S3 bucket name the ALB access logs will be stored in. A unique identifier will be appended to the bucket name"
  default     = "stratus-aws-alb-logs"
}

variable "lb_timeout" {
  description = "ALB Idle session timeout"
  default     = "60"
}

variable "lb_is_internal" {
  description = "Should the ALB be internal"
  default     = "false"
}

variable "lb_ssl_policy" {
  description = "The ALB TLS policy to use"
  default     = "ELBSecurityPolicy-TLS-1-2-2017-01"
}

variable "lb_https_only" {
  description = "Should the ALB use HTTPS. If set to true only the HTTPS listner will be created"
  default     = "false"
}

variable "lb_iam_cert_name" {
  description = "The IAM certificate name to use"
  default     = ""
}

variable "lb_acm_cert_domain" {
  description = "The ACM certificate domain name to use"
  default     = ""
}

variable "lb_health_endpoint" {
  description = "The Stratus endpoint the LB/target group should do health tests against"
  default     = "/geoserver/rest/manage/health"
}

variable "domain_name" {
  description = "Route53 domain name"
  default     = ""
}

variable "dns_name_prefix" {
  description = "The DNS name prefix that will be used with the domain name"
  default     = "stratus"
}

variable "env" {
  description = "Environment designation, defaults to DEV"
  default     = "dev"
}

variable "cache_node_count" {
  description = "The number of elasticache redis nodes"
  default     = "1"
}

variable "cache_instance_type" {
  description = "The elasticache redis instance type"
  default     = "cache.m4.large"
}

variable "cache_snapshot_retention_days" {
  description = "The elasticache redis instance type"
  default     = "35"
}

variable "cache_engine_version" {
  description = "The elasticache redis version to be used"
  default     = "3.2.10"
}

variable "cache_backup_window" {
  description = "The daily time range in which backup will be performed. Time is in UTC"
  default     = "03:00-04:00"
}

variable "cache_maintenance_window" {
  description = "The weekly time range in which AWS maintenance could be performed. Time is in UTC"
  default     = "sun:06:00-sun:07:00"
}

variable "db_performance_insights_enabled" {
  description = "If AWS performance insights should be abled on the RDS cluster"
  default     = "true"
}

variable "db_performance_insights_encryption_key_id" {
  description = "The AWS KMS Key ID to use with performance insights. When provided, performance insights is automatically enabled"
  default     = ""
}

variable "db_create_instance" {
  description = "If you would like to create an RDS instance"
  default     = "false"
}

variable "db_snapshot_id" {
  description = "ID of snapshot to initialize RDS instance with"
  default     = ""
}

variable "db_create_snapshot_on_termination" {
  description = "Should a snapshot be created when the database instance is terminated"
  default     = "false"
}

variable "db_is_internal" {
  description = "If the RDS instance should be created in private subnets, and set publicly_accessible set to false. Will not modify the secuirty groups"
  default     = "true"
}

variable "db_username" {
  description = "The database superuser username"
  default     = "stratus"
}

variable "db_password" {
  description = "The database superuser password"
  default     = "4%5xvx9Esr2*"
}

variable "db_name" {
  description = "The default database name to create"
  default     = "data"
}

variable "db_multi_az" {
  description = "Should the database instance be highly available"
  default     = "false"
}

variable "db_instance_type" {
  description = "The database instance type"
  default     = "db.t2.medium"
}

variable "db_engine" {
  description = "The database engine type/class"
  default     = "postgres"
}

variable "db_engine_version" {
  description = "The database engine version"
  default     = "9.6.6"
}

variable "db_monitoring_interval" {
  description = "Enhanced monitoring interval"
  default     = "15"
}

variable "db_backup_retention_days" {
  description = "Number of days to keep database snapshots"
  default     = "35"
}

variable "db_backup_window" {
  description = "The daily time range in which backup will be performed. Time is in UTC"
  default     = "03:00-03:30"
}

variable "db_maintenance_window" {
  description = "The weekly time range in which AWS maintenance could be performed. Time is in UTC"
  default     = "sun:06:00-sun:07:00"
}

variable "db_apply_changes_immediately" {
  description = "When configuration changes are made to the database instances, should those changes be performed in real-time, or only within the maintenance window"
  default     = "true"
}

variable "db_auto_minor_version_upgrade" {
  description = "Should AWS automatically upgrade the database instance when a new minor version is available (eg 9.6.1 -> 9.6.2). This is done within the maintenance window"
  default     = "true"
}

variable "db_allow_major_version_upgrade" {
  description = "Could the database instance be upgraded when a major version is released (eg 9.5 -> 9.6). The upgrade itself will have to be initiated manually"
  default     = "true"
}

variable "db_storage_size" {
  description = "The storage size of the database instance"
  default     = "100"
}

variable "db_cluster_node_count" {
  description = "The number of Aurora read replicas to create. When set to 0, RDS will be used instead of Aurora"
  default     = 0
}

variable "db_storage_encrypted" {
  description = "If the database storage device should be encrypted. Should specify db_encryption_key_id"
  default     = false
}

variable "db_encryption_key_id" {
  description = "The AWS KMS Key ID to encrypt database storage with. When provided, storage is automatically encrypted"
  default     = ""
}

variable "efs_type" {
  description = "The filesystem performance mode"
  default     = "generalPurpose"
}

variable "efs_create_file_system" {
  description = "If we should create a filesystem"
  default     = "false"
}

variable "efs_storage_encrypted" {
  description = "If the EFS volume should be encrypted. Should specify efs_encryption_key_id"
  default     = false
}

variable "efs_encryption_key_id" {
  description = "The AWS KMS Key ID to use to encrypt the EFS volume. When provided, EFS is automatically encrypted"
  default     = ""
}

variable "artifacts_bucket" {
  description = "The s3 bucket name that stores the the Stratus jar"
  default     = "your-s3-bucket-us-east-1"
}

variable "failover_artifacts_bucket" {
  description = "The failover s3 bucket name that stores the the Stratus jar"
  default     = "your-s3-bucket-us-west-1"
}

variable "ec_jar_bucket_path" {
  description = "The path to the Stratus jar in the artifacts_bucket S3 bucket"
  default     = "snapshot/stratus-application-1.1.0-SNAPSHOT-exec.jar"
}

variable "app_name" {
  description = "This name will be used for resource naming and tagging"
  default     = "stratus"
}

variable "bastion_instance_type" {
  default     = "t2.medium"
  description = "Instance type to used for public EC2 access"
}

variable "bastion_create_instance" {
  default     = true
  description = "Should a small public EC2 instance be built in this configuration"
}

variable "bastion_source_ips" {
  type        = "list"
  default     = ["0.0.0.0/0"]
  description = "A list of ip addresses that can access the public EC2 instance"
}

variable "extra_java_opts" {
  default     = "-Dstratus.catalog.redis.caching.enable-rest-caching=true -Dstratus.catalog.redis.caching.enable-ows-caching=true"
  description = "Specifiy extra java opts for Stratus to start with"
}

variable "vpc_peer_id" {
  default     = ""
  description = "The VPC ID of the VPC tp peer with the one created in this deployment. Must be in the same AWS account as this deployment"
}

variable "external_script" {
  default     = "s3://your-s3-bucket-us-east-1/default_jndi.py"
  description = "An external script (bash/python) that will run on the OGC and UI nodes prior to starting Stratus. Protocol can either be http/https or s3"
}

variable "iam_policy_document" {
  default     = ""
  description = "An additional IAM policy that will be associated with all EC2 instances"
}
