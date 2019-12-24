# Contributing
Contributions are welcome! Feel free to submit an issue to check to see if you want to start working on a feature but aren't
sure if it will be accepted.

### Conventions
This project requires Java 8+. However Java 8 API additions aren't used in the `core` module to remain compatible with Android SDK level 19.
This does not apply to the `client` module.

You can set your editor up with [Editor Config](https://www.editorconfig.org) to work with the [.editorconfig file](.editorconfig)
* Use 1 tab for identation
* 2 tabs for a continuation indent

Use standard Java naming conventions. Use `myCoolId` instead of `myCoolID` and use `myCoolDb` instead of `myCoolDB`. 

Exceptions:
* `myIODevice` not `myIoDevice`
* `myPVCurrent` not `myPvCurrent`
* `myKWH` not `myKwh`
* `myAH` not `myAh`
* There may be more exceptions already present

Although I wish that some of these conventions could be changed, changing them would result in confusion and more inconsistencies.

### Using Nullable and NotNull
General rule of thumb:
* Do not use Nullable and NotNull religiously
* If something is Nullable, you should probably annotate it with Nullable
* Just because something is NotNull doesn't mean you need to annotate it and everything else with NotNull

### Customizing
The different command line options give you may ways to receive data and export data. CouchDB and InfluxDB
are both supported.

If you want to store data in another database, you can create your own implementation of [PacketHandler](src/main/java/me/retrodaredevil/solarthing/packets/handling/PacketHandler.java)

If your implementation is general enough, submit a pull request so others can use your implementation as well!
