FROM eclipse-temurin:21-jre-jammy
EXPOSE 8081

ARG APP_HOME=/app

WORKDIR $APP_HOME

COPY build/libs/finance-service-*-SNAPSHOT.jar finance-app.jar

CMD ["java","-Dspring.profiles.active=docker", "-jar", "./finance-app.jar"]
