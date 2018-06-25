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
