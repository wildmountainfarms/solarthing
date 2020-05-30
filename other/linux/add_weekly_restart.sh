#!/usr/bin/env sh

# Reboot at 01:30 on every Monday
(crontab -u root -l 2>/dev/null ; echo "30 1 * * 1 /sbin/shutdown -r now") | crontab -u root - || exit 1
