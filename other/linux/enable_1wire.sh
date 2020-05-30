#!/usr/bin/env sh

grep -qxF 'w1-gpio' /etc/modules || echo 'w1-gpio' >> /etc/modules
grep -qxF 'w1-therm' /etc/modules || echo 'w1-therm' >> /etc/modules
grep -qxF 'dtoverlay=w1-gpio-pullup,gpiopin=4' /boot/config.txt || echo 'dtoverlay=w1-gpio-pullup,gpiopin=4' >> /boot/config.txt

echo 1-Wire should be enabled now. You should reboot
