# Quick Start With Outback MATE
If you haven't already, [click here](quickstart.md) to view how to clone this repo and install the service.

Once everything is installed, you're ready to edit the configs. You will cd to the `program` directory.
```
# If you have it installed in /opt
cd /opt/solarthing/program
```

Copy some template config files
```
cp ../config_templates/mate_io.json config/
cp ../config_templates/base/mate_template.json config/base.json
```
Edit `base.json`

Once your configuration is how you want it, you can go back to the [quickstart](quickstart.md) to enable and start the service.

Run the program for the first time
```
java -jar solarthing.jar mate --unique 60 --io config/mate_io.json --database config/influxdb.json
```
