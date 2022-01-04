# This updates the PATH variable to contain solarthing/program/.bin
# There is no shebang at the top of this file because this script must be run with the source command
# Source this file like so:
# source other/linux/path_update.sh

# This file is being sourced, so we have to use $BASH_SOURCE, not $0
if [ -n "${BASH_SOURCE+1}" ]; then
  PATH="$(cd "$(dirname "$BASH_SOURCE")"/../.. && pwd)/program/.bin:$PATH"
else
  # If BASH_SOURCE is not set (maybe we are in zsh or similar, then just assume installed in /opt/solarthing
  PATH="/opt/solarthing/program/.bin:$PATH"
fi
