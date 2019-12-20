# Saving Packets (The `io` JSON property)
There are many ways to save packets (the way data is represented in this program)

### Example Configurations
You can see [this](../config_templates) directory for some examples

This is for couchdb
```
{
  "type": "couchdb",
  "settings": {
    "packet_upload": {
      "throttle_factor": 3,
      "initial_skip": 3
    },
    "command_download": {
      "throttle_factor": 3,
      "initial_skip": 4
    }
  },
  "config": {
    "protocol": "http",
    "host": "localhost",
    "port": 5984,
    "username": "admin",
    "password": "relax",
    "connection_timeout": 0,
    "socket_timeout": 0
  }
}
```
The "settings" object is used to specify settings for specific uses in the program. Right now the program has hard coded values
for packet_upload and command_download. Only the mate program uses the command_download values.

The config is used to specify the CouchDB config. Everything after "port" is optional. connection_timeout and socket_timeout
are in seconds. Decimal or whole numbers can be used.

This is for latest save
```
{
  "type": "latest",
  "config": {
    "file": "latest_json.json"
  }
}
```
In the above example the latest packet will be saved to the file "latest_json.json".
