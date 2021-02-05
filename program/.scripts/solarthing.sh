#!/usr/bin/env sh

BASEDIR=$(dirname "$0")

BASE_CONFIG=$1
if [ -z "$BASE_CONFIG" ] || [ "$BASE_CONFIG" = " " ]; then
  BASE_CONFIG="config/base.json"
fi
echo Using base config = "$BASE_CONFIG"

"$BASEDIR/download_if_needed.sh" || exit 1

if ls log4j2.xml 1>/dev/null 2>&1; then
  export LOG4J_log4j2_configurationFile=log4j2.xml
  echo Using log4j2.xml file!
fi

if java --add-opens 2>&1 | grep 'requires modules' >/dev/null; then
  java --add-opens=java.base/java.lang.invoke=ALL-UNNAMED -jar "$BASEDIR/../solarthing.jar" --base "$BASE_CONFIG"
else
  java -jar "$BASEDIR/../solarthing.jar" --base "$BASE_CONFIG"
fi

