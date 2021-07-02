# Quick Start Renogy Charge Controller or other SRNE (re)branded charge controllers
* If you haven't already, [click here](quickstart.md) to view how to clone this repo and install the service.
* If you don't have a cable to connect to your device, [click here](../solar/README.md#connecting-to-renogy-rover)
* Looking for legacy (pre SolarThing 2021.5.0) documentation? [Click here](./legacy_rover.md).
* If you don't have a Rover and you see the term "rover", that's OK. This will still work with your
charge controller as long as your charge controller uses the same protocol as the Rover. Whenever you see Rover,
you can think "Rover and compatible devices".
* You're going to notice that the configuration of this is very similar to the [request quickstart](./quickstart_request.md).
That's because this actually uses the request program. You may notice that there is a `program/rover` directory. That directory
is no longer used and can be ignored.

---

Once everything is installed, you're ready to edit the configs. You will `cd` to the `program/request` directory.
```
cd /opt/solarthing/program/request
```
If you are already using the request directory, click [here](./custom_directories.md).

Copy some template config files ([default_linux_serial](../../config_templates/io/default_linux_serial.json) and [rover_template](../../config_templates/base/rover_request_template.json))
```
# sudo should not be required unless permissions were not set up correctly (add yourself to the solarthing group)
cp ../../config_templates/io/default_linux_serial.json config/
cp ../../config_templates/base/rover_request_template.json config/base.json
```

Now edit `config/base.json`
* Most rovers have a default modbus address of 1, hence the `"1"` present in the template configuration. However,
this is not always the case for newer Rover models, especially if a BT module has been plugged into them. You will
likely have to use the `rover-setup` program to scan
  * rover-setup will be deprecated soon, and there will need to be a new feature for this
* Make sure you change the `"io"` property to correctly point to the io JSON file you want to use. Remember paths
are relative to `program/request`, so `config/io.json` would point to `program/request/config/io.json`.


### I'm ready to use this for real!
Once your configuration is how you want it, you can go back to the [quickstart](quickstart.md#configuration-continued) to enable and start the service.

### I'm not ready to test this on my Rover yet
That's OK! If you want to get some "dummy" data into your database of choice, you can use a different
IO configuration. [Go here](./rover_dummy.md) for more info.

### I want to test this on my Rover without a database!
See [Rover Setup Info](rover_setup_info.md) for information on how to use the `rover-setup` program.
This is a good option if you want to interact with your Rover live.

---

### Setting voltage values of the User battery type
If you want to set values of the user battery type, this cannot be completely done from SolarThing.
You have to manually go to your Rover and set it to the User battery type. SolarThing can set the battery type of
your Rover, but if you set it to User through SolarThing, the values will be locked.

#### \<Insert Advanced Feature\> isn't working on my Rover!
Yes, I hear you. When I first started trying to configure certain things on my Rover, it just straight up didn't work.
Here's an incomplete list of things I was unable to do:
* Settings certain values that are supposed to be writable
* Getting (accurate) special power control values
  * These values didn't really seem correct (maybe they only work on certain charge controllers or only when the battery type is lithium)

Note that a lot of the more advanced features are untested. They exist in SolarThing because they were in the
Rover's Modbus protocol document.
