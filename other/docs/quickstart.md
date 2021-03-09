# Quickstart
Ready to get started? Run these commands:

If you're using Windows to run SolarThing, check [this](windows_usage.md) out!

If you have a Raspberry Pi and don't know what you're doing, click [here](./raspberry_pi_setup.md).

Run this to quickly get setup (Linux Only):
```shell script
# This clones this repository in /opt, then creates a user and group named solarthing, then updates the ownership of the cloned repository
# This does NOT configure other random files on your system
curl https://raw.githubusercontent.com/wildmountainfarms/solarthing/master/other/linux/clone_install.sh | sudo bash
# If when testing the program, it helps to have permission to edit files owned by the solarthing group, and you will also want to be allowed to use serial ports
sudo usermod -a -G solarthing,dialout,tty,video $USER
```
Or if you want to check out how the **simple** installation works: [click here](../linux/clone_install.sh)

It is also recommended to make sure your timezone is correct. To check the time on your system, run `date`. If it is incorrect and
you are using a Raspberry Pi, you can run `sudo raspi-config` to change it.

If you don't have Java installed, [click here](installing_java.md).

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

* [**Rover Quickstart**](quickstart_rover.md) - Monitors Renogy Rover and other supported products
  * ^^ This is probably the option you want!!
* [**Mate Quickstart**](quickstart_mate.md) - Monitors Outback Mate1/2 devices
* [Request Quickstart](quickstart_request.md) - Upload temperature sensor data

## Configuration Continued
Also learn about [analytics data we collect](./google_analytics.md).
Now you have started to configure your `base.json` file, decide what databases you want to use below. 
Also, note that you can choose to use none of them if you just want to get data before going further.

This quickstart will walk you through setting up CouchDB, but also note that InfluxDB is an option.
See also [database choice](database_choice.md).

### CouchDB Setup
```shell script
cp ../../config_templates/databases/couchdb_template.json config/couchdb.json
# Edit it with your editor of choice
```
Learn how to install and setup CouchDB [here](couchdb_setup.md). That will walk you though editing couchdb.json
and setting up CouchDB.

Note that there are other databases to choose from, but unless you know what you're doing, go ahead and use CouchDB.


### Add databases to base configuration
Edit `config/base.json` with your editor of choice
```json5
{
  //...
  "databases": [
    "config/couchdb.json"
  ]
}
```
Note you can also have multiple databases, and, if you're just testing, you don't need any databases!

Note that if you decided to put them in the [program/config](../../program/config), your databases will look something like this:
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
**NOTE: Once you start the `solarthing-<program type>` service, you should NOT run `./run.sh` until you stop the service**.
You can stop the service like this: `sudo systemctl stop solarthing-<program type>`


Now SolarThing should be set up and running!

---

---

### Run Without systemd service
There are many platforms that don't have systemd: Mac, Windows, and plenty of different Linux Distros.

Right now I am not officially supporting these other options, but SolarThing should run just fine
on them with some additional setup. If you want to suggest an additional platform to support, let us know
on [our issues page](https://github.com/wildmountainfarms/solarthing/issues).

