# Sources and Fragments
SolarThing has sources and fragments, a concept that is pretty simple to understand.

A fragment ID is an identifier for a single SolarThing instance running.

A source ID is a way to group fragments. Most of the time you should always leave this as `default`.

---

Fragment IDs are unique, even across different source IDs.


### Our setup
Mate Pi:
* mate program:
  * source: `default`
  * fragmentId: `1`
* request program
  * source: `default`
  * fragmentId: `101`

RoverPi:
* rover program:
  * source: `default`
  * fragmentId: `2`
  
Both Mate Pi and Rover Pi monitor the same system, so they have the same source ID, `default`.
You can also see that since Mate Pi is running two different programs, each has a different fragment ID.
  
  
### Example Setup
Mate Pi:
* mate program:
  * source: `default`
  * fragmentId: `1`

Rover Pi:
* rover program:
  * source: `default`
  * fragmentId: `2`

Barn Pi:
* rover program:
  * source: `barn`
  * fragmentId: `3`
  
In this example setup, Barn Pi's rover program has a fragment ID of `3`, and a source of `barn`. The fragment ID has to
be different, but the source ID of `barn` was chosen because we want to be able to differentiate from each
battery system.
