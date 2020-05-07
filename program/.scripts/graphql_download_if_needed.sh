#!/usr/bin/env sh

BASEDIR=$(dirname "$0")

if ! ls "$BASEDIR/../graphql/solarthing-graphql.jar" 1>/dev/null 2>&1; then
  "$BASEDIR"/../graphql_download_solarthing.sh || exit 1
fi
