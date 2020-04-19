#!/usr/bin/env sh

# Reboot at 01:30 on every Monday
(crontab -l || exit 1; echo "30 1 * * 1 shutdown -r now")| crontab -u root - || exit 1
