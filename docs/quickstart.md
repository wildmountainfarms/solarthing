# Quickstart
Ready to get started? Run these commands:

```shell script
cd /opt
sudo git clone https://github.com/wildmountainfarms/solarthing
cd solarthing
sudo systemd/install.sh
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

First, decide what databases you want to use:
### CouchDB
```shell script
cp ../config_templates/databases/couchdb_template.json config/couchdb.json
# Edit it with your editor of choice
```

### InfluxDB
```shell script
cp ../config_templates/databases/influxdb_template.json config/influxdb.json
# Edit it with your editor of choice
```
### Add databases to base configuration
TODO document this

### Enable and Start
Now you can enable and start the service
```shell script
sudo systemctl enable solarthing # Run on boot
sudo systemctl start solarthing # Start the service now
```


