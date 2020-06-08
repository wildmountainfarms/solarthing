## Installing Java
If you install the full Raspberry Pi OS image, Java should already be installed.

However, if you are using and older Raspberry Pi, the default Java 11 will not work on it. More info on that here: https://www.raspberrypi.org/forums/viewtopic.php?p=1308846

You can install Java 8 by running (On Raspberry Pi)
```shell script
sudo apt update
sudo apt install openjdk-8-jdk openjdk-8-jdk-headless
```

For more information on installing Java, go here: https://openjdk.java.net/install/. Make sure you get Java 8 or newer.
