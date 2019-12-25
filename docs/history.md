# History
This program started in the summer of 2017.

### Inspiration
@eidolon1138 is the one who originally came up with the idea to collect data from his Outback Mate device. He helped
set up the database and @retrodaredevil did the rest. Eventually @retrodaredevil created an android app making it much
more convenient than a website.

@retrodaredevil came up with the idea of the outhouse status when he walked all the way out to the outhouse only to find
that it was occupied! He walked all the way back inside, then went back out a few minutes later. He knew that something
had to be done about this first world problem.


#### 2017
* A perl script was created in a single day to collect data from an Outback Mate serial port
* The terrible perl script was ditched to start on the Java program. The program allowed packets to be added to a CouchDB database
#### 2018
* This continued in the summer of 2018. The formatting of the packets was completely rethought. The web application
was created and completed in less than a week.
* An Android app was created to see the data continuously updated in a status notification
#### 2019
* Outhouse status was added
* Renogy rover support was added
* To maintain compatibility with the previous packet structure, Source and Fragment packets types were added to
have the ability to have multiple instances uploading packets to a single database
* InfluxDB support was added allowing for easy configuration of a Grafana dashboard
* Raspberry Pi running the outhouse program didn't survive the freezing temperatures (RIP outhousepi 2019-2019)


### Moving from Gson to Jackson
This project started out with Jackson, but as of 2019.12.24, I have started to move to Gson. I originally chose Gson for its
simplicity. It has served this project very well and is very user friendly. However, I got tired of writing custom
deserializing functions to deserialize advanced packets. Jackson is very annotation orientated and is very
feature rich. The added complexity of Jackson is worth the speed of development it brings.

Useful links:
* https://www.baeldung.com/jackson-field-serializable-deserializable-or-not
* https://www.baeldung.com/jackson-deserialize-immutable-objects
* https://github.com/FasterXML/jackson-docs/wiki/JacksonPolymorphicDeserialization

### Legacy
[The perl script](../helloworld.pl) is a legacy program. It was the program that started solarthing.
After learning perl for a day. I went straight back to Java, which I am more familiar with.
