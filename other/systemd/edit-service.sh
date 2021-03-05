#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

SERVICE=$1
if [ -z ${SERVICE+x} ]; then
  echo "You need to specify a service!"
  exit 1
fi

NAME="solarthing-$SERVICE.service"
FILE_PATH="/etc/systemd/system/$NAME"

FILE_HASH=$(md5sum "$FILE_PATH" 2>/dev/null)

if [ -z ${EDITOR+x} ]; then
  vi "$FILE_PATH" || exit 1
else
  $EDITOR "$FILE_PATH" || exit 1
fi

if [ ! "$FILE_HASH" = "$(md5sum "$FILE_PATH" 2>/dev/null)" ]; then
  systemctl daemon-reload || exit 1
  echo "Reloaded systemctl"
fi
