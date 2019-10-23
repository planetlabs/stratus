resource "aws_instance" "bastion" {
  subnet_id                   = "${aws_subnet.public_subnets.0.id}"
  count                       = "${var.bastion_create_instance ? 1 : 0}"
  ami                         = "${data.aws_ami.amazon-linux.id}"
  instance_type               = "${var.bastion_instance_type}"
  key_name                    = "${var.key_name}"
  iam_instance_profile        = "${aws_iam_instance_profile.geoserver.name}"
  monitoring                  = true
  vpc_security_group_ids      = ["${aws_security_group.bastion.id}"]
  user_data                   = "${data.template_file.userdata-bastion.rendered}"
  associate_public_ip_address = true
  depends_on                  = ["aws_autoscaling_group.ogc"]

  lifecycle {
    create_before_destroy = true
  }

  root_block_device {
    volume_size = "10"
    volume_type = "gp2"
  }

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-bastion-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_eip" "bastion" {
  count    = "${var.bastion_create_instance ? 1 : 0}"
  vpc      = true
  instance = "${aws_instance.bastion.id}"
}
