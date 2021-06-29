#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

if [ "$#" -gt 0 ]; then
  TARGET=$1
  scp program/.downloads/solarthing-SNAPSHOT.jar "$TARGET:/opt/solarthing/program/.downloads" || exit 1
  ssh "$TARGET" "/opt/solarthing/use_snapshot.sh" || exit 1
  echo "Success!"
else
  echo You need to give at least one argument!
  exit 1
fi
