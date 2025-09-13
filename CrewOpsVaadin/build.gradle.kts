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
        implementation("org.springframework.boot:spring-boot-starter-cache")
        implementation("org.springframework.boot:spring-boot-starter-data-redis")

        implementation(project(":CrewOpsModel"))
        implementation(project(":CrewOpsSecurity"))
    }

    dependencyManagement {
        imports {
            mavenBom("com.vaadin:vaadin-bom:${property("vaadinVersion")}")
        }
    }

vaadin {
    optimizeBundle = true
    pnpmEnable = true
}

tasks.named("build") {
    dependsOn("vaadinBuildFrontend")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    imageName.set("${project.group}/${project.name.toLowerCase()}:${project.version}")
}