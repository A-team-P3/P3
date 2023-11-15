# Use the official Java 17 image from eclipse-temurin
FROM eclipse-temurin:17

# Set the working directory inside the container
WORKDIR /opt/docker/spring-boot

# Install xargs and other necessary tools using apk
RUN apk add --no-cache findutils

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
