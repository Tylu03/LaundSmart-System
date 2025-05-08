FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY ./src ./src
COPY ./src/main/resources/config/firebase_key.json ./src/main/resources/config/firebase_key.json
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine as production
WORKDIR /app
COPY --from=build target/*.jar target/app.jar
COPY --from=build ./src/main/resources/config/firebase_key.json config/firebase_key.json
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/app.jar", "--spring.profiles.active=prod"]