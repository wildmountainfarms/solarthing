# Quickstart
Ready to get started? Run these commands:

```shell script
cd /opt # You can clone it somewhere else, however if you do that, you will have to change the solarthing.service file if you plan to install the systemd service
sudo git clone --depth=1 https://github.com/wildmountainfarms/solarthing
cd solarthing
sudo other/systemd/install.sh # This only works on Linux systems that use systemd. Using this makes running the program long term very easy
```

## Edit Configurations
Now that the service is installed, all you have to do is edit the configurations in `/opt/solarthing/program/config`.
```shell script
# Navigate to your program directory
cd /opt/solarthing/program
```
NOTE: In each configuration, paths are relative to the `program` directory.

### Program Specific Configuration
You will have to adjust the configuration to your needs and based on the type of program you want to run.

[Mate Quickstart](quickstart_mate.md)

[Rover Quickstart](quickstart_rover.md)

[PVOutput Quickstart](quickstart_pvoutput.md) (requires CouchDB and either the Mate or Rover program)

## Configuration Continued
Now that you have started to configure your `base.json` file, decide what databases you want to use:

### CouchDB "Database"
```shell script
cp ../config_templates/databases/couchdb_template.json config/couchdb.json
# Edit it with your editor of choice
```

### InfluxDB "Database"
```shell script
cp ../config_templates/databases/influxdb_template.json config/influxdb.json
# Edit it with your editor of choice
```
### Latest "Database"
```shell script
cp ../config_templates/databases/latest_save_json_template.json config/latest.json
# Edit it with your editor of choice
```

### Add databases to base configuration
Edit `config/base.json` with your editor of choice
```json5
{
  //...
  "databases": [
    "config/couchdb.json",
    "config/influxdb.json",
    "config/latest.json"
  ]
}
```
You can use 0 or all of the available databases. 

### Enable and Start
Now you can enable and start the service
```shell script
sudo systemctl enable solarthing # Run on boot
sudo systemctl start solarthing # Start the service now
```
If you do not have systemd on your system or did not install the service, you can run solarthing manually by running `java -jar solarthing.jar config/base.json`. You may have to use `sudo`

### Run Without systemd service
If you don't want to run the systemd service, or just want immediate feedback in your terminal,
you can run `sudo ./solarthing.sh` or run `sudo java -jar solarthing.jar config/base.json` (both assume
that your base config is named `base.json` and is located in the `config` directory.)

