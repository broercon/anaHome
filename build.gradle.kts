plugins {
    kotlin("jvm") version "2.1.20"
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("plugin.spring") version "1.9.23"
}

group = "org.anaHome"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("io.github.microutils:kotlin-logging:3.0.5")

    // Kotlin support
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // PostgreSQL Driver (use H2 for in-memory testing if preferred)
    runtimeOnly("org.postgresql:postgresql")
    implementation(dependencyNotation = "com.github.docker-java:docker-java:3.2.5")

    // JAXB API Dependency
    implementation("javax.xml.bind:jaxb-api:2.3.1")

    // JAXB Runtime Dependency
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.3")

    // Development tools
    // developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql:1.19.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}