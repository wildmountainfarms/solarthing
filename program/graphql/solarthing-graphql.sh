#!/usr/bin/env sh

BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

if ! ls "solarthing-graphql.jar" 2>/dev/null; then
  ../download_solarthing.sh || exit 1
fi
java -jar solarthing-graphql.jar --spring.config.location=config/application.properties
