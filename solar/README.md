# Detailed Solar Implementation
Shows set up for Outback MATE and Renogy Rover communication

## About
Outback Mate products have a serial port on them that prints data every second for each device connected.
Because of this, you can hook up just about anything to the Mate and receive and parse data from it and even send
commands to your mate! This program aims to be compatible with new and old Mate firmwares. This does not implement
FlexNET DC Packets, but FX, MX/FM, and Renogy Rover communication is supported.

This does not supper the Outback MATE 3. This is because the MATE3 does not have a DB9 serial port.

You can look at how we parse packets for 
[FX Here](../core/src/main/java/me/retrodaredevil/solarthing/solar/outback/fx/FXStatusPackets.java),
[MX/FM Here](../core/src/main/java/me/retrodaredevil/solarthing/solar/outback/mx/MXStatusPackets.java),
[Renogy Read Here](../core/src/main/java/me/retrodaredevil/solarthing/solar/renogy/rover/modbus/RoverModbusSlaveRead.java) and
[Renogy Write Here](../core/src/main/java/me/retrodaredevil/solarthing/solar/renogy/rover/modbus/RoverModbusSlaveWrite.java)

### Running
```shell script
# The program directory is always the working directory when running Solarthing
cd program
```
Testing SolarThing:
```shell script
solar/virtual_mate.sh | java -jar solarthing.jar "config/base.json"
```
Running SolarThing for Real:
```shell script
java -jar solarthing.jar "config/base.json"
```

#### Mate Configuration
```json
{
  "type": "mate",
  "correct_check_sum": true,
  "io": "<your io config file here>"
}
```
The mate configuration has the unique property `correct_check_sum`. This makes it easy to change values in `virtual_mate.sh`. By using this,
we can change values quicker values without calculating the checksum ourselves and just have the program do it for us.
Obviously you don't want to use that when you are getting reliable data from a serial port.

### Connecting to Outback MATE
Connecting a Raspberry Pi or any device to an Outback MATE's serial port is pretty simple. For the easiest solution,
you should get a serial to USB cable (RS232 to USB).

### Connecting to Renogy Rover
If you have the USB to RS232 cable that comes with the Rover, all you have to do is connect it to your computer of
choice and it will act like a serial port!

If you don't have that cable, you can jump through some hoops to do conversion to allow the Pi UART to read it, or you can order a nice RS232 to DB9 cable and a break out

Please see [this](http://renogy.boards.net/thread/535/using-rj11-cable-connect-raspberry) thread to see my my experience on creating a DIY cable.

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

## Products to support in the future

https://www.rototron.info/raspberry-pi-solar-serial-rest-api-tutorial/
* https://github.com/rdagger/Expo-Solar-Tracker
* Tracer MT-5. Example: https://github.com/xxv/tracer
* EPsolar Tracer BN. Example: https://github.com/kasbert/epsolar-tracer

TriStar Modbus
* https://www.stellavolta.com/content/MSCTSModbusCommunication.pdf
* http://www.hardysolar.com/charge-controller/dl/morningstar-charge-controller-sunsaver-mppt.pdf

## Identifier Order
Identifiers are used in SolarPackets to identify solar products and act as keys for Maps. Identifiers should
implement Comparable<Identifier> so Identifiers have a defined order which is this:

* Outback packets in order (port 1, port 2, etc)
* Renogy packet in order according to serial number (serial 65, serial 1234, serial 5989)
