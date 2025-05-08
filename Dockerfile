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
FROM eclipse-temurin:24-alpine
LABEL maintainer="Giuseppe Della Penna"

RUN apk upgrade --no-cache && apk add --no-cache python3 py3-numpy gawk bash

COPY --from=build /usr/app/target/BPMNModelTranslator-1.0-SNAPSHOT-shaded.jar /usr/app/
COPY ./BDTest/main.sh /usr/app/

WORKDIR /usr/app

#ENTRYPOINT ["java", "-jar", "BPMNModelTranslator-1.0-SNAPSHOT-shaded.jar" ]
ENTRYPOINT ["bash", "main.sh"]

