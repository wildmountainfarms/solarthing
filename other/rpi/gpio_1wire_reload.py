#!/usr/bin/env python3

import RPi.GPIO as GPIO
import sys
import time
from pathlib import Path


"""
Example usage:
sudo -u solarthing /opt/solarthing/other/rpi/gpio_1wire_reload.py 14 /sys/bus/w1/devices/28-0301a279f5ff/name /sys/bus/w1/devices/28-0301a279ffb2/name
I recommend using pin 17, but pin 14 works as long as you aren't using it for serial communication

May need to run this:
sudo apt-get install python3-dev python3-rpi.gpio
"""


def main(args):
    print("Running main args: {}".format(args))
    GPIO.setmode(GPIO.BCM)
    pin = int(args[0])
    required_files = args[1:]
    print("Pin: {}".format(pin))
    print("Required files: {}".format(required_files))

    GPIO.setup(pin, GPIO.OUT)
    GPIO.output(pin, GPIO.HIGH)

    time.sleep(5)
    while True:
        if any(not Path(file).exists() for file in required_files):
            print("Power cycling")
            GPIO.output(pin, GPIO.LOW)
            time.sleep(2)
            GPIO.output(pin, GPIO.HIGH)
            time.sleep(15)
            continue

        time.sleep(5)


def start_main():
    try:
        main(sys.argv[1:])
    except Exception as e:
        GPIO.cleanup()
        print("cleaning up")
        raise e


if __name__ == '__main__':
    start_main()
