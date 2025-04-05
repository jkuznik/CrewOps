plugins {
    id("com.vaadin") version "24.6.6"
}

repositories {
    maven (url = uri("https://maven.vaadin.com/vaadin-addons"))
}

extra["vaadinVersion"] = "24.6.6"

val production: Boolean = project.hasProperty("production")

dependencies {

    if (production) {
        // using vaadin-core instead of vaadin artifact should force to use only free components
        implementation("com.vaadin:vaadin-core:24.7.1") {
            exclude(group = "com.vaadin", module = "vaadin-dev")
        }
    } else {
        implementation("com.vaadin:vaadin-core:24.7.1")
    }

    implementation("com.vaadin:vaadin-spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

//    implementation(project(":CrewOpsCore"))
}

dependencyManagement {
    imports {
        mavenBom("com.vaadin:vaadin-bom:${property("vaadinVersion")}")
    }
}

tasks {
    if (production) {
        named("build") {
            dependsOn("vaadinBuildFrontend")
        }
    }
}

defaultTasks("clean", "vaadinBuildFrontend", "build")