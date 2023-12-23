# SolarThing
![SolarThing](other/docs/solarthing_logo.png "SolarThing")

[![](https://img.shields.io/github/last-commit/wildmountainfarms/solarthing.svg)](https://github.com/wildmountainfarms/solarthing/commits/master)
[![](https://img.shields.io/github/stars/wildmountainfarms/solarthing.svg?style=social)](https://github.com/wildmountainfarms/solarthing/stargazers)
[![](https://img.shields.io/github/v/release/wildmountainfarms/solarthing.svg)](https://github.com/wildmountainfarms/solarthing/releases)
[![](https://img.shields.io/github/release-date/wildmountainfarms/solarthing.svg)](https://github.com/wildmountainfarms/solarthing/releases)

Stores solar data in a database to view on Android, Grafana, or PVOutput

<p style="text-align: center;">
    <a href="#supported-products">Supported Products</a> &bull;
    <a href="https://solarthing.readthedocs.io/">Documentation</a> &bull;
    <a href="#features">Features</a> &bull;
    <a href="#supported-databases">Supported Databases</a> &bull;
    <a href="#examples">Examples</a>
</p>

## Supported Products
* **Outback MATEs** (FX Inverter, MX/FM Charge Controller)
* **Renogy Rover** (And other Renogy products) over modbus serial.
  * Includes Rover, Rover Elite, Wanderer, Adventurer, Dual Input DCDC Charger, Rover Boost and possibly others
  * Compatible with all [SRNE Solar](https://www.srnesolar.com) Charge Controllers (And rebranded products)
  * Compatible with **Zenith Grape** Solar Charge Controller, **PowMr** MPPT Charge Controller, **RICH** SOLAR MPPT, **WindyNations TrakMax** MPPT
* **EPEver Tracer**
  * Includes the AN series and the TRIRON N series
  * Possibly includes the BN series (untested)
* DS18B20 Temperature Sensors and PZEM-003 and PZEM-017 Shunts

## Quickstart
Ready to install? Use the [Quickstart](https://solarthing.readthedocs.io/en/latest/quickstart/install/index.html)!


## Features
* Supports **multiple types of solar products**.
* Runs reliably **24-7**. Recovers from connection errors and has verbose logging features.
* Fully customizable through JSON (**No programming experience required**).
* Supports CouchDB, InfluxDB, local JSON file, and PVOutput exporting.
  * Multiple databases can even be used at the same time!
  * Packets are uploaded in parallel to multiple databases at the same time
* Can [report CPU temperature](https://solarthing.readthedocs.io/en/latest/config/file/base-json/request/cpu-temperature.html).
* Runs inside a Docker container

## Supported Databases
* CouchDB
  * Allows for [SolarThing Android](https://github.com/wildmountainfarms/solarthing-android) and [SolarThing Server](https://solarthing.readthedocs.io/en/latest/quickstart/data/solarthing-server/index.html) to function
  * Used for PVOutput data collection
* GraphQL
  * Allows use of CouchDB SolarThing data with Grafana
  * Supplements the CouchDB database
* InfluxDB
  * Simplest to set up with Grafana
* [PVOutput.org](https://pvoutput.org)
  * Allows for viewing of data on [pvoutput.org](https://pvoutput.org)
  * Requires CouchDB to be set up
* REST API
  * With the "post" database, all packets can be posted to a URL endpoint, useful for REST APIs


## Examples
PVOutput Wild Mountain Farms: [PVOutput System](https://pvoutput.org/intraday.jsp?sid=72206) and 
[PVOutput SolarThing Teams](https://pvoutput.org/listteam.jsp?tid=1528)

---

SolarThing Android: [Github](https://github.com/wildmountainfarms/solarthing-android)
|
[Google Play](https://play.google.com/store/apps/details?id=me.retrodaredevil.solarthing.android)

SolarThing Android displays data in a persistent notification that updates at a configurable rate
![alt text](other/docs/solarthing-android-notification-screenshot-1.jpg "SolarThing Android Notification")
<hr/>

You can get data in [Grafana](https://github.com/grafana/grafana) via InfluxDB or via CouchDB+SolarThing GraphQL.

Grafana is very customizable. Rearrange graphs and make it how you want!
![alt text](other/docs/grafana-screenshot-1.png "SolarThing with Grafana")

---

### Usage at Wild Mountain Farms
We monitor an Outback MATE2, Renogy Rover PG 40A, EPEver Tracer2210AN (20A) using a Raspberry Pi 3.
Each device has its own instance of SolarThing running. Each instance uploads data to CouchDB. CouchDB, Grafana,
and SolarThing GraphQL run on a separate "NAS" computer. This NAS runs the automation and pvoutput programs.
The automation program handles the sending of Slack messages for low battery notifications.

## Database Setup
* [CouchDB setup](https://solarthing.readthedocs.io/en/latest/quickstart/config/database/couchdb.html)<br/>
  * Used for SolarThing Android, and SolarThing Server
* [InfluxDB 2.0 setup](https://solarthing.readthedocs.io/en/latest/quickstart/config/database/influxdb2.html)<br/>
  * Used for direct Grafana queries

## [Developer Use](other/docs/developer_use.md)
## [Contributing](CONTRIBUTING.md)
## [Updating](https://solarthing.readthedocs.io/en/latest/updating.html)

## Configuration
This uses all JSON for configuring everything. The files you edit are all in one place unless you decide to move them.

See [configuration](https://solarthing.readthedocs.io/en/latest/configuration.html) to see how to set them up

## [SolarThing Alternatives](https://solarthing.readthedocs.io/en/latest/misc/alternatives.html)

## Suggestions?
If you have suggestions on how to improve the documentation or have a feature request, I'd love to
hear from you! 
[SolarThing Issues](https://github.com/wildmountainfarms/solarthing/issues)
or
[SolarThing Discussions](https://github.com/wildmountainfarms/solarthing/discussions)

Any confusion you get while setting up SolarThing is something that can be improved upon.
If you need help, ask for help!
That way we can make the documentation clearer for everyone!

---

[![](https://img.shields.io/badge/author-Lavender%20Shannon-brightgreen.svg)](https://github.com/retrodaredevil)
[![](https://img.shields.io/github/repo-size/wildmountainfarms/solarthing.svg)](#)
[![](https://img.shields.io/github/languages/code-size/wildmountainfarms/solarthing.svg)](#)
[![](https://img.shields.io/librariesio/github/wildmountainfarms/solarthing.svg)](https://libraries.io/github/wildmountainfarms/solarthing)
[![](https://img.shields.io/github/commit-activity/m/wildmountainfarms/solarthing.svg)](#)
