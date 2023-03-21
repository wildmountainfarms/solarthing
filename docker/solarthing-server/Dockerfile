FROM eclipse-temurin:19-jre-jammy as builder
ARG BOOT_JAR
COPY ${BOOT_JAR} application.jar
RUN java -Djarmode=layertools -jar application.jar extract


FROM eclipse-temurin:19-jre-jammy
LABEL org.opencontainers.image.source=https://github.com/wildmountainfarms/solarthing

RUN ["mkdir", "/app"]
WORKDIR "/app"
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
