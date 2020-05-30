#!/usr/bin/env sh

# Reboot at 01:30 every other day
(crontab -u root -l 2>/dev/null ; echo "30 1 */2 * * /sbin/shutdown -r now") | crontab -u root - || exit 1
