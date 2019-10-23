resource "aws_efs_file_system" "stratus" {
  performance_mode = "${var.efs_type}"
  count            = "${var.efs_create_file_system ? 1 : 0}"
  encrypted        = "${var.efs_storage_encrypted || length(var.efs_encryption_key_id) > 0 ? true : false}"
  kms_key_id       = "${length(var.efs_encryption_key_id) > 0 ? var.efs_encryption_key_id : ""}"

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_efs_mount_target" "stratus" {
  count           = "${var.efs_create_file_system ? length(var.availability_zones) : 0}"
  file_system_id  = "${aws_efs_file_system.stratus.id}"
  subnet_id       = "${element(aws_subnet.private_subnets.*.id, count.index)}"
  security_groups = ["${aws_security_group.efs.id}"]
}

resource "aws_s3_bucket_object" "efs" {
  count      = "${var.efs_create_file_system ? 1 : 0}"
  key        = "efs_info.json"
  bucket     = "${var.lb_access_log_bucket_prefix}-${random_id.identifier.hex}"
  depends_on = ["aws_s3_bucket.alb-logging"]

  content = <<EOF
{
  "file_system_id": "${aws_efs_file_system.stratus.id}",
  "mount_points_ids": ["${join("\",\"", aws_efs_mount_target.stratus.*.id)}"],
  "dns_name": "${aws_efs_file_system.stratus.dns_name}"
}
EOF
}
