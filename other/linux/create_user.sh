#!/usr/bin/env sh

# This has to be run with root

if sysadminctl 2>&1 | grep adminUser; then
  echo Mac OS is not supported yet!
  exit 1
fi

# Add group
if ! grep -q solarthing /etc/group >/dev/null 2>&1; then
  groupadd solarthing
  if ! grep -q solarthing /etc/group >/dev/null 2>&1; then
    echo Unable to create solarthing group
    exit 1
  fi
fi


# Add user
if ! id -u solarthing >/dev/null 2>&1; then
  useradd -r -g solarthing -G dialout,tty,video solarthing 2>/dev/null # create user with correct groups
  if ! id -u solarthing >/dev/null 2>&1; then
    echo Unable to create user
    exit 1
  fi
  passwd -l solarthing || (echo Could not lock solarthing passwd; exit 1)
fi
# Add user to groups
usermod -a -G dialout,tty,video solarthing || exit 1 # add groups to user just to make sure they have all needed groups
usermod -a -G gpio solarthing 2>/dev/null && echo Added solarthing to gpio || echo No gpio group
# dialout and tty is for serial port access
# video is for 'vcgencmd' command

echo Created \"solarthing\" user and group.
echo You can add a user to the group with \"adduser \<USER NAME HERE\> solarthing\".
echo You need to log out and log back in after adding groups.

#echo solarthing adm dialout cdrom sudo audio video plugdev games users input netdev gpio i2c spi

