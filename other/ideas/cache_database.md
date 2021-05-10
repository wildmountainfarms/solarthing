
The `solarthing` CouchDB database has a lot of data in it, and going over all of that data at once
isn't plausible. So, a "cache" database which can store information for a single hour would be useful

`solarthing-cache` could have IDs such as:
* `cache_2021-04-24T09:00Z` for April 24, 2021 at 09:00 to 10:00 UTC
* This also doesn't have to be one per hour, we could make it customizable with default period: `PS1H`.

SolarThing GraphQL could handle the calculation of these cache documents and can also handle the
public facing API of getting that data for a particular hour. This data could be used in SolarThing Android
and of course in Grafana.

Whenever the logic of the cache document creation changes, all the caches will have to be recalculated.
The SolarThing GraphQL program could calculate the past week in the background. The SolarThing GraphQL
program could also have the option of not handling the cache database, but then it wouldn't have
many features.

