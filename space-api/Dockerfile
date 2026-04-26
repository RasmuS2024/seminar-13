FROM gradle:8.14-jdk21 AS build
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
COPY space-api/build.gradle ./space-api/
COPY space-api/src ./space-api/src

RUN ./gradlew :space-api:build -x test --no-daemon

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

RUN groupadd -r appgroup && useradd -r -g appgroup -d /app appuser
COPY --from=build /app/space-api/build/libs/*.jar app.jar
RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]