FROM ghcr.io/retrodaredevil/lavender-jre:21-ubuntu-noble as builder
ARG JAR_LOCATION
COPY ${JAR_LOCATION} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --launcher --destination result


FROM ghcr.io/retrodaredevil/lavender-jre:21-ubuntu-noble

RUN ["mkdir", "/solarthing"]
WORKDIR "/solarthing"
RUN ["mkdir", "classes"]
COPY --from=builder result/dependencies/ classes/
COPY --from=builder result/snapshot-dependencies/ classes/
COPY --from=builder result/spring-boot-loader/ classes/
COPY --from=builder result/application/ classes/

RUN ["mkdir", "/app"]
WORKDIR "/app"
# Make sure you use the right JarLauncher fully qualified class
#   https://docs.spring.io/spring-boot/docs/3.2.0-SNAPSHOT/reference/html/executable-jar.html#appendix.executable-jar.launching.manifest
ENTRYPOINT ["java", "-XX:ErrorFile=/app/jvmlog/hs_err_pid%p.log", "-cp", "/solarthing/classes", "org.springframework.boot.loader.launch.JarLauncher"]
