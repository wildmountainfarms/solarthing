#!/usr/bin/env sh

cd /opt || exit 1
git clone --depth=1 --single-branch https://github.com/wildmountainfarms/solarthing.git || exit 1

cd solarthing || exit 1
other/linux/create_user.sh || exit 1
other/linux/update_perms.sh || exit 1
