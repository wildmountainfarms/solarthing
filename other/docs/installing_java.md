## Installing Java
You need to install Java if
* You are using a RPi1 or RPi0
  * If a Java version is installed, it likely won't work as it is [not compatible](https://www.raspberrypi.org/forums/viewtopic.php?p=1308846).
* You are using the Lite version of Raspberry Pi OS (Java not installed by default)

You can install Java 8 by running (On Raspberry Pi)
```shell script
sudo apt update
sudo apt install openjdk-8-jdk openjdk-8-jdk-headless
```

If you have a Raspberry Pi that **is not** an RPi1 or an RPi0, you can install Java 11 easily:
```shell script
# (Do this instead of installing Java 8)
sudo apt update
sudo apt install openjdk-11-jdk openjdk-11-jdk-headless
```
If you have an RPi1 or RPi0, [see here](./installing_java_11_adv.md) for installing Java 11 (If you are brave and really like Java 11 for some reason).

I would like to upgrade SolarThing to Java 11 by ~2023, so for new updates after then, Java 11 may be required.


For more information on installing Java, go here: https://openjdk.java.net/install/. Make sure you get Java 8 or newer.
