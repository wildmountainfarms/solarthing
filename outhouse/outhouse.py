#!/usr/bin/env python3
import time
import sys


TRIG = 23
ECHO = 24

SENSOR_PIN = 4

class DummyData:
    def __init__(self):
        self.distances = [60, 60, 60, 96, 96, 96, 96, 160, 160, 160, 160, 10, 160, 160, 160, 160, 160, 160]
        self.distance_counter = 0

    def get_humidity_temperature(self):
        time.sleep(1)
        return 50, 23

    def get_distance(self):
        r = self.distances[self.distance_counter % len(self.distances)]
        self.distance_counter += 1
        return r

    def cleanup(self):
        pass

class SensorData:

    def __init__(self):
        import RPi.GPIO as GPIO
        GPIO.setmode(GPIO.BCM)
        GPIO.setwarnings(False)

        GPIO.setup(TRIG,GPIO.OUT)
        GPIO.output(TRIG, False)
        GPIO.setup(ECHO,GPIO.IN)

    def get_humidity_temperature(self):
        from Adafruit_DHT import read_retry, DHT11
        hum, temp = read_retry(DHT11, SENSOR_PIN)
        hum = int(hum)
        temp = int(temp)
        return hum, temp

    def get_distance(self):
        import RPi.GPIO as GPIO
        GPIO.output(TRIG, True)
        time.sleep(0.00001)
        GPIO.output(TRIG, False)

        while GPIO.input(ECHO)==0:
            time.sleep(0.00001)  # be nice to the CPU
        pulse_start = time.time()
        while GPIO.input(ECHO)==1:
            time.sleep(0.00001)
        pulse_end = time.time()

        assert pulse_start is not None and pulse_end is not None
        pulse_duration = pulse_end - pulse_start

        distance = pulse_duration * 17150
        if 2 < distance < 400:
            return distance
        else:
            return None

    def cleanup(self):
        import RPi.GPIO as GPIO
        GPIO.cleanup()


class OccupancyHandler:
    def __init__(self, max_count=6):
        self.max_count = max_count

        self.occupied = False
        self.counter = 0

    def on_distance(self, distance):
        if distance is None:
            return
        if distance < 30:
            self.counter = self.max_count
        elif distance < 50:
            self.counter += 3
        elif distance < 70:
            self.counter += 2
        elif distance < 90:
            self.counter += 1
        else:
            if distance > 150:
                self.counter -= 3
            elif distance > 130:
                self.counter -= 2
            elif distance > 110:
                self.counter -= 1

        self.counter = max(0, min(self.max_count, self.counter))
        if self.counter <= 1:
            self.occupied = False
        elif self.counter >= 4:
            self.occupied = True


def main(args):
    data = None
    try:
        if len(args) >= 1 and args[0].lower() == "test":
            data = DummyData()
        else:
            data = SensorData()
        time.sleep(.1)

        occupancy_handler = OccupancyHandler()

        while True:
            distance = data.get_distance()
            time.sleep(.03)  # give CPU a little rest
            hum, temp = data.get_humidity_temperature()

            occupancy_handler.on_distance(distance)

            print("\n{} {} {}".format("true" if occupancy_handler.occupied else "false", temp, hum), end="\r", flush=True)
            print("[DEBUG] distance was: {}, counter: {}, occupied: {}, temp: {}, humidity: {}".format(distance, occupancy_handler.counter, occupancy_handler.occupied, temp, hum), file=sys.stderr)
            time.sleep(.5)
    except KeyboardInterrupt:
        pass
    finally:
        if data is not None:
            data.cleanup()

if __name__ == '__main__':
    main(sys.argv[1:])
