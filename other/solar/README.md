# Detailed Solar Implementation
Some information about how this program works with solar products.

Ready to get started? Go to the **[QUICKSTART](../docs/quickstart.md)**!

---

## About
Outback Mate products have a serial port on them that prints data every second for each device connected.
Because of this, you can hook up just about anything to the Mate and receive and parse data from it and even send
commands to your mate! This program aims to be compatible with new and old Mate firmwares. This does not implement
FlexNET DC Packets, but FX, MX/FM, and Renogy Rover communication is supported.

This does not support the Outback MATE 3. This is because the MATE3 does not have a DB9 serial port.

You can look at how we parse packets for 
[FX Here](../../core/src/main/java/me/retrodaredevil/solarthing/solar/outback/fx/FXStatusPackets.java),
[MX/FM Here](../../core/src/main/java/me/retrodaredevil/solarthing/solar/outback/mx/MXStatusPackets.java),
[Renogy Read Here](../../core/src/main/java/me/retrodaredevil/solarthing/solar/renogy/rover/modbus/RoverModbusSlaveRead.java) and
[Renogy Write Here](../../core/src/main/java/me/retrodaredevil/solarthing/solar/renogy/rover/modbus/RoverModbusSlaveWrite.java)

### Connecting to Outback MATE
Connecting a Raspberry Pi or any device to an Outback MATE's serial port is pretty simple. For the easiest solution,
you should get a serial to USB cable (RS232 to USB).

### Connecting to Renogy Rover
If you have the USB to RS232 cable that comes with the Rover, all you have to do is connect it to your computer of
choice and it will act like a serial port!

If you don't have that cable, you can jump through some hoops to do conversion to allow the Pi UART to read it, or you can order a nice RS232 to DB9 cable and a break out board.

Please see [this](http://renogy.boards.net/thread/535/using-rj11-cable-connect-raspberry) thread to see my my experience on creating a DIY cable.

You can also view [Renogy Rover RS232 Pinout](resources/renogy_rover_rs232_pinout.png). Note that this is partially incorrect;
you need to connect the Rover's TX to your RX and the Rover's RX to your TX. Make sure you measure voltage
with a multimeter so you don't send 15V through a Raspberry Pi's GPIO like I did!

NOTE: Rover Elites use RS-485 serial communication and must use a different cable than regular Renogy Rovers. It is
unknown if they use the same protocol, but I assume it is the same.

### In the future
In the future, it might be beneficial to allow the Rover program to request data from multiple Rover Elites at once
because RS-485 supports multiple devices on the same serial wire.

### Outback References
http://outbackpower.com/resources/documents/product-documentation

New documentation:
http://www.outbackpower.com/downloads/documents/system_management/mate/mate_serial_communicationsguide.pdf

Older documentation:
https://www.wmrc.edu/projects/BARenergy/manuals/outback-manuals/Mate_Serial_Comm_R302.pdf

Mate Manual:
https://www.wmrc.edu/projects/BARenergy/manuals/outback-manuals/Mate-rev-230.pdf

[Mate3](http://www.outbackpower.com/downloads/documents/system_management/mate3s/mate3s_programmingguide_web.pdf)

FX Manual:

* [gfx (legacy)](http://outbackpower.com/downloads/documents/inverter_chargers/gfx_series/gfx_operators_manual.pdf)
* [m series - FX Mobile Operator](http://outbackpower.com/downloads/documents/inverter_chargers/m_series/fx_mobile_operator.pdf)
* [GTFX/GVFX](http://outbackpower.com/downloads/documents/inverter_chargers/gtfx_gvfx_series/gtfx_gvfx_series_programming_manual.pdf)
* [radian](http://outbackpower.com/downloads/documents/inverter_chargers/radian_8048a_4048a/gs_8048a_4048a_operator.pdf)

MX Manual:
https://www.wmrc.edu/projects/BARenergy/manuals/outback-manuals/MX60_REV_C.pdf


## Renogy References:
Rover manual: https://www.renogy.com/template/files/Manuals/Rover%20203040%20Manual.pdf

Software download: https://www.renogy.com/template/files/Solar%20Station%20Monitor.zip

Modbus protocol: renogy.boards.net/thread/266/rover-modbus

Zenith manual: https://grapesolar.com/docs/ZENITH%2060%20Manual.pdf

TrakMax manual: https://www.windynation.com/cm/TrakMax40BT%20Controller%20Manual_R1.pdf

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

### Outback Mate3 Software
SolarThing doesn't support Outback Mate3 devices, but there's lots of other software you can use
* http://www.jeperez.com/monitor-solar-outback/
* http://outbackpower.com/forum/viewtopic.php?f=1&t=13225
