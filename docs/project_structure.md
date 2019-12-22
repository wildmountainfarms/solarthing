# Project Structure

### [core](../core)
* A gradle subproject
* Contains core code used in all other subprojects
### [client](../client)
* A gradle subproject
* Contains the program for uploading packets to databases
### [pvoutput](../pvoutput)
* A gradle subproject
* An API for https://pvoutput.org

---

### [program](../program)
* A directory that is used as the working directory when running the `client` program.
### [config_templates](../config_templates)
* A directory that contains configuration templates that should be copied to [program/config](../program/config)
### [solar](../solar)
* A directory that contains documentation for this project relating to solar data
### [outhouse](../outhouse)
* A directory that contains documentation for this project relating to outhouse data
### [systemd](../systemd)
* A directory that contains files for installing the solarthing service to a system that uses systemd
