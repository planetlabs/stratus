resource "aws_elasticache_parameter_group" "stratus" {
  name   = "${var.app_name}-${var.env}-${random_id.identifier.hex}"
  family = "redis3.2"
}

resource "aws_elasticache_subnet_group" "stratus" {
  name       = "${var.app_name}-${var.env}-${random_id.identifier.hex}"
  subnet_ids = ["${aws_subnet.private_subnets.*.id}"]
}

resource "aws_elasticache_replication_group" "stratus" {
  count                         = 1
  replication_group_id          = "stratus-${random_id.identifier.hex}"
  replication_group_description = "Stratus Cluster"
  node_type                     = "${var.cache_instance_type}"
  automatic_failover_enabled    = "${var.cache_node_count > 1 ? true : false}"
  auto_minor_version_upgrade    = true
  engine_version                = "${var.cache_engine_version}"
  parameter_group_name          = "${aws_elasticache_parameter_group.stratus.id}"
  port                          = 6379
  subnet_group_name             = "${aws_elasticache_subnet_group.stratus.name}"
  security_group_ids            = ["${aws_security_group.elasticache.id}"]
  maintenance_window            = "${var.cache_maintenance_window}"
  snapshot_window               = "${var.cache_backup_window}"
  snapshot_retention_limit      = "${var.cache_snapshot_retention_days}"
  apply_immediately             = true
  number_cache_clusters         = "${var.cache_node_count}"

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_s3_bucket_object" "elasticache_replication_group" {
  key        = "elasticache_info.json"
  bucket     = "${var.lb_access_log_bucket_prefix}-${random_id.identifier.hex}"
  depends_on = ["aws_s3_bucket.alb-logging"]

  content = <<EOF
{
  "primary_endpoint": "${aws_elasticache_replication_group.stratus.primary_endpoint_address}",
  "id": "${aws_elasticache_replication_group.stratus.id}"
}
EOF
}
