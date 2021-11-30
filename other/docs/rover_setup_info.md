# Rover setup program
This has some more information on how to use the `rover-setup` program.

Note: This will be deprecated soon in favor of documentation I have not yet written.

How to get started: [Rover Quickstart](quickstart_rover.md)

### What is this for?
The `rover-setup` program allows you to quickly get data from your rover by easily requesting
certain data from it. This also allows you to set certain values.

### Example usage:
First, make sure your `base.json` file has `rover-setup` set as the `type`.

Run it:
```shell script
# Also note that you will likely have to run this as root unless you give your user the correct groups to access serial ports
./run.sh
```
Request data:
```
batteryvoltage
25.5
dailykwh
0.0
```

Although I'd love to document all the possible options, there are many, so you can read [this file](../../client/src/main/java/me/retrodaredevil/solarthing/program/RoverSetupProgram.java) to understand it better.
