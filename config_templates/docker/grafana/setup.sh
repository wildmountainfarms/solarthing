#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

mkdir data
chown 2000:2000 data || exit 1

mkdir -p provisioning/datasources
mkdir provisioning/plugins
mkdir provisioning/notifiers
mkdir provisioning/dashboards
chown -R 2000:2000 provisioning || exit 1

echo "Completed creating directories successfully. If they already existed, there should be errors above. That is normal"
