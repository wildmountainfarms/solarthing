# SolarThing
Parses data from an Outback MATE, communicates with a renogy rover, and uses CouchDB as a database!

## Supported Products
* Communicates with <strong>Outback MATEs</strong> 1/2 via the DB9 serial port. This supports receiving and parsing data from FX's, MX's and FM's
* Communicates with <strong>Renogy Rover</strong> (And other Renogy products) over modbus serial.

## Uses
* Upload data to a database
* Display data in Android Application
* Display data in Web Application
* Use as an API for your own uses!

# Why should you use this?
This program supports communication with more solar products than any other open source program. The program
was built to reliably store data in a database while running 24-7. You can customize this program to fit your
needs without programming. But, if you are a programmer, this API will make your life a lot easier.

## What This is currently used for
This program is run on a raspberry pi at Wild Mountain Farms (www.wildmountainfarms.com).
That program uploads packets to a CouchDB database on a separate computer which hosts the web portion
found here: [solarthing-web](https://github.com/wildmountainfarms/solarthing-web). 

In the future, this project may extend to more IoT uses other than just solar and outhouse status. But the name will
forever stick! Long live <strong>SolarThing</strong>!

### Displaying
Primarily, we are viewing the data in the Android app. Originally a web app was created. The Android app is superior 
and more convenient.

SolarThing Android: [Github](https://github.com/wildmountainfarms/solarthing-android)
|
[Google Play](https://play.google.com/store/apps/details?id=me.retrodaredevil.solarthing.android)

[SolarThing Web](https://github.com/wildmountainfarms/solarthing-web)

If you decide to use InfluxDB, you can easily create your own Dashboard with Grafana.

### Individual documentation
[Solar readme](solar/README.md)

[Outhouse Status readme](outhouse/README.md)

## Using the program
You can see the [Outback/Renogy Rover](solar/README.md) README for using the program with outback or renogy products.

You can see the [Outhouse](outhouse/README.md) README for using the outhouse program.

The [input and output](input_and_outputs.md) README is documentation for the `--io` option used in all of the sub programs.

### Developer Use
[![](https://jitpack.io/v/wildmountainfarms/solarthing.svg)](https://jitpack.io/#wildmountainfarms/solarthing)

You can import the most current release by using www.jitpack.io. 
* Useful for parsing JSON packets stored in CouchDB. [solarthing-android](https://github.com/wildmountainfarms/solarthing-android) uses this
* You can parse Outback Mate packets directly
* You can read and write to a Renogy Rover easily

Import it like this:
```groovy
allprojects {
    repositories {
        maven { url 'https://www.jitpack.io' }
    }
}
dependencies {
    implementation 'com.github.wildmountainfarms.solarthing:core:<CURRENT VERSION HERE>'
}
```

### Customizing
The different command line options give you may ways to receive data and export data. CouchDB and InfluxDB
are both supported.

If you want to store data in another database, you can create your own implementation of [PacketHandler](src/main/java/me/retrodaredevil/solarthing/packets/handling/PacketHandler.java)

If your implementation is general enough, submit a pull request so others can use your implementation as well!

### Contributing
Contributions are welcome! Feel free to submit an issue to check to see if you want to start working on a feature but aren't
sure if it will be accepted.

### Conventions
This project requires Java 8+. However Java 8 API additions aren't used to remain compatible with Android SDK level 19.

### Compiling
Run `./compile_and_move.sh`. Make sure your working directory is in the root of this project. ([this directory](.))

### Database Setup
[CouchDB setup](couchdb.md)

[InfluxDB setup](influxdb_config.md): (Basically None)<br/>
*Please note that InfluxDB is not supported by the web application or Android application. Use this if you only want to use Grafana*
### [Technical](technical.md)

#### Configuration
This uses jewelcli for its configuration. Using this library makes it very easy to use interfaces with multiple inheritance. http://jewelcli.lexicalscope.com/examples.html

#### Logging
This uses slf4j to log and uses log4j2 as the main implementation. https://logging.apache.org/log4j/2.x/manual/appenders.html

### TODO
* Figure out how to use https://emoncms.org/ to graph data
* Implement Outback FlexNet DC Packets
* Cache some data from Renogy Rover that won't be updated
* Create Arduino program to simulate MATE or Rover

### Completed TODO:
* Provide option/configuration for multiple MATEs (maybe using multiple databases with an id at the end? i.e.: solarthing-1, solarthing-2 or commands-1, commands-2)
    * Done by using fragmented packets. Will be stored in the same database but uses InstancePackets to indicate source and fragment ids
* Add field to MX Status Packet to indicate whether it supports dailyAH and field to indicate the version of the MX or if it is a FM
* Create a PacketHandler that saves json data to a file location that can be easily accessed using a Apache web server
* Add better logging with timestamps
* Make Grafana easy to use by supporting InfluxDB as a database

### [History](history.md)
