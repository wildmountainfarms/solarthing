#!/usr/bin/env sh

BASEDIR=$(dirname "$0")

if [ "$USER" != "root" ] && [ "$USER" != "solarthing" ]; then
  echo "Must run with sudo or with solarthing user!"
  exit 1
fi

if [ "$USER" = "solarthing" ]; then
  "$BASEDIR/raw_sdkman.sh" "$@"
else
  sudo -n -u solarthing "$BASEDIR/raw_sdkman.sh" "$@"
fi
