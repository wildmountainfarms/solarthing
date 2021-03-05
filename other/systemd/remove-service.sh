#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

SERVICE=$1
NAME="solarthing-$SERVICE.service"
FILE_PATH="/etc/systemd/system/$NAME"

rm "$FILE_PATH" || exit 1
echo Removed
systemctl daemon-reload || exit 1
echo "Reloaded systemctl"
