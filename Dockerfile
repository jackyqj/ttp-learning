FROM dvmarques/openjdk-17-jdk-alpine-with-timezone:latest

WORKDIR /app

COPY . .
RUN ./mvnw clean package -DskipTests

RUN cp target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
