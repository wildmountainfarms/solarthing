## Using CouchDB
All you need is a vanilla CouchDB instance to create a usable database
for solarthing.

## Creating it
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
