FROM ghcr.io/retrodaredevil/lavender-jre:21-ubuntu-jammy
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
ENTRYPOINT ["java", "-XX:ErrorFile=/app/jvmlog/hs_err_pid%p.log", "-jar", "/solarthing/solarthing.jar"]
