mvn clean compile assembly:single
mv target/solarthing-0.0.1-SNAPSHOT-jar-with-dependencies.jar program/solarthing.jar
echo "Compiled and moved successfully"
