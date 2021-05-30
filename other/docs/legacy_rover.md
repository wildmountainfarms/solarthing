## Legacy Rover
This documentation applies to SolarThing versions less than 2021.5.0. If you are using an up to date
version of SolarThing, you should [follow this quickstart](./quickstart_rover.md) for setting up your rover config.

---

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
edit `base.json`
```json5
{
  //...
  "io": "config/default_linux_serial.json"
}
```

### other parameters
the `bulk_request` parameter is by default true in solarthing versions >= 2020.3.2. by keeping it true, requests to the rover
are a lot faster.

### configuring the dummy file
if you want to test this program **without a rover**, both the `rover` and `rover-setup` program types support it.
you can edit your `base.json` like so:
```json5
{
  //...
  "dummy": "test/dummy_rover.json"
}
```
if the default file in `test/dummy_rover.json` doesn't work for you, feel free to copy it to your `config` directory and change it.

### I want to test this without a Renogy Rover
Make sure to configure the `dummy` field as the above section describes, then just run `./run.sh`.

