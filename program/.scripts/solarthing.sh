#!/usr/bin/env sh

BASEDIR=$(dirname "$0")

BASE_CONFIG=$1
if [ -z "$BASE_CONFIG" ] || [ "$BASE_CONFIG" = " " ]; then
  BASE_CONFIG="config/base.json"
fi
echo Using base config = "$BASE_CONFIG"

"$BASEDIR/download_if_needed.sh" || exit 1

if java --add-opens 2>&1 | grep 'requires modules' >/dev/null; then
  java --add-opens=java.base/java.lang.invoke=ALL-UNNAMED -jar "$BASEDIR/../solarthing.jar" "config/base.json"
else
  java -jar "$BASEDIR/../solarthing.jar" "config/base.json"
fi

