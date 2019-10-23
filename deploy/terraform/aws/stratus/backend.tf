# terraform {
#   backend "s3" {
#     bucket         = "terraform-463f53083d0d"
#     key            = "stratus/terraform.tfstate"
#     region         = "us-east-1"
#     encrypt        = "true"
#     dynamodb_table = "prod_stratus_tf_state_lock"
#   }
# }
