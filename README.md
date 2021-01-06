![SolarThing](other/docs/solarthing_logo.png "SolarThing")

[![](https://img.shields.io/github/last-commit/wildmountainfarms/solarthing.svg)](https://github.com/wildmountainfarms/solarthing/commits/master)
[![](https://img.shields.io/github/stars/wildmountainfarms/solarthing.svg?style=social)](https://github.com/wildmountainfarms/solarthing/stargazers)
[![](https://img.shields.io/github/v/release/wildmountainfarms/solarthing.svg)](https://github.com/wildmountainfarms/solarthing/releases)
[![](https://img.shields.io/github/release-date/wildmountainfarms/solarthing.svg)](https://github.com/wildmountainfarms/solarthing/releases)
[![](https://img.shields.io/github/downloads/wildmountainfarms/solarthing/total.svg)](other/docs/quickstart.md)

Stores solar data in a database to view on Android, Grafana, or PVOutput

<p align="center">
    <a href="#supported-products">Supported Products</a> &bull;
    <a href="other/docs/quickstart.md">Quickstart</a> &bull;
    <a href="#features">Features</a> &bull;
    <a href="#supported-databases">Supported Databases</a> &bull;
    <a href="#examples">Examples</a>
</p>

## Supported Products
* **Outback MATEs** (FX Inverter, MX/FM Charge Controller)
* **Renogy Rover** (And other Renogy products) over modbus serial.
  * Includes Rover, Rover Elite, Wanderer, and possibly others
  * Compatible with all [SRNE Solar](https://www.srnesolar.com) Charge Controllers (And rebranded products)
  * Also compatible with **Zenith Grape** Solar Charge Controller, **PowMr** MPPT Charge Controller, **RICH** SOLAR MPPT, **WindyNations TrakMax** MPPT

# Quickstart
Ready to install? Use the [Quickstart](other/docs/quickstart.md)!

# Features
* Supports **multiple types of solar products**.
* Runs reliably **24-7**. Recovers from connection errors and has verbose logging features.
* Fully customizable through JSON (**No programming experience required**).
* Supports CouchDB, InfluxDB, local JSON file, and PVOutput exporting.
* Can [report Raspberry Pi CPU temperature](other/docs/raspberry_pi_cpu_temperature.md).
* Easy setup on Linux. Runs *without* root.

## Supported Databases
* CouchDB
  * Allows for [SolarThing Android](https://github.com/wildmountainfarms/solarthing-android) and [SolarThing Web](https://github.com/wildmountainfarms/solarthing-web) to function
  * Used for PVOutput data collection
* GraphQL
  * Allows use of CouchDB SolarThing data with Grafana
  * Supplements the CouchDB database
* InfluxDB
  * Simplest to set up with Grafana
* [PVOutput.org](https://pvoutput.org)
  * Allows for viewing of data on [pvoutput.org](https://pvoutput.org)
  * Requires CouchDB to be set up
  * Enables usage of the [PVOutput Mycroft skill](https://github.com/wildmountainfarms/pvoutput-mycroft)
* REST API
  * With the "post" database, all packets can be posted to a URL endpoint, useful for REST APIs


### Examples
PVOutput Wild Mountain Farms: https://pvoutput.org/intraday.jsp?sid=72206

---

SolarThing Android: [Github](https://github.com/wildmountainfarms/solarthing-android)
|
[Google Play](https://play.google.com/store/apps/details?id=me.retrodaredevil.solarthing.android)

SolarThing Android displays data in a persistent notification that updates at a configurable rate
![alt text](other/docs/solarthing-android-notification-screenshot-1.jpg "SolarThing Android Notification")
<hr/>

If you decide to use InfluxDB, you can easily create your own Dashboard with [Grafana](https://github.com/grafana/grafana).

Grafana is very customizable. Rearrange graphs and make it how you want!
![alt text](other/docs/grafana-screenshot-1.png "SolarThing with Grafana")

---

[SolarThing Web](https://github.com/wildmountainfarms/solarthing-web)

While it takes some configuring, SolarThing web is a simple way to display solar data.
![alt text](other/docs/solarthing-web-screenshot-1.png "SolarThing Web")

---

### Can I run this?
Of course! This runs on Java 8+ and has been tested to work with Java 8 and Java 11. This runs on Linux, Windows and Mac,
but is easiest to set up on Linux systems running Debian with systemd (such as the default Raspberry Pi OS or Ubuntu).

You can get the latest jar file on [our releases page](https://github.com/wildmountainfarms/solarthing/releases), but it
is recommended that you use the script to automatically download it. The [quickstart](other/docs/quickstart.md) can help
you get SolarThing up and running.

## Usage at Wild Mountain Farms
We monitor an Outback MATE2 with a Raspberry Pi 1 and a Renogy Rover charge controller with a Raspberry Pi Zero W.
Both SolarThing instances upload data to CouchDB, hosted on the computer also hosting Grafana and SolarThing Web.
This computer also gets data from CouchDB and uploads it to PVOutput using the `pvoutput-upload` program.

We used to also use InfluxDB for allowing easy displaying of data on Grafana, but we now use CouchDB and
SolarThing GraphQL for that.

## Using the program
You can see the [Outback/Renogy Rover](other/solar/README.md) README for using the program with outback or renogy products.

The [input and output](other/docs/input_and_outputs.md) README is documentation for the `io` JSON property option used in all of the sub programs.

### Database Setup
[CouchDB setup](other/docs/couchdb_setup.md)<br/>
*Used for the android and web application*

[InfluxDB setup](other/docs/influxdb_setup.md)<br/>
*Used for Grafana*

#### [Developer Use](other/docs/developer_use.md)
#### [Contributing](CONTRIBUTING.md)
#### [Technical](other/docs/technical/technical.md)
#### [Project Structure](other/docs/technical/project_structure.md)
#### [History](other/docs/history.md)
#### [Google Analytics](other/docs/google_analytics.md)
#### [Updating](other/docs/updating.md)

#### Configuration
This uses all JSON for configuring everything. The files you edit are all in one place unless you decide to move them.

See [Quickstart](other/docs/quickstart.md) to see how to set them up

### Renogy Rover Monitoring Alternatives
Don't like something about SolarThing? Here are some alternatives to monitor your Renogy Rover.
* https://github.com/corbinbs/solarshed
* https://github.com/logreposit/renogy-rover-reader-service
* https://github.com/menloparkinnovation/renogy-rover
* https://github.com/floreno/renogy-rover-modbus
* https://github.com/CyberRad/CoopSolar
* https://github.com/amigadad/SolarDataCollection

### Suggestions?
If you have suggestions on how to improve the documentation or have a feature request, I'd love to
hear from you! [SolarThing Issues](https://github.com/wildmountainfarms/solarthing/issues)

If you get confused while trying to configure solarthing, that's probably because the documentation is
always a work in progress. If you find something confusing, please report it, so I can make it clearer.

---

[![](https://img.shields.io/badge/author-Joshua%20Shannon-brightgreen.svg)](https://github.com/retrodaredevil)
[![](https://img.shields.io/github/repo-size/wildmountainfarms/solarthing.svg)](#)
[![](https://img.shields.io/github/languages/code-size/wildmountainfarms/solarthing.svg)](#)
[![](https://img.shields.io/librariesio/github/wildmountainfarms/solarthing.svg)](build.gradle)
[![](https://img.shields.io/github/commit-activity/m/wildmountainfarms/solarthing.svg)](#)
