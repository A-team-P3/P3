# Use the official Java 17 image from eclipse-temurin
FROM eclipse-temurin:17

# Set the working directory inside the container
WORKDIR /opt/docker/spring-boot

# Update package lists and install findutils
RUN apt-get update && apt-get install -y findutils && rm -rf /var/lib/apt/lists/*

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
