FROM gradle:8.14-jdk21 AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew

RUN --mount=type=cache,target=/root/.gradle/caches \
    --mount=type=cache,target=/root/.gradle/wrapper \
    ./gradlew :telemetry-service:dependencies --no-daemon --quiet

COPY config ./config
COPY telemetry-service/build.gradle.kts ./telemetry-service/

COPY telemetry-service/src/main/proto ./telemetry-service/src/main/proto
RUN --mount=type=cache,target=/root/.gradle/caches \
    ./gradlew :telemetry-service:generateProto --no-daemon

COPY telemetry-service/src/main/java ./telemetry-service/src/main/java
COPY telemetry-service/src/main/resources ./telemetry-service/src/main/resources

RUN --mount=type=cache,target=/root/.gradle/caches \
    ./gradlew :telemetry-service:bootJar -x test --no-daemon --parallel && \
    mv /app/telemetry-service/build/libs/*.jar /app/app.jar

RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre-alpine AS runtime
RUN apk add --no-cache curl && \
    addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -s /bin/sh -D appuser
WORKDIR /app

COPY --from=build --chown=appuser:appgroup /app/dependencies/ ./
COPY --from=build --chown=appuser:appgroup /app/spring-boot-loader/ ./
COPY --from=build --chown=appuser:appgroup /app/snapshot-dependencies/ ./
COPY --from=build --chown=appuser:appgroup /app/application/ ./

USER appuser
EXPOSE 9091 9090
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:9091/actuator/health || exit 1
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]