# Raspberry Pi CPU Temperature
For all the programs that upload to databases, they support the ability to add the Raspberry Pi's CPU temperature
as a packet. You just have to add this json to your `base.json`:
```json5
{
  //...
  "extra_option_flags": [ "rpi_cpu_temp" ]
}
```

This works by using the `vcgencmd measure_temp` that is only available on Raspberry Pis

You can see an example [here](../../config_templates/base/mate_template_with_rpi_cpu_temperature.json)

If you have any suggestions for statistics to add, feel free to request them!
