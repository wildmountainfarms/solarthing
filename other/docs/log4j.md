# Log4j
SolarThing uses Log4j2 for logging. (But not the GraphQL program yet, everything else yes)

You can override the default configuration in a few ways:

### `log4j2.xml` in working directory
When executing `run.sh`, it is configured to detect a file called `log4j2.xml` in the working
directory (such as `program/rover` or `program/XXXX`). If it finds it, it will use that for the configuration.

You can find different log4j2 configurations [here](../../config_templates/log). Just remember to
rename your desired configuration to `log4j2.xml` when you put in your working directory for your program.

## Advanced Info
Below this is advanced info that the typical user doesn't need to look into.

---
### Environment Variable
```shell script
export LOG4J_CONFIGURATION_FILE=<path to config>
```
### JVM Argument
```
java -Dlog4j2.configurationFile=<path to config> ...
```

More info: https://stackoverflow.com/questions/778933/log4j-configuration-via-jvm-arguments
