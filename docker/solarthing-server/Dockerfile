FROM eclipse-temurin:19-jre-jammy as builder
ARG JAR_LOCATION
COPY ${JAR_LOCATION} application.jar
RUN java -Djarmode=layertools -jar application.jar extract


FROM eclipse-temurin:19-jre-jammy

RUN apt-get update && \
    apt-get install -y --no-install-recommends tzdata && \
    rm -rf /var/lib/apt/lists/*

RUN ["mkdir", "/app"]
WORKDIR "/app"
RUN ["mkdir", "classes"]
COPY --from=builder dependencies/ classes/
COPY --from=builder snapshot-dependencies/ classes/
COPY --from=builder spring-boot-loader/ classes/
COPY --from=builder application/ classes/
ENTRYPOINT ["java", "-cp", "classes", "org.springframework.boot.loader.JarLauncher"]
