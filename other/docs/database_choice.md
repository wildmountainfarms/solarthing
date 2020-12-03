# Database Choice
Choosing a database may be difficult, but usually there's a good choice.

Note that you can use multiple databases if you can't decide on a single one. (This is what we did
for the longest time until moving to CouchDB + GraphQL).

Note that SolarThing only supports InfluxDB 1.X.

#### Want to create simple queries and display data in Grafana?
Pick **InfluxDB**.

#### Want to use the Android App?
Pick **CouchDB**.

#### Want to upload data to PVOutput or Solcast?
Pick **CouchDB**.

#### Want to have a Slack bot notify you if your battery voltage gets low?
Pick **CouchDB**.

#### Want a secure and simple setup?
Pick **InfluxDB**. CouchDB requires some setup and extra design documents to make it secure.

#### Want to use GraphQL + Grafana?
Pick **CouchDB**. Note this option is more advanced.

#### Want a Web interface to manage your database?
Pick **CouchDB**. CouchDB's web interface is very simple and intuitive. Although CouchDB + SolarThing requires some set up,
CouchDB's web interface makes it a good experience.

---
### Why multiple databases?
CouchDB was chosen as the first database because it supported JSON documents and was simple to get up and running.

InfluxDB support was added to make displaying data in Grafana very simple. 

CouchDB is the most supported database in SolarThing, although it is the hardest to set up. 
Historical data in CouchDB is easier to maintain (allowing for bulk PVOutput uploads),
and InfluxDB wasn't made for JSON data like CouchDB was.

---
Have a suggestion for a new database to add? Create an issue on [our issues page](https://github.com/wildmountainfarms/solarthing/issues).
