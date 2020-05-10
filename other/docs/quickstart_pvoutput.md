# Quick Start With PVOutput
If you haven't already, [click here](quickstart.md) to view how to clone this repo and install the service.

**NOTE**: This program is a supplement to either the `mate` or `rover` program and is not set up to get data
from solar devices.

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


### I'm ready to use this for real!
Once your configuration is how you want it, you can go back to the [quickstart](quickstart.md#configuration-continued) to enable and start the service.

---

#### Why is this a separate program and not a "database"?
This project is set up this way because it makes future changes easier. If you were to use an advanced feature of
this project such as fragmented packets, the only way to upload all of your data to pvoutput would be to do it
in one program instead of two.


