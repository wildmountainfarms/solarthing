
# This has to be run with root

groupadd solarthing || exit 1
echo Created \"solarthing\" group you can add a user to the group with \"adduser \<USER NAME HERE\> solarthing\". You may need to reboot

echo The groups you probably need to be a part of are \(These are the Raspberry Pis default:\):
echo solarthing adm dialout cdrom sudo audio video plugdev games users input netdev gpio i2c spi
