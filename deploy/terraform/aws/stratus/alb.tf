resource "aws_lb_target_group" "ui" {
  name                 = "${var.app_name}-ui-${var.env}-${random_id.identifier.hex}"
  port                 = 8080
  protocol             = "HTTP"
  vpc_id               = "${aws_vpc.main.id}"
  deregistration_delay = 40

  health_check {
    interval            = 20
    path                = "${var.lb_health_endpoint}"
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 3
    matcher             = "200"
  }

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_lb_target_group" "ogc" {
  name                 = "${var.app_name}-ogc-${var.env}-${random_id.identifier.hex}"
  port                 = 8080
  protocol             = "HTTP"
  vpc_id               = "${aws_vpc.main.id}"
  deregistration_delay = 40

  health_check {
    interval            = 20
    path                = "${var.lb_health_endpoint}"
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 3
    matcher             = "200"
  }

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_lb" "main" {
  name            = "${var.app_name}-${var.env}-${random_id.identifier.hex}"
  internal        = "${var.lb_is_internal}"
  idle_timeout    = "${var.lb_timeout}"
  subnets         = ["${split(",", var.lb_is_internal ? join(",", aws_subnet.private_subnets.*.id) : join(",", aws_subnet.public_subnets.*.id))}"]
  security_groups = ["${aws_security_group.alb.id}"]

  access_logs {
    bucket = "${var.lb_access_log_bucket_prefix}-${random_id.identifier.hex}"
  }

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_lb_listener" "http" {
  count             = "${var.lb_https_only && var.lb_acm_cert_domain != "" ? 0 : 1}"
  load_balancer_arn = "${aws_lb.main.id}"
  port              = "80"
  protocol          = "HTTP"

  default_action {
    target_group_arn = "${aws_lb_target_group.ogc.id}"
    type             = "forward"
  }
}

resource "aws_lb_listener_rule" "rest_http" {
  count        = "${var.lb_https_only && var.lb_acm_cert_domain != "" ? 0 : 1}"
  listener_arn = "${aws_lb_listener.http.arn}"
  priority     = 1

  action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.ui.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/geoserver/rest*"]
  }
}

resource "aws_lb_listener_rule" "gwc-rest_http" {
  count        = "${var.lb_https_only && var.lb_acm_cert_domain != "" ? 0 : 1}"
  listener_arn = "${aws_lb_listener.http.arn}"
  priority     = 3

  action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.ui.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/geoserver/gwc/rest*"]
  }
}

resource "aws_lb_listener_rule" "j_spring_security_check_http" {
  count        = "${var.lb_https_only && var.lb_acm_cert_domain != "" ? 0 : 1}"
  listener_arn = "${aws_lb_listener.http.arn}"
  priority     = 4

  action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.ui.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/geoserver/j_spring_security_check"]
  }
}

resource "aws_lb_listener_rule" "j_spring_security_logout_http" {
  count        = "${var.lb_https_only && var.lb_acm_cert_domain != "" ? 0 : 1}"
  listener_arn = "${aws_lb_listener.http.arn}"
  priority     = 5

  action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.ui.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/geoserver/j_spring_security_logout"]
  }
}

resource "aws_lb_listener_rule" "web_http" {
  count        = "${var.lb_https_only && var.lb_acm_cert_domain != "" ? 0 : 1}"
  listener_arn = "${aws_lb_listener.http.arn}"
  priority     = 2

  action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.ui.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/geoserver/web*"]
  }
}

# Need to figure out how to use both ACM and IAM certificate without duplicating the listener and listener rules
data "aws_iam_server_certificate" "cert" {
  count  = "${var.lb_iam_cert_name != "" ? 1 : 0}"
  name   = "${var.lb_iam_cert_name}"
  latest = true
}

data "aws_acm_certificate" "cert" {
  count    = "${var.lb_acm_cert_domain != "" ? 1 : 0}"
  domain   = "${var.lb_acm_cert_domain}"
  statuses = ["ISSUED"]
}

resource "aws_lb_listener" "https" {
  count             = "${var.lb_acm_cert_domain != "" ? 1 : 0}"
  load_balancer_arn = "${aws_lb.main.id}"
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "${var.lb_ssl_policy}"
  certificate_arn   = "${data.aws_acm_certificate.cert.arn}"

  default_action {
    target_group_arn = "${aws_lb_target_group.ogc.id}"
    type             = "forward"
  }
}

resource "aws_lb_listener_rule" "rest_https" {
  count        = "${var.lb_acm_cert_domain != "" ? 1 : 0}"
  listener_arn = "${aws_lb_listener.https.arn}"
  priority     = 1

  action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.ui.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/geoserver/rest*"]
  }
}

resource "aws_lb_listener_rule" "gwc-rest_https" {
  count        = "${var.lb_acm_cert_domain != "" ? 1 : 0}"
  listener_arn = "${aws_lb_listener.https.arn}"
  priority     = 3

  action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.ui.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/geoserver/gwc/rest*"]
  }
}

resource "aws_lb_listener_rule" "j_spring_security_check_https" {
  count        = "${var.lb_acm_cert_domain != "" ? 1 : 0}"
  listener_arn = "${aws_lb_listener.https.arn}"
  priority     = 4

  action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.ui.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/geoserver/j_spring_security_check"]
  }
}

resource "aws_lb_listener_rule" "j_spring_security_logout_https" {
  count        = "${var.lb_acm_cert_domain != "" ? 1 : 0}"
  listener_arn = "${aws_lb_listener.https.arn}"
  priority     = 5

  action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.ui.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/geoserver/j_spring_security_logout"]
  }
}

resource "aws_lb_listener_rule" "web_https" {
  count        = "${var.lb_acm_cert_domain != "" ? 1 : 0}"
  listener_arn = "${aws_lb_listener.https.arn}"
  priority     = 2

  action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.ui.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["/geoserver/web*"]
  }
}
