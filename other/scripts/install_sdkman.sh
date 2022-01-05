#!/usr/bin/env sh

if [ "$USER" != "root" ]; then
  echo "You must be root to install!"
  exit 1
fi

BASEDIR=$(dirname "$0")
SOLARTHING_ROOT=$(cd "$BASEDIR/../.." && pwd)
export SDKMAN_DIR="$SOLARTHING_ROOT/program/.downloads/sdkman"

echo "Going to install sdkman for solarthing root=$SOLARTHING_ROOT"
echo "SDKMAN_DIR=$SDKMAN_DIR"

curl -s "https://get.sdkman.io?rcupdate=false" | sudo --non-interactive --preserve-env="SDKMAN_DIR" bash || (echo "Install failed"; exit 1) || exit 1

echo "Installed, now updating permissions."

# Basically, just do what update_perms.sh does here, but only to the SDKMAN directory
chmod -R g+rw "$SDKMAN_DIR" || exit 1 # all files and directories get group read/write permissions
find "$SDKMAN_DIR" -type d -exec chmod g+xs {} \; || exit 1 # When you create a file in any directory, its group should be that of its directory
chown -R solarthing:solarthing "$SDKMAN_DIR" || exit 1
echo "Finished installing"
