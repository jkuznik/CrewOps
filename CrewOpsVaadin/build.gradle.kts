plugins {
    id("com.vaadin") version "24.6.6"
}

extra["vaadinVersion"] = "24.6.6"

dependencies {
    implementation("com.vaadin:vaadin-spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("com.vaadin:vaadin-bom:${property("vaadinVersion")}")
    }
}