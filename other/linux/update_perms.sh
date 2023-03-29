#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR/../.." || exit 1

if [ "$USER" != "root" ]; then
  echo "You must be root to update permissions!"
  exit 1
fi

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
if ! git config --global --get-all safe.directory | grep -qx "$(pwd)"; then
    git config --global --add safe.directory $(pwd) || echo "Could not add $(pwd) as safe directory for root user"
fi
# Run git config after so that there is no chance of permission errors
git config --local core.sharedRepository group || (echo "Please report this error!"; exit 1)
echo Done
echo "Permissions for $(pwd) have been updated. Please run the following command without sudo. This will allow your user to access the repository easily."
echo "  git config --global --add safe.directory /opt/solarthing"

# If you screw something up while messing with permissions, this may help:
# diff <(find . -perm /g+x -type f) <(find . -perm /o+x -type f) | grep \< | tr -d '< ' | sudo xargs chmod g-x
