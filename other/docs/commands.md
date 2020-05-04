# Commands
Currently the `mate` program supports commands if you are using CouchDB. [SolarThing Android](https://github.com/wildmountainfarms/solarthing-android)
can send commands. There may be other ways to send commands in the future.

Commands are disabled by default. You can add `"allow_commands": true` to your base.json to allow commands. Then, you can
add commands by adding the `"commands"` attribute. There is an example in [mate_template_commands.json](../../config_templates/base/mate_template_commands.json).

Before editing your `base.json`, you may want to decide what commands you want to have. It is recommended to put
commands in `program/config/commands`. You can see example commands in [config_templates/commands](../../config_templates/commands).

Commands can be simple, or very complex. If you need help making a command do a certain series of commands, feel free to
ask for help on [our issues page](https://github.com/wildmountainfarms/solarthing/issues).

