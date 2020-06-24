### `program` directory
This directory contains many other directories. See their descriptions below.

Note: running `./download_solarthing.sh` or `./graphql_download_solarthing.sh` will update/download SolarThing.
When doing this make sure to run a `git pull` before hand to get the latest version.

---

### Descriptions

#### Directories
* `mate` the working directory for the mate program.
* `rover` the working directory for the rover program.
* `pvoutput` the working directory for the PVOutput program.
* `graphql` the working directory for the GraphQL program.
* `message` the working directory for the message program.
* `request` the working directory for the request program.
* `custom1` the working directory for your program of choice.

#### other directories
* `.downloads` downloaded SolarThing jar files are stored here.
* `.scripts` contains scripts used when running/setting up SolarThing.
* `.legacy` contains unused legacy code. You shouldn't edit files in here.
* `config` is where shared configuration should go.

#### Files
* `README.md` this file!
* `solarthing.jar` (must be compiled or downloaded). If downloaded, a symbolic link goes here.
* `download_solarthing.sh` downloads the version of SolarThing that this commit defaults to.

#### Sub directory files
* `config` is where most configuration data should go. **Edit these files for configuring**
* `.data` is where SolarThing stores data related to the program. Don't edit these files
* `authorized` is where authorized public keys should go. Copy files from `unauthorized` here to authorize a command sender
  * Only used in [mate](mate) so far
* `unauthorized` contains unauthorized public keys that can be copied into `authorized`.
  * Only used in [mate](mate) so far
* `test` contains files to be used while testing SolarThing. Do not edit these files.
* `logs` contains all the log files
