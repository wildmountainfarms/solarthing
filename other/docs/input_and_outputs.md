# Inputs and Outputs
Each sub program for SolarThing uses some sort of communication to get their data. This can be as simple
as stdin and stdout or can get as complex as a custom serial port configuration.

### Specifying
To specify your io config, you give the path to the io config file you're about to create in your base.json

base.json:
```json
{
  "type": "my program type",
  "io": "<my io file relative to the 'program/rover' directory>",
  ...
}
```

### Examples
You can see some examples in [this](../../config_templates/io) directory

#### Simple serial port example
In this example the serial_config is automatically determined based on which program you choose. You should never have to change it
```json
{
  "type": "serial",
  "port": "/dev/ttyUSB0",
  "serial_config": null
}
```
If you need to change the serial_config, you can do this:
```json
{
  "type": "serial",
  "port": "/dev/ttyUSB0",
  "serial_config": {
    "baud": 9600,
    "data_bits": 8,
    "stop_bits": 1,
    "parity": "none",
    "rts": false,
    "dtr": false
  }
}
```
All the values in the above example are optional EXCEPT ```baud```.

Stop bits can be 1, 1.5 or 2. Parity can be none, odd, even, mark, or space

#### stdin/stdout example
```json
{
  "type": "standard"
}
```

