#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

./gradlew shadow || exit 1
other/scripts/set_snapshot.sh "solarthing" client/build/libs/client-0.0.1-SNAPSHOT-all.jar || exit 1
echo "Compiled and moved successfully"
