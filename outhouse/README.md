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
outhouse/occupancy_weather.py | java -jar solarthing.jar outhouse --host 192.168.1.110 --unique 30
# or this in /etc/rc.local
(cd /home/pi/Programming/SolarThing/solarthing 
&& (outhouse/occupancy_weather.py 
| java -jar solarthing.jar outhouse --user user --passwd password --host 192.168.10.250 --unique 15 --tf 3 --ct 30 --st 5) >output.txt 2>errors.txt) &
```

