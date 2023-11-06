FROM eclipse-temurin:19-jre-jammy
ARG JAR_LOCATION
ARG COMMIT_HASH=""
ARG REF=""

RUN ["mkdir", "/solarthing"]
COPY --chmod=0444 ${JAR_LOCATION} /solarthing/solarthing.jar
ENV DOCKER=""
ENV COMMIT_HASH="$COMMIT_HASH"
ENV REF="$REF"

RUN ["mkdir", "/app"]
WORKDIR "/app"
# Currently this entry point does not need any allow opens.
#   The default raw_solarthing will run solarthing.jar with `--add-opens=java.base/java.lang.invoke=ALL-UNNAMED`
#   Because Retrofit (https://stackoverflow.com/a/61259233/5434860)
#   However, that bug/workaround only affects Java versions [9, 13], but we are running a higher Java version so we are unaffected.
ENTRYPOINT ["java", "-XX:ErrorFile=/app/jvmlog/hs_err_pid%p.log", "-jar", "/solarthing/solarthing.jar"]
