FROM gradle:8.14-jdk21 AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradlew ./
RUN chmod +x gradlew
COPY gradle ./gradle
COPY src ./src
COPY src/main/proto ./src/main/proto

RUN ./gradlew :build -x test --no-daemon && \
    cp $(find /app/build/libs -name "*.jar" -not -name "*plain*" | head -1) /app/app.jar

FROM eclipse-temurin:21-jre-alpine AS runtime
RUN apk add --no-cache tzdata curl
RUN addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -s /bin/sh -D appuser
WORKDIR /app
COPY --from=build /app/app.jar app.jar
RUN chown -R appuser:appgroup /app
USER appuser
EXPOSE 9091
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:9091/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]