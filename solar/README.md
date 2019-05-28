# solar
A program that listens for packets from a OutBack POWER Mate device and uploads it to a database.

### Running
```
solar/virtual_mate.sh | java -jar SolarThing.jar --host 192.168.1.110 --cc --unit-test
```
If you are running this on a Pi with a serial port connected, you would probably run
```
java -jar SolarThing.jar --host 192.168.1.110
```
For each example replace the host with the ip of your CouchDB database IP.

### References
New documentation:
http://www.outbackpower.com/downloads/documents/system_management/mate/mate_serial_communicationsguide.pdf

Older documentation:
https://www.wmrc.edu/projects/BARenergy/manuals/outback-manuals/Mate_Serial_Comm_R302.pdf
