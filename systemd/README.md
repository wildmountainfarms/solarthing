# systemd SolarThing service
If your system uses systemd, the files in this directory allow the easy installation of SolarThing
if you want to run the `client` program from the `program` directory.

```shell script
cd systemd
./install.sh
cd ../program
echo Configure stuff in the config directory!
systemctl start solarthing # start the service now
systemctl enable solarthing # make the service run on boot
```
