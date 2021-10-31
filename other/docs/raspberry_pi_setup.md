# Raspberry Pi Setup
So you have a Raspberry Pi and you're ready to get SolarThing working? This page can help you
get up and running quickly if you're unfamiliar with Raspberry Pis and connecting them to your WiFi or Ethernet.

## Download Raspberry Pi OS Lite
This is a version of Linux for Raspberry Pis! I recommend downloading the "Lite" version.

Go here: https://www.raspberrypi.org/software/operating-systems/ and download Raspberry Pi OS **Lite**.
If you decide to download the one "with desktop and recommended software", updating it will take forever whenever you
decide to update it. If you think you're going to hook your Raspberry Pi up to a monitor you *could* download the desktop version.

## Download Balena Etcher
There are many ways to flash an OS image to an SD card, but Etcher is one of the easiest ways. Download it
here: https://www.balena.io/etcher/

Once you've downloaded it, open it and start flashing the Raspberry Pi OS image to it.

## Decide how you want to access it

### Using a monitor and keyboard with mouse
If you choose this option, make sure you have a monitor, keyboard and mouse, then put your SD card in your Pi and power it up! 
You can start to follow SolarThing instructions such as the quickstart! Leave this page, you're done!

### Another option with SSH (headless)
If you choose this option, take out your SD card from your computer, then put it back in. When you do this,
you should see either one (on Windows/Mac) or two (on Linux) external drives show up on your computer. Go into the drive
named "boot". In this folder, there should be a bunch of files. Now, create a file named `ssh` with NO file extension. This
enables SSH. If you are using Ethernet, you're done. If you're using WiFi, take a look at the next section.

RetroPie has great documentation for this sort of thing: https://retropie.org.uk/docs/SSH/

#### Connecting to WiFi (headless)
If you want to install a Python program to help you with this, go [here](https://github.com/retrodaredevil/headless-setup).
Otherwise, follow these manual instructions. On your drive, create a file named `wpa_supplicant.conf` and put this in it:
```
ctrl_interface=DIR=/var/run/wpa_supplicant GROUP=netdev
update_config=1
country=US

network={
    ssid="Your SSID name here"
    psk="Your password here"
}
```
Replace `US` with whatever your country is, then save the file.

Now take the SD card out, put it in the Pi, and start to follow other SolarThing instructions! You're done on this page!
