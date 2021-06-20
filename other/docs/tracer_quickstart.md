## Tracer quickstart
Work in progress

I found my serial on `/dev/ttyACM0` using an RPi4

`dmesg | grep "tty"` was usful in finding the serial: https://raspberrypi.stackexchange.com/a/88086

`ls -al serial* ttyA*` is useful

Tracer uses Half-Duplex RS485, which involves only an A and a B wire (and ground), where only one device can transmit at a time

https://raspberrypi.stackexchange.com/a/14237

https://github.com/kasbert/epsolar-tracer/tree/master/xr_usb_serial_common-1a

https://www.maxlinear.com/support/design-tools/software-drivers

https://www.solarpoweredhome.co.uk/


```shell
# You could get this issue: https://github.com/gnab/rtl8812au/issues/147
sudo apt-get update && sudo apt-get install --reinstall raspberrypi-bootloader raspberrypi-kernel
sudo apt-get install raspberrypi-kernel-headers
# reboot
# Install XR USB stuff
cd ~
git clone https://github.com/kasbert/epsolar-tracer
cd epsolar-tracer/xr_usb_serial_common-1a
make
sudo rmmod cdc-acm
sudo modprobe -r usbserial
sudo modprobe usbserial
sudo insmod ./xr_usb_serial_common.ko
# Plug in (or unplug and replug), the USB serial
```

https://raw.githubusercontent.com/torvalds/linux/v5.13-rc6/drivers/usb/serial/xr_serial.c


Json data from first packet outputted
```json
{
  "ratedInputVoltage" : 100.0,
  "ratedInputCurrent" : 2000.0,
  "ratedInputPower" : 520.0,
  "ratedOutputVoltage" : 24.0,
  "ratedOutputCurrent" : 20.0,
  "ratedOutputPower" : 52000.0,
  "chargingTypeValue" : 2,
  "ratedLoadOutputCurrent" : 2000.0,
  "inputVoltage" : 0.02,
  "pvCurrent" : 0.0,
  "pvWattage" : 0.0,
  "batteryVoltage" : 12.04,
  "chargingCurrent" : 0.0,
  "chargingPower" : 0.0,
  "loadVoltage" : 0.0,
  "loadCurrent" : 0.0,
  "loadPower" : 0.0,
  "batteryTemperatureCelsius" : 22.63,
  "insideControllerTemperatureCelsius" : 21.75,
  "powerComponentTemperatureCelsius" : 21.75,
  "batterySOC" : 34,
  "remoteBatteryTemperatureCelsius" : 0.0,
  "realBatteryRatedVoltageValue" : 12,
  "batteryStatusValue" : 0,
  "chargingEquipmentStatusValue" : 1,
  "dailyMaxInputVoltage" : 0.02,
  "dailyMinInputVoltage" : 0.0,
  "dailyMaxBatteryVoltage" : 12.08,
  "dailyMinBatteryVoltage" : 12.04,
  "dailyKWHConsumption" : 0.0,
  "monthlyKWHConsumption" : 0.0,
  "yearlyKWHConsumption" : 0.0,
  "cumulativeKWHConsumption" : 0.0,
  "dailyKWH" : 0.0,
  "monthlyKWH" : 0.0,
  "yearlyKWH" : 0.0,
  "cumulativeKWH" : 0.0,
  "carbonDioxideReductionTons" : 0.0,
  "netBatteryCurrent" : 0.0,
  "batteryTemperatureCelsius331D" : 22.63,
  "ambientTemperatureCelsius" : 22.63,
  "batteryTypeValue" : 1,
  "batteryCapacityAmpHours" : 200,
  "temperatureCompensationCoefficient" : 3,
  "highVoltageDisconnect" : 16.0,
  "chargingLimitVoltage" : 15.0,
  "overVoltageReconnect" : 15.0,
  "equalizationVoltage" : 14.6,
  "boostVoltage" : 14.4,
  "floatVoltage" : 13.8,
  "boostReconnectVoltage" : 13.2,
  "lowVoltageReconnect" : 12.6,
  "underVoltageRecover" : 12.2,
  "underVoltageWarning" : 12.0,
  "lowVoltageDisconnect" : 11.1,
  "dischargingLimitVoltage" : 10.6,
  "secondMinuteHourDayMonthYearRaw" : 14297963040273,
  "equalizationChargingCycleDays" : 30,
  "batteryTemperatureWarningUpperLimit" : 65.0,
  "batteryTemperatureWarningLowerLimit" : 615.36,
  "insideControllerTemperatureWarningUpperLimit" : 85.0,
  "insideControllerTemperatureWarningUpperLimitRecover" : 75.0,
  "powerComponentTemperatureWarningUpperLimit" : 85.0,
  "powerComponentTemperatureWarningUpperLimitRecover" : 75.0,
  "lineImpedance" : 0.0,
  "nightPVVoltageThreshold" : 5.0,
  "lightSignalStartupDelayTime" : 10,
  "dayPVVoltageThreshold" : 6.0,
  "lightSignalTurnOffDelayTime" : 10,
  "loadControlModeValue" : 0,
  "workingTimeLength1Raw" : 256,
  "workingTimeLength2Raw" : 256,
  "turnOnTiming1Raw" : 81604378624,
  "turnOffTiming1Raw" : 25769803776,
  "turnOnTiming2Raw" : 81604378624,
  "turnOffTiming2Raw" : 25769803776,
  "lengthOfNightRaw" : 3072,
  "batteryRatedVoltageCode" : 0,
  "loadTimingControlSelectionValueRaw" : 0,
  "isLoadOnByDefaultInManualMode" : true,
  "equalizeDurationMinutes" : 120,
  "boostDurationMinutes" : 120,
  "dischargingPercentage" : 30,
  "chargingPercentage" : 100,
  "batteryManagementModeValue" : 0,
  "isManualLoadControlOn" : false,
  "isLoadTestModeEnabled" : false,
  "isLoadForcedOn" : false,
  "isInsideControllerOverTemperature" : false,
  "isNight" : true,
  "dailyAH" : 0
}
```
