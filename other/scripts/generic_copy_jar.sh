#!/usr/bin/env bash
BASEDIR=$(dirname "$0")

if [ "$#" -eq 2 ]; then
  JAR_PREFIX=$1
  TARGET=$2
  cd "$BASEDIR/../../program/" || exit 1
  JAR_FILE=$(readlink "$JAR_PREFIX.jar")
  FILE_NAME=$(basename "$JAR_FILE")
  ESCAPED_JAR_PREFIX=$(printf "%q" "$JAR_PREFIX")
  if echo "$FILE_NAME" | grep "SNAPSHOT" >/dev/null 2>&1; then
    scp "$JAR_FILE" "$TARGET:/opt/solarthing/program/.downloads/$JAR_PREFIX-TEMP.jar" || exit 1
    # shellcheck disable=SC2029
    ssh "$TARGET" "/opt/solarthing/other/scripts/set_snapshot.sh $ESCAPED_JAR_PREFIX /opt/solarthing/program/.downloads/$ESCAPED_JAR_PREFIX-TEMP.jar" || exit 1
  else
    scp "$JAR_FILE" "$TARGET:/opt/solarthing/program/.downloads/$FILE_NAME" || exit 1
    ESCAPED_DESTINATION=$(printf "%q" "/opt/solarthing/program/.downloads/$FILE_NAME")
    # shellcheck disable=SC2029
    ssh "$TARGET" "/opt/solarthing/other/scripts/generic_use_jar.sh $ESCAPED_JAR_PREFIX.jar $ESCAPED_DESTINATION" || exit 1
  fi
  echo "Success!"
else
  echo You need to give at least one argument!
  exit 1
fi
