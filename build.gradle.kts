plugins {
    id("java")
    id("io.spring.dependency-management") version "1.1.7"
    id("jacoco")
}

subprojects {
    dependencyLocking {
        lockAllConfigurations()
    }

    extra.set("grpcVersion", "1.75.0")
    extra.set("spring-framework.version", "6.2.18")
    extra.set("tomcat.version", "11.0.22")
    extra.set("netty.version", "4.1.135.Final")
    extra.set("commons-lang3.version", "3.18.0")
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
