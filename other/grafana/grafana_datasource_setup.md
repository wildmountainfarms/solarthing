Note: This assumes you have Grafana installed, and that you have set up SolarThing GraphQL
## Setting up datasource on Grafana
Follow the installation instructions here: https://grafana.com/grafana/plugins/fifemon-graphql-datasource/installation

In the GraphQL Datasource configuration,
1. Make sure the URL in Grafana is set to `http://localhost:8080/graphql` or similar.
2. Test the connection, and if it succeeds, you're good to go!
3. Start creating your own queries! [Here](graphql_queries.md) are some examples.


---

#### [Docs for grafana-cli](https://grafana.com/docs/grafana/latest/administration/cli/#plugins-commands)

---
---

## Not recommended installation instructions:
clone https://github.com/fifemon/graphql-datasource in `/var/lib/grafana/plugins`

You will need to be a member of the `grafana` group to access that directory.

```shell script
sudo usermod -a -G grafana $USER
# Now log out and log back in, or
exec su -l $USER # if you're too lazy to log out and back in

cd /var/lib/grafana/plugins
git clone https://github.com/fifemon/graphql-datasource
```
