NOTE! This is outdated! New documentation here: https://solarthing.readthedocs.io

Documentation for this particular page here: https://solarthing.readthedocs.io/en/latest/config/couchdb.html

# CouchDB
All you need is a vanilla CouchDB instance to create a usable database. You can download it here: https://couchdb.apache.org/

You should setup the database as a single node.

Once you have it running, it defaults to port 5984.

## Configuring SolarThing for CouchDB
An example configuration can be found [here](../../config_templates/databases/couchdb_template.json).

* `connection_timeout` and `call_timeout` are in seconds and can be decimal values. Setting them to 0 makes it so there's no timeout
  * `call_timeout` is the timeout for the entire call, `connection_timeout` is the timeout for once the connection has been made
  * So, `call_timeout` is larger than `connection_timeout`. 
  * Note: `call_timeout` used to be called `socket_timeout`
* To start with, you should **put in admin credentials**. You can replace these credentials with a less privileged user later.

## Required setup
SolarThing has a special program to help you set up CouchDB. We're going to run that now. You should have
created a couchdb.json file.

You will now run this command:
```shell
java -jar solarthing.jar --couchdb-setup config/couchdb.json
# Or use this if you are in one of program's subdirectories
java -jar ../solarthing.jar --couchdb-setup config/couchdb.json
```
That command will take you through some setup. Read what it says, you'll have to press enter a few times and
you will have to give it input. It is recommended that the user you create is called `uploader`. Choose a password for
this user. Once you've done that and completed this, you can replace the credentials in `couchdb.json` with these new credentials.

It's possible that in the future features will be added to SolarThing that require you to run this setup again,
but it is unlikely.
