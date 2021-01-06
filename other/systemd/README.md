# systemd SolarThing service
If your system uses systemd, the files in this directory allow the easy installation of SolarThing
if you want to run the `client` program from the `program` directory.

NOTE: By default, this assumes that you have the root solarthing directory in `/opt`. If you do not, you will
have to manually change some things in here. (Running ./install.sh will not work unless you have it installed in `/opt`)

```shell script
cd other/systemd
./install.sh <mate|rover|graphql|pvoutput>
cd ../../program
echo Configure stuff in the config directory!
systemctl start solarthing # start the service now
systemctl enable solarthing # make the service run on boot
```

## Does my system have systemd? What's that?
systemd is something that's on Linux computers based on Debian and it's also on a few distros not based on Debian.
If you have a Raspberry Pi, it's on that. If your computer is running Ubuntu, it's on that too.

Systemd is not on Windows or Mac.
