## CouchDB
This is a **submodule**, not documentation for SolarThing. If you are looking for information about
setting up CouchDB with solarthing go [here](../other/docs/couchdb_setup.md).

### Info
CouchDB is a less popular database nowadays and so its Java library support isn't the greatest. Ektorp and LightCouch
are good, but are outdated. This aims to fix that.

Goals:
* Immutable data types in the library
* Not tightly coupled to the underlying JSON library
  * In this case, this will not be tightly coupled to Jackson. This means no Jackson specific types will be
    exposed via the public API. The consumers of this API can parse JSON from Strings using Jackson or their preferred
    library.
  * This may not seem ideal, but anyone who uses GSON or another library will appreciate this.
* Response objects should be very similar to the actual response from CouchDB
* Multiple ways to authenticate with a CouchDB database.

This will be a work in progress for a while, and may not ever get used, but I really would like a
CouchDB library with immutable data that is easy to use.

https://cwiki.apache.org/confluence/display/COUCHDB/Java
