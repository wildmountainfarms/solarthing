# Detailed Solar Implementation
Shows set up for Outback MATE and Renogy Rover communication

## About
Outback Mate products have a serial port on them that prints data every second for each device connected.
Because of this, you can hook up just about anything to the Mate and receive and parse data from it and even send
commands to your mate! This program aims to be compatible with new and old Mate firmwares. This does not implement
FlexNET DC Packets, but FX, MX/FM, and Renogy Rover communication is supported.

You can look at how we parse packets for 
[FX Here](../src/main/java/me/retrodaredevil/solarthing/solar/outback/fx/FXStatusPackets.java),
[MX/FM Here](../src/main/java/me/retrodaredevil/solarthing/solar/outback/mx/MXStatusPackets.java),
[Renogy Read Here](../src/main/java/me/retrodaredevil/solarthing/solar/renogy/rover/RoverModbusRead.java) and
[Renogy Write Here](../src/main/java/me/retrodaredevil/solarthing/solar/renogy/rover/RoverModbusRead.java)

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
# For getting data from outback mate:
((cd /home/pi/solarthing && printf "" > command_input.txt 
&& java -Djava.library.path=/usr/lib/jni -jar solarthing.jar mate --host 192.168.10.250 --user username --passwd password --unique 30 --tf 3 --instant --latest-save /var/www/html/index.json --source default --fragment 1 --ct 30 --st 30
) 1>output.txt 2>errors.txt) &

# For getting data from Renogy rover:
((cd /home/pi/solarthing 
&& java -jar solarthing.jar rover --host 192.168.10.250 --user username --passwd password --source default --fragment 2 --unique 30 --ct 30 --st 30
) 1>output.txt 2>errors.txt) &
```

### Connecting to Outback MATE
Connecting a Raspberry Pi or any device to an Outback MATE's serial port is pretty simple. For the easiest solution,
you should get a serial to USB cable. It is also possible to make a DIY solution and connect the serial interface
to a Raspberry Pi GPIO. I do not have experience with that.

### Connecting to Renogy Rover
If you have the USB to RS232 cable that comes with the Rover, all you have to do is connect it to your computer of
choice and it will act like a serial port!


If you don't have that cable, you can jump through some hoops to do conversion to allow the Pi UART to read it, or you can order a nice RS232 to DB9 cable and a break out

*DO NOT CONNECT THE RJ12 CABLE DIRECTLY TO YOUR PI*. Beware there is 15V in two of the pins in the RJ12 cable, plus you need
an adapter or a way to convert the RS232 signal to something the pi can read. I recommend an adapter.

TODO: Put pin out and recommenced products to buy here

### Outback References
New documentation:
http://www.outbackpower.com/downloads/documents/system_management/mate/mate_serial_communicationsguide.pdf

Older documentation:
https://www.wmrc.edu/projects/BARenergy/manuals/outback-manuals/Mate_Serial_Comm_R302.pdf

Mate Manual:
https://www.wmrc.edu/projects/BARenergy/manuals/outback-manuals/Mate-rev-230.pdf


## Renogy References:
Rover manual: https://www.renogy.com/template/files/Manuals/Rover%20203040%20Manual.pdf

Software download: https://www.renogy.com/template/files/Solar%20Station%20Monitor.zip

Modbus protocol: renogy.boards.net/thread/266/rover-modbus



https://www.overstock.com/Electronics/Renogy-RNG-CTRL-RVR20-BT-Solar-Charge-Controller-MPPT-20A-with-BT/20973007/product.html

## Products to support in the future

https://www.rototron.info/raspberry-pi-solar-serial-rest-api-tutorial/
* https://github.com/rdagger/Expo-Solar-Tracker
* Tracer MT-5. Example: https://github.com/xxv/tracer
* EPsolar Tracer BN. Example: https://github.com/kasbert/epsolar-tracer
