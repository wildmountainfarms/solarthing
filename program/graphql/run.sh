#!/usr/bin/env bash

BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

../.scripts/graphql_download_if_needed.sh

# we are already cd'd to $BASEDIR
export SOLARTHING_ROOT="../.."
source ../.scripts/use_sdkman_if_available.sh # requires bash

java -jar ../solarthing-graphql.jar --spring.config.location=config/application.properties
