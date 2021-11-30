#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

# These settings are good for headless setups

# This writes a lot of log messages, so disable it
systemctl disable rng-tools
systemctl stop rng-tools

systemctl disable bluetooth
systemctl stop bluetooth

systemctl disable hciuart  # this is related to bluetooth
systemctl stop hciuart

# We don't need anything detecting keyboard inputs for a headless RPi server
systemctl disable triggerhappy
systemctl stop triggerhappy

# We don't need anything sound related
systemctl disable alsa-utils
systemctl stop alsa-utils


# Disable loading of modules responsible for bt:
# sudo echo "dtoverlay=pi3-disable-bt" >> /boot/config.txt
# Remove bluetooth completely:
# sudo apt-get purge bluez -y

# To see what is taking up a lot of time on boot, run this command
# systemd-analyze blame
