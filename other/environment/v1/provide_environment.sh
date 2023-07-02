# This file is designed to be source'd, so it is not executable.
if [ "$SHELL" != "/bin/bash" ]; then
  echo "This script requires bash and may not be compatible with other shells. BASH=$BASH SHELL=$SHELL"
fi

latest_release() {

  if ! JSON_DATA="$(curl -sS https://api.github.com/repos/wildmountainfarms/solarthing/releases/latest)"; then
    # If the exit code is not 0, then error information was printed to stderr.
    return 1
  fi
  TAG_NAME_JSON="$(echo "$JSON_DATA" | jq '.tag_name')"
  if [ "$TAG_NAME_JSON" = "null" ]; then
    ERROR_MESSAGE_JSON="$(echo "$JSON_DATA" | jq '.message')"
    if [ "$ERROR_MESSAGE" = "null" ]; then
      echo "Unknown error. Printing JSON" >&2
      echo "$JSON_DATA" >&2
    else
      ERROR_MESSAGE="$(echo "$ERROR_MESSAGE_JSON" | jq -r)"
      echo "Got error: $ERROR_MESSAGE" >&2
    fi
    return 1
  fi
  TAG_NAME="$(echo "$TAG_NAME_JSON" | jq -r)"
  RELEASE="$(echo "$TAG_NAME" | cut -c 2-)"
  if [ -z "$RELEASE" ]; then
    return 1
  fi

  echo "$RELEASE"
}
download() {
  # arguments: 1:version
  if [[ $# -ne 1 ]]; then
    echo "Must pass 1 argument" >&2
    return 1
  fi
  version="$1"
  jar_name="$(jar_name "-$version")"
  echo "Downloading version: $version with jar name: $jar_name"
  mkdir -p "$SOLARTHING_DOWNLOAD_DIRECTORY"
  # -J to preserve "last modified"
  curl -sSfLJ --connect-timeout 10 --max-time 300 -o "$SOLARTHING_DOWNLOAD_DIRECTORY/$jar_name" "https://github.com/wildmountainfarms/solarthing/releases/download/v$version/$jar_name"
}
is_valid_jar() {
  if [[ $# -ne 1 ]]; then
    echo "Must pass 1 argument" >&2
    return 1
  fi
  zip -qT "$1" 1>/dev/null 2>&1
  return $?
}
on_download_failure() {
  if [ ! -v SOLARTHING_IGNORE_DOWNLOAD_FAILURES ]; then
    echo "Not ignoring download failure"
    exit 213
  fi
}

jar_name() {
  if [[ $# -gt 1 ]]; then
    echo "Must pass 0 or 1 arguments!" >&2
    return 1
  fi
  suffix="$1"
  variant="${SOLARTHING_VARIANT:-client}"
  case $variant in
    "client")
      echo "solarthing$suffix.jar"
      ;;
    "server" | "graphql")
      echo "solarthing-graphql$suffix.jar"
      ;;
    *)
      echo "Unknown variant: $variant" >&2
      return 1
      ;;
  esac
}

if [ -v SOLARTHING_VERSION ]; then
  # This function downloads the jar file if it needs to, and then prints out the name of the jar file
  if [[ ! -v SOLARTHING_DOWNLOAD_DIRECTORY ]]; then
    echo "SOLARTHING_VERSION must be set"
    exit 1
  fi
  case $SOLARTHING_VERSION in
    "latest")
      link_jar="$SOLARTHING_DOWNLOAD_DIRECTORY/$(jar_name '-latest')"
      if ! is_valid_jar "$link_jar" || [ -v SOLARTHING_CHECK_DOWNLOAD ]; then
        echo "Fetching latest version from GitHub"
        if release="$(latest_release)"; then
          if is_valid_jar "$SOLARTHING_DOWNLOAD_DIRECTORY/$(jar_name "-$release")"; then
            echo "Already downloaded"
            ln -sf "$(jar_name "-$release")" "$link_jar"
          elif download "$release"; then
            echo "Downloaded successfully"
            # The link_jar file ends up in the same directory as what it is pointing at,
            #   so the path to the jar file being pointed at is pretty easy to compute.
            ln -sf "$(jar_name "-$release")" "$link_jar"
          else
            echo "Failed to download"
            on_download_failure
          fi
        else
          echo "Failed to retrieve latest version"
          on_download_failure
        fi
        if ! is_valid_jar "$link_jar"; then
          echo "Download failure occurred. No fallback jar to use. Exiting..."
          exit 213
        fi
      fi
      export SOLARTHING_JAR="$link_jar"
      ;;
    *)
      if [ -v SOLARTHING_CHECK_DOWNLOAD ]; then
        echo "(Warning) Setting SOLARTHING_CHECK_DOWNLOAD for a pinned version has no effect"
      fi
      jar_file="$SOLARTHING_DOWNLOAD_DIRECTORY/$(jar_name "-$SOLARTHING_VERSION")"
      if ! is_valid_jar "$jar_file"; then
        if download "$SOLARTHING_VERSION"; then
          echo "Downloaded successfully"
        else
          echo "Failed to download"
          exit 213
        fi
      fi
      export SOLARTHING_JAR="$jar_file"
      ;;
  esac
else
  echo "SOLARTHING_VERSION not set, so SOLARTHING_JAR will not be set"
fi
