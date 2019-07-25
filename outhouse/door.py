#!/usr/bin/env python3
import sys
import time


INPUT_PIN = 26


class GPIODoor:
    def __init__(self, pin=26):
        self.pin = pin
        import RPi.GPIO as GPIO
        GPIO.setmode(GPIO.BCM)
        GPIO.setwarnings(False)
        GPIO.setup(pin, GPIO.IN, pull_up_down=GPIO.PUD_UP)

    def is_open(self):
        import RPi.GPIO as GPIO
        # when the door is closed, the sensor is pressed -> a value of 0 means door is closed
        # when the door is open, the sensor is not pressed -> a value of 1 means the door is open
        return GPIO.input(self.pin) == 1

    def cleanup(self):
        import RPi.GPIO as GPIO
        GPIO.cleanup()

class PeriodDoor:
    def __init__(self, seconds_closed, seconds_open):
        self.seconds_closed = seconds_closed
        self.seconds_open = seconds_open

    def is_open(self):
        seconds = time.time()
        interval = self.seconds_closed + self.seconds_open
        period = seconds % interval
        return period > self.seconds_closed

    def cleanup(self):
        pass


def time_millis():
    return int(round(time.time() * 1000))


def main(args):
    door = None
    try:
        if len(args) >= 1 and args[0].lower() == "test":
            door = PeriodDoor(5, 5)
        else:
            door = GPIODoor()
        was_open = None
        last_close = None
        last_open = None
        while True:
            is_open = door.is_open()
            if was_open is False and is_open:  # door just opened
                last_close = time_millis()
            elif was_open is True and not is_open:  # door just closed
                last_open = time_millis()

            was_open = is_open
            print("\nDOOR {} {} {}".format("true" if is_open else "false", last_close, last_open), end="\r", flush=True)
            time.sleep(1)

    finally:
        if door is not None:
            door.cleanup()

if __name__ == '__main__':
    main(sys.argv[1:])
