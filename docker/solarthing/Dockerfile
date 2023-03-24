FROM eclipse-temurin:19-jre-jammy
ARG JAR_LOCATION

RUN apt-get update && \
    apt-get install -y --no-install-recommends tzdata && \
    rm -rf /var/lib/apt/lists/*

RUN ["mkdir", "/app"]
WORKDIR "/app"
COPY --chmod=0444 ${JAR_LOCATION} solarthing.jar

# Currently this entry point does not need any allow opens.
#   The default raw_solarthing will run solarthing.jar with `--add-opens=java.base/java.lang.invoke=ALL-UNNAMED`
#   Because Retrofit (https://stackoverflow.com/a/61259233/5434860)
#   However, that bug/workaround only affects Java versions [9, 13], but we are running a higher Java version so we are unaffected.
ENTRYPOINT ["java", "-jar", "solarthing.jar"]
