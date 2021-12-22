NEW DOCUMENTATION HERE: https://solarthing.readthedocs.io/en/latest/data/graphql-grafana.html

# GraphQL Quickstart
If you haven't already, [click here](quickstart.md) to view how to clone this repo and install the service.

Note that you must have set up either the `mate`, `rover`, or `request` program for this to be useful.
This program is used to expose CouchDB as a GraphQL API, which is commonly used with Grafana and [the GraphQL Datasource](https://github.com/fifemon/graphql-datasource).

Once everything is installed, you're ready to edit the configs. You will cd to the `program/mate` directory.
```
cd /opt/solarthing/program/graphql
```

Now copy the `application.properties` to the config directory:
```shell script
cp ../../config_templates/graphql/application.properties config/
```

Edit `application.properties`:
```
solarthing.config.database=../config/couchdb/couchdb.json
```
Make sure the path to your `couchdb.json` is correct.

NOTE: Sometimes you may have put your `couchdb.json` in `program/rover/config`. If you did this,
that's perfectly fine, you just need to change the path slightly:
```
solarthing.config.database=../rover/config/couchdb/couchdb.json
```


### Run for the first time
Run `./run.sh`.

### [Set up with Grafana](../grafana/grafana_datasource_setup.md)

