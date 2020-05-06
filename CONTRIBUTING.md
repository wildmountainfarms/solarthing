# Contributing
Contributions are welcome! Feel free to submit an issue to check to see if you want to start working on a feature but aren't
sure if it will be accepted.

### Compiling (For Testing)
Run `./compile_and_move.sh`. (You only need to do this if you've changed the code) This updates [solarthing.jar](program/solarthing.jar).

## Submitting Changes
When you submit changes, you should make sure you follow conventions. Most of the time you should test your changes.

If the `.gitignore` file needs something added to it, please add it **at the bottom**.

---

### Conventions
This project requires Java 8+. However, Java 8 API additions aren't used in the `core` module to remain compatible with Android SDK level 19.
This does not apply to the `client` module.

You can set your editor up with [Editor Config](https://www.editorconfig.org) to work with the [.editorconfig file](.editorconfig)
* Use 1 tab for indentation
* 2 tabs for a continuation indent

Use standard Java naming conventions. Use `myCoolId` instead of `myCoolID` and use `myCoolDb` instead of `myCoolDB`. 

Exceptions:
* `myIODevice` not `myIoDevice`
* `myPVCurrent` not `myPvCurrent`
* `myKWH` not `myKwh`
* `myAH` not `myAh`
* There may be more exceptions already present

Although I wish that some of these conventions could be changed, changing them would result in confusion and more inconsistencies.

### Styling
```javascript
if(true){ // OK
}
if (true) { // OK
}
if (true){ // OK
}
if(true) { // OK
}
```
Any of those styles will work. You will see throughout the code that it is pretty inconsistent.
This is because I don't have a preference and I don't really notice much of a difference.

But...
```javascript
if( true ){ // NOT OK
}
```

### Using Nullable and NotNull
General rule of thumb:
* Do not use Nullable and NotNull religiously.
* If something is Nullable, you should probably annotate it with Nullable.
* Just because something is NotNull doesn't mean you need to annotate it and everything else with NotNull.
* But if something is commonly used (public api), then annotating with NotNull can be helpful.
* Use `org.jetbrains.annotations.Nullable` or `javax.validation.constraints.NotNull`
  * Why? Because the `javax.validation` variant is available at runtime, which is useful in some cases.

### Customizing
The different command line options give you may ways to receive data and export data. CouchDB and InfluxDB
are both supported.

If you want to store data in another database, you can create your own implementation of [PacketHandler](core/src/main/java/me/retrodaredevil/solarthing/packets/handling/PacketHandler.java)

If your implementation is general enough, submit a pull request so others can use your implementation as well!

Questions? Bugs? Comments? Visit [our issues page](https://github.com/wildmountainfarms/solarthing)!
