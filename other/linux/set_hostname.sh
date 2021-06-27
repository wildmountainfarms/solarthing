#!/usr/bin/env sh


if [ "$#" -gt 0 ]; then
  NEW_HOSTNAME=$1
  echo "Going set the hostname to: $NEW_HOSTNAME"
  echo "Press enter to continue"
  # shellcheck disable=SC2034
  read -r UNUSED_VARIABLE || exit 1
  hostnamectl set-hostname "$NEW_HOSTNAME" || exit 1
  echo "127.0.0.1 $NEW_HOSTNAME" >> /etc/hosts || exit 1
  echo "Success!"
else
  echo You need to give at least one argument!
  exit 1
fi
