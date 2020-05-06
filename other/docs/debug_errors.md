# Debug Errors
SolarThing is complex to set up. If you've made it this far, good job!

If SolarThing spits out errors and crashes, usually that error tells you exactly what went wrong.
If you're unable to figure out what went wrong, please make an issue on [our issues page](https://github.com/wildmountainfarms/solarthing/issues).

#### File Not Found
If you get a message containing `FileNotFoundException`, it's likely because your paths aren't correct or
a file is not in the correct spot. Here's an example:
```
...
java.io.FileNotFoundException: config/base.json (No such file or directory)
...
```
The above error means that there is no file in the path config/base.json. This is relative to your program's
directory, so if you are running the rover program, check in [program/rover/config](../../program/rover/config) and
make sure there's a file named `base.json`.

#### Json Related Errors
JSON related errors are the most common errors. You will likely see an error like this:
```
........................... (Fatal)Error while parsing ProgramOptions. args=[config/base.json]
com.fasterxml.jackson.databind.... <Helpful message here>
```
If you look at where the `<Helpful message here>` is, you may be able to figure out the problem.

Sometimes the JSON problem is a syntax problem, meaning you may be missing a single character, and sometimes
it may be because you are missing a property such as `type`.
