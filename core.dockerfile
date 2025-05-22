FROM gradle:8.13.0-jdk21 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy settings from the root project
COPY settings.gradle.kts build.gradle.kts ./
COPY gradle gradle/

# Copy only the submodule code and dependencies
COPY CrewOpsModel CrewOpsModel/
COPY CrewOpsSecurity CrewOpsSecurity/
COPY CrewOpsCore CrewOpsCore/

# Build the submodule JAR
RUN gradle :CrewOpsCore:build -x test --no-daemon --info

# Use a lightweight JDK image for runtime
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/CrewOpsCore/build/libs/*.jar app.jar

# Expose the application port (change if needed)
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java -jar app.jar --CREWOPS_CLIENT_ID_HASH=$CREWOPS_CLIENT_ID_HASH --JWT_CREWOPS=$JWT_CREWOPS"]
