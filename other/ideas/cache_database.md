
The `solarthing` CouchDB database has a lot of data in it, and going over all of that data at once
isn't plausible. So, a "cache" database which can store information for a single hour would be useful

`solarthing-cache` could have IDs such as:
* `cache_2021-04-24T09:00Z_PS1H` for April 24, 2021 at 09:00 to 10:00 UTC
* This also doesn't have to be one per hour, we could make it customizable with default period: `PS1H`.

SolarThing GraphQL could handle the calculation of these cache documents and can also handle the
public facing API of getting that data for a particular hour. This data could be used in SolarThing Android
and of course in Grafana.

Whenever the logic of the cache document creation changes, all the caches will have to be recalculated.
The SolarThing GraphQL program could calculate the past week in the background. The SolarThing GraphQL
program could also have the option of not handling the cache database, but then it wouldn't have
many features.

The important thing is that caches are just that: caches. Deleting them shouldn't lose any data. The only thing
deleting a cache loses is time.

Caches for:
* charge controller information
  * Daily kWh
  * highest pv wattage, voltage, amps
  * highest charging wattage, voltage, amps
* FX 
  * FX_DAILY stuff
  * AC mode timings (How long has AC Use been active)
  * FX Operational mode timings
* MX
  * Charger mode timings
* Rover
  * Charging mode timings
* High and low battery voltages

Queries look like:
* Queries ask for the data they want directly, so someone can ask for "daily kWh" information for a certain period,
not just for all cached information
  * This means that document IDs could look like `cache_2021-04-24T09:00Z_PS1H_default_CHARGE_CONTROLLER`
* While it might be possible to expose data directly through the GraphQL API, it would add a lot of overhead
because this data will likely be queried through SolarThing Android, which uses this same type system.
Because it uses the same type system, it would be better to directly send this data through a REST API

