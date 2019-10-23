output "alb_dns_name" {
  value = "${aws_lb.main.dns_name}"
}

output "elasticache_dns_name" {
  value = "${aws_elasticache_replication_group.stratus.0.primary_endpoint_address}"
}

output "efs_dns_name" {
  value = "${var.efs_create_file_system ? join(" ", aws_efs_file_system.stratus.*.dns_name) : ""}"
}

output "rds_dns_name" {
  value = "${var.db_cluster_node_count == 0 ? join(" ", aws_db_instance.stratus.*.address) : ""}"
}

output "aurora_write_dns_name" {
  value = "${var.db_cluster_node_count > 0 ? join(" ", aws_rds_cluster.stratus.*.endpoint) : ""}"
}

output "aurora_read_dns_name" {
  value = "${var.db_cluster_node_count > 0 ? join(" ", aws_rds_cluster.stratus.*.reader_endpoint) : ""}"
}

output "ami_id" {
  value = "${data.aws_ami.amazon-linux.id}"
}

# output "dns_name" {
# value = "${var.dns_name_prefix}.${data.aws_route53_zone.public.name}"
# }

output "bastion_eip" {
  value = "${aws_eip.bastion.0.public_ip}"
}
