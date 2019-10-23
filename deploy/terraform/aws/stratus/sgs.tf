resource "aws_security_group" "alb" {
  name        = "${var.app_name}-alb-${var.env}-${random_id.identifier.hex}"
  description = "Stratus ALB"
  vpc_id      = "${aws_vpc.main.id}"

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-alb-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_security_group" "nodes" {
  name        = "${var.app_name}-nodes-${var.env}-${random_id.identifier.hex}"
  description = "Stratus nodes"
  vpc_id      = "${aws_vpc.main.id}"

  ingress {
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = ["${aws_security_group.alb.id}"]
    self            = true
  }

  ingress {
    from_port       = 22
    to_port         = 22
    protocol        = "tcp"
    security_groups = ["${aws_security_group.bastion.id}"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-nodes-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_security_group" "bastion" {
  count       = "${var.bastion_create_instance ? 1 : 0}"
  name        = "${var.app_name}-bastion-${var.env}-${random_id.identifier.hex}"
  description = "Stratus public EC2 instance"
  vpc_id      = "${aws_vpc.main.id}"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = "${var.bastion_source_ips}"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-bastion-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_security_group" "elasticache" {
  name        = "${var.app_name}-elasticache-${var.env}-${random_id.identifier.hex}"
  description = "Stratus Elasticache"
  vpc_id      = "${aws_vpc.main.id}"

  ingress {
    from_port       = 6379
    to_port         = 6379
    protocol        = "tcp"
    security_groups = ["${aws_security_group.nodes.id}", "${aws_security_group.bastion.id}" ]
    self            = true
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-elasticache-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_security_group" "rds" {
  name        = "${var.app_name}-rds-${var.env}-${random_id.identifier.hex}"
  count       = "${var.db_create_instance ? 1 : 0}"
  description = "Stratus RDS"
  vpc_id      = "${aws_vpc.main.id}"

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = ["${aws_security_group.nodes.id}", "${aws_security_group.bastion.id}"]
    self            = true
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-rds-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_security_group" "efs" {
  name        = "${var.app_name}-efs-${var.env}-${random_id.identifier.hex}"
  count       = "${var.efs_create_file_system ? 1 : 0}"
  description = "Stratus EFS"
  vpc_id      = "${aws_vpc.main.id}"

  ingress {
    from_port       = 2049
    to_port         = 2049
    protocol        = "tcp"
    security_groups = ["${aws_security_group.nodes.id}", "${aws_security_group.bastion.id}"]
    self            = true
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-efs-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}
