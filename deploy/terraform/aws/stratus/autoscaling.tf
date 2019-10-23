data "aws_ami" "amazon-linux" {
  most_recent = true
  name_regex  = "-gp2$"
  owners      = ["137112412989"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }
}

resource "aws_autoscaling_group" "ui" {
  vpc_zone_identifier  = ["${aws_subnet.private_subnets.*.id}"]
  name                 = "${var.app_name}-ui-${var.env}-${random_id.identifier.hex}"
  max_size             = 1
  min_size             = 1
  desired_capacity     = 1
  force_delete         = true
  default_cooldown     = 240
  launch_configuration = "${aws_launch_configuration.ui.name}"
  target_group_arns    = ["${aws_lb_target_group.ui.arn}"]
  termination_policies = ["NewestInstance"]

  depends_on = [
    "aws_elasticache_replication_group.stratus",
    "aws_efs_mount_target.stratus",
    "aws_db_instance.stratus",
    "aws_rds_cluster_instance.stratus",
  ]

  tags = [
    {
      key                 = "Name"
      value               = "${var.app_name}-ui-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
      propagate_at_launch = "true"
    },
    {
      key                 = "Environment"
      value               = "${var.env}"
      propagate_at_launch = "true"
    },
    {
      key                 = "Project"
      value               = "${terraform.workspace}"
      propagate_at_launch = "true"
    },
    {
      key                 = "Application"
      value               = "${var.app_name}"
      propagate_at_launch = "true"
    },
    {
      key                 = "deploy_id"
      value               = "${random_id.identifier.hex}"
      propagate_at_launch = "true"
    },
  ]
}

resource "aws_autoscaling_group" "ogc" {
  vpc_zone_identifier   = ["${aws_subnet.private_subnets.*.id}"]
  depends_on            = ["aws_autoscaling_group.ui"]
  name                  = "${var.app_name}-ogc-${var.env}-${random_id.identifier.hex}"
  max_size              = "${var.max_instance_count}"
  min_size              = "${var.min_instance_count}"
  force_delete          = true
  default_cooldown      = 240
  launch_configuration  = "${aws_launch_configuration.ogc.name}"
  target_group_arns     = ["${aws_lb_target_group.ogc.arn}"]
  termination_policies  = ["NewestInstance"]
  wait_for_elb_capacity = 0

  tags = [
    {
      key                 = "Name"
      value               = "${var.app_name}-ogc-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
      propagate_at_launch = "true"
    },
    {
      key                 = "Environment"
      value               = "${var.env}"
      propagate_at_launch = "true"
    },
    {
      key                 = "Project"
      value               = "${terraform.workspace}"
      propagate_at_launch = "true"
    },
    {
      key                 = "Application"
      value               = "${var.app_name}"
      propagate_at_launch = "true"
    },
    {
      key                 = "deploy_id"
      value               = "${random_id.identifier.hex}"
      propagate_at_launch = "true"
    },
  ]
}

resource "aws_launch_configuration" "ui" {
  name                 = "${var.app_name}-ui-${var.env}-${replace(uuid(), "-", "")}"
  image_id             = "${data.aws_ami.amazon-linux.id}"
  iam_instance_profile = "${aws_iam_instance_profile.geoserver.name}"
  instance_type        = "${var.ui_instance_type}"
  security_groups      = ["${aws_security_group.nodes.id}"]
  user_data            = "${data.template_file.userdata-ui.rendered}"
  key_name             = "${var.key_name}"
  enable_monitoring    = true

  lifecycle {
    create_before_destroy = true
    ignore_changes        = ["name"]
  }

  root_block_device {
    volume_size = "40"
    volume_type = "gp2"
  }
}

resource "aws_launch_configuration" "ogc" {
  name                 = "${var.app_name}-ogc-${var.env}-${replace(uuid(), "-", "")}"
  image_id             = "${data.aws_ami.amazon-linux.id}"
  iam_instance_profile = "${aws_iam_instance_profile.geoserver.name}"
  instance_type        = "${var.ogc_instance_type}"
  security_groups      = ["${aws_security_group.nodes.id}"]
  user_data            = "${data.template_file.userdata-ogc.rendered}"
  key_name             = "${var.key_name}"
  enable_monitoring    = true

  lifecycle {
    create_before_destroy = true
    ignore_changes        = ["name"]
  }

  root_block_device {
    volume_size = "40"
    volume_type = "gp2"
  }
}

resource "aws_autoscaling_policy" "up" {
  name                      = "${var.app_name}-ogc-up-${var.env}-${random_id.identifier.hex}"
  adjustment_type           = "ChangeInCapacity"
  policy_type               = "StepScaling"
  autoscaling_group_name    = "${aws_autoscaling_group.ogc.name}"
  metric_aggregation_type   = "Average"
  estimated_instance_warmup = 120

  step_adjustment {
    scaling_adjustment          = 1
    metric_interval_upper_bound = 10.0
  }

  step_adjustment {
    scaling_adjustment          = 2
    metric_interval_lower_bound = 10.0
    metric_interval_upper_bound = 20.0
  }

  step_adjustment {
    scaling_adjustment          = 4
    metric_interval_lower_bound = 20
  }
}

resource "aws_autoscaling_policy" "down" {
  name                   = "${var.app_name}-ogc-down-${var.env}-${random_id.identifier.hex}"
  scaling_adjustment     = -1
  adjustment_type        = "ChangeInCapacity"
  cooldown               = 240
  autoscaling_group_name = "${aws_autoscaling_group.ogc.name}"
}

resource "aws_cloudwatch_metric_alarm" "cpu-high" {
  alarm_name          = "${var.app_name}-cpu-high-${var.env}-${random_id.identifier.hex}"
  comparison_operator = "GreaterThanOrEqualToThreshold"
  evaluation_periods  = "1"
  metric_name         = "CPUUtilization"
  namespace           = "AWS/EC2"
  period              = "60"
  statistic           = "Average"
  threshold           = "60"
  alarm_description   = "This metric monitors ec2 cpu utilization"
  alarm_actions       = ["${aws_autoscaling_policy.up.arn}"]

  dimensions {
    AutoScalingGroupName = "${aws_autoscaling_group.ogc.name}"
  }
}

resource "aws_cloudwatch_metric_alarm" "cpu-low" {
  alarm_name          = "${var.app_name}-cpu-low-${var.env}-${random_id.identifier.hex}"
  comparison_operator = "LessThanOrEqualToThreshold"
  evaluation_periods  = "2"
  metric_name         = "CPUUtilization"
  namespace           = "AWS/EC2"
  period              = "900"
  statistic           = "Average"
  threshold           = "40"
  alarm_description   = "This metric monitors ec2 cpu utilization"
  alarm_actions       = ["${aws_autoscaling_policy.down.arn}"]

  dimensions {
    AutoScalingGroupName = "${aws_autoscaling_group.ogc.name}"
  }
}
