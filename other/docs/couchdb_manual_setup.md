# CouchDB Manual Setup
SolarThing already automatically creates necessary databases and views, but if you need to set it up manually, this page will help.

For general info, go [here](couchdb_setup.md).

### Adding Views
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
