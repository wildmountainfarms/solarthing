## Alter Database Use
Examples on how solarthing-alter is used

Example packets:
* flag "disable_fx_silent_generator_off" set for the next 24 hours
  * execution reason: OpenSourceExecutionReason
    * Open source: sender: "lavender android-asdf", October 01, 2021 at 21:44:03.320, packet schedule flag
* flag "disable_fx_silent_generator_off" set weekdays from 04:00 to 11:00
  * execution reason: Manual setup in CouchDB (idk)
* Schedule command "GEN OFF" on October 11, 2021 at 8:54:03.340
  * execution reason: OpenSourceExecutionReason
    * Open source: sender: "lavender android-asdf", October 01, 2021 at 21:45:05.400, packet schedule command



Usage:
* Lavender types into slack "schedule generator off for 5PM"
  * Schedule command packet gets put into solarthing-open
  * automation program handles the open packet and puts a packet in solarthing-alter as described above
  * slack client waits a max of ~7 seconds to look for the packet in solarthing-alter. If it finds this packet, it tells
  the client it was a success.
    * It knows that the packet it found the executoin reason which has the same schedule command packet. This schedule command packet can
    be compared with ours using the unique request ID
* Lavender types "unschedule generator off"
