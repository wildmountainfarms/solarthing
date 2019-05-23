# solarthing
A program that listens for packets from a OutBack POWER Mate device and uploads it to a database.
## What This is used for
This program is run on a raspberry pi at Wild Mountain Farms (www.wildmountainfarms.com).
That program uploads packets to a CouchDB database on a separate computer which hosts the web portion
found here: https://github.com/retrodaredevil/solarthing-web . With each of these combined we are able
to see the current battery voltage and other information along with a graph to see past data.
### Developer Use
Currently this project is not set up to be used as an API but maybe in the future it will be.
### Compiling
Run the command
```
mvn clean compile assembly:single
```
Move the jar to the root folder and name the jar `SolarThing.jar`

You would then usually run a command similar to 
```
source solar/virtual_mate.sh | java -jar SolarThing.jar --host 192.168.1.110 --cc --unit-test
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
