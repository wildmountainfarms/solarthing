## Our System
This file just has some values for Wild Mountain Farms solar panel system so I don't forget them.

### Devices
* 1:FX 2:FX 3:MX 4:MX
* Renogy Rover PG 40A
* EPEver Tracer 20A

### Inverters
Both FX's are 3400 watts each

### Panels
* 2310 Watts across 14 panels (14 165 Watt panels)
  * Angle varies throughout the year as it is adjusted when necessary
* 8 panels (4s2p) 75W each for 600W
  * Realistically I'd say this is probably 350W because of how old these panels are
  * Angle is 47 degrees from horizontal. Note these panels are also tilted maybe 10-20 degrees
  * Siemens SP75
    * 4.8A Short Circuit, 4.4A rated
    * 21.7V Open Circuit, 17V rated
* 2 panels (2s) 305W each for 610W
  * Angle is 47 degrees from horizontal (note front panel is actually 45 degrees). Note these panels are also tilted maybe 10-20 degrees
  * QCELLS - [Q.PEAK-G4.1](https://www.q-cells.us/en/main/products/solar_panels/residential/residential03.html) 305
    * 9.84A short circuit, 9.35A rated
    * 40.05V open circuit, 32.62V rated

### FX 1 charging config:
absorb setpoint 29.2 vdc
absorb time limit 1.5 hours
float setpoint 27.2 vdc
float time period 1.0 hours
refloat setpoint 25.0 vdc
equalize setpoint 30.0 vdc
equalize time period 2.0 hours
