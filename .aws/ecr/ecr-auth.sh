#!/bin/bash
# Authenticate Docker with AWS ECR

# Fail on error
set -e

# Set your AWS region (change if needed)
AWS_REGION="eu-central-1"

# Get your AWS Account ID dynamically
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query "Account" --output text)

echo "Logging in to Amazon ECR in region: $AWS_REGION (account: $AWS_ACCOUNT_ID)"

aws ecr get-login-password --region $AWS_REGION \
  | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

echo "✅ Successfully logged in to Amazon ECR"
