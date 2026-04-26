FROM gradle:8.14-jdk21 AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle gradlew ./
RUN chmod +x gradlew
COPY gradle ./gradle
COPY space-api/build.gradle.kts ./space-api/
COPY space-api/src ./space-api/src

RUN ./gradlew :space-api:build -x test --no-daemon && \
    cp /app/space-api/build/libs/*.jar /app/app.jar

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN apk add --no-cache tzdata curl

RUN addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -s /bin/sh -D appuser

WORKDIR /app

COPY --from=build /app/app.jar app.jar

RUN rm -rf /tmp/* /var/cache/apk/* && \
    chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]