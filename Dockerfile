ARG DOCKER_REGISTRY
ARG FABRIC_STARTER_REPOSITORY
ARG FABRIC_STARTER_VERSION

ARG GRADLE_IMAGE
ARG JDK_IMAGE

FROM ${DOCKER_REGISTRY:-docker.io}/${FABRIC_STARTER_REPOSITORY:-olegabu}/fabric-tools-extended:${FABRIC_STARTER_VERSION:-2x} as fabrictools

FROM ${GRADLE_IMAGE:-'gradle:7-jdk11-alpine'} as gradle
ARG JAR_FILE=build/libs/fabric2-rest-api-0.0.1-SNAPSHOT.jar

COPY . .
RUN gradle clean build -x test
RUN cp ${JAR_FILE} /fabric2-rest-api.jar

FROM ${JDK_IMAGE:-'adoptopenjdk/openjdk11:alpine-jre'}

# copy fabic executables if changed
COPY --from=fabrictools /etc/hyperledger/fabric/core.yaml /
COPY --from=fabrictools /usr/local/bin/peer /usr/local/bin

ENV JAR_DIR=/fabric2-rest-api
RUN mkdir ${JAR_DIR}
WORKDIR ${JAR_DIR}
COPY --from=gradle /fabric2-rest-api.jar ./

EXPOSE 8080

ENTRYPOINT ["java","-jar","./fabric2-rest-api.jar"]
