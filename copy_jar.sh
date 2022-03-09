#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

if [ "$#" -gt 0 ]; then
  TARGET=$1
  cd program/ || exit 1
  JAR_FILE=$(readlink "solarthing.jar")
  FILE_NAME=$(basename "$JAR_FILE")
  if echo "$FILE_NAME" | grep "SNAPSHOT"; then
    scp "$JAR_FILE" "$TARGET:/opt/solarthing/program/.downloads/solarthing-TEMP.jar" || exit 1
    ssh "$TARGET" "/opt/solarthing/other/scripts/set_snapshot.sh solarthing /opt/solarthing/program/.downloads/solarthing-TEMP.jar" || exit 1
  else
    scp "$JAR_FILE" "$TARGET:/opt/solarthing/program/.downloads/$FILE_NAME" || exit 1
    ssh "$TARGET" "/opt/solarthing/other/scripts/use_jar.sh solarthing.jar /opt/solarthing/program/.downloads/$FILE_NAME" || exit 1
  fi
  echo "Success!"
else
  echo You need to give at least one argument!
  exit 1
fi
