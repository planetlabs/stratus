aws_region    us-east-1
availability_zones    us-east-1a
vpc_cidr    10.12.0.0/16
key_name    
ui_instance_type    t2.medium
ogc_instance_type    t2.medium
min_instance_count    2
max_instance_count    20
lb_access_log_bucket_prefix    stratus-aws-alb-logs
lb_timeout    60
lb_is_internal    FALSE
lb_ssl_policy    ELBSecurityPolicy-TLS-1-2-2017-01
lb_https_only    FALSE
lb_iam_cert_name    
lb_health_endpoint    /geoserver/rest/manage/health
domain_name    stratus
dns_name_prefix    Environment designation, defaults to DEV
env    dev
cache_node_count    1
cache_instance_type    cache.m4.large
cache_snapshot_retention_days    35
cache_engine_version    3.2.4
cache_backup_window    03:00-04:00
cache_maintenance_window    sun:06:00-sun:07:00
db_create_instance    FALSE
db_snapshot_id    
db_create_snapshot_on_termination    FALSE
db_is_internal    TRUE
db_username    stratus
db_password    4%5xvx9Esr2*
db_multi_az    FALSE
db_instance_type    db.t2.medium
db_engine    postgres
db_engine_version    9.6.3
db_monitoring_interval    15
db_backup_retention_days    35
db_backup_window    03:00-03:30
db_maintenance_window    sun:06:00-sun:07:00
db_apply_changes_immediately    TRUE
db_auto_minor_version_upgrade    TRUE
db_allow_major_version_upgrade    TRUE
db_storage_size    100
efs_type    generalPurpose
efs_create_file_system    FALSE
artifacts_bucket    your-s3-bucket-us-east-1
failover_artifacts_bucket    your-s3-bucket-us-west-1
ec_jar_bucket_path    snapshot/stratus-application-0.7.0-SNAPSHOT-exec.jar
app_name    stratus
bastion_instance_type    t2.medium
bastion_create_instance    TRUE
bastion_source_ips    0.0.0.0/0
extra_java_opts    -Dstratus.catalog.redis.caching.enable-rest-caching=true -Dstratus.catalog.redis.caching.enable-ows-caching=true
