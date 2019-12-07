# Contributing
Contributions are welcome! Feel free to submit an issue to check to see if you want to start working on a feature but aren't
sure if it will be accepted.

### Conventions
This project requires Java 8+. However Java 8 API additions aren't used in the `core` module to remain compatible with Android SDK level 19.
This does not apply to the `client` module.

You can set your editor up with [Editor Config](https://www.editorconfig.org) to work with the [.editorconfig file](.editorconfig)

Use standard Java naming conventions. Use `myCoolId` instead of `myCoolID` and use `myCoolDb` instead of `myCoolDB`. The exception
here is that you SHOULD use `myIODevice` instead of `myIoDevice`.


### Customizing
The different command line options give you may ways to receive data and export data. CouchDB and InfluxDB
are both supported.

If you want to store data in another database, you can create your own implementation of [PacketHandler](src/main/java/me/retrodaredevil/solarthing/packets/handling/PacketHandler.java)

If your implementation is general enough, submit a pull request so others can use your implementation as well!
