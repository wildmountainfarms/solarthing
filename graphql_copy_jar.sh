#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

# TODO update this to be like copy_jar.sh

if [ "$#" -gt 0 ]; then
  TARGET=$1
  scp program/.downloads/solarthing-graphql-SNAPSHOT.jar "$TARGET:/opt/solarthing/program/.downloads" || exit 1
  ssh "$TARGET" "/opt/solarthing/graphql_use_snapshot.sh" || exit 1
  echo "Success!"
else
  echo You need to give at least one argument!
  exit 1
fi
