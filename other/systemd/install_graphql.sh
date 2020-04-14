#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

cp solarthing-graphql.service /etc/systemd/system

