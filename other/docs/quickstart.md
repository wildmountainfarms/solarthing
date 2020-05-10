# Quickstart
Ready to get started? Run these commands:

If you're using Windows, check [this](windows_usage.md) out!

Run this to quickly get setup (Linux Only):
```shell script
# This clones this repository in /opt, then creates a user and group named solarthing, then updates the ownership of the cloned repository
# This does NOT configure other random files on your system
curl https://raw.githubusercontent.com/wildmountainfarms/solarthing/master/other/linux/clone_install.sh | sudo bash
# If when testing the program, it helps to have permission to edit files owned by the solarthing group, and you will also want to be allowed to use serial ports
sudo usermod -a -G solarthing,dialout $USER
```
Or if you want to check out how the **simple** installation works: [click here](../linux/clone_install.sh)

## Edit Configurations
Now SolarThing is installed, all you have to do is edit configurations in `/opt/solarthing/program/<mate|rover|graphql|pvoutput>/config`.
```shell script
# Navigate to your program's directory
cd /opt/solarthing/program/<mate|rover|graphql|pvoutput>
```
NOTE: In each program, paths are relative to their respective directory.<br/>
For instance if you are running the `rover` program, paths will be relative to [program/rover](../../program/rover).

### Program Specific Configuration
You will have to adjust the configuration to your needs and based on the type of program you want to run.

[Mate Quickstart](quickstart_mate.md)

[Rover Quickstart](quickstart_rover.md)

[PVOutput Quickstart](quickstart_pvoutput.md) (requires CouchDB and either the Mate or Rover program)

## Configuration Continued
Also learn about [analytics data we collect](./google_analytics.md).
Now you have started to configure your `base.json` file, decide what databases you want to use below. 
Also, note that you can choose to use none of them if you just want to get data before going further.

### CouchDB "Database"
```shell script
cp ../../config_templates/databases/couchdb_template.json config/couchdb.json
# Edit it with your editor of choice
```

### InfluxDB "Database"
```shell script
cp ../../config_templates/databases/influxdb_template.json config/influxdb.json
# Edit it with your editor of choice
```
### Latest "Database"
```shell script
cp ../../config_templates/databases/latest_save_json_template.json config/latest.json
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
If you are just testing and don't want to setup a database, you don't have to!

Note that if you decided to put them in the [program/config](../../program/config), your databases will look like this:
```json5
{
  //...
  "databases": [
    "../config/couchdb.json",
    "../config/influxdb.json",
    "../config/latest.json"
  ]
}
```

### Running for the first time
Once you have your configuration the way you want it, it's time for a test run. Run this command:
```shell script
# Using 'sudo' may be required depending on how you set it up
./run.sh
```
Note that you can also do this:
```shell script
./run.sh config/base.json # you can specify your base config if you want
```

If you got an error, you can look at this (hopefully) helpful [debug errors](debug_errors.md) page.

### Running in the long run
If your Linux distro uses systemd, you can go [here](../systemd/README.md) to learn how to install the service. Then run these commands.
```shell script
sudo systemctl enable solarthing-<program type> # Run on boot
sudo systemctl start solarthing-<program type> # Start the service now
```

### Run Without systemd service
There are many platforms that don't have systemd: Mac, Windows, and plenty of different Linux Distros.

Right now I am not officially supporting these other options, but SolarThing should run just fine
on them with some additional setup. If you want to suggest an additional platform to support, let us know
on [our issues page](https://github.com/wildmountainfarms/solarthing/issues).

