# outhouse
Part of the IoT code for Wild Mountain Farms

#### This is not currently being maintained.

## Info
distance.py spits out distances from the HC-SR04

temp.py spits out temperature (in C) and humidity from a DHT11


### outhouse.py
This spits out distance, temperature and humidity information, in that order. distance is in centimeters (a float),
temperature is in celsius (an int), humidity is a number from 0 to 100 representing humidity (an int)

## Outhouse Configuration
```json
{
  "type": "outhouse",
  "io": "<standard io/out file>"
}
```
In the future the ability to specify a command to run will allow for easier configuration so you
don't have to manually pipe the output of outhouse.py into the program.
