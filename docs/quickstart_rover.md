# Quick Start With Renogy Rover
If you haven't already, [click here](quickstart.md) to view how to clone this repo and install the service.

Once everything is installed, you're ready to edit the configs. You will cd to the `program` directory.
```
# If you have it installed in /opt
cd /opt/solarthing/program
```

Copy some template config files
```
cp ../config_templates/default_linux_serial.json config/
cp ../config_templates/base/rover_template.json config/base.json
```
Edit `base.json`
```json
{
  "type": "rover",
  "io": "config/default_linux_serial.json"
}
```
