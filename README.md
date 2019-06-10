# solarthing
A program that listens for packets from a OutBack POWER Mate device and uploads it to a database.
## What This is used for
This program is run on a raspberry pi at Wild Mountain Farms (www.wildmountainfarms.com).
That program uploads packets to a CouchDB database on a separate computer which hosts the web portion
found here: https://github.com/retrodaredevil/solarthing-web . With each of these combined we are able
to see the current battery voltage and other information along with a graph to see past data.

Although this is named solarthing, it is expanding to be more than just solar data collection. The latest feature is
the outhouse status. This allows us to store and display three pieces of data: occupancy,
temperature and humidity.

In the future, this project may extend to more IoT uses other than just solar and outhouse status. But the name will
forever stick! Long live <strong>solarthing</strong>!

### Displaying
Primarily, we are viewing the data in the Android app. Originally a web app was created. The Android app is superior 
and more convenient.

SolarThing Android: [Github](https://github.com/retrodaredevil/solarthing-android)
|
[Google Play](https://play.google.com/store/apps/details?id=me.retrodaredevil.solarthing.android)

[SolarThing Web](https://github.com/retrodaredevil/solarthing-web)
### Individual documentation
[Solar readme](solar/README.md)

[Outhouse Status readme](outhouse/README.md)

### Developer Use
You can import the most current release by using www.jitpack.io. This project is set up to be able to parse JSON
and turn it into a packet. https://github.com/retrodaredevil/solarthing-android is currently using this.

### Compiling
Run the command
```
mvn clean compile assembly:single
```
Move the jar to the root folder and name the jar `SolarThing.jar`


### What the database structure looks like
The CouchDB has two databases in it. "solarthing" and "outhouse". Each database has many packets stored in the
"PacketCollection" format. Each packet in the database holds other packets that were saved at the same time. This makes
it simple to link FX1 FX2 and MX3 packets to one single packet. By default the program links packets together by saving
packets only when there haven't been any new packets for 250 millis seconds.

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
