# Quick Start Renogy Charge Controller or other SRNE (re)branded charge controllers
If you haven't already, [click here](quickstart.md) to view how to clone this repo and install the service.

If you don't have a cable to connect to your device, [click here](../solar/README.md#connecting-to-renogy-rover)

Once everything is installed, you're ready to edit the configs. You will `cd` to the `program` directory.
```
cd /opt/solarthing/program/rover
```

Copy some template config files ([default_linux_serial](../../config_templates/io/default_linux_serial.json) and [rover_template](../../config_templates/base/rover_template.json) or [rover_setup_template](../../config_templates/base/rover_setup_template.json))
```
# sudo should not be required unless permissions were not set up correctly (add yourself to the solarthing group)
cp ../../config_templates/io/default_linux_serial.json config/
cp ../../config_templates/base/rover_template.json config/base.json
# or do this if you want to run the setup program:
cp ../../config_templates/base/rover_setup_template.json config/base.json
```
Edit `base.json`
```json5
{
  //...
  "io": "config/default_linux_serial.json"
}
```

### Other parameters
The `bulk_request` parameter is by default true in SolarThing versions >= 2020.3.2. By keeping it true, requests to the rover
are a lot faster.

### Configuring the dummy file
If you want to test this program **without a rover**, both the `rover` and `rover-setup` program types support it.
You can edit your `base.json` like so:
```json5
{
  //...
  "dummy": "test/dummy_rover.json"
}
```
If the default file in `test/dummy_rover.json` doesn't work for you, feel free to copy it to your `config` directory and change it.

### I want to test this without a Renogy Rover
Make sure to configure the `dummy` field as the above section describes, then just run `./run.sh`.

### I'm ready to use this for real!
Once your configuration is how you want it, you can go back to the [quickstart](quickstart.md#configuration-continued) to enable and start the service.

### I want to test this easily!
See [Rover Setup Info](rover_setup_info.md) for information on how to use the `rover-setup` program.

---

### Setting voltage values of the User battery type
If you want to set values of the user battery type, this cannot be completely done from SolarThing.
You have to manually go to your Rover and set it to the User battery type. SolarThing can set the battery type of
your Rover, but if you set it to User through SolarThing, the values will be locked.

#### \<Insert Advanced Feature\> isn't working on my Rover!
Yes, I hear you. When I first started trying to configure certain things on my Rover, it just straight up didn't work.
Here's an incomplete list of things I was unable to do:
* Settings certain values
* Getting (accurate) special power control values
  * These values didn't really seem correct (maybe they only work on certain charge controllers or only when the battery type is lithium)

Note that a lot of the more advanced features are untested. They exist in SolarThing because they were in the
Rover's Modbus protocol document.
