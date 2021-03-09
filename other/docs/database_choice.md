# Database Choice
Choosing a database may be difficult, but usually there's a good choice.

Note that you can use multiple databases if you can't decide on a single one. (This is what we did
for the longest time until moving to CouchDB + GraphQL).

Note that SolarThing supports InfluxDB 1.X and 2.0, but you need different config files depending on which one you use,
and they behave differently. (Also please just use InfluxDB 2.0 if you need to use InfluxDB instead of CouchDB)

#### Want to create simple queries and display data in Grafana?
Pick **InfluxDB**.

#### Want to use the Android App?
Pick **CouchDB**.

#### Want to upload data to PVOutput or Solcast?
Pick **CouchDB**.

#### Want to have a Slack bot notify you if your battery voltage gets low?
Pick **CouchDB**.

#### Want to use GraphQL + Grafana?
Pick **CouchDB**. Note this option is more advanced.

---
### Why multiple databases?
CouchDB was chosen as the first database because it supported JSON documents and was simple to get up and running.

InfluxDB support was added to make displaying data in Grafana very simple. 

CouchDB is the most supported database in SolarThing, although it is the hardest to set up. 
Historical data in CouchDB is easier to maintain (allowing for bulk PVOutput uploads),
and InfluxDB wasn't made for JSON data like CouchDB was.

---
Have a suggestion for a new database to add? Create an issue on [our issues page](https://github.com/wildmountainfarms/solarthing/issues).
