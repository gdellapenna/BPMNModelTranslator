#
# Build stage
#
FROM maven:3-eclipse-temurin-24-alpine AS build
WORKDIR /usr/app
COPY ./src/ /usr/app/src/
COPY ./docker-pom.xml /usr/app/pom.xml
RUN mvn  clean package

#
# Package stage
#
FROM eclipse-temurin:24-jre-alpine 
LABEL maintainer="Giuseppe Della Penna"
#RUN apk upgrade --no-cache && apk add --no-cache iputils-ping build-base python3 py3-numpy bc psmisc gawk bash
COPY --from=build /usr/app/target/BPMNModelTranslator-1.0-SNAPSHOT-shaded.jar /usr/app/BPMNModelTranslator.jar
WORKDIR /usr/app
ENTRYPOINT ["java", "-jar", "BPMNModelTranslator.jar" ]
#ENTRYPOINT ["bash", "main.sh"]

