# Log4j
SolarThing uses Log4j2 for logging. (But not the GraphQL program yet, everything else yes)

You can override the default configuration in a few ways:

### `log4j2.xml` in working directory
When executing `run.sh`, it is configured to detect a file called `log4j2.xml` in the working
directory. If it finds it, it will use that for the configuration.
### Environment Variable
```shell script
export LOG4j_log4j2_configurationFile=<path to config>
```
### JVM Argument
```
java -Dlog4j2.configurationFile=<path to config> ...
```

More info: https://stackoverflow.com/questions/778933/log4j-configuration-via-jvm-arguments
