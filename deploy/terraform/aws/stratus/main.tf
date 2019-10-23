# Specify the provider and access details

provider "aws" {
  region = "${var.aws_region}"
}

resource "random_id" "identifier" {
  byte_length = 3
}

resource "aws_iam_instance_profile" "geoserver" {
  name = "${var.app_name}-${var.env}-${random_id.identifier.hex}"
  role = "${aws_iam_role.stratus.name}"
}

resource "aws_iam_role" "stratus" {
  name = "${var.app_name}-${var.env}-${random_id.identifier.hex}"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "",
      "Effect": "Allow",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
}

data "template_file" "userdata-ui" {
  template = "${file("${path.module}/node-userdata.sh")}"

  vars {
    deploy_id                      = "${random_id.identifier.hex}"
    node_name_prefix               = "${var.app_name}-ui-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    artifacts_bucket_name          = "${var.artifacts_bucket}"
    failover_artifacts_bucket_name = "${var.failover_artifacts_bucket}"
    jar_bucket_path                = "${var.ec_jar_bucket_path}"
    logging_bucket_name            = "${var.lb_access_log_bucket_prefix}-${random_id.identifier.hex}"
    node_type                      = "writer"
    java_opts                      = "${var.extra_java_opts} -Dstratus.admin-enabled=true"
    external_script                = "${var.external_script}"
  }
}

data "template_file" "userdata-ogc" {
  template = "${file("${path.module}/node-userdata.sh")}"

  vars {
    deploy_id                      = "${random_id.identifier.hex}"
    node_name_prefix               = "${var.app_name}-ogc-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    artifacts_bucket_name          = "${var.artifacts_bucket}"
    failover_artifacts_bucket_name = "${var.failover_artifacts_bucket}"
    jar_bucket_path                = "${var.ec_jar_bucket_path}"
    logging_bucket_name            = "${var.lb_access_log_bucket_prefix}-${random_id.identifier.hex}"
    node_type                      = "reader"
    java_opts                      = "${var.extra_java_opts} -Dstratus.admin-enabled=false"
    external_script                = "${var.external_script}"
  }
}

data "template_file" "userdata-bastion" {
  template = "${file("${path.module}/bastion-userdata.sh")}"

  vars {
    logging_bucket_name = "${var.lb_access_log_bucket_prefix}-${random_id.identifier.hex}"
    deploy_id           = "${random_id.identifier.hex}"
    node_name_prefix    = "${var.app_name}-bastion-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
  }
}

data "template_file" "logging_bucket_policy" {
  template = "${file("${path.module}/s3_bucket_policy.json")}"

  vars {
    bucket_name = "${var.lb_access_log_bucket_prefix}-${random_id.identifier.hex}"
  }
}

data "template_file" "ec2_iam_role" {
  template = "${file("${path.module}/ec2_iam_role.json")}"

  vars {
    artifacts_bucket_name          = "${var.artifacts_bucket}"
    failover_artifacts_bucket_name = "${var.failover_artifacts_bucket}"
    logging_bucket_name            = "${var.lb_access_log_bucket_prefix}-${random_id.identifier.hex}"
    deploy_id                      = "${random_id.identifier.hex}"
  }
}

resource "aws_iam_role_policy" "instance" {
  name   = "${var.app_name}-${var.env}-${random_id.identifier.hex}"
  role   = "${aws_iam_role.stratus.name}"
  policy = "${data.template_file.ec2_iam_role.rendered}"
}

resource "aws_iam_role_policy" "extra-policy" {
  count  = "${length(var.iam_policy_document) > 0 ? 1 : 0}"
  name   = "${var.app_name}-${var.env}-${random_id.identifier.hex}-extra-policy"
  role   = "${aws_iam_role.stratus.name}"
  policy = "${var.iam_policy_document}"
}

resource "aws_s3_bucket" "alb-logging" {
  bucket        = "${var.lb_access_log_bucket_prefix}-${random_id.identifier.hex}"
  acl           = "private"
  policy        = "${data.template_file.logging_bucket_policy.rendered}"
  force_destroy = "true"

  versioning {
    enabled = true
  }
}
