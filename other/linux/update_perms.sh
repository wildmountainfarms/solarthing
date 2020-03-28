#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR/../.." || exit 1

echo This updates the permission of all the files in the solarthing repository. Modifications will be made to the directory printed below:
pwd || exit 1
echo "Press enter to continue"
read -r UNUSED_VARIABLE || exit 1

echo Updating...
chmod -R g+rw . || exit 1 # all files and directories get group read/write permissions
find . -type d -exec chmod g+s {} \; || exit 1 # When you create a file in any directory, its group should be that of its directory
chgrp -R solarthing . || exit 1
echo Done
