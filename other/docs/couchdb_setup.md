# CouchDB
All you need is a vanilla CouchDB instance to create a usable database. You can download it here: https://couchdb.apache.org/

Once you have it running, it defaults to port 5984.

## Configuring SolarThing for CouchDB
An example configuration can be found [here](../../config_templates/databases/couchdb_template.json).

* CouchDB can be configured to download commands from the `commands` database if the `allow-commands` is set to true
in a mate program config.

* `connection_timeout` and `socket_timeout` are in seconds and can be decimal values. Setting them to 0 makes it so there's no timeout

---

## Password Protecting
When you install CouchDB, it should add the admin member to each of your databases. However, this makes it so
a password is required when using CouchDB. This is probably the best option, but that means you have to know
the username and password when using the Android app, AND the web application requires CouchDB to NOT be password protected

For some databases I want public, I remove the admin member from them. NOTE: This is a security risk and can
allow anyone to edit databases that are public. Add convenience at your own risk.

#### [More info about CouchDB](./couchdb_info.md)
