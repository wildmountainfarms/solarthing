# Turing SolarThing
Turing SolarThing is basically a programming language inside SolarThing. Turing SolarThing is expressed in JSON, so it
can't be a programming language right? Well, although it's limited and expressed in JSON, it's turing complete, hence the name.

Really it's just a way to configure chaining commands, but the nature of it allows for lots of customization
through simple constructs. You can use 'race' actions as if statements, and you can use 'call' actions to easily
repeat actions and separate code into more readable parts.

Locks can be used to stop other running actions from doing something at the same time.
Locks are global, so if they are locked in one action, they are locked in all actions. Declarations are local,
so you can have the same name in different actions.

Examples:
* [config_templates/commands](../config_templates/commands)
* Simple examples below

---

Simple Declaration:
```json
{
  "type": "declaration",
  "main": {
    "type": "queue",
    "actions": [
      { "type": "lock", "name": "send_commands" },
      { "type": "call", "name": "run_commands" },
      { "type": "unlock", "name": "send_commands" }
    ]
  },
  "run_commands": {
    "type": "queue",
    "actions": [
      { "type": "log", "message": "Starting wait" },
      { "type": "waitms", "wait": "1000" },
      { "type": "log", "message": "Finished sequence!" }
    ]
  }
}
```

---
Race Example:

```json
{
  "type": "race",
  "racers": [
    [{ "type": "waitms", "wait": 500}, { "type": "log", "message": "500ms finished first!"}],
    [{ "type": "waitms", "wait": 200}, { "type": "log", "message": "200ms finished first!"}],
    [{ "type": "waitms", "wait": 1000}, { "type": "log", "message": "1000ms finished first!"}]
  ]
}
```

