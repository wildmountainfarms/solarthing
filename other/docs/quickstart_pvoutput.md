# Quick Start With PVOutput
If you haven't already, [click here](quickstart.md) to view how to clone this repo and install the service.

**NOTE**: This program is a supplement to other programs (such as `mate` or `rover`) and is not set up to get data from solar devices directly.

Once everything is installed, you're ready to edit the configs. You will cd to the `program` directory.
```
cd /opt/solarthing/program/pvoutput
```

Copy some template config files ([pvoutput_upload_template](../../config_templates/base/pvoutput_upload_template.json))
```
cp ../../config_templates/base/pvoutput_upload_template.json config/base.json
```
Edit `base.json`
```json
{
  "type": "pvoutput-upload",
  "system_id": 100,
  "api_key": "<YOUR API KEY>",
  "database": "config/couchdb.json",
  "timezone": null,
  "source": "default"
}
```
If you set `timezone` to null, it will use your default time zone. Make sure to correctly configure this while setting
up your output at https://pvoutput.org. `source` is the source id of the data and is usually set to "default".

This requires that you set up an account on https://pvoutput.org. This also requires you to set up a CouchDB database.

#### Advanced Configuration
You can view an advanced configuration [here](../../config_templates/base/pvoutput_upload_template_advanced.json).

```json5
{
  // ...
  "join_teams": true,
  "time_zone": "US/Mountain",
  "default_fragment": 1,
  "include_undefined_sources": true,
  "required": {
    "1": ["OutbackIdentifier(address=3)", "OutbackIdentifier(address=4)"]
  },
  "analytics_enabled": true,
  "voltage_identifier": {
    "fragment": 1,
    "identifier": "OutbackIdentifier(address=3)"
  },
  "temperature_identifier": {
    "fragment": 3, "identifier": "DataIdentifier(dataId=1)"
  }
}
```
* `join_teams` normally set to true. When true, the program will automatically join the SolarThing team. Keep this
enabled to be on the SolarThing team! (Defaults to false if not defined)
* `time_zone` can be used to set the time zone if you want to use a different time zone than the current system time zone.
* `default_fragment` usually should never be defined. It's only for users who've been using SolarThing since before 2020.
* `include_undefined_sources` usually should never be defined. It's only for users who've been using SolarThing since before 2020.
* `required` can be used to make sure that certain data is present before uploading to PVOutput
  * In this case, two outback devices with addresses of 3 and 4 must be present. Both of these devices are being monitored by
  a device with a fragment ID of 1.
* `analytics_enabled` can be set to false to disable Google Analytics
* `voltage_identifier` can be used to upload voltage data to PVOutput.
  * In this case, an MX device with an address of 3 on fragment 1 will be used for its `inputVoltage` (pv voltage).
* `temperature_identifier` can be used to upload temperature data to PVOutput.
  * In this case, a DS18B20 sensor on fragment 3 with a data ID of 1 is used


### I'm ready to use this for real!
Once your configuration is how you want it, you can go back to the [quickstart](quickstart.md#configuration-continued) to enable and start the service.

---

#### Why is this a separate program and not a "database"?
This project is set up this way because it makes future changes easier. If you were to use an advanced feature of
this project such as fragmented packets, the only way to upload all of your data to pvoutput would be to do it
in one program instead of two. And if your charge controller resets in the middle of the day, 
(maybe your clock is off or you have wind power) the daily kWh generation won't become off.

This means that you can have multiple instances of SolarThing running and compile data from
each program and upload to PVOutput all at once.
