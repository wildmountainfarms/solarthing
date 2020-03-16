# Influx Grafana Queries
This document contains useful queries to use with both InfluxDB and Grafana

## About Queries
Influx queries are very similar to SQL queries. You can easily "ask" for certain values of data. There are a few gotchas.
InfluxDB supports retention policies and SolarThing allows you to easily specify them. If you try to write a query without
specifying a retention policy, it will use the default retention policy. This is great, but if you are using multiple retention
policies, it is (to my knowledge) impossible to query for data from all the retention policies. This means that you can
only get data from one retention policy.

### Annotations:
##### AC Mode Change
```
SELECT "acModeValue" FROM "normal_forever_retention"."solarthing_events" WHERE ("packetType" = 'FX_AC_MODE_CHANGE') AND "previousACModeValue" > -1 AND "address" = 1 AND $timeFilter
```
##### Successful commands
```
SELECT "command" FROM "normal_forever_retention"."solarthing_events" WHERE ("packetType" = 'MATE_COMMAND_SUCCESS') AND $timeFilter
```
##### Operational Mode Changes
```
SELECT "operationalModeValue" FROM "normal_forever_retention"."solarthing_events" WHERE ("packetType" = 'FX_OPERATIONAL_MODE_CHANGE') AND "previousOperationalModeValue" > -1 AND "address" = 1 AND $timeFilter
```

*Contributions are welcome!*
