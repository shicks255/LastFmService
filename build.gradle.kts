import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("jacoco")
    id("io.gitlab.arturbosch.detekt") version "1.16.0-RC2"
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.spring") version "1.4.30"
    kotlin("plugin.jpa") version "1.4.30"
}

group = "com.steven.hicks"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.postgresql:postgresql")
    implementation("net.logstash.logback:logstash-logback-encoder:4.11")

    implementation ("org.springdoc:springdoc-openapi-ui:1.5.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(
                org.gradle.api.tasks.testing.logging.TestLogEvent.STARTED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
        )
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = BigDecimal("0.75")
            }
        }
    }
}

tasks.check {
    doLast { tasks.jacocoTestCoverageVerification }
}

tasks.register<Copy>("copyJar") {
    println("$buildDir")
    from("build/libs/LastFmService-0.0.1-SNAPSHOT.jar")
    into("Z:\\lastFmService")
}