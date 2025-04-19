dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-core:6.4.4")
    implementation("org.springframework.security:spring-security-web:6.4.4")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    implementation("org.postgresql:postgresql:42.7.5")
    implementation("org.liquibase:liquibase-core:4.31.1")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")

    testImplementation("org.testcontainers:testcontainers:1.20.6")
    testImplementation("org.testcontainers:junit-jupiter:1.20.6")
    testImplementation("org.testcontainers:postgresql:1.20.6")

    implementation(project(":CrewOpsModel"))
}

tasks.test {
    useJUnitPlatform()
}

