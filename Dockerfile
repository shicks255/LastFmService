FROM adoptopenjdk:11.0.10_9-jdk-hotspot

ARG JAR_FILE=build/*.jar

ADD "./build/libs/LastFmService-0.0.1-SNAPSHOT.jar" app.jar

EXPOSE 8686

ENTRYPOINT ["java", "-jar", "app.jar"]
