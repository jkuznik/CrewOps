    plugins {
            id("com.vaadin") version "24.7.1"
        }

        repositories {
            maven (url = uri("https://maven.vaadin.com/vaadin-addons"))
        }

        extra["vaadinVersion"] = "24.7.1"

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

            implementation("org.vaadin.addons.componentfactory:vcf-timeline:1.0.2")
            implementation("io.overcoded:panel-for-vaadin:24.3.0")
            implementation("org.vaadin.stefan:fullcalendar2:6.3.1")

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

    val imageTag: String = System.getenv("IMAGE_TAG") ?: "latest"

    tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
        dependsOn("vaadinBuildFrontend")

        val imageTag: String = System.getenv("IMAGE_TAG") ?: "latest"
        imageName.set("jkuznik-ecr/crewops-vaadin:$imageTag")

        buildpacks.set(listOf(
            "paketobuildpacks/amazon-corretto",
            "paketobuildpacks/java"
        ))

        environment.set(mapOf(
            "BP_JVM_VERSION" to "21",
            "SPRING_PROFILES_ACTIVE" to "production",
            "SERVER_PORT" to "8081"
        ))
    }



