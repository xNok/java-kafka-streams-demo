FROM openjdk:11-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file into the container
COPY target/java-kafka-streams-demo.jar /app/

# Define the command to run your application
CMD ["java", "-jar", "your-kafka-streams-app.jar"]