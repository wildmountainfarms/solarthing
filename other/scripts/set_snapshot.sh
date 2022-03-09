#!/usr/bin/env sh
BASEDIR=$(dirname "$0")

if [ "$#" -eq 2 ]; then
  JAR_PREFIX="$1"
  RELATIVE_PATH=$(realpath --relative-to="$BASEDIR/../../program/" "$2")
  cd "$BASEDIR/../../program/" || exit 1
  SNAPSHOT_1=".downloads/$JAR_PREFIX-SNAPSHOT-1.jar"
  SNAPSHOT_2=".downloads/$JAR_PREFIX-SNAPSHOT-2.jar"
  CURRENT_REFERENCE=$(readlink "$JAR_PREFIX.jar")

  if [ "$CURRENT_REFERENCE" = "$SNAPSHOT_1" ]; then
    cp "$RELATIVE_PATH" "$SNAPSHOT_2" || exit 1
    ../other/scripts/use_jar.sh "$JAR_PREFIX.jar" "$SNAPSHOT_2" || exit 1
  else
    cp "$RELATIVE_PATH" "$SNAPSHOT_1" || exit 1
    ../other/scripts/use_jar.sh "$JAR_PREFIX.jar" "$SNAPSHOT_1" || exit 1
  fi
else
  echo "Usage: ./set_snapshot.sh solarthing <path to jar>"
fi
