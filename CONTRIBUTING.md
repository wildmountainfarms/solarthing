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
This project requires Java 8+. You can fully utilize Java 8 APIs.

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

### Timer/Timeout Conventions
The SolarThing codebase has to deal with timers and timeouts. If you just need a timeout (ex: do something after 5 seconds),
use `System.nanoTime()`. `System.nanoTime()` will not jump around if the clock of the system is changed, so by using this
obscure bugs can be prevented. In many cases, you need an absolute time that is consistent across systems. This is where it's
OK to use `System.currentTimeMillis()` or `Instant.now()`.

So:
* Use `System.nanoTime()` for timeouts and timers that can work with relative time
* Use `System.currentTimeMillis()` or `Instant.now()` if you need an absolute time
* Suffix your variable names. So instead of `long lastRunTimestamp`, it should be `long lastRunTimestampMillis` (for an epoch millisecond value)

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
* Use `me.retrodaredevil.solarthing.annotations.Nullable` or `me.retrodaredevil.solarthing.annotations.NotNull`
  * Why the custom NotNull/Nullable? We want good Kotlin support, and we want to be able to annotate more than just methods.
* If something is exposed through GraphQL and it is not null, always put a `@NotNull` on it.

### Branching
If you are committing directly to the wildmountainfarms/solarthing project, you should be using branch names using
the `my-branch-name` format. Unless you have access to commit to the solarthing project directly, 
you can name the branch whatever you would like on your forked solarthing repository.

### Customizing
The different command line options give you may ways to receive data and export data. CouchDB and InfluxDB
are both supported.

If you want to store data in another database, you can create your own implementation of [PacketHandler](core/src/main/java/me/retrodaredevil/solarthing/packets/handling/PacketHandler.java)

If your implementation is general enough, submit a pull request so others can use your implementation as well!

Questions? Bugs? Comments? Visit [our issues page](https://github.com/wildmountainfarms/solarthing)!
