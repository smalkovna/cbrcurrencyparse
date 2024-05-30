FROM openjdk:11.0.16-jdk-slim
RUN apt-get update && apt-get install -y maven
WORKDIR /src/main
COPY pom.xml .
COPY src ./src
RUN mvn clean package
EXPOSE 8080
COPY target/cbrcurrencyparse-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]