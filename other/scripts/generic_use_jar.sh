#!/usr/bin/env sh
BASEDIR=$(dirname "$0")

if [ "$#" -eq 2 ]; then
  JAR_NAME="$1"
  RELATIVE_PATH=$(realpath --relative-to="$BASEDIR/../../program/" "$2")
  ln -sf "$RELATIVE_PATH" "$BASEDIR/../../program/$JAR_NAME" || exit 1
  echo "Made $JAR_NAME reference $2"
else
  echo "Usage: ./generic_use_jar.sh solarthing.jar <path to jar>"
fi

