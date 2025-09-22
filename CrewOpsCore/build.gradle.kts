dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mail:4.0.0-M2")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.postgresql:postgresql:42.7.5")
    implementation("org.liquibase:liquibase-core:4.31.1")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")

    implementation("org.passay:passay:1.6.6")

    testImplementation("org.testcontainers:testcontainers:1.20.6")
    testImplementation("org.testcontainers:junit-jupiter:1.20.6")
    testImplementation("org.testcontainers:postgresql:1.20.6")

    implementation(project(":CrewOpsModel"))
    implementation(project(":CrewOpsSecurity"))
}

tasks.test {
    useJUnitPlatform()
}

val imageTag: String = System.getenv("IMAGE_TAG") ?: "latest"

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    imageName.set("jkuznik-ecr/crewops-core:$imageTag")

    buildpacks.set(
        listOf(
            "paketobuildpacks/amazon-corretto",
            "paketobuildpacks/java"
        )
    )

    environment.set(
        mapOf(
            "BP_JVM_VERSION" to "21",
        )
    )
}

