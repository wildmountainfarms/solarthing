### TODO
* Create Arduino or Java program to simulate MATE or Rover
* Create a calendar program to be able to view daily kWh on a calendar interface
  * If we do something with a calendar, we could use Google calendar https://developers.google.com/calendar/create-events/
* Packet for disk usage
* Send packet when mate serial port hasn't output data for 30 seconds
* Create Dockerfile and example docker-compose file
* Short term record packets for high/low battery voltage, FX inverter current, pv wattage, charging current, etc  
  * This would be very useful so that if packets are replaced there is still information on how
  high or low the battery voltage got or how high the load was
  * Another possibility is that multiple packets could be saved per each period
    * For instance this document id: `2021,06,12,19,(15/30),[d9587f95]` could also have additional things to tack onto
    it such as: `2021,06,12,19,(15/30),[d9587f95]-high_battery_voltage` or `2021,06,12,19,(15/30),[d9587f95]-high_fx_inv`.
    This has the advantage of being easy to query. When data is queried, the last packet in that period is queried
    along with the record packets, meaning no additional code needs to be written to find the high/low packets
    for older packets.
    * Because of the additional overhead of saving more packets per hour, the `unique` option could be lowered to
    something like 15 per hour.
* Look into graylog or logstash for better logging
  * https://hub.docker.com/r/graylog/graylog
* Look into implementing pymate like features into SolarThing: https://github.com/jorticus/pymate
  * https://github.com/jorticus/pymate/blob/master/doc/protocol/Protocol.md
* Make sure clients tolerate quick disconnects and reconnects from a device so that devices with unstable connections
don't look like they're constantly disconnecting and reconnecting
* Add packet for rover write commands that ran - (we already have one for mate commands)
* Possibly refactor PacketListReceiver
  * Have a way to tell if a packet included in the packets list is important enough to warrant storing in the database.
    * Right now we do a bunch of packets.isEmpty() checks to see if we should continue adding packets, but there's probably a better way
* Better way to update SNAPSHOT jar so that running SolarThing instances don't get mad
  * We have a great setup for versioned SolarThing jar files, because running instances will still use
  whatever jar solarthing.jar pointed them to originally, but this isn't the case when we actually change the jar it is pointing to
* Backend Grafana plugin to allow commands to be sent
  * Might not actually need a backend plugin if we can just use the proxy instead
  * https://github.com/grafana/grafana/issues/12556
  * https://grafana.com/docs/grafana/latest/developers/plugins/add-authentication-for-data-source-plugins/
  * This is not necessarily completed, but has been overcome with the /command/run endpoint.
    * Not completely ideal as the dashboard the call is made on must be private to avoid exposing private API key
* GraphQL queries for getting the angle of the sun in the sky (current and highest point in the day)
  * https://ipgeolocation.io/documentation/astronomy-api.html - unintuitive Java SDK with bad documentation
  * https://github.com/caarmen/SunriseSunset - gross requirement to use java.util.Calendar
  * https://github.com/mikereedell/sunrisesunsetlib-java - better, but still requires use of java.util.Calendar
* Serialize convenience fields only if a certain condition is met (maybe a config option)
  * Heck, maybe only serialize convenience fields in debug messages
* Don't serialize available commands packet in every single packet collection
* The Tracer doesn't have a way to tell if it's in absorb mode, but we could use the settings to tell if it has reached the setpoint
and is not giving full available power.
  * I'm not sure how we should indicate something like this on Grafana quite yet
* Metadata packet that describes the solar panels connected to a charge controller between a period of time
* Program that uploads malicious data to solarthing_open to better understand how SolarThing reacts to malicious data
* Make validation function that prevents people from adding packets to CouchDB that are super far in the future.
  * Especially solarthing_open
* In tests that check the template automation configurations, we should try to create the actions instead of just validating
the creation of an ActionNode. This would require the creation of a dummy ActionEnvironment, but could be useful for checking the validity
of different configurations, where some configurations are designed for the automation program and others are designed to be
run in the request program, each with different environments.
* Be able to reference actions defined in other files. We have the "declaration" action, but that only allows
the definition of callable actions in the same file.
* Add something in couchdb-setup to allow for easy setup of replication between CouchDB instances
* Add an execution reason that will be deserialized from unknown execution reasons
  * This execution reason cannot be serialized
  * We need this because we will likely add more ExecutionReasons in the future and want to make sure that clients
  can deserialize these ExecutionReasons no matter what -- execution reasons are just metadata
* Make an action similar to the command execution feedback action that can be used in the automation program
for basic logging.
* Have the Slack chat bot react to messages that were processed, successfully requested,
and also react to messages that have been fully processed (request successfully performed).
  * This will require some sort of scheduling logic that makes SolarThing check to see if something was completed
  e.g.: an alter packet was successfully deleted
* Create abstraction for sending custom formatted messages. Right now some calls to `MessageSender` pass strings specific to slack
* Possibly rename graphql program to rest program
* Have a request node be able to upload packets if the clock on the device is off by a decent margin
* Might be good for the `noheartbeat` message sender event to alert when heartbeats come back or if a particular heartbeat is still not present
* A GraphQL query that can return a list of errors/warnings/statuses.
  * Recent device disconnects
  * Scheduled commands
  * Active flags
  * AC Use/Drop status
* Allow io configurations to be specified in-line rather than in a separate file.
* Add solarthing subcommand that checks whether the configuration has any errors in it
* Remove rover-setup program
* Make the cryptography utils more object-oriented so that we can swap out implementations more easily
  * KeyUtil contains a cipher transformation that is not as secure as it could be. Everything else can stay the same
* GraphQL query to display a log of all the times the generator was run, including details on if it was an EQ or not
* Make chatbot interpret "cancel generator off at 5pm" or provide better way for cancelling commands than ID
* Packets for when a command is accepted or rejected and when its action is done running
* Make it so that MessageEvents can easily use a NanoProvider rather than System.nanoTime() directly.
* Add alert to detect when messages are being spammed to solarthing_open
* Add info about the state of CouchDB on the home page of SolarThing web
* Add TOML support for config files: https://github.com/FasterXML/jackson-dataformats-text
* Have a way to query record data such as the day with the highest daily kWh value
  * Likely involves cache database. 
  * A single cache document could include an entire month's worth of data, so this will not involve `CacheHandler`.
  * Calculation can be based off of `chargeControllerAccumulation`.
* Have a CouchDB validation function that stops old packets from being saved
* CouchDB web page for sending commands
* Command that turns off generator before it goes into absorb
* Turn on generator when certain rules are met (run command when certain rules are met)
  * Potential rules:
    * Battery voltage is below X
    * Battery voltage is below X for Y duration
    * Battery voltage average of last X duration is at or below Y
    * Net load is above X
    * Net load is above X for Y duration
    * Net load average of last X duration is at or above Y
    * Predicted net load average of next X duration is at or above Y
      * Will predict load using data from past week
      * Will predict charge using solcast
* Add this to web: https://github.com/IvanGoncharov/graphql-voyager
* Created a "program2" directory that allows for an easier creation of custom directories
* Change floats to BigDecimals
* Create a custom type recognizable by some custom jackson serializer.
  * The type can be something like AnnotatedList<SomeObject> and will allow strings
  above the object in the json array
  
### Completed
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
* Store metadata
  * Put this in a document named `meta` in `solarthing_closed`
* Add temperature event to message program
* Create GraphQL query that allows someone to get dailyKWH from a large time period (many days) for
easy displaying in Grafana
* Do something with https://www.home-assistant.io/ somehow
* Log data to https://solcast.com.au/
* Notification for when MX wakes up and goes straight to float (happens on older models)
* Support InfluxDB 2.0
* Don't report analytics for dummy rover programs
* Silence warning for GraphQL
* Make CouchDB and InfluxDB database configuration warn when setting unused values
* Add way to scan for rover modbus slave addresses
* Have standard timings for grouping/sorting packets and increase time for GraphQL grouped queries so that
  all temperature readings show up even if one is from 10 minutes ago.
* More accurate solcast by using actual kWh data throughout the day
* Have option to use shorter document IDs
  * CouchDB recommends shorter IDs: https://docs.couchdb.org/en/stable/maintenance/performance.html#document-s-id
* Add action to store an informational message in the database
* Automation program should have access to data since the beginning of the day and should query solarthing-events
* Add way to queue up commands in the automation program
  * The goal here is to be able to say "start generator at 5PM" and then also be able to cancel it
* Have a CouchDB view that emits null so that the millis view can be more efficient and people can use include_docs=true
  * https://www.dimagi.com/blog/what-every-developer-should-know-about-couchdb/
* Separate ActionNode code into its own action-node module
* Use `millisNull` view instead of `millis` view
* Integration tests
* Create button/widget on Grafana that sends an encrypted command to shut off the generator
  * Did this by creating the `/command/run` endpoint and using [cloudspout-button-panel](https://github.com/cloudspout/cloudspout-button-panel/)
  * Not perfect: Requires making a dashboard private to avoid exposing API key to users that are not logged in
* Automation that sends a command to each program that accepts commands.
  * Done using heartbeats
  * Alerting could be faster if we had the automation program monitor for a heartbeat request that didn't go through
  but for now just a heartbeat being absent after its expected duration is good enough
* Create cli Python program that can be ran like: `solarcheck /dev/ttyUSB0`. It will detect whether the port can be opened.
  It will then listen for data to see if it is a MATE device. If it is not, it will send Modbus data to see if it is a
  Rover or Tracer
* Chat bot command to see the current heartbeats
* Make an alternative for RetryFailedPacketHandler. Details in comments in class.
* Actually start using SLF4J in graphql program
* Get rid of InstantType
* Add event packets for rover and tracer devices (like we did for MXs and FXs)
* Make monitor-service only start after WiFi has kicked in
* Use https://github.com/tbroyer/gradle-errorprone-plugin

### Look into
* Look into supporting Elasticsearch, MongoDB, Graphite
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

#### Additions I'm not going to work on
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
