### TODO
* Create Arduino or Java program to simulate MATE or Rover
* Log CPU temperature, ram, etc into a database and logs.
  * Use the `vcgencmd measure_temp` available on Raspberry Pis
* Add readable string alongside identifier tag for InfluxDB
* Create a calendar program to be able to view daily kWH on a calendar interface
  * If we do something with a calendar, we could use google calendar https://developers.google.com/calendar/create-events/
* Have unique identifier for each jar file
* AC Use->AC Drop, turn on AUX until NO AC. (GEN OFF) command using MATE's built in AC Use/Drop.

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
* Save DailyFX data to a file so if the program restarts mid-day, accumulated data is not reset
* Change command_feedback to something generic and use that database for "event"-like data
  * Also upload this to InfluxDB
* Have packets for inverters going on/off, AC Drop/AC Use, Daily kWH->0, etc
  * We still need to do some of these

### TODO Look into
* Figure out how to use https://emoncms.org/ to graph data
* Look into supporting Elasticsearch, MongoDB, Graphite
* Do something with https://www.home-assistant.io/ somehow
* Log data to https://solcast.com.au/
* Support https://dweet.io
  * Will make using freeboard easy
* https://thingspeak.com/
* MQTT

#### TODO - Additions I'm not going to work on
These might be useful to some people. I will not implement these in the future, but pull requests are welcome!
* Implement Outback FlexNet DC Packets
* Create 'exporter' module which will have a Prometheus exporter that runs and gets data from CouchDB
  * This is not useful to me, but anyone else is welcome to do this
