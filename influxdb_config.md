# Configuring InfluxDB Database
When running SolarThing, you can specify databases using JSON files. An example of a configuration can be found [here](config_templates/influxdb_template.json)

### Specifying `database` and `measurement`
Note that dashes (`-`) and other special characters cannot be used in either of these.

For both `database` and `measurement`, if you don't specify either, it is the same as setting them both
to null.

If you specify `database`, all of the packets will go into that specific database. If you do not specify
`database`, the packets will go into a certain database determined by the program you are running. A `mate` or `rover` program's
packets will go into the `"solarthing"` database. An `outhouse` program's packets will go into the `"outhouse"` database.

If you specify a `measurement`, all of the packets will always be stored as that specific measurement. If you
do not specify `measurement`, one of two things will happen. If the database was specified, `measurement` will be determined by
the type of program running, just like above. If the database was not specified, `measurement` will be set to
the type of packets that is being saved.

#### Recommended
It is recommended to have `database` set to `"default_database"` and to have `measurement` set to `null`, or unspecified.

When you do this, all the packets will go into `"default_database"` and the `measurement` will be determined by
the type of program you are running.
