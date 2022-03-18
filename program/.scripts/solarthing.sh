#!/usr/bin/env sh

BASEDIR=$(dirname "$0")

BASE_CONFIG=$1
if [ -z "$BASE_CONFIG" ] || [ "$BASE_CONFIG" = " " ]; then
  BASE_CONFIG="config/base.json"
fi
echo Using base config = "$BASE_CONFIG"


"$BASEDIR/../.bin/local_log_solarthing" run --base "$BASE_CONFIG"
