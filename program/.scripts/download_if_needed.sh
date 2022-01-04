#!/usr/bin/env sh

BASEDIR=$(dirname "$0")

if ! ls "$BASEDIR/../solarthing.jar" 1>/dev/null 2>&1; then
  "$BASEDIR/../download_solarthing.sh" || exit 1
fi
