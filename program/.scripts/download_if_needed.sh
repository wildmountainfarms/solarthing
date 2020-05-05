#!/usr/bin/env sh

BASEDIR=$(dirname "$0")

if ! ls "$BASEDIR/../solarthing.jar" 2>/dev/null; then
  ../download_solarthing.sh || exit 1
fi
