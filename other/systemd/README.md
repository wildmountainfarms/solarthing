# systemd SolarThing service
If your system uses systemd, the files in this directory allow the easy installation of SolarThing
if you want to run the `client` program from the `program` directory.

NOTE: By default, this assumes that you have the root solarthing directory in `/opt`. If you do not, you will
have to manually change some things in here.

```shell script
cd systemd
./install.sh
cd ../../program
echo Configure stuff in the config directory!
systemctl start solarthing # start the service now
systemctl enable solarthing # make the service run on boot
```

You can also install the separate `solarthing-graphql` service. This involves the same steps as above,
but with `./install_graphql.sh`.
