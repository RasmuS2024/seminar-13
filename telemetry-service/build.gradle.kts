plugins {
    id("java")
    id("org.springframework.boot") version "3.5.14"
    id("io.spring.dependency-management") version "1.1.7"
    id("jacoco")
    id("com.google.protobuf") version "0.9.5"
    id("checkstyle")
}

checkstyle {
    toolVersion = "13.6.0"
}

configurations.checkstyle {
    resolutionStrategy {
        force("org.codehaus.plexus:plexus-utils:3.6.1")
    }
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val grpcVersion: String by project

dependencyManagement {
    imports {
        mavenBom("io.grpc:grpc-bom:${grpcVersion}")
    }
}

dependencies {
    implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE")
    implementation("io.grpc:grpc-protobuf")
    implementation("io.grpc:grpc-stub")
    implementation("io.grpc:grpc-netty")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    implementation("com.google.protobuf:protobuf-java:3.25.5")
    implementation("com.google.protobuf:protobuf-java-util:3.25.5")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation ("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    runtimeOnly("org.postgresql:postgresql:42.7.11")

    implementation("org.springframework.kafka:spring-kafka:3.3.16")

    compileOnly ("org.projectlombok:lombok")
    compileOnly("org.apache.tomcat:annotations-api:6.0.53")

    annotationProcessor ("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.5"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs(
                "build/generated/source/proto/main/java",
                "build/generated/source/proto/main/grpc"
            )
        }
    }
}

springBoot {
    mainClass = "seminars.Main"
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