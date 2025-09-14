#!/bin/bash
# Rebuild the homepage Docker image

# Variables
IMAGE_NAME="homepage"                  # Local Docker image name
DOCKERFILE="homepage.dockerfile"       # Dockerfile path
BUILD_CONTEXT="."                       # Build context (current directory)

# Rebuild the image
echo "Building Docker image $IMAGE_NAME..."
docker build -f $DOCKERFILE -t $IMAGE_NAME $BUILD_CONTEXT

# Notify user
if [ $? -eq 0 ]; then
    echo "Docker image $IMAGE_NAME built successfully!"
else
    echo "Failed to build Docker image $IMAGE_NAME."
fi
