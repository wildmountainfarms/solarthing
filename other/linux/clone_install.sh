#!/usr/bin/env sh

cd /opt || exit 1
if ! type git 1>/dev/null 2>/dev/null; then
  if ! type apt-get 1>/dev/null 2>/dev/null; then
    echo "Git is not installed, and this system does not have apt-get, so install git on your own then rerun this script"
    exit 1
  fi
  echo "Git is not installed, so we will try to install it now"
  apt-get update || exit 1
  apt-get install -y git || exit 1
fi
git clone --depth=1 --single-branch https://github.com/wildmountainfarms/solarthing.git || exit 1

cd solarthing || exit 1
other/linux/create_user.sh || exit 1
other/linux/update_perms.sh continue || exit 1
