ARG DOCKER_REGISTRY
ARG FABRIC_STARTER_VERSION
ARG JAR_FILE=build/libs/fabric2-rest-api-0.0.1-SNAPSHOT.jar

FROM ${DOCKER_REGISTRY:-docker.io}/olegabu/fabric-tools-extended:${FABRIC_STARTER_VERSION:-2x} as fabrictools

FROM adoptopenjdk/openjdk11:alpine-jre

# copy fabic executables if changed
COPY --from=fabrictools /etc/hyperledger/fabric/core.yaml /
COPY --from=fabrictools /usr/local/bin/peer /usr/local/bin

EXPOSE 8080

ADD build/libs/fabric2-rest-api-0.0.1-SNAPSHOT.jar fabric2-rest-api.jar
ENTRYPOINT ["java","-jar","/fabric2-rest-api.jar"]
