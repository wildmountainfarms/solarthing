# Setting up InfluxDB 2.0 Database
This works only for 2.0+ InfluxDB installations. For a deprecated solution for 1.X databases, click [here](./influxdb_setup.md).

InfluxDB 2.0 is a simple database that allows for complex queries on SolarThing data. It is great for getting
a solution up and running, but doesn't have all the options of using CouchDB and SolarThing GraphQL.

### Installing
Click [here](https://docs.influxdata.com/influxdb/v2.0/get-started/) to go to the installation page. Or use the `docker-compose.yml` file
found [in this folder](../../config_templates/docker/influxdb2) if you are familiar with docker and docker-compose.

### Configuring SolarThing
When running SolarThing, you can specify databases using JSON files. An example of a configuration can be found [here](../../config_templates/databases/influxdb2_template.json).

You should keep `org` as `solarthing-org` unless you have a reason to change it. Once you have a token in the config and have
the url set correctly, you're ready to use it! SolarThing will automatically create buckets as described in technical below.

---

### Technical

Unlike SolarThing's 1.X InfluxDB implementation, there is no complicated setup for database/bucket and measurement names.
SolarThing will create and use buckets named `solarthing` and `solarthing_events`. Measurement names will be based
on the type of packet going into the bucket. Flux makes it easy to query different types of measurements at once, which
can be used if you need to query the battery voltage from multiple devices.
#### Downsides
Unlike with SolarThing's InfluxDB 1.X implementation which is now deprecated, SolarThing's InfluxDB 2.0 implementation
does not and will not support multiple retention policies. This means that you have to manually set up
retention rules on the buckets created by SolarThing, and every datapoint will be in the bucket for the
same amount of time.

You can always limit how frequently SolarThing puts data into InfluxDB, but that may mean that you won't have
updated data in your database for 60 seconds until SolarThing uploads data to InfluxDB. This isn't perfect, which
is why the recommended approach is to use CouchDB, which is more supported and has better features to maintain data long
term, but also only keep a packet from every X minutes while staying up to date at all times.
