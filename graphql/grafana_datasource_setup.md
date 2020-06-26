## Setting up datasource on Grafana
You can install https://github.com/fifemon/graphql-datasource by cloning it in `/var/lib/grafana/plugins`

You will need to be a member of the `grafana` group to access that directory.

```shell script
sudo usermod -a -G grafana $USER
# Now log out and log back in, or
exec su -l $USER # if you're too lazy to log out and back in

cd /var/lib/grafana/plugins
git clone https://github.com/fifemon/graphql-datasource
```
Then make sure the URL in Grafana is set to `http://localhost:8080/graphql` or similar.

Might be useful: https://grafana.com/docs/grafana/latest/administration/cli/#plugins-commands
