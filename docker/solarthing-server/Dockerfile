FROM eclipse-temurin:19-jre-jammy as builder
ARG BOOT_JAR
COPY ${BOOT_JAR} application.jar
RUN java -Djarmode=layertools -jar application.jar extract


FROM eclipse-temurin:19-jre-jammy
# https://github.com/opencontainers/image-spec/blob/main/annotations.md#pre-defined-annotation-keys
LABEL org.opencontainers.image.title="SolarThing Server"
LABEL org.opencontainers.image.description="SolarThing Server running in a docker container"
LABEL org.opencontainers.image.source="https://github.com/wildmountainfarms/solarthing"
LABEL org.opencontainers.image.ref.name=solarthing-server
LABEL org.opencontainers.image.vendor=wildmountainfarms
LABEL org.opencontainers.image.version=SNAPSHOT
LABEL org.opencontainers.image.documentation="https://solarthing.readthedocs.io/en/latest/"
LABEL org.opencontainers.image.url="https://solarthing.readthedocs.io/en/latest/"
LABEL org.opencontainers.image.authors="retrodaredevil"
LABEL org.opencontainers.image.base.name="docker.io/eclipse-temurin:19-jre-jammy"

RUN ["mkdir", "/app"]
WORKDIR "/app"
RUN ["mkdir", "classes"]
COPY --from=builder dependencies/ classes/
COPY --from=builder snapshot-dependencies/ classes/
COPY --from=builder spring-boot-loader/ classes/
COPY --from=builder application/ classes/
ENTRYPOINT ["java", "-cp", "classes", "org.springframework.boot.loader.JarLauncher"]
