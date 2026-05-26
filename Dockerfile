# syntax=docker/dockerfile:1

# Build stage: compile and package the WAR using Maven + JDK 17
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY web ./web

RUN mvn -B -DskipTests clean package

# Runtime stage: deploy WAR on Tomcat 10 (Jakarta EE compatible)
FROM tomcat:10.1-jdk17-temurin

# Remove default webapps for a smaller runtime footprint
RUN rm -rf /usr/local/tomcat/webapps/*

# Deploy as ROOT so app is available at http://localhost:8080/
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
