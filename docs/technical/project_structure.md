# Project Structure

### [core](../../core)
* A gradle subproject
* Contains core code used in all other subprojects
### [client](../../client)
* A gradle subproject
* Contains the program for uploading packets to databases
### [serviceapi](../../serviceapi)
* A gradle subproject
* An API for https://pvoutput.org and for https://emoncms.org
### [schema-generator](../../schema-generator) (WIP)
* A gradle subproject
* A subprogram that generates JSON schema
### [json-datasource](../../json-datasource) (WIP)
* A gradle subproject
* A subprogram that is a REST interface that exposes SolarThing data from CouchDB to Grafana

---

### [program](../../program)
* A directory that is used as the working directory when running the `client` program.
* Files in this directory are not pushed to GitHub unless explicitly added
### [config_templates](../../config_templates)
* A directory that contains configuration templates that should be copied to [program/config](../../program/config)
### [solar](../../solar)
* A directory that contains documentation for this project relating to solar data
### [outhouse](../../legacy/outhouse)
* A directory that contains documentation for this project relating to outhouse data
### [systemd](../../systemd)
* A directory that contains files for installing the solarthing service to a system that uses systemd
### [docs](../../docs)
* A directory that contains lots of documentation in the markdown format
### [legacy](../../legacy)
* A directory containing old files that are no longer used
### [other](../../other)
* A directory containing miscellaneous files
