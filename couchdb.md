## Using CouchDB
All you need is a vanilla CouchDB instance to create a usable database
for solarthing and outhouse.

## Databases
You must create each database if you wish to use the features they come with
* "solarthing" Stores status packets from the Outback MATE
* "rover" Stores status packets from the Renogy Rover
* "commands" Commands are saved here then deleted to send commands to the Outback MATE. Commands in here are encrypted for integrity
* "command_feedback" Stores feedback for commands that were sent to the Outback MATE
* "outhouse" Stores packets from the outhouse

"solarthing", "command_feedback", and "outhouse" should be password protected. "commands" is not required to be password
protected because data in it is protected by encryption to provide integrity.

## Creating it
Each database must have the same "_design/packets/_view/millis" view

You can start by creating a new view and specifying the "_design" to be packets.

The view name must be "millis" and the map function looks like this:

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
