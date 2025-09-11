#!/bin/bash
set -euo pipefail

# === LOAD AWS CREDENTIALS FROM .ENV ===
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/../.env"

if [ ! -f "$ENV_FILE" ]; then
  echo "❌ Missing .env file with AWS credentials!"
  exit 1
fi

export $(grep -v '^#' "$ENV_FILE" | xargs)

# === CONFIGURATION ===
TASK_FAMILY="crewops-core"
REGION="${AWS_DEFAULT_REGION:-eu-north-1}"
OUTPUT_FILE="$SCRIPT_DIR/crewops-core-latest-task-definition.json"

# === FETCH LATEST TASK DEFINITION USING ECS CREDENTIALS ===
echo "👉 Fetching latest task definition for family: $TASK_FAMILY"

LATEST_TASK_DEF_ARN=$(AWS_ACCESS_KEY_ID="$AWS_ECS_ACCESS_KEY_ID" \
                      AWS_SECRET_ACCESS_KEY="$AWS_ECS_SECRET_ACCESS_KEY" \
                      AWS_DEFAULT_REGION="$REGION" \
                      aws ecs list-task-definitions \
                        --family-prefix "$TASK_FAMILY" \
                        --sort DESC \
                        --max-items 1 \
                        --query 'taskDefinitionArns[0]' \
                        --output text)

# Remove any trailing 'None' or whitespace
LATEST_TASK_DEF_ARN=$(echo "$LATEST_TASK_DEF_ARN" | sed 's/None//g' | tr -d '[:space:]')

if [ -z "$LATEST_TASK_DEF_ARN" ]; then
  echo "❌ No task definitions found for family $TASK_FAMILY"
  exit 1
fi

echo "✅ Latest task definition ARN: $LATEST_TASK_DEF_ARN"

# Convert to family:revision format
LATEST_TASK_DEF=$(basename "$LATEST_TASK_DEF_ARN")

# Describe task definition and save to file
AWS_ACCESS_KEY_ID="$AWS_ECS_ACCESS_KEY_ID" \
AWS_SECRET_ACCESS_KEY="$AWS_ECS_SECRET_ACCESS_KEY" \
AWS_DEFAULT_REGION="$REGION" \
aws ecs describe-task-definition \
  --task-definition "$LATEST_TASK_DEF" \
  --region "$REGION" \
  --query "taskDefinition" \
  --output json > "$OUTPUT_FILE"

echo "✅ Task definition saved to $OUTPUT_FILE"
