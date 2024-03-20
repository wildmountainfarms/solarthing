#!/usr/bin/env bash
# This script made by Lavender Shannon for SolarThing (https://github.com/wildmountainfarms/solarthing)
# This script is used for installing drivers onto Linux systems to allow RS485 communication to work.
# Designed for Linux kernel versions < 6.6
# If you are running 6.6 or above, this script is not necessary (https://github.com/kasbert/epsolar-tracer/issues/57#issuecomment-1807195710)
#
# This script piggybacks off of the work done here: https://github.com/kasbert/epsolar-tracer
set -e


echo "This script made by Lavender Shannon. This script uses drivers from https://github.com/kasbert/epsolar-tracer"
echo "This script is meant to be used for SolarThing users, but can be used for any device requiring RS485 drivers."
echo "Please report issues with this script at https://github.com/wildmountainfarms/solarthing/issues"
echo "This script requires that you have dkms installed and kernel headers installed, and either update-ramfs installed or update-tirfs installed."
echo

# ================ Root user check ==============
if [ "$EUID" -ne 0 ]; then
  echo "You are not running the script as root. Please use sudo!"
  echo "If you would like to ignore this warning and continue, press enter now"
  # shellcheck disable=SC2034
  read -r UNUSED_VARIABLE
fi


# ================= Kernel version check and continue confirmation ==============
# shellcheck disable=SC2016
echo 'Going to run `uname -a` below.'
uname -a
echo 'This script is only necessary for Linux kernel versions < 6.6. If your version is >= 6.6, please stop the script now (CTRL+C).'
echo "Press enter to continue"
# shellcheck disable=SC2034
read -r UNUSED_VARIABLE

# ========= Requirements checks ============

if ! type dkms 1>/dev/null; then
  echo "Need dkms installed! sudo apt-get install dkms"
  exit 1
fi

initramfs_update_command=(
  echo
  "<You need to install and run an update initramfs implementation now>"
)

if type update-initramfs; then
  initramfs_update_command=(
    update-initramfs
    -u
  )
  echo "Found update-initramfs"
elif type update-tirfs; then
  initramfs_update_command=(update-tirfs)
  echo "Found update-tirfs"
else
  echo "update-initramfs and update-tirfs not found on the system. Please install one of them. Run one of these commands:"
  echo -e "\tsudo apt install initramfs-tools"
  echo -e "\tsudo apt install tiny-initramfs"
  echo "Please note you may also have to install the kernel headers for your device. For example, on a Raspberry Pi:"
  echo -e "\tsudo apt install raspberrypi-kernel-headers"
  echo
  echo "If you would like to ignore this warning and continue, press enter now"
  # shellcheck disable=SC2034
  read -r UNUSED_VARIABLE
fi


# ============== Start temporary directory usage ==============

# Thanks https://stackoverflow.com/a/34676160/5434860
# mktemp -d will, by default, create a temporary directory in /tmp
WORK_DIR=$(mktemp -d)
if [[ ! "$WORK_DIR" || ! -d "$WORK_DIR" ]]; then
  echo "Could not create temp dir"
  exit 1
fi
function cleanup {
  rm -rf "$WORK_DIR"
  echo "Deleted temp working directory $WORK_DIR"
}
trap cleanup EXIT

#echo "Work dir is $WORK_DIR"

git clone --depth=1 --single-branch --branch=master "https://github.com/kasbert/epsolar-tracer" "$WORK_DIR"

echo
echo "Successfully cloned epsolar-tracer repository. Continuing..."
echo

# ========== Remove existing =============
echo "Going to try to remove existing module if it exists..."
set +e
dkms_remove_output=$(dkms remove xr_usb_serial_common/1a --all 2>&1)
dkms_remove_exit_code=$?
set -e
if [ $dkms_remove_exit_code -eq 3 ]; then
  echo "xr_usb_serial_common is not currently installed. No need to remove"
elif [ $dkms_remove_exit_code -eq 1 ]; then
  # Exit codes and messages here: https://github.com/dell/dkms/issues/306
  if echo "$dkms_remove_output" | grep "must be root" >/dev/null; then
    echo "You need to run this command as root!"
    exit 1
  fi
  echo "Exit code of 1 with this output:"
  echo "$dkms_remove_output"
  exit 1
elif [ $dkms_remove_exit_code -eq 0 ]; then
  echo "Successfully removed xr_usb_serial_common"
else
  echo "Unknown exit code from dkms remove: $dkms_remove_exit_code"
  echo "Got this output:"
  echo "$dkms_remove_output"
  echo
  echo "Consider reporting this issue at https://github.com/wildmountainfarms/solarthing/issues"
  echo "Press enter to ignore this error and continue anyway"
  # shellcheck disable=SC2034
  read -r UNUSED_VARIABLE
fi

# ========= Install ==========
echo "Will now install the driver..."
cp -a "$WORK_DIR/xr_usb_serial_common-1a" /usr/src/
echo -e "\tAdding module..."
dkms add -m xr_usb_serial_common -v 1a
echo -e "\tBuilding module..."
dkms build -m xr_usb_serial_common -v 1a || (echo -e "\nBuilding failed. Please make sure you have headers for your system installed (e.i. sudo apt install raspberrypi-kernel-headers)"; exit 1)
echo -e "\tInstalling module"
dkms install -m xr_usb_serial_common -v 1a
echo "Blacklisting cdc-acm by creating file /etc/modprobe.d/blacklist-cdc-acm.conf"
echo blacklist cdc-acm > /etc/modprobe.d/blacklist-cdc-acm.conf || echo "Failed to blacklist!"

echo "Going to update initramfs"
"${initramfs_update_command[@]}"

echo "Successfully installed! Please restart your device for these changes to take effect."
