#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

git pull || (echo "Failed to pull!"; exit 1)
program/download_solarthing.sh || exit 1
program/graphql_download_solarthing.sh || exit 1

FULLDIR=$(pwd)
if [ "$FULLDIR" = "/opt/solarthing" ]; then
  echo
  other/linux/update_perms.sh continue || (echo "The above output may look scary, but you need to run this using sudo to update permissions"; exit 1)
else
  echo Not updating perms because installed in non-standard location
fi
