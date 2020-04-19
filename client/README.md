## "client" subproject
This project utilizes code in the `core` module to read data from an Outback MATE or Renogy Rover
and uploads that data to a database.


### Ideas
#### Logging
##### 2020.04.18
We need a better way to log. As it is right now, we have "debug" and "info" logs. Everything goes into
debug, which is fine because debug logs **should be deleted** once we don't need them anymore. However,
ANY error gets put into the "info" logs. This makes looking at the info log after the network was
down a huge pain. Some errors and warnings happen continuously during a failure. 

I think we need more log files. Maybe this: "debug", "info", and "summary". Debug and info logs
can be automatically deleted, but summary logs should contain minimal lines per day. 

#### Restarting Device
##### 2020.04.18
The Mate subprogram has been tested and can go months without a device restart. However, the Renogy
Rover program doesn't seem as tolerant for some reason. I believe that this is a problem with
hardware that can, for some reason, be fixed by restarting the device. For now, I recommend to
set up automatic restarts about every week.
