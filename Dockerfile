# Use a base image with Java 17 or your desired version
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the application jar file into the container
COPY target/gaming-service-0.0.1-SNAPSHOT.jar /app/gaming-service-0.0.1-SNAPSHOT.jar

# Expose the port that your application will run on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/gaming-service-0.0.1-SNAPSHOT.jar"]
