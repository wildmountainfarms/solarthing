### `program` directory
This directory should be used as the working directory when running solarthing.jar.

#### Sub directories
* `.data` is where SolarThing stores data related to the program. Don't edit these files
* `authorized` is where authorized public keys should go. Copy files from `unauthorized` here to authorize a command sender
* `config` is where all configuration data should go. **Edit these files for configuring**
* `legacy` contains unused legacy code. You shouldn't edit files in here
* `logs` contains all the log files
* `solar` contains files to be used while testing SolarThing. Do not edit these files.
* `unauthorized` contains unauthorized public keys that can be copied into `authorized`.

#### Files
* `command_input.txt` you can write commands to be executed to this file by using `cat >command_input.txt`
* `latest_json.json` the default location for storing the latest set of packets.
* `README.md` this file!
* `solarthing.jar` the compiled SolarThing jar for running the `client` submodule
* `solarthing.sh` an executable that makes running SolarThing on Linux very easy
