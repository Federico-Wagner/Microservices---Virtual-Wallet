# Usamos un JDK liviano para Java 21
FROM eclipse-temurin:21-jdk-alpine

# Variable que apunta al JAR generado por Gradle
ARG JAR_FILE=build/libs/*.jar

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el JAR generado al contenedor y lo renombramos a app.jar
COPY ${JAR_FILE} app.jar

# Comando para ejecutar el microservicio
ENTRYPOINT ["java","-jar","/app/app.jar"]
