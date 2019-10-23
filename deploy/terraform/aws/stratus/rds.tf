resource "aws_db_instance" "stratus" {
  count = "${var.db_create_instance && var.db_cluster_node_count == 0 ? 1 : 0}"

  lifecycle {
    ignore_changes = ["username"]
  }

  allocated_storage           = "${var.db_storage_size}"
  apply_immediately           = "${var.db_apply_changes_immediately}"
  auto_minor_version_upgrade  = "${var.db_auto_minor_version_upgrade}"
  allow_major_version_upgrade = "${var.db_allow_major_version_upgrade}"
  backup_retention_period     = "${var.db_backup_retention_days}"
  backup_window               = "${var.db_backup_window}"
  copy_tags_to_snapshot       = true
  db_subnet_group_name        = "${aws_db_subnet_group.stratus.id}"
  engine                      = "${var.db_engine}"
  engine_version              = "${var.db_engine_version}"
  final_snapshot_identifier   = "${var.app_name}-${var.env}-${terraform.workspace}-${random_id.identifier.hex}-final"
  skip_final_snapshot         = "${var.db_create_snapshot_on_termination ? 0 : 1}"
  identifier                  = "${var.app_name}-${var.env}-${terraform.workspace}-${random_id.identifier.hex}"
  instance_class              = "${var.db_instance_type}"
  kms_key_id                  = "${length(var.db_encryption_key_id) > 0 ? var.db_encryption_key_id : "" }"
  maintenance_window          = "${var.db_maintenance_window}"
  monitoring_interval         = "${var.db_monitoring_interval}"
  monitoring_role_arn         = "${aws_iam_role.rds-monitoring.arn}"
  multi_az                    = "${var.db_multi_az}"
  storage_type                = "gp2"
  password                    = "${var.db_password}"
  parameter_group_name        = "${aws_db_parameter_group.stratus.id}"
  publicly_accessible         = "${var.db_is_internal ? 0 : 1}"
  storage_encrypted           = "${var.db_storage_encrypted || length(var.db_encryption_key_id) > 0 ? true : false}"
  snapshot_identifier         = "${length(var.db_snapshot_id) > 0 ? var.db_snapshot_id : "" }"
  username                    = "${var.db_username}"
  vpc_security_group_ids      = ["${aws_security_group.rds.id}"]

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_db_parameter_group" "stratus" {
  count       = "${var.db_create_instance ? 1 : 0}"
  name        = "${var.app_name}-${var.env}-${random_id.identifier.hex}"
  family      = "${var.db_cluster_node_count > 0 ? "aurora-postgresql9.6" : "postgres9.6"}"
  description = "Stratus RDS database parameter group"

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }

  parameter {
    name         = "max_parallel_workers_per_gather"
    value        = "8"
    apply_method = "immediate"
  }
}

resource "aws_db_subnet_group" "stratus" {
  count       = "${var.db_create_instance ? 1 : 0}"
  name        = "${var.app_name}-${var.env}-${random_id.identifier.hex}"
  description = "Stratus"
  subnet_ids  = ["${split(",", var.db_is_internal ? join(",", aws_subnet.private_subnets.*.id) : join(",", aws_subnet.public_subnets.*.id))}"]

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_iam_role" "rds-monitoring" {
  name        = "${var.app_name}-rds-monitoring-${var.env}-${terraform.workspace}-${random_id.identifier.hex}"
  description = "RDS Enhanced Monitoring"
  count       = "${var.db_create_instance ? 1 : 0}"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "monitoring.rds.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_iam_policy" "rds-monitoring" {
  count       = "${var.db_create_instance ? 1 : 0}"
  name        = "${var.app_name}-rds-enhanced-monitoring-${var.env}-${terraform.workspace}-${random_id.identifier.hex}"
  description = "rds-enhanced-monitoring"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
      {
          "Sid": "EnableCreationAndManagementOfRDSCloudwatchLogGroups",
          "Effect": "Allow",
          "Action": [
              "logs:CreateLogGroup",
              "logs:PutRetentionPolicy"
          ],
          "Resource": [
              "arn:aws:logs:*:*:log-group:RDS*"
          ]
      },
      {
          "Sid": "EnableCreationAndManagementOfRDSCloudwatchLogStreams",
          "Effect": "Allow",
          "Action": [
              "logs:CreateLogStream",
              "logs:PutLogEvents",
              "logs:DescribeLogStreams",
              "logs:GetLogEvents"
          ],
          "Resource": [
              "arn:aws:logs:*:*:log-group:RDS*:log-stream:*"
          ]
      }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "rds-monitoring" {
  count      = "${var.db_create_instance ? 1 : 0}"
  role       = "${aws_iam_role.rds-monitoring.name}"
  policy_arn = "${aws_iam_policy.rds-monitoring.arn}"
}

resource "aws_s3_bucket_object" "rds" {
  count      = "${var.db_create_instance && var.db_cluster_node_count == 0 ? 1 : 0}"
  key        = "rds_info.json"
  bucket     = "${var.lb_access_log_bucket_prefix}-${random_id.identifier.hex}"
  depends_on = ["aws_s3_bucket.alb-logging"]

  content = <<EOF
{
  "type": "rds",
  "endpoint": "${aws_db_instance.stratus.address}",
  "arn": "${aws_db_instance.stratus.arn}",
  "id": "${aws_db_instance.stratus.id}",
  "username": "${aws_db_instance.stratus.username}",
  "password": "${var.db_password}",
  "db_name": "${var.db_name}"
}
EOF
}

resource "aws_s3_bucket_object" "aurora" {
  count      = "${var.db_create_instance && var.db_cluster_node_count > 0 ? 1 : 0}"
  key        = "rds_info.json"
  bucket     = "${var.lb_access_log_bucket_prefix}-${random_id.identifier.hex}"
  depends_on = ["aws_s3_bucket.alb-logging"]

  content = <<EOF
{
  "type": "aurora",
  "read_endpoint": "${aws_rds_cluster.stratus.reader_endpoint}",
  "write_endpoint": "${aws_rds_cluster.stratus.endpoint}",
  "id": "${aws_rds_cluster.stratus.id}",
  "username": "${aws_rds_cluster.stratus.master_username}",
  "password": "${var.db_password}",
  "db_name": "${var.db_name}"
}
EOF
}

resource "aws_rds_cluster" "stratus" {
  apply_immediately               = "${var.db_apply_changes_immediately}"
  backup_retention_period         = "${var.db_backup_retention_days}"
  count                           = "${var.db_create_instance && var.db_cluster_node_count > 0 ? 1 : 0}"
  cluster_identifier              = "${var.app_name}-${var.env}-${terraform.workspace}-${random_id.identifier.hex}"
  db_subnet_group_name            = "${aws_db_subnet_group.stratus.id}"
  db_cluster_parameter_group_name = "${aws_rds_cluster_parameter_group.stratus.id}"
  engine                          = "aurora-postgresql"
  engine_version                  = "${var.db_engine_version}"
  final_snapshot_identifier       = "${var.app_name}-${var.env}-${terraform.workspace}-${random_id.identifier.hex}-final"
  kms_key_id                      = "${length(var.db_encryption_key_id) > 0 ? var.db_encryption_key_id : "" }"
  master_username                 = "${var.db_username}"
  master_password                 = "${var.db_password}"
  preferred_backup_window         = "${var.db_backup_window}"
  preferred_maintenance_window    = "${var.db_maintenance_window}"
  skip_final_snapshot             = "${var.db_create_snapshot_on_termination ? 0 : 1}"
  snapshot_identifier             = "${length(var.db_snapshot_id) > 0 ? var.db_snapshot_id : "" }"
  storage_encrypted               = "${var.db_storage_encrypted || length(var.db_encryption_key_id) > 0 ? true : false}"
  vpc_security_group_ids          = ["${aws_security_group.rds.id}"]
}

resource "aws_rds_cluster_instance" "stratus" {
  apply_immediately               = "${var.db_apply_changes_immediately}"
  auto_minor_version_upgrade      = "${var.db_auto_minor_version_upgrade}"
  count                           = "${var.db_create_instance ? var.db_cluster_node_count : 0}"
  cluster_identifier              = "${aws_rds_cluster.stratus.id}"
  db_subnet_group_name            = "${aws_db_subnet_group.stratus.id}"
  db_parameter_group_name         = "${aws_db_parameter_group.stratus.id}"
  engine                          = "aurora-postgresql"
  identifier                      = "${var.app_name}-${var.env}-${terraform.workspace}-${random_id.identifier.hex}-${count.index}"
  instance_class                  = "${var.db_instance_type}"
  monitoring_role_arn             = "${aws_iam_role.rds-monitoring.arn}"
  monitoring_interval             = "${var.db_monitoring_interval}"
  publicly_accessible             = "${var.db_is_internal ? 0 : 1}"
  performance_insights_enabled    = "${var.db_performance_insights_enabled || length(var.db_performance_insights_encryption_key_id) > 0 ? true : false}"
  performance_insights_kms_key_id = "${length(var.db_performance_insights_encryption_key_id) > 0 ? var.db_performance_insights_encryption_key_id : "" }"

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_rds_cluster_parameter_group" "stratus" {
  count       = "${var.db_create_instance && var.db_cluster_node_count > 0 ? 1 : 0}"
  description = "Stratus RDS cluster parameter group"
  name        = "${var.app_name}-${var.env}-cluster-${random_id.identifier.hex}"
  family      = "aurora-postgresql9.6"

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}
