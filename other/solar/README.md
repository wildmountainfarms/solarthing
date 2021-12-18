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

---

### Connecting to Renogy Rover
Some Renogy products use the RS232 protocol and some use the RS485 protocol.


---

### RS232 Cable to buy
I've been told that the [Rich Solar RS232 Cable](https://richsolar.com/products/rs232-cable) also works for the Rover.
This cable is the only cable I know of that you can buy that will work with the Rover.

**Just because a cable says USB to RJ12 does not mean that it will work**. This is why most of the time, you will
make a DIY cable.

### RS485 Cable to buy
Newer Renogy products use the RS485 protocol. This isn't necessarily better or worse, but Renogy actually sells
a pre-made cable for RS485 to USB. I recommend you buy that cable [here](https://www.renogy.com/rs485-to-usb-serial-cable/).

---

## IMPORTANT if you are making a DIY cable
Some of these instructions below require you to figure some stuff out on your own. There's no tutorial video
for making DIY cables, so when making one, when you connect to your Rover, make sure you use a multimeter to check
voltages across different pins. Rovers with an RS232 port supply 15V of power, which you do not want to connect to anything.
Rovers with RS485 ports supply 5V of power, which again, you don't want to connect to anything.

### DIY RS232
Please see [this](http://renogy.boards.net/thread/535/using-rj11-cable-connect-raspberry) thread to see my experience on creating a DIY cable.

You can also view [Renogy Rover RS232 Pinout](resources/renogy_rover_rs232_pinout.png). Note that this is partially incorrect;
you need to connect the Rover's TX to your RX and the Rover's RX to your TX. Make sure you measure voltage
with a multimeter so you don't send 15V through a Raspberry Pi's GPIO like I did!

I recommend getting a USB to RS232 DB9 adapter and a DB9 breakout board. The DB9 breakout board can then
be used to easily wire to an RJ12 port.


### DIY RS485
You can look at [this](../solar/resources/DCC%20Charger%20Modbus%20RS485%20V1.7.pdf) to see the pin out.
You will have to buy a USB to RS485 adapter, then wire it to an RJ45 connector with the correct pinouts.

---

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

https://renogy.boards.net/thread/565/hp-series-smart-solar-cc
* "administrator", "111111"

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

### Random Useful Forum Links
These links document people talking about SolarThing or creating cables
* https://diysolarforum.com/threads/renogy-devices-and-raspberry-pi-bluetooth-wifi.30235
* http://renogy.boards.net/thread/535/using-rj11-cable-connect-raspberry
