#!/usr/bin/env bash
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

other/scripts/generic_copy_jar.sh solarthing-graphql "$@"
