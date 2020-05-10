#!/usr/bin/env sh

# This has to be run with root

if sysadminctl 2>&1 | grep adminUser; then
  echo Mac OS is not supported yet!
  exit 1
fi

# Add group
if ! id -g solarthing >/dev/null 2>&1; then
  groupadd solarthing
  if ! id -g solarthing >/dev/null 2>&1; then
    echo Unable to create solarthing group
    exit 1
  fi
fi


# Add user
if ! id -u solarthing >/dev/null 2>&1; then
  useradd -r -g solarthing solarthing 2>/dev/null
  if ! id -u solarthing >/dev/null 2>&1; then
    echo Unable to create user
    exit 1
  fi
  passwd -l solarthing || (echo Could not lock solarthing passwd; exit 1)
fi
# Add user to groups
usermod -a -G dialout solarthing || exit 1 # Add solarthing to 'dialout`, which allows access to serial ports

echo Created \"solarthing\" user and group.
echo You can add a user to the group with \"adduser \<USER NAME HERE\> solarthing\".
echo You need to log out and log back in after changing groups.

#echo The groups you probably need to be a part of are \(These are the Raspberry Pis default:\):
#echo solarthing adm dialout cdrom sudo audio video plugdev games users input netdev gpio i2c spi

