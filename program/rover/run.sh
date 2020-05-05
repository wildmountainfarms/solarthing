#!/usr/bin/env sh

BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

../.scripts/solarthing.sh "$1"
