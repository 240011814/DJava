FROM 106.52.158.70:9000/library/openjdk:24

ARG GIT_COMMIT
WORKDIR /app

ENV GIT_COMMIT=$GIT_COMMIT
COPY target/Djava-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]