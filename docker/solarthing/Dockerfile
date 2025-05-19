# NOTE: When updating base image, be sure to update the label in docker-build.yml for base image
FROM ghcr.io/retrodaredevil/lavender-jre:21-ubuntu-noble
ARG JAR_LOCATION
ARG COMMIT_HASH=""
ARG REF=""

# Install netcat because users may want to run nc to access configuration server
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends netcat-openbsd && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN ["mkdir", "/solarthing"]
COPY --chmod=0444 ${JAR_LOCATION} /solarthing/solarthing.jar
ENV DOCKER=""
ENV COMMIT_HASH="$COMMIT_HASH"
ENV REF="$REF"

RUN ["mkdir", "/app"]
WORKDIR "/app"
ENTRYPOINT ["java", "-XX:ErrorFile=/app/jvmlog/hs_err_pid%p.log", "-jar", "/solarthing/solarthing.jar"]
