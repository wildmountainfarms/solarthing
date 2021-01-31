# Setting up InfluxDB 2.0 Database

### Installing
Click [here](https://docs.influxdata.com/influxdb/v2.0/get-started/) to go to the installation page.

### Downsides
Unlike with SolarThing's InfluxDB 1.X implementation which is now deprecated, SolarThing's InfluxDB 2.0 implementation
does not and will not support multiple retention policies. This means that you have to manually set up
retention rules on the buckets created by SolarThing, and every datapoint will be in the bucket for the
same amount of time.

You can always limit how frequently SolarThing puts data into InfluxDB, but that may mean that you won't have
updated data in your database for 60 seconds until SolarThing uploads data to InfluxDB. This isn't perfect, which
is why the recommended approach is to use CouchDB, which is more supported and has better features to maintain data long
term, but also only keep a packet from every X minutes while staying up to date at all times.

### Configuring SolarThing
When running SolarThing, you can specify databases using JSON files. An example of a configuration can be found [here](../../config_templates/databases/influxdb2_template.json).
