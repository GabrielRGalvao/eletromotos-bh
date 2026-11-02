FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw package -DskipTests -B

FROM eclipse-temurin:21-jre

WORKDIR /app

RUN useradd --system --uid 1001 aplicacao
COPY --from=build /workspace/target/*.jar app.jar

RUN mkdir -p /data/vistorias && chown -R aplicacao:aplicacao /data
USER aplicacao

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]