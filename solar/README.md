# solar
A program that listens for packets from a OutBack POWER Mate device and uploads it to a database.


## About
Outback Mate products have a serial port on them that prints data every second for each device connected.
Because of this, you can hook up just about anything to the Mate and receive and parse data from it and even send
commands to your mate! This program aims to be compatible with new and old Mate firmwares. This does not implement
FlexNET DC Packets, but FX and MX/FM packets are supported.

You can look at how we parse packets for 
[FX Here](../src/main/java/me/retrodaredevil/solarthing/solar/outback/fx/FXStatusPackets.java) and 
[MX/FM Here](../src/main/java/me/retrodaredevil/solarthing/solar/outback/mx/MXStatusPackets.java)

### Running
```
solar/virtual_mate.sh | java -jar SolarThing.jar solar --host 192.168.1.110 --cc --unit-test --unique 60
```
Note the `--cc`. This stands for correct checksum. To make it easy in `virtual_mate.sh` to change values, using `--cc`
makes it quicker to change values without calculating the checksum ourselves and just have the program do it for us.
Obviously you don't want to use that when you are getting reliable data from a serial port. The `--unit-test` makes
it so it takes input from `System.in` instead of trying to establish a connection with a serial port

If you are running this on a Pi with a serial port connected, you would probably run
```
java -jar solarthing.jar solar --host 192.168.1.110 --unique 60
```
For each example replace the host with the ip of your CouchDB database IP.

Example command that I use while running it on a raspberry pi:
```
# this is located in my /etc/rc.local
(cd /home/pi/solarthing && printf "" > command_input.txt 
&& java -Djava.library.path=/usr/lib/jni -jar solarthing.jar --host 192.168.10.250 --user user --passwd password --unique 30 --tf 3 --instant solar >output.txt 2>errors.txt) &
```

### Outback References
New documentation:
http://www.outbackpower.com/downloads/documents/system_management/mate/mate_serial_communicationsguide.pdf

Older documentation:
https://www.wmrc.edu/projects/BARenergy/manuals/outback-manuals/Mate_Serial_Comm_R302.pdf

Mate Manual:
https://www.wmrc.edu/projects/BARenergy/manuals/outback-manuals/Mate-rev-230.pdf


## Renogy References:
https://www.rototron.info/raspberry-pi-solar-serial-rest-api-tutorial/

https://www.overstock.com/Electronics/Renogy-RNG-CTRL-RVR20-BT-Solar-Charge-Controller-MPPT-20A-with-BT/20973007/product.html

### Thoughts on sending commands

#### Authentication
On client (android or web app), sign with private key. If that device is authorized, it will have it's public
key on the Raspberry Pi running solarthing.

Talk about a standard password for everything running solarthing

We will sign currentTimeMillis() and the command with our private key. When the Pi decrypts the message with our 
public key, it will be able to parse the time and the command
