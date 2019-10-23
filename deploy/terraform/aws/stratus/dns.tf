data "aws_route53_zone" "public" {
  name  = "${var.domain_name}."
  count = "${var.domain_name == "" ? 0 : 1}"
}

resource "aws_route53_record" "stratus-alb" {
  count   = "${var.domain_name == "" ? 0 : 1}"
  zone_id = "${data.aws_route53_zone.public.zone_id}"
  name    = "${var.dns_name_prefix}.${data.aws_route53_zone.public.name}"
  type    = "A"

  alias {
    name                   = "${aws_lb.main.dns_name}"
    zone_id                = "${aws_lb.main.zone_id}"
    evaluate_target_health = true
  }
}
