data "aws_availability_zones" "available" {}

resource "aws_vpc" "main" {
  cidr_block           = "${var.vpc_cidr}"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_subnet" "public_subnets" {
  count                   = "${length(var.availability_zones)}"
  cidr_block              = "${cidrsubnet(aws_vpc.main.cidr_block, 8, count.index + 1)}"
  availability_zone       = "${data.aws_availability_zones.available.names[count.index]}"
  vpc_id                  = "${aws_vpc.main.id}"
  map_public_ip_on_launch = true

  tags {
    Environment = "${var.env}"
    Name        = "public-${count.index}-${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_subnet" "private_subnets" {
  count                   = "${length(var.availability_zones)}"
  cidr_block              = "${cidrsubnet(aws_vpc.main.cidr_block, 8, count.index + length(var.availability_zones) +1)}"
  availability_zone       = "${data.aws_availability_zones.available.names[count.index]}"
  vpc_id                  = "${aws_vpc.main.id}"
  map_public_ip_on_launch = false

  tags {
    Environment = "${var.env}"
    Name        = "private-${count.index}-${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_internet_gateway" "igw" {
  vpc_id = "${aws_vpc.main.id}"

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_nat_gateway" "nat" {
  allocation_id = "${aws_eip.nat.id}"
  subnet_id     = "${aws_subnet.public_subnets.0.id}"
}

resource "aws_vpc_endpoint" "s3" {
  vpc_id          = "${aws_vpc.main.id}"
  service_name    = "com.amazonaws.${var.aws_region}.s3"
  route_table_ids = ["${aws_route_table.private.id}", "${aws_route_table.public.id}"]
}

resource "aws_route_table" "public" {
  vpc_id = "${aws_vpc.main.id}"

  tags {
    Environment = "${var.env}"
    Name        = "public-${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_route_table" "private" {
  vpc_id = "${aws_vpc.main.id}"

  tags {
    Environment = "${var.env}"
    Name        = "private-${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_route" "public-igw" {
  route_table_id         = "${aws_route_table.public.id}"
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = "${aws_internet_gateway.igw.id}"
}

resource "aws_route" "private-ngw" {
  route_table_id         = "${aws_route_table.private.id}"
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = "${aws_nat_gateway.nat.id}"
}

resource "aws_route" "public-pcx" {
  count                     = "${var.vpc_peer_id != "" ? 1 : 0}"
  route_table_id            = "${aws_route_table.public.id}"
  destination_cidr_block    = "${data.aws_vpc.peer.cidr_block}"
  vpc_peering_connection_id = "${aws_vpc_peering_connection.stratus.id}"
}

resource "aws_route" "private-pcx" {
  count                     = "${var.vpc_peer_id != "" ? 1 : 0}"
  route_table_id            = "${aws_route_table.private.id}"
  destination_cidr_block    = "${data.aws_vpc.peer.cidr_block}"
  vpc_peering_connection_id = "${aws_vpc_peering_connection.stratus.id}"
}

# Add a route to the main route table in the peer vpc.
# Will only have an effect if subnets are not associated with any route table, or are explicitly associated with the main route able
resource "aws_route" "peer-main-pcx" {
  count                     = "${var.vpc_peer_id != "" ? 1 : 0}"
  route_table_id            = "${data.aws_route_table.peer-main.id}"
  destination_cidr_block    = "${aws_vpc.main.cidr_block}"
  vpc_peering_connection_id = "${aws_vpc_peering_connection.stratus.id}"
}

data "aws_vpc" "peer" {
  count = "${var.vpc_peer_id != "" ? 1 : 0}"
  id    = "${var.vpc_peer_id}"
}

data "aws_route_table" "peer-main" {
  count  = "${var.vpc_peer_id != "" ? 1 : 0}"
  vpc_id = "${data.aws_vpc.peer.id}"

  filter {
    name = "association.main"

    values = [
      "true",
    ]
  }
}

resource "aws_vpc_peering_connection" "stratus" {
  count       = "${var.vpc_peer_id != "" ? 1 : 0}"
  peer_vpc_id = "${data.aws_vpc.peer.id}"
  vpc_id      = "${aws_vpc.main.id}"
  auto_accept = true

  accepter {
    allow_remote_vpc_dns_resolution = true
  }

  requester {
    allow_remote_vpc_dns_resolution = true
  }

  tags {
    Environment = "${var.env}"
    Name        = "${var.app_name}-${terraform.workspace}-${var.env}-${random_id.identifier.hex}"
    Project     = "${terraform.workspace}"
    Application = "${var.app_name}"
    deploy_id   = "${random_id.identifier.hex}"
  }
}

resource "aws_route_table_association" "public" {
  count          = "${length(var.availability_zones)}"
  subnet_id      = "${element(aws_subnet.public_subnets.*.id, count.index)}"
  route_table_id = "${aws_route_table.public.id}"
}

resource "aws_route_table_association" "private" {
  count          = "${length(var.availability_zones)}"
  subnet_id      = "${element(aws_subnet.private_subnets.*.id, count.index)}"
  route_table_id = "${aws_route_table.private.id}"
}

resource "aws_eip" "nat" {
  vpc = true
}
