plugins {
    id("java")
    id("io.spring.dependency-management") version "1.1.7"
    id("jacoco")
    id("checkstyle")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

checkstyle {
        toolVersion = "10.12.5"
        configFile = rootProject.file("config/checkstyle/sun_checks.xml")
}

tasks.test {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}