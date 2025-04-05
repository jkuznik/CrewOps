plugins {
    id("java")
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "7.0.2"
}

allprojects {
    group = "pl.crewops"
    version = "0.0.1-SNAPSHOT"

    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "com.diffplug.spotless")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(23))
        }
    }

    repositories {
        mavenCentral()
    }

    spotless {
        java {
            palantirJavaFormat()
            trimTrailingWhitespace()
            endWithNewline()
            removeUnusedImports()
        }
    }

    configurations.named("compileOnly") {
        extendsFrom(configurations.getByName("annotationProcessor"))
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        developmentOnly("org.springframework.boot:spring-boot-devtools")

        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-docker-compose:3.4.4")
}