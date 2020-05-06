# Quick Start With Outback MATE
If you haven't already, [click here](quickstart.md) to view how to clone this repo and install the service.

Once everything is installed, you're ready to edit the configs. You will cd to the `program/mate` directory.
```
# If you have it installed in /opt
cd /opt/solarthing/program/mate
```

Copy some template config files ([default_linux_serial](../../config_templates/io/default_linux_serial.json) and [mate_template](../../config_templates/base/mate_template.json))
```
cp ../../config_templates/io/default_linux_serial.json config/
cp ../../config_templates/base/mate_template.json config/base.json
```
Edit `base.json`
```json5
{
  //...
  "io": "config/default_linux_serial.json",
  "correct_check_sum": false
}
```
The mate configuration has the unique property `correct_check_sum`. This makes it easy to change values in `virtual_mate.sh`. By using this,
we can change values quicker values without calculating the checksum ourselves and just have the program do it for us.
Obviously you don't want to use that when you are getting reliable data from a serial port.

Advanced configuration:
```json5
{
  //...
  "fx_warning_ignore": {
    "1": 0,
    "2": 32
  },
  "master_fx": 1,
  "fx_charge_settings": {
    "rebulk_voltage": null,
    "absorb_voltage": 29.2,
    "absorb_time_hours": 1.5,
    "float_voltage": 27.2,
    "float_time_hours": 1.0,
    "refloat_voltage": 25.0,
    "equalize_voltage": 30.0,
    "equalize_time_hours": 2.0
  }
}
```
`fx_warning_ignore` is used for the "event" packets to ignore certain warnings. `master_fx` is used in addition to 
`fx_charge_settings` to calculate/emulate the timers that can be viewed on the MATE. This is **optional**!!! This is also in beta
and is not recommended to try unless you have everything else working.

If you want to learn about commands, you can look at [commands](./commands.md). This is **optional** and should only be
looked at once everything else is working.

### I want to test this without an Outback Mate!
You can run `solar/virtual_mate.sh | ./run.sh`

### I'm ready to use this for real!
Once your configuration is how you want it, you can go back to the [quickstart](quickstart.md#configuration-continued) to enable and start the service.

### In the future
If you use a CouchDB database for this, you can send commands to it and this will execute them. Currently
only one command is able to be sent (Don't worry, this is disabled by default). In the future I plan to add the ability
to configure sending commands. Right now the documentation for this isn't complete.
