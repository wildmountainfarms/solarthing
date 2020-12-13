#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

./gradlew clean shadow || exit 1
cp client/build/libs/client-0.0.1-SNAPSHOT-all.jar program/.downloads/solarthing-SNAPSHOT.jar || exit 1
./use_snapshot.sh
echo "Compiled and moved successfully"
