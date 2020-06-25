### TODO
* Create Arduino or Java program to simulate MATE or Rover
* Create a calendar program to be able to view daily kWh on a calendar interface
  * If we do something with a calendar, we could use Google calendar https://developers.google.com/calendar/create-events/
* Create button/widget on Grafana that sends an encrypted command to shut off the generator
* Create GraphQL query that allows someone to get dailyKWH from a large time period (many days) for
easy displaying in Grafana
* Packet for disk usage
* Store data such as the panel tilt that can change over time
  * ~~this could go into a database called solarthing-meta~~
  * Put this in a document named `meta` in `solarthing_closed
* Make CouchDB and InfluxDB database configuration warn when setting unused values
* Make sure that the latest packet handler is really the latest. (Aux On has been frequently sent twice with the generator shutoff)
* Add temperature event to message program
  
### Android TODO
* Notification for when FXs are in EQ, but any MX is in absorb or float
* Notification for when MX wakes up and goes straight to float (happens on older models)

### Completed TODO:
* Provide option/configuration for multiple MATEs (maybe using multiple databases with an id at the end? i.e.: solarthing-1, solarthing-2 or commands-1, commands-2)
    * Done by using fragmented packets. Will be stored in the same database but uses InstancePackets to indicate source and fragment ids
* Add field to MX Status Packet to indicate whether it supports dailyAH and field to indicate the version of the MX or if it is a FM
* Create a PacketHandler that saves json data to a file location that can be easily accessed using a Apache web server
* Add better logging with timestamps
* Make Grafana easy to use by supporting InfluxDB as a database
* Add a basic file for a systemd service
* Stop requiring command line parameters and have option to use all JSON configs
* Integrate power usage
* Detect when Mate packets go from address 4->1 or 1->1 to detect a new collection of packets
  * This was done by just removing duplicate packets. Not ideal, but it works.
* Change command_feedback to something generic and use that database for "event"-like data
  * Also upload this to InfluxDB
* Have packets for inverters going on/off, AC Drop/AC Use, Daily kWh->0, etc
* Log CPU temperature, ram, etc into a database and logs.
  * Use the `vcgencmd measure_temp` available on Raspberry Pis
* Query all Renogy Rover data at once by reading almost all the registers.
* Use Google Analytics Collection API https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters
  * Could use this: https://github.com/brsanthu/google-analytics-java
* Make systemd programs use "solarthing" user instead of root
* Use Mattermost as a way to send push notifications for things
* DS18B20 temperature sensor
* Make command requests use JSON

### TODO Look into
* Look into supporting Elasticsearch, MongoDB, Graphite
* Do something with https://www.home-assistant.io/ somehow
* Log data to https://solcast.com.au/
* Support https://dweet.io
  * Will make using freeboard easy
* https://thingspeak.com/
* https://github.com/netdata (grafana alternative?)
* https://github.com/grafana/loki (for logging)
* thingsboard.io
* Cypher [guide here](https://neo4j.com/developer/guide-sql-to-cypher/)
  * Looks interesting
* [Metrictank](https://grafana.com/oss/metrictank/)
* IntelliJ is complaining about "busy waiting". We do a lot of this. Should we change how we're doing this?

#### TODO - Additions I'm not going to work on
These might be useful to some people. I will not implement these in the future, but pull requests are welcome!
* Implement Outback FlexNet DC Packets
  * I do not have a FlexNet DC unit and I do not plan to get one anytime soon
* Create 'exporter' module which will have a Prometheus exporter that runs and gets data from CouchDB
  * This is not useful to me, but anyone else is welcome to do this
* Use https://emoncms.org/ to graph data (https://emoncms.org/site/api#input)
  * Some of this is done, but it will not be worked on any further by me
* Use Matrix Chat as a notification service https://github.com/matrix-org
  * I found Synapse a pain to set up, but might be worth trying in the future
  
  
#### Interesting stuff that will probably never be implemented
* https://github.com/resilience4j/resilience4j It might be cool to try and heavily structure the client program around
resilience4j
