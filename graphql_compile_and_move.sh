#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

./gradlew clean graphql:bootJar || exit 1
other/scripts/set_snapshot.sh "solarthing-graphql" graphql/build/libs/graphql-0.0.1-SNAPSHOT.jar || exit 1
echo "Compiled and moved graphql boot jar successfully"
