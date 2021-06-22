#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

echo "This will edit config files on your system to disable cdc-acm "
echo "Only run this script if you need RS485 support for your EPEver tracer. DO NOT RUN OTHERWISE. THIS DOES NOT MAGICALLY FIX YOUR CABLE NOT WORKING."
# shellcheck disable=SC2034
read -r UNUSED_VARIABLE || exit 1

if ! type dkms 1>/dev/null 2>/dev/null; then
  echo "Need dkms installed! sudo apt-get install dkms"
  exit 1
fi

mkdir .downloads 2>/dev/null
cd .downloads || exit 1

git clone https://github.com/kasbert/epsolar-tracer || exit 1
cd epsolar-tracer/xr_usb_serial_common-1a || exit 1
cp -a ../xr_usb_serial_common-1a /usr/src/
dkms add -m xr_usb_serial_common -v 1a
dkms build -m xr_usb_serial_common -v 1a
dkms install -m xr_usb_serial_common -v 1a
echo blacklist cdc-acm > /etc/modprobe.d/blacklist-cdc-acm.conf
update-initramfs -u
