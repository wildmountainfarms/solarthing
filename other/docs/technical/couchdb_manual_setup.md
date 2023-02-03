# CouchDB Manual Setup
SolarThing already automatically creates necessary databases and views, but if you need to set it up manually, this page will help.

For general info, go [here](couchdb_setup.md).

## Password Protecting
When you install CouchDB, it should add the admin member to each of your databases. However, this makes it so
a password is required when using CouchDB. This is probably the best option, but that means you have to know
the username and password when using the Android app, AND the web application requires CouchDB to NOT be password protected

For some databases I want public, I remove the admin member from them. NOTE: This is a security risk and can
allow anyone to edit databases that are public. Add convenience at your own risk.

#### [More info about CouchDB](./couchdb_info.md)

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

### Making CouchDB readonly
If you removed all the users from your databases like I did to make it public, then you might want to add
this validation function to make sure when someone makes a request to CouchDB with no authorization, they can't
change or add any documents.

Some credit to https://github.com/iriscouch/manage_couchdb

There are some examples [here](../couchdb/design_docs).

What the document will look like:
```json
{
  "_id": "_design/readonly_auth",
  "language": "javascript",
  "validate_doc_update": "<escaped thing here>"
}
```
Final result:
```json
{
  "_id": "_design/readonly_auth",
  "language": "javascript",
  "validate_doc_update": "function(newDoc, oldDoc, userCtx, secObj) {\n\n  secObj.admins = secObj.admins || {};\n  secObj.admins.names = secObj.admins.names || [];\n  secObj.admins.roles = secObj.admins.roles || [];\n\n  var isAdmin = false;\n  if(userCtx.roles.indexOf('_admin') !== -1) {\n    isAdmin = true;\n  }\n  if(secObj.admins.names.indexOf(userCtx.name) !== -1) {\n    isAdmin = true;\n  }\n  for(var i = 0; i < userCtx.roles; i++) {\n    if(secObj.admins.roles.indexOf(userCtx.roles[i]) !== -1) {\n      isAdmin = true;\n    }\n  }\n\n  if(!isAdmin) {\n    throw {'unauthorized':'This is read only when unauthorized'};\n  }\n}"
}
```

More info here: https://docs.couchdb.org/en/stable/ddocs/ddocs.html

## Adding an uploader user
You should follow this to create a user to upload data: https://stackoverflow.com/questions/3684749/creating-regular-users-in-couchdb

You can also use that to create a user that can be used to read the database if you decide to require authentication to read the database

## Manual Maintenance
CouchDB should do this automatically, but in case it doesn, run this: (replace lavender with your username)

```shell script
curl -H "Content-Type: application/json" -X POST -u lavender http://localhost:5984/solarthing/_compact
```
And this too:
```shell script
curl -H "Content-Type: application/json" -X POST -u lavender http://localhost:5984/solarthing/_compact/packets
```

### Temporary CouchDB
If you are testing, you can create a temporary CouchDB (data will not be kept once it is stopped if you run this command)
```shell
sudo docker run -p 5984:5984 -e COUCHDB_USER=admin -e COUCHDB_PASSWORD=relax couchdb:latest
```

