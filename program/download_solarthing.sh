#!/usr/bin/env sh

BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

# shellcheck source=.scripts/export_version.sh
. .scripts/export_version.sh || exit 1

if ! ls ".downloads/solarthing-$SOLARTHING_VERSION.jar" 1>/dev/null 2>&1; then
  echo Going to download version: "$SOLARTHING_VERSION"
  mkdir ".downloads" 2>/dev/null
  (cd .downloads && wget "https://github.com/wildmountainfarms/solarthing/releases/download/v$SOLARTHING_VERSION/solarthing-$SOLARTHING_VERSION.jar") || exit 1
else
  echo Already downloaded
fi
ln -sf ".downloads/solarthing-$SOLARTHING_VERSION.jar" solarthing.jar || exit 1
echo Using SolarThing Version: "$SOLARTHING_VERSION"
