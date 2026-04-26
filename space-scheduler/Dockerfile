FROM gradle:8.14-jdk21 AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle
COPY space-scheduler/build.gradle.kts ./space-scheduler/
COPY space-scheduler/src ./space-scheduler/src

RUN ./gradlew :space-scheduler:build -x test --no-daemon

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

RUN groupadd -r appgroup && useradd -r -g appgroup -d /app appuser
COPY --from=build /app/space-scheduler/build/libs/*.jar app.jar
RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]