#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

git pull
if [ "$BASEDIR" = "/opt/solarthing" ]; then
  other/linux/update_perms.sh continue
else
  echo Not updating perms because installed in non-standard location
fi
program/download_solarthing.sh
program/graphql_download_solarthing.sh
