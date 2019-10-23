
data "http" "host_ip" {
  url = "http://icanhazip.com"
}

resource "aws_security_group" "terraform_host_redis" {
  name = "redis"
  ingress {
    from_port = 6379
    to_port = 6379
    protocol = "tcp"
    cidr_blocks = [
      "${chomp(data.http.host_ip.body)}/32"
    ]
  }  
}