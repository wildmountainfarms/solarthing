#!/usr/bin/env bash

if [ "$USER" != "solarthing" ]; then
  echo "You must use the solarthing user!"
  exit 1
fi

BASEDIR=$(dirname "$0")
SOLARTHING_ROOT=$(cd "$BASEDIR/../.." && pwd)

export SDKMAN_DIR="$SOLARTHING_ROOT/program/.downloads/sdkman" || exit 1
source "$SOLARTHING_ROOT/program/.downloads/sdkman/bin/sdkman-init.sh" || exit 1
sdk "$@"
