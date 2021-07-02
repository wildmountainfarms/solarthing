# Quick Start EPEver Tracer
* If you haven't already, [click here](quickstart.md) to view how to clone this repo and install the service.
* Compatible with EPEver tracer models.
* Prerequisites: You have an RS485 to USB cable that works with your tracer

---

Unlike other serial adapters, the RS485 adapter on the Raspberry Pi doesn't work out of the box. To get it to work,
you need to install an additional driver on your Raspberry Pi. A few years down the line, you shouldn't have to do this,
but now (2021), you do. The awesome project [epsolar-tracer](https://github.com/kasbert/epsolar-tracer) project is
responsible for this driver. I made a script that piggybacks off of the installation instructions. You can run it by:
```shell
sudo apt-get update # not necessary if you've updated recently
sudo apt-get install dkms # May already be installed
sudo /opt/solarthing/other/linux/install_updated_serial_driver.sh
```

Once that is done running, you should reboot. After the reboot with the USB serial adapter plugged in, run
```shell
ls /dev/tty*
```
You should notice that there is a file/device called `ttyXRUSB0`. If there is, the driver installation worked, and you can continue!

Once everything is installed, you're ready to edit the configs. You will `cd` to the `program/request` directory.
```
cd /opt/solarthing/program/request
```
If you are already using the request directory, click [here](./custom_directories.md).

Copy some template config files ([default_linux_serial](../../config_templates/io/default_linux_serial.json) and [tracer_request_template](../../config_templates/base/tracer_request_template.json))
```
# sudo should not be required unless permissions were not set up correctly (add yourself to the solarthing group)
cp ../../config_templates/io/xr_serial_io.json config/
cp ../../config_templates/base/tracer_request_template.json config/base.json
```

Now edit `config/base.json`
* Most tracers have a default modbus address of 1, hence the `"1"` present in the template configuration.
* Make sure you change the `"io"` property to correctly point to the io JSON file you want to use. Remember paths
  are relative to `program/request`, so `config/io.json` would point to `program/request/config/io.json`.
  
### I'm ready to use this for real!
Once your configuration is how you want it, you can go back to the [quickstart](quickstart.md#configuration-continued) to enable and start the service.
