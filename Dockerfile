FROM gradle:6-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean build --no-daemon

FROM openjdk:11.0.11-jre-slim
VOLUME /tmp
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/sherlock.jar
ENTRYPOINT ["java", "-jar","/app/sherlock.jar"]