#!/usr/bin/env sh
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1

SERVICE=$1
DESTINATION_PATH="/etc/systemd/system/solarthing-$SERVICE.service"
SOLARTHING_ROOT=$(cd ../.. && pwd)
PROGRAM_DIRECTORY="$SOLARTHING_ROOT/program/$SERVICE"

if [ ! -f "$PROGRAM_DIRECTORY/run.sh" ]; then
  echo "$SERVICE is not a valid service!"
  exit 1
fi

if [ "$SERVICE" = "graphql" ]; then
  TEMPLATE_FILE="solarthing-graphql.service.template"
elif [ "$SERVICE" = "monitor-service" ]; then
  TEMPLATE_FILE="monitor-service.service.template"
else
  TEMPLATE_FILE="solarthing.service.template"
fi
echo "SolarThing root is $SOLARTHING_ROOT"
# Thanks https://stackoverflow.com/a/2705678/5434860
ESCAPED_SOLARTHING_ROOT=$(printf '%s\n' "$SOLARTHING_ROOT" | sed -e 's/[]\/$*.^[]/\\&/g');
sed -e "s/##name##/$SERVICE/g" -e "s/##solarthing_root##/${ESCAPED_SOLARTHING_ROOT}/" "$TEMPLATE_FILE" >"$DESTINATION_PATH" || exit 1
echo Added service
systemctl daemon-reload || exit 1
echo "Reloaded systemctl"
