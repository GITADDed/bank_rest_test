# ===== build stage =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
# если у тебя есть .mvn/ и mvnw — можно копировать их тоже (не обязательно)
RUN mvn -q -e -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

# ===== runtime stage =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# jar имя может отличаться — лучше копировать "единственный" jar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
