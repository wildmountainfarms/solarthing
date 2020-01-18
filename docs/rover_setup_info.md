# Rover setup program
This has some more information on how to use the `rover-setup` program.

How to get started: [Rover Quickstart](quickstart_rover.md)

### What is this for?
The `rover-setup` program allows you to quickly get data from your rover by easily requesting
certain data from it. This also allows you to set certain values.

### Example usage:
First, make sure your `base.json` file has `rover-setup` set as the `type`.

Run it:
```shell script
java -jar solarthing.jar base.json
```
Request data:
```
batteryvoltage
25.5
dailykwh
0.0
```

Although I'd love to document all the possible options, there are many, so you can read [this file](../client/src/main/java/me/retrodaredevil/solarthing/program/RoverSetupProgram.java) to understand it better.

### Warning:
This supports the ability to "factory reset" your rover. If you do this, it will set your serial port to 100. (It did for me anyway)
