# solarthing
Parses data from an Outback MATE, communicates with a renogy rover, and uses CouchDB as a database!
## What This is used for
This program is run on a raspberry pi at Wild Mountain Farms (www.wildmountainfarms.com).
That program uploads packets to a CouchDB database on a separate computer which hosts the web portion
found here: https://github.com/wildmountainfarms/solarthing-web . With each of these combined we are able
to see the current battery voltage and other information along with a graph to see past data.

Although this is named solarthing, it is expanding to be more than just solar data collection. The latest feature is
the outhouse status. This allows us to store and display four pieces of data: occupancy,
temperature, humidity and door opened/closed. 
Luckily if you want to just use the solar feature for Outback Mate data, you can do that!

In the future, this project may extend to more IoT uses other than just solar and outhouse status. But the name will
forever stick! Long live <strong>solarthing</strong>!

### Displaying
Primarily, we are viewing the data in the Android app. Originally a web app was created. The Android app is superior 
and more convenient.

SolarThing Android: [Github](https://github.com/wildmountainfarms/solarthing-android)
|
[Google Play](https://play.google.com/store/apps/details?id=me.retrodaredevil.solarthing.android)

[SolarThing Web](https://github.com/wildmountainfarms/solarthing-web)
### Individual documentation
[Solar readme](solar/README.md)

[Outhouse Status readme](outhouse/README.md)

### Developer Use
[![](https://jitpack.io/v/wildmountainfarms/solarthing.svg)](https://jitpack.io/#wildmountainfarms/solarthing)

You can import the most current release by using www.jitpack.io. 
* Useful for parsing JSON packets stored in CouchDB. [solarthing-android](https://github.com/wildmountainfarms/solarthing-android) uses this
* You can parse Outback Mate packets directly
* You can read and write to a Renogy Rover easily

### Customizing
This project doesn't have too many options because it was primarily set up to store packets in CouchDB. If you want
to store data in another database, you can create your own implementation of [PacketHandler](src/main/java/me/retrodaredevil/solarthing/packets/handling/PacketHandler.java)

If your implementation is general enough, submit a pull request so others can use your implementation as well!

### Contributing
Contributions are welcome! Feel free to submit an issue to check to see if you want to start working on a feature but aren't
sure if it will be accepted.

### Conventions
This project requires Java 8+. However Java 8 API additions aren't used to remain compatible with Android SDK level 19.

### Compiling
Run the command
```
mvn clean compile assembly:single
```
Move the jar to the root folder and name the jar `solarthing.jar`


### What the database structure looks like
The CouchDB has a few databases in it. Each database has many packets stored in the
"PacketCollection" format. Each packet in the database holds other packets that were saved at the same time. This makes
it simple to link FX1 FX2 and MX3 packets to one single packet. By default the program links packets together by saving
packets when it's been 250 ms after the first data received from a packet. 

Example:

* We receive 10% of Packet 1 so we start the 250ms timer
* We receive 90% of Packet 1 and 80% of Packet 2
* We receive 20% of Packet 2 and 99% of Packet 3
* 250ms is up so Packet 1 and 2 are saved together, Packet 3 will be saved next time

Usually packets aren't cut off like this, but sometimes it happens

You can see how to set up the views and design documents for each database [here](couchdb.md)

### Duplicate Packets in a single PacketCollection
It is expected that if the program falls behind trying to save packets, that what should be two or three PacketCollections
are put into one. Without adding additional threads to the program, it is not possible to solve this. Because I do not
plan to add additional threads to the program, it will remain like this so you should expect that a packet in the database
may have one or two other identical packets from almost the same time.

### Inspiration
@eidolon1138 is the one who originally came up with the idea to collect data from his Outback Mate device. He helped
set up the database and @retrodaredevil did the rest. Eventually @retrodaredevil created an android app making it much
more convenient than a website.

@retrodaredevil came up with the idea of the outhouse status when he walked all the way out to the outhouse only to find
that it was occupied! He walked all the way back inside, then went back out a few minutes later. He knew that something
had to be done about this first world problem.

### Legacy
[The perl script](helloworld.pl) is a legacy program. It was the program that started solarthing.
After learning perl for a day. I went straight back to Java, which I am more familiar with.

### TODO
* Add better logging with timestamps
* Create a PacketHandler that saves json data to a file location that can be easily accessed using a Apache web server
* Figure out how to use https://emoncms.org/ to graph data
* Implement Outback FlexNet DC Packets
* Add field to MX Status Packet to indicate whether it supports dailyAH and field to indicate the version of the MX or if it is a FM
* Cache some data from Renogy Rover that won't be updated

### Completed TODO:
* Provide option/configuration for multiple MATEs (maybe using multiple databases with an id at the end? i.e.: solarthing-1, solarthing-2 or commands-1, commands-2)
    * Done by using fragmented packets. Will be stored in the same database but uses InstancePackets to indicate source and fragment ids
