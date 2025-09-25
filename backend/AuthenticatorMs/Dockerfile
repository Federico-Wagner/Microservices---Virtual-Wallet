FROM eclipse-temurin:21-jdk-alpine

ARG JAR_FILE=build/libs/*.jar

WORKDIR /app

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]