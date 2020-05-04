## CouchDB Info
This contains additional info about CouchDB.

For general info, go [here](couchdb_setup.md).

## Databases
These databases are automatically created:
* `solarthing` Stores status packets from the Outback MATE and Renogy Rover
* `solarthing_events` Stores event-type packets from the Outback MATE and Renogy Rover
* `commands` Commands are saved here then deleted to send commands to the Outback MATE. Commands in here are encrypted for integrity


## Enabling CORS
If you are using the web application, you should enable Cross Origin Reference Sharing.

## What a request looks like
Although you will usually be using an API or have a graphical config to choose
different settings, a request will almost always look like this:
```GET/solarthing/_design/packets/_view/millis?startkey=1546650568194```
To view that in a url, you will usually type something like 
"MY_IP:5984/solarthing/_design/......."

### [Manual Setup](./couchdb_manual_setup.md)
