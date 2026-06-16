FROM gradle:8.14-jdk21 AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew

RUN --mount=type=cache,target=/root/.gradle/caches,sharing=locked \
    --mount=type=cache,target=/root/.gradle/wrapper,sharing=locked \
    ./gradlew :space-api:dependencies --no-daemon --quiet

COPY config ./config
COPY space-api/build.gradle.kts ./space-api/

COPY space-api/src/main/proto ./space-api/src/main/proto
RUN --mount=type=cache,target=/root/.gradle/caches \
    ./gradlew :space-api:generateProto --no-daemon

COPY space-api/src/main/java ./space-api/src/main/java
COPY space-api/src/main/resources ./space-api/src/main/resources

RUN --mount=type=cache,target=/root/.gradle/caches \
    ./gradlew :space-api:bootJar -x test --no-daemon --parallel && \
    mv /app/space-api/build/libs/*.jar /app/app.jar

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
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8082/actuator/health || exit 1
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]