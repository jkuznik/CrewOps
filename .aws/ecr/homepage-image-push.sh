#!/bin/bash
# Push homepage Docker image to AWS ECR with a random hash tag
# Automatically fetches AWS account ID and region from your config

# Set variables
REPO_NAME="jkuznik-ecr/homepage"
LOCAL_IMAGE="homepage:latest"

# Fetch AWS account ID
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)

# Fetch AWS default region from configuration
AWS_REGION=$(aws configure get region)
if [ -z "$AWS_REGION" ]; then
  AWS_REGION="us-east-1"  # fallback if not set
fi

# Generate random 7-character hex string (like Git short hash)
HASH_TAG=$(openssl rand -hex 4)  # 8 hex chars

# Full ECR image URI
ECR_IMAGE="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$REPO_NAME:$HASH_TAG"

# Authenticate Docker with ECR
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

# Tag the local image with the random hash
docker tag $LOCAL_IMAGE $ECR_IMAGE

# Push the image to ECR
docker push $ECR_IMAGE

# Print info
echo "Pushed $LOCAL_IMAGE to $ECR_IMAGE"
