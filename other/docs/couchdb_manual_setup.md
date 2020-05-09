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

### Making CouchDB readonly
If you removed all the users from your databases like I did to make it public, then you might want to add
this validation function to make sure when someone makes a request to CouchDB with no authorization, they can't
change or add any documents.

Some credit to https://github.com/iriscouch/manage_couchdb
```javascript 1.5
function(newDoc, oldDoc, userCtx, secObj) {

  secObj.admins = secObj.admins || {};
  secObj.admins.names = secObj.admins.names || [];
  secObj.admins.roles = secObj.admins.roles || [];

  var isAdmin = false;
  if(userCtx.roles.indexOf('_admin') !== -1) {
    isAdmin = true;
  }
  if(secObj.admins.names.indexOf(userCtx.name) !== -1) {
    isAdmin = true;
  }
  for(var i = 0; i < userCtx.roles; i++) {
    if(secObj.admins.roles.indexOf(userCtx.roles[i]) !== -1) {
      isAdmin = true;
    }
  }

  if(!isAdmin) {
    throw {'unauthorized':'This is read only when unauthorized'};
  }
}
```

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
  "_rev": "1-148648098406082f1227e6d4a02fb0be",
  "language": "javascript",
  "validate_doc_update": "function(newDoc, oldDoc, userCtx, secObj) {\n\n  secObj.admins = secObj.admins || {};\n  secObj.admins.names = secObj.admins.names || [];\n  secObj.admins.roles = secObj.admins.roles || [];\n\n  var isAdmin = false;\n  if(userCtx.roles.indexOf('_admin') !== -1) {\n    isAdmin = true;\n  }\n  if(secObj.admins.names.indexOf(userCtx.name) !== -1) {\n    isAdmin = true;\n  }\n  for(var i = 0; i < userCtx.roles; i++) {\n    if(secObj.admins.roles.indexOf(userCtx.roles[i]) !== -1) {\n      isAdmin = true;\n    }\n  }\n\n  if(!isAdmin) {\n    throw {'unauthorized':'This is read only when unauthorized'};\n  }\n}"
}
```
