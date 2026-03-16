
# Этап сборки
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/analytics-service/target/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "/app/app.jar"]



# Копируем jar из предыдущего этапа
# COPY --from=build /app/user-service/target/user-service.jar app.jar
