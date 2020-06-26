# Raspberry Pi CPU Temperature
For all the programs that upload to databases, they support the ability to add the Raspberry Pi's CPU temperature
as a packet. You just have to add this json to your `base.json`:
```json5
{
  //...
  "request": [ 
    { "type": "rpi-cpu-temp" }
  ]
}
```

Learn more about the `request` field [here](request_field.md).

This works by using the `vcgencmd measure_temp` that is only available on Raspberry Pis

You can see an example [here](../../config_templates/base/mate_template_with_rpi_cpu_temperature.json)

If you have any suggestions for statistics to add, feel free to request them!

### What to try when this doesn't work
If you have everything else in SolarThing working normally, but this isn't working, there are a few things you can try.

If you are using the systemd service, it should be configured to run as the user 'solarthing'. If SolarThing was
set up correctly, the user 'solarthing' should be a member of the group 'video'. You can make sure this is the case by
running `groups solarthing` and make sure 'video' appears. If it doesn't, the easiest thing to do is to run `/other/linux/create_user.sh`
which will make sure the 'solarthing' user is in the correct groups.

If that doesn't work, make sure you have everything spelled correctly. And if that still doesn't work,
drop by [our issues page](https://github.com/wildmountainfarms/solarthing/issues) and give us a section of your `logs/log_info.log` file and your `config/base.json`.
