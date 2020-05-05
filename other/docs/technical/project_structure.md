# Project Structure

### [core](../../../core)
* A gradle subproject
* Contains core code used in all other subprojects
### [client](../../../client)
* A gradle subproject
* Contains the program for uploading packets to databases
### [serviceapi](../../../serviceapi)
* A gradle subproject
* An API for https://pvoutput.org and for https://emoncms.org
### [graphql](../../../graphql)
* A gradle subproject
* A subprogram that exposes a CouchDB database as a GraphQL Server.
### [json-datasource](../../../json-datasource) (WIP)
* A gradle subproject
* A subprogram that is a REST interface that exposes SolarThing data from CouchDB to Grafana

---

### [program](../../../program)
* Contains directories that are used as working directories when running SolarThing
* Files in this directory are not pushed to GitHub unless explicitly added (or there's an exception in .gitignore)
### [config_templates](../../../config_templates)
* A directory that contains configuration templates that should be copied to `program/<program>/config`

---

### [other](../..)
* A directory containing scripts and documentation (and)
### [other/solar](../../solar)
* A directory that contains documentation for this project relating to solar data
### [other/systemd](../../systemd)
* A directory that contains files for installing the solarthing service to a system that uses systemd
### [other/docs](..)
* A directory that contains lots of documentation in the markdown format
### [other/legacy](../../legacy)
* A directory containing old files that are no longer used
