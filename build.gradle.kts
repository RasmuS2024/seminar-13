plugins {
    id("java")
    id("io.spring.dependency-management") version "1.1.7"
    id("jacoco")
}

val grpcVersion = "1.68.1"

subprojects {
    ext["netty.version"] = "4.1.133.Final"
    ext["commons-lang3.version"] = "3.18.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}
