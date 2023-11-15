# Use the official Java 21 image as a parent image
FROM openjdk:21

# Set the working directory inside the container
WORKDIR /app

# Copy the gradle build file into the container
COPY build.gradle .

# Copy the source code into the container
COPY src /app/src

# Build the application
RUN ./gradlew build

# Expose the port the app runs on
EXPOSE 8080

# Run the application
CMD ["./gradlew", "bootRun"]
