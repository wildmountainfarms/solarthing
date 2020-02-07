## Using CouchDB
All you need is a vanilla CouchDB instance to create a usable database.

CouchDB takes some initial setup to make it usable before you can configure SolarThing.

**NOTE**: In the future, I plan to make CouchDB easier to configure by removing the need to setup the `millis` view as described below.

## Databases
You must create each database if you wish to use the features they come with
* `solarthing` Stores status packets from the Outback MATE and Renogy Rover
* `solarthing_events` Stores event-type packets from the Outback MATE and Renogy Rover
* `commands` Commands are saved here then deleted to send commands to the Outback MATE. Commands in here are encrypted for integrity
* [Legacy] `command_feedback` Stores feedback for commands that were sent to the Outback MATE
* [Legacy] `outhouse` Stores packets from the outhouse

Each database except `commands` is recommended to be password protected. `commands` is not required to be password
protected because commands are encrypted to provide integrity and allow certain senders to execute commands.

However, in my situation, I do not password protect any of these databases because it makes it hard to have anyone read the database.
You should ALWAYS password protect databases such as the `_users` database. You can do that by adding a member to that database.
In my opinion, securing CouchDB is not super easy because everything is public by default and there's no easy way to make it private write, public read.

# Adding Initial Documents to CouchDB
1. First, create the databases you want as described above
2. Create a design called `packets` and add a view named `millis` to that database. 
3. Configure the `millis` view as described below.

Each database must have the same `_design/packets/_view/millis` view

You can start by creating a new view and specifying the _design to be `packets`.

The view name must be `millis` and the map function looks like this:

```
function(doc) {
  emit(doc.dateMillis, doc);
}
```

## What a request looks like
Although you will usually be using an API or have a graphical config to choose
different settings, a request will almost always look like this:
```GET/solarthing/_design/packets/_view/millis?startkey=1546650568194```
To view that in a url, you will usually type something like 
"MY_IP:5984/solarthing/_design/......."

# Configuring SolarThing for CouchDB
An example configuration can be found [here](../config_templates/databases/couchdb_template.json)

* CouchDB can be configured to download commands from the `commands` database if the `allow-commands` is set to true
in a mate program config.

* `connection_timeout` and `socket_timeout` are in seconds and can be decimal values. Setting them to 0 makes it so there's no timeout
