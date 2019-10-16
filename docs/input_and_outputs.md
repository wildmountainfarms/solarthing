# Inputs and Outputs
Each sub program for SolarThing uses some sort of communication to get their data. This can be as simple
as stdin and stdout or can get as complex as a custom serial port configuration.

### Specifying
Specifying the file that has the configuration for the input/output is simple:

```
java -jar solarthing.jar <sub program> --io my_io_file.json
```

### Examples
You can see some examples in [this](../config_templates) directory

#### stdin/stdout example
```
{
  "type": "standard"
}
```

#### Simple serial port example
In this example the serial_config is automatically determined based on which program you choose. You should never have to change it
```
{
  "type": "serial",
  "port": "/dev/ttyUSB0",
  "serial_config": null
}
```
If you need to change the serial_config, you can do this:
```
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
All of the values in the above example are optional EXCEPT ```baud```.

Stop bits can be 1, 1.5 or 2. Parity can be none, odd, even, mark, or space

