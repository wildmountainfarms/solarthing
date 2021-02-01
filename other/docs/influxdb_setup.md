# Setting up InfluxDB 1.X Database (deprecated)
For new users, please do not use InfluxDB 1.X unless you have a reason (more familiar, etc).
Go [here](./influxdb2_setup.md) for InfluxDB 2.0 setup.

---

### Installing
Click [here](https://docs.influxdata.com/influxdb/v1.7/introduction/installation/) to go to the installation page.
Once installed, you don't need to do anything to the installation itself, it's ready to go!

### Configuring SolarThing
When running SolarThing, you can specify databases using JSON files. An example of a configuration can be found [here](../../config_templates/databases/influxdb_template.json).

### Specifying `database` and `measurement`
Note that dashes (`-`) and other special characters cannot be used in either of these.

For both `database` and `measurement`, if you don't specify either, it is the same as setting them both
to null.

If you specify `database`, all packets will go into that specific database. If you do not specify
`database`, status packets go into `solarthing` and event packets go into `solarthing_events`.

If you specify a `measurement`, all packets will always be stored as that specific measurement. If you
do not specify `measurement`, one of two things will happen. If the database was specified, `measurement` will be determined by
the type of program running, just like above. If the database was not specified, `measurement` will be set to
the type of packets that is being saved.

#### Recommended Setup #1
It is recommended to have `database` set to `"default_database"` and to have `measurement` set to `null`, or unspecified.

When you do this, all the packets will go into `"default_database"` and the `measurement` will be determined by
the type of program you are running.

With this configuration, it makes it easy to write queries to get data however, if you have many different packet types,
the auto-complete won't be so nice to you. This configuration has the advantage of being able to query multiple packet types
in a single query (**this makes graphing battery voltages of all your devices a lot easier than Recommended Setup #2**).

#### Recommended Setup #2
This option is the option that is probably better if you want a database structure that is easier to be
viewed as a whole. In Recommended Setup #1, all of the data is stuffed into a single measurement. In this setup,
we have a different measurement for each packet type. This makes some queries more difficult, but makes auto complete a lot better.

Set `database` to `"default_database"` or leave it unspecified. Leave `measurement` unspecified.

Now each packet type will be put in its own measurement, just like measurements were intended to be used.

#### Not Recommended
If you specify both `database` and `measurement`, the program will put event packets into the same database and measurement of other packets.
