#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

echo "This will edit config files on your system to disable cdc-acm "
# shellcheck disable=SC2034
read -r UNUSED_VARIABLE || exit 1

mkdir .downloads 2>/dev/null
cd .downloads || exit 1

git clone https://github.com/kasbert/epsolar-tracer || exit 1
cd epsolar-tracer/xr_usb_serial_common-1a || exit 1
echo TODO

