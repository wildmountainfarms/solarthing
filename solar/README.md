# solar
A program that listens for packets from a OutBack POWER Mate device and uploads it to a database.

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
java -jar SolarThing.jar solar --host 192.168.1.110 --unique 60
```
For each example replace the host with the ip of your CouchDB database IP.

### References
New documentation:
http://www.outbackpower.com/downloads/documents/system_management/mate/mate_serial_communicationsguide.pdf

Older documentation:
https://www.wmrc.edu/projects/BARenergy/manuals/outback-manuals/Mate_Serial_Comm_R302.pdf
