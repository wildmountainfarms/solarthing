#!/usr/bin/env sh

BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

../.scripts/graphql_download_if_needed.sh
java -jar solarthing-graphql.jar --spring.config.location=config/application.properties
