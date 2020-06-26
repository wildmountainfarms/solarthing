# Quick start with Request program
If you haven't already, [click here](quickstart.md) to view how to clone this repo and install the service.

The request program can be used for uploading temperature sensor data and Raspberry Pi
CPU Temperature.

This supports DS18B20 temperature sensors.

Once everything is installed, you're ready to edit the configs. You will cd to the `program/mate` directory.
```
cd /opt/solarthing/program/request
```

Copy the request template:
```shell script
cp ../../config_templates/base/request_template.json config/base.json
```

Now edit `config/base.json`.
```json
{
  "type": "request",
  "source": "default",
  "fragment": 3,
  "unique": 30,
  "databases": [
    "../config/couchdb.json"
  ],
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
The objects in the `request` field represent what values are uploaded.

If you are monitoring a DS18B20 temperature sensor, you can change the `directory` field to point to
the correct device in `/sys/bus/w1/devices`. Also learn [how to set up a DS18B20 sensor](DS18B20_sensor_setup.md).
