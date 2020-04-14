#!/usr/bin/env sh

BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

java -jar solarthing-graphql.jar --spring.config.location=config/application.properties
