# Use the official Java 21 image as a parent image
FROM openjdk:21

# Set the working directory inside the container
WORKDIR /opt/docker/spring-boot

# Copy the gradle wrapper and other necessary files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .

# Set the execute permission on the Gradle wrapper
RUN chmod +x gradlew

# Copy the source code into the container
COPY src src

# Build the application
RUN ./gradlew build

# Expose the port the app runs on
EXPOSE 8080

# Run the application
CMD ["./gradlew", "bootRun"]