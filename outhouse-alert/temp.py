import time
from Adafruit_DHT import read_retry, DHT11

SENSOR_NAME = DHT11
SENSOR_PIN = 4
time.sleep(2)
while True:
    hum, temp = read_retry(SENSOR_NAME, SENSOR_PIN)
    hum = int(hum)
    temp = int(temp)
    print("{} {}".format(temp, hum))
    time.sleep(.75)

