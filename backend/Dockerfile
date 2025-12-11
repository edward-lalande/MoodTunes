FROM gradle:8.5-jdk21 AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

RUN gradle shadowJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/build/libs/moodtunes-all.jar app.jar
COPY .env .env

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
