# Request Field
The `request` field is an array of objects that *can* be present in configurations for the
`mate`, `rover`, or `request` programs.

This is a common way to define what to upload to the database. In the `mate` and `rover` program case, it supplements
the data they already provide.


Here's an example of requesting the Raspberry Pi CPU Temperature
```json5
{
  // ...
  "request": [
    {
      "type": "rpi-cpu-temp"
    }
  ]
}
```

You can also request data from a DS18B20 temperature sensor:
```json5
{
  // ...
  "request": [
    {
      "type": "rpi-cpu-temp"
    },
    {
      "type": "w1-temperature",
      "directory": "/sys/bus/w1/devices/28-000006470bec",
      "data_id": 1
    }
  ]
}
```
If you are interesting in learning more about the DS18B20 sensor, [click here](DS18B20_sensor_setup.md).
