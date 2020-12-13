#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

# I made this its own script because of how frequently I use it when copying test snapshots via SSH

ln -sf "../.downloads/solarthing-graphql-SNAPSHOT.jar" program/graphql/solarthing-graphql.jar || exit 1
echo "Made solarthing-graphql.jar reference snapshot"
