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

### Inspiration
@eidolon1138 is the one who originally came up with the idea to collect data from his Outback Mate device. He helped
set up the database and @retrodaredevil did the rest. Eventually @retrodaredevil created an android app making it much
more convenient than a website.

@retrodaredevil came up with the idea of the outhouse status when he walked all the way out to the outhouse only to find
that it was occupied! He walked all the way back inside, then went back out a few minutes later. He knew that something
had to be done about this first world problem.
