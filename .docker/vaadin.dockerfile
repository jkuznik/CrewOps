# Etap budowania
FROM gradle:8.13.0-jdk21 AS builder

# Ustaw katalog roboczy
WORKDIR /app

# Skopiuj pliki konfiguracyjne projektu głównego
COPY ../settings.gradle.kts ../build.gradle.kts ./
COPY ../gradle ./gradle

# Skopiuj wymagane submoduły
COPY ../CrewOpsModel ./CrewOpsModel
COPY ../CrewOpsSecurity ./CrewOpsSecurity
COPY ../CrewOpsVaadin ./CrewOpsVaadin

# Zbuduj aplikację (pomijając testy)
RUN gradle :CrewOpsVaadin:build -x test --no-daemon --info -Pproduction -Dvaadin.force.production.build=true


# Etap uruchomieniowy - użycie lekkiego obrazu
FROM eclipse-temurin:21-jdk-alpine

# Ustaw katalog roboczy
WORKDIR /app

# Skopiuj artefakt JAR z etapu build
COPY --from=builder /app/CrewOpsVaadin/build/libs/*.jar app.jar

# Wystaw port
EXPOSE 8081

# Komenda startowa
ENTRYPOINT ["sh", "-c", "java -jar app.jar -Pproduction --spring.profiles.active=prod --CREWOPS_CLIENT_ID_INPUT=$CREWOPS_CLIENT_ID_INPUT --JWT_CREWOPS=$JWT_CREWOPS"]
