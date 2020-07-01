# Voltage Sensor
This is a simple arduino program to send the raw value over serial.

This can be used to monitor the battery voltage of something.

### Useful links
* https://www.engineersgarage.com/knowledge_share/series-combination-of-batteries-voltage-monitor/

### Setup
Plug the Arduino into the Pi via USB, and give the Arduino another power source since
it can't draw enough from the Pi unless you're using a Pi3 or later with USB3.0.

### Testing
Use `cu`. https://www.cyberciti.biz/hardware/5-linux-unix-commands-for-connecting-to-the-serial-console/

### Testing Arduino On The pi headless
Install arduino (and its drivers) on a Raspberry Pi
```shell script
sudo apt install arduino
```
Install arduino-cli https://arduino.github.io/arduino-cli/installation/

Then run
```shell script
arduino-cli core update-index
arduino-cli board list
```
