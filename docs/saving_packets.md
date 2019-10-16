# Saving Packets
There are many ways to save packets (the way data is represented in this program)

### Example Commands
```
((cd /home/pi/solarthing/program && \
java -jar solarthing.jar mate --unique 45 --source default --fragment 1 \
--database "config/couchdb.json" "config/latest.json" --io "config/mate_io.json" --allow-commands) 1>output.txt 2>errors.txt) &
```
In the above example, the program will use the configurations located at "config/couchdb.json" and "config/latest.json". The above
example is also the exact command I use in /etc/rc.local on my Raspberry Pi that listen to a MATE.

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
