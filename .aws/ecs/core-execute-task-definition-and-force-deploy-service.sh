#!/bin/bash
set -euo pipefail

# === LOAD AWS CREDENTIALS FROM .ENV ===
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/../.env"

if [ ! -f "$ENV_FILE" ]; then
  echo "❌ Missing .env file with AWS credentials!"
  exit 1
fi

# Export variables from .env
export $(grep -v '^#' "$ENV_FILE" | xargs)

# === CONFIGURATION ===
REGION="${AWS_DEFAULT_REGION:-eu-north-1}"
CLUSTER_NAME="crewopc-basic-cluster"
SERVICE_NAME="crewops-core-service"
TASK_FAMILY="crewops-core"

# Local task definition JSON file
TASK_FILE="$SCRIPT_DIR/crewops-core-latest-task-definition.json"
CLEAN_TASK_FILE="$SCRIPT_DIR/task-to-register.json"

# === CLEAN TASK DEFINITION FOR RE-REGISTRATION (remove nulls) ===
jq '{
  family,
  taskRoleArn,
  executionRoleArn,
  networkMode,
  containerDefinitions,
  volumes,
  placementConstraints,
  requiresCompatibilities,
  cpu,
  memory,
  runtimePlatform,
  enableFaultInjection
} + (if .pidMode != null then {pidMode: .pidMode} else {} end)
  + (if .ipcMode != null then {ipcMode: .ipcMode} else {} end)
  + (if .proxyConfiguration != null then {proxyConfiguration: .proxyConfiguration} else {} end)
  + (if .inferenceAccelerators != null then {inferenceAccelerators: .inferenceAccelerators} else {} end)
  + (if .ephemeralStorage != null then {ephemeralStorage: .ephemeralStorage} else {} end)
' "$TASK_FILE" > "$CLEAN_TASK_FILE"

# === REGISTER NEW TASK DEFINITION ===
echo "👉 Registering new task definition..."
REGISTER_OUTPUT=$(AWS_ACCESS_KEY_ID="$AWS_ECS_ACCESS_KEY_ID" \
                 AWS_SECRET_ACCESS_KEY="$AWS_ECS_SECRET_ACCESS_KEY" \
                 AWS_DEFAULT_REGION="$REGION" \
                 aws ecs register-task-definition \
                   --cli-input-json file://"$CLEAN_TASK_FILE" \
                   --region "$REGION")

NEW_TASK_DEF_ARN=$(echo "$REGISTER_OUTPUT" | jq -r '.taskDefinition.taskDefinitionArn')
echo "✅ New task definition registered: $NEW_TASK_DEF_ARN"

# === FETCH LATEST TASK DEFINITION ARN (just registered) ===
LATEST_TASK_DEF_ARN=$(AWS_ACCESS_KEY_ID="$AWS_ECS_ACCESS_KEY_ID" \
                     AWS_SECRET_ACCESS_KEY="$AWS_ECS_SECRET_ACCESS_KEY" \
                     AWS_DEFAULT_REGION="$REGION" \
                     aws ecs list-task-definitions \
                       --family-prefix "$TASK_FAMILY" \
                       --sort DESC \
                       --max-items 1 \
                       --query 'taskDefinitionArns[0]' \
                       --output json | jq -r 'select(. != null)')

if [ -z "$LATEST_TASK_DEF_ARN" ]; then
  echo "❌ Failed to fetch latest task definition ARN"
  exit 1
fi

echo "✅ Latest task definition ARN: $LATEST_TASK_DEF_ARN"

# === FORCE ECS SERVICE DEPLOYMENT WITH LATEST TASK DEFINITION ===
echo "👉 Updating ECS service to use latest task definition..."
AWS_ACCESS_KEY_ID="$AWS_ECS_ACCESS_KEY_ID" \
AWS_SECRET_ACCESS_KEY="$AWS_ECS_SECRET_ACCESS_KEY" \
AWS_DEFAULT_REGION="$REGION" \
aws ecs update-service \
  --cluster "$CLUSTER_NAME" \
  --service "$SERVICE_NAME" \
  --task-definition "$LATEST_TASK_DEF_ARN" \
  --force-new-deployment \
  --region "$REGION"

echo "✅ ECS service force deployment triggered successfully!"

# === CLEAN UP TEMPORARY FILES ===
echo "🗑️  Cleaning up temporary files..."
rm -f "$CLEAN_TASK_FILE"
rm -f "$TASK_FILE"

echo "✅ Cleanup completed. Script finished."
