# outhouse-alert
Part of the IoT code for Wild Mountain Farms

## Info
distance.py spits out distances from the HC-SR04

temp.py spits out tempurature (in C) and humidity from a DHT11

### outhouse.py
This spits out distance, temperature and humidity information, in that order. distance is in centimeters (a float),
temperature is in celsius (an int), humidity is a number from 0 to 100 representing humidity (an int)

### Distance
When `python3 distance.py` is run, it prints out data for the distance
periodically. It prints out a number representing the distance in centimeters.

### Temperature and Humidity
When `python3 temp.py` is run, it prints out data for temperature and humidity
periodically. The first number is the temperature in Celsius and the second
number is a number from 0 to 100 representing the humidity.

