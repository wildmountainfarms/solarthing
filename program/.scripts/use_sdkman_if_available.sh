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


if [ -z "${NO_SOLARTHING_SDKMAN+1}" ]; then
  SDKMAN_DIR="$SOLARTHING_ROOT/program/.downloads/sdkman"
  if [ -f "$SDKMAN_DIR/bin/sdkman-init.sh" ]; then
    SDKMAN_DIR=$(cd "$SDKMAN_DIR" && pwd)
    echo "Using sdkman installation with SDKMAN_DIR=$SDKMAN_DIR"
    export SDKMAN_DIR
    source "$SDKMAN_DIR/bin/sdkman-init.sh"
  fi
fi
