FROM adoptopenjdk/openjdk11:alpine-jre
EXPOSE 8080
ARG JAR_FILE=build/fabric2-rest-api-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} fabric2-rest-api.jar
ENTRYPOINT ["java","-jar","/fabric2-rest-api.jar"]