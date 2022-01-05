# This file must be sourced to take affect
# Must be sourced by bash!

if [ -z "${SOLARTHING_ROOT+1}" ]; then
  echo "ERROR! SOLARTHING_ROOT not set!" 1>&2
  return
fi

if [ -z "${BASH_VERSION+1}" ]; then
  echo "ERROR! This must be sourced using bash!" 1>&2
  return
fi


if [ -z "${SDKMAN_DIR+1}" ] || [ -n "${USE_SOLARTHING_SDKMAN+1}" ]; then
  # Only set SDKMAN_DIR if it is not already set
  # This allows people to use their own directory, or set it to blank to manually disable
  export SDKMAN_DIR=$(cd "$SOLARTHING_ROOT/program/.downloads/sdkman" && pwd)
fi

if [ -f "$SDKMAN_DIR/bin/sdkman-init.sh" ]; then
  echo "Using sdkman installation with SDKMAN_DIR=$SDKMAN_DIR"
  source "$SDKMAN_DIR/bin/sdkman-init.sh"
fi

