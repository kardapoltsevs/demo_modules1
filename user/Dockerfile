FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
