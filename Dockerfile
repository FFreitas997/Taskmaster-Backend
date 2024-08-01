# Use an official Maven image as a build stage
FROM maven:3.9.8-amazoncorretto-21 AS build

# Set or create the build directory
WORKDIR /build

# Copy pom.xml file to the build directory
COPY pom.xml .

# Download all dependencies and plugins for the build process
RUN mvn dependency:go-offline

# Copy the source code to the build directory
COPY src ./src

# Clean and build the application without running the tests
RUN mvn clean package -DskipTests


# Use an official OpenJDK image as a runtime stage
FROM amazoncorretto:21

# set the app arguments
ARG PROFILE=prod
ARG APP_VERSION=1.0.0

# Set or create the app directory
WORKDIR /app

# Copy the JAR file from the build stage to the app directory
COPY --from=build /build/target/Taskmaster-1.0.0.jar /app/

# Expose the port that the application will listen on
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=${PROFILE}
ENV JAR_VERSION=${APP_VERSION}
ENV POSTGRES_URL=missing_postgres_url
ENV POSTGRES_USERNAME=missing_postgres_username
ENV POSTGRES_PASSWORD=missing_postgres_password
ENV KEYCLOAK_ISSUER_URI=missing_keycloak_issuer_uri

CMD java -jar -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} Taskmaster-${JAR_VERSION}.jar