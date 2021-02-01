#!/usr/bin/env sh

export SOLARTHING_VERSION="2021.1.2"

if [ -n "${SOLARTHING_VERSION_OVERRIDE+x}" ]; then
  echo Using override
  export SOLARTHING_VERSION="$SOLARTHING_VERSION_OVERRIDE"
fi
