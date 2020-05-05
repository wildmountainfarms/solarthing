# systemd SolarThing service
If your system uses systemd, the files in this directory allow the easy installation of SolarThing
if you want to run the `client` program from the `program` directory.

NOTE: By default, this assumes that you have the root solarthing directory in `/opt`. If you do not, you will
have to manually change some things in here.

```shell script
cd other/systemd
./install.sh <mate|rover|graphql|pvoutput>
cd ../../program
echo Configure stuff in the config directory!
systemctl start solarthing # start the service now
systemctl enable solarthing # make the service run on boot
```
