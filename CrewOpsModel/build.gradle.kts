//plugins {
//    id("java")
//}
//
//group = "pl.crewops"
//version = "0.0.1-SNAPSHOT"
//
//repositories {
//    mavenCentral()
//}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("jakarta.validation:jakarta.validation-api:3.1.1")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.3")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}