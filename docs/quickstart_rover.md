# Quick Start With Renogy Rover
If you haven't already, [click here](quickstart.md) to view how to clone this repo and install the service.

Once everything is installed, you're ready to edit the configs. You will cd to the `program` directory.
```
# If you have it installed in /opt
cd /opt/solarthing/program
```

Copy some template config files ([default_linux_serial](../config_templates/io/default_linux_serial.json) and [rover_template](../config_templates/base/rover_template.json) or [rover_setup_template](../config_templates/base/rover_setup_template.json))
```
cp ../config_templates/io/default_linux_serial.json config/
cp ../config_templates/base/rover_template.json config/base.json
# or do this if you want to run the setup program:
cp ../config_templates/base/rover_setup_template.json config/base.json
```
Edit `base.json`
```json
{
  ...
  "io": "config/default_linux_serial.json"
}
```

### Configuring the dummy file
If you want to test this program **without a rover**, both the `rover` and `rover-setup` program types support it.
You can edit your `base.json` like so:
```json
{
  ...
  "dummy": "solar/dummy_rover.json"
}
```
If the default file in `solar/dummy_rover.json` doesn't work for you, feel free to copy it to your `program` directory or into
the `config` directory and change it.

### I want to test this without a Renogy Rover
Make sure to configure the `dummy` field as the above section describes, then just run `java -jar solarthing.jar`.

### I'm ready to use this for real!
Once your configuration is how you want it, you can go back to the [quickstart](quickstart.md#Configuration Continued) to enable and start the service.

### I want to test this easily!
See [Rover Setup Info](rover_setup_info.md) for information on how to use the `rover-setup` program.

---

#### \<Insert Advanced Feature\> isn't working on my Rover!
Yes, I hear you. When I first started trying to configure certain things on my Rover, it just straight up didn't work.
Here's an incomplete list of things I was unable to do:
* Set the voltage thresholds
* Settings most of the values
* Getting special power control values
