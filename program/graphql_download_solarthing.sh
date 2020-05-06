#!/usr/bin/env sh

BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

# shellcheck source=.scripts/export_version.sh
. "$BASEDIR"/.scripts/export_version.sh || exit 1

if ! ls ".downloads/solarthing-graphql-$SOLARTHING_VERSION.jar" 2>/dev/null; then
  echo Going to download version: "$SOLARTHING_VERSION"
  mkdir ".downloads" 2>/dev/null
  (cd .downloads && wget "https://github.com/wildmountainfarms/solarthing/releases/download/v$SOLARTHING_VERSION/solarthing-graphql-$SOLARTHING_VERSION.jar") || exit 1
fi
ln -sf "../.downloads/solarthing-graphql-$SOLARTHING_VERSION.jar" graphql/solarthing-graphql.jar || exit 1
echo Using SolarThing Version: "$SOLARTHING_VERSION"
