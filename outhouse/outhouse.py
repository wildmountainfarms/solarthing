import sys
import door as d
import occupancy_weather as ow


def main(args):
    door = None
    try:
        if len(args) >= 1 and args[0].lower() == "test":
            door = d.PeriodDoor(5, 5)
            data = ow.DummyData()
        else:
            door = d.GPIODoor()
            data = ow.SensorData()

        occupancy_handler = ow.OccupancyHandler()
        was_open = None
        last_close = None
        last_open = None
        while True:
            is_open = door.is_open()
            if was_open is False and is_open:  # door just opened
                last_close = d.time_millis()
            elif was_open is True and not is_open:  # door just closed
                last_open = d.time_millis()

            was_open = is_open
            print("\nDOOR {} {} {}".format("true" if is_open else "false", last_close, last_open), end="\r", flush=True)

            distance = data.get_distance()
            time.sleep(.03)  # give CPU a little rest
            hum, temp = data.get_humidity_temperature()

            occupancy_handler.on_distance(distance)
            print("\nOCCUPANCY {}".format("true" if occupancy_handler.occupied else "false"), end="\r")
            print("\nWEATHER {} {}".format(temp, hum), end="\r", flush=True)
            time.sleep(1)

    finally:
        if door is not None:
            door.cleanup()



if __name__ == '__main__':
    main(sys.argv[1:])
