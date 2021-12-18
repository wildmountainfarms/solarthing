# This updates the PATH variable to contain solarthing/program/.bin
# There is no shebang at the top of this file because this script must be run with the source command
# Source this file like so:
# source other/linux/path_update.sh

# This file is being sourced, so we have to use $BASH_SOURCE, not $0
PATH="$(cd "$(dirname "$BASH_SOURCE")"/../.. && pwd)/program/.bin:$PATH"
