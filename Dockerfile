ARG DOCKER_REGISTRY
ARG FABRIC_STARTER_VERSION
ARG JAR_FILE=build/libs/fabric2-rest-api-0.0.1-SNAPSHOT.jar

FROM ${DOCKER_REGISTRY:-docker.io}/olegabu/fabric-tools-extended:${FABRIC_STARTER_VERSION:-2x} as fabrictools

FROM gradle:7-jdk11-alpine

COPY . .

RUN gradle clean build -x test
RUN mkdir /fabric2-rest-api
RUN cp build/libs/fabric2-rest-api-0.0.1-SNAPSHOT.jar /fabric2-rest-api/fabric2-rest-api.jar

# copy fabic executables if changed
COPY --from=fabrictools /etc/hyperledger/fabric/core.yaml /
COPY --from=fabrictools /usr/local/bin/peer /usr/local/bin

EXPOSE 8080

WORKDIR /fabric2-rest-api
ENTRYPOINT ["java","-jar","fabric2-rest-api.jar"]
