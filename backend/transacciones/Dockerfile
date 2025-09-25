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





#FROM alpine:3.18
#
## Dependencias mínimas
#RUN apk add --no-cache bash tar gzip
#
## Copiar JDK al contenedor
#COPY jdk-21.tar.gz /opt/
#
## Extraer JDK
#RUN mkdir -p /usr/lib/jvm && \
#    tar -xzf /opt/jdk-21.tar.gz -C /usr/lib/jvm && \
#    rm /opt/jdk-21.tar.gz
#
## JAVA_HOME y PATH
## ⚠ Ajustar el nombre exacto de la carpeta creada al extraer el tar.gz
##ENV JAVA_HOME=/usr/lib/jvm/jdk-21.0.0+9
#ENV JAVA_HOME=/usr/lib/jvm/jdk-25+36
#ENV PATH="$JAVA_HOME/bin:$PATH"
#
#WORKDIR /app
#
#COPY build/libs/users-0.0.1-SNAPSHOT.jar app.jar
#
#EXPOSE 8082
#
#CMD ["java", "-jar", "app.jar"]

