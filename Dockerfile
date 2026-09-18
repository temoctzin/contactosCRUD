FROM gradle:9.7.1-jdk17 AS build
WORKDIR /workspace
COPY agendaTelefonica/ /workspace/
RUN gradle bootWar -x test --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.war ./app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]