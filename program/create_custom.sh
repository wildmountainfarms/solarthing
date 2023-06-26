#!/usr/bin/env sh

echo "Hey! This script is deprecated"

BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1
if [ "$#" -eq 0 ]; then
  echo "Must have one argument!"
  exit 1
fi

NAME="$1"

mkdir "$NAME" || exit 1
mkdir "$NAME/config" || exit 1
ln -sf "../request/run.sh" "$NAME/run.sh" || exit 1
