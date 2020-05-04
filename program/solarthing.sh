#!/usr/bin/env sh

BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

if java --add-opens 2>&1 | grep 'requires modules' >/dev/null; then
  java --add-opens=java.base/java.lang.invoke=ALL-UNNAMED -jar solarthing.jar "config/base.json"
else
  java -jar solarthing.jar "config/base.json"
fi

