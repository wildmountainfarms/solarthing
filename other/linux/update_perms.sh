#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR/../.." || exit 1

echo This updates the permission of all the files in the solarthing repository. Modifications will be made to the directory printed below:
pwd || exit 1
if [ "$#" -gt 0 ]; then
  if [ "$1" = "continue" ]; then
    echo continuing
  else
    echo unknown argument "$1"
    exit 1
  fi
else
  echo "Press enter to continue"
  # shellcheck disable=SC2034
  read -r UNUSED_VARIABLE || exit 1
fi

echo Updating...
chmod -R g+rw . || exit 1 # all files and directories get group read/write permissions
find . -type d -exec chmod g+xs {} \; || exit 1 # When you create a file in any directory, its group should be that of its directory
chown -R solarthing:solarthing . || exit 1
# Run git config after so that any permission errors can be fixed before altering this property
git config --local core.sharedRepository group || exit 1
echo Done

# If you screw something up while messing with permissions, this may help:
# diff <(find . -perm /g+x -type f) <(find . -perm /o+x -type f) | grep \< | tr -d '< ' | sudo xargs chmod g-x
