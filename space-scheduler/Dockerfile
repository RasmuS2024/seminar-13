FROM gradle:8.14-jdk21 AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew

RUN --mount=type=cache,target=/root/.gradle/caches \
    --mount=type=cache,target=/root/.gradle/wrapper \
    ./gradlew :space-scheduler:dependencies --no-daemon

COPY space-scheduler/build.gradle.kts ./space-scheduler/
COPY space-scheduler/src ./space-scheduler/src
COPY config ./config

RUN --mount=type=cache,target=/root/.gradle/caches \
    ./gradlew :space-scheduler:bootJar -x test --no-daemon && \
    cp $(find /app/space-scheduler/build/libs -name "*.jar" -not -name "*plain*" | head -1) /app/app.jar

FROM eclipse-temurin:21-jre-alpine AS runtime
RUN apk add --no-cache tzdata curl && \
    addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -s /bin/sh -D appuser
WORKDIR /app
COPY --from=build --chown=appuser:appgroup /app/app.jar app.jar
USER appuser
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]