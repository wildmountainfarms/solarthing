#!/usr/bin/env bash

BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

../.scripts/graphql_download_if_needed.sh

# we are already cd'd to $BASEDIR
export SOLARTHING_ROOT="../.."
source ../.scripts/use_sdkman_if_available.sh # requires bash

if java --add-opens 2>&1 | grep 'requires modules' >/dev/null; then
  java --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.lang.invoke=ALL-UNNAMED -jar ../solarthing-graphql.jar --spring.config.location=config/application.properties
else
  java -jar ../solarthing-graphql.jar --spring.config.location=config/application.properties
fi
