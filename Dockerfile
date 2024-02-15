# Use an official OpenJDK runtime as a parent image
FROM openjdk:11

# Set the working directory in the container
WORKDIR /app


COPY target/scala-2.13/newScalaApi-assembly-1.0-SNAPSHOT.jar /app/app.jar

ENV PLAY_HTTP_SECRET_KEY=""

ENV DB_USER=""

EXPOSE 9000

CMD ["java", "-jar", "/app/app.jar"]