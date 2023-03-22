FROM eclipse-temurin:19-jre-jammy
ARG JAR_LOCATION

RUN ["mkdir", "/app"]
WORKDIR "/app"
COPY --chmod=0444 ${JAR_LOCATION} solarthing.jar
ENTRYPOINT ["java", "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED", "-jar", "solarthing.jar"]
