# outhouse
Part of the IoT code for Wild Mountain Farms

## Info
distance.py spits out distances from the HC-SR04

temp.py spits out temperature (in C) and humidity from a DHT11

### outhouse.py
This spits out distance, temperature and humidity information, in that order. distance is in centimeters (a float),
temperature is in celsius (an int), humidity is a number from 0 to 100 representing humidity (an int)

## Running
You would normally run this command:
```
# this in /etc/rc.local
(cd /home/pi/solarthing/program
&& (outhouse/occupancy_weather.py 
| java -jar solarthing.jar outhouse --unique 15 --io config_templates/standard_io.json) 2>errors.txt) &
```

