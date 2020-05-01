# Turing SolarThing

```json
{
  "type": "declaration",
  "main": {
    "type": "queue",
    "actions": [
      {
        "type": "race",
        "racers": [
          [{ "type": "lock", "name": "send_commands"}, { "type": "call", "name": "run_commands"}],
          [{ "type": "waitms", "wait": 5000}, { "type": "seterror", "error": "Something else had a lock on run_commands"}]
        ]
      },
      { "type": "unlock", "name": "send_commands" }
    ]
  },
  "run_commands": {
    "type": "queue",
    "actions": [
      { "type": "matecommand", "command": "DROP" },
      { "type": "matecommand", "command": "AUX ON" },
      {
        "type": "race",
        "racers": [
          [{ "type": "fxcondition", "condition": "NO AC" }, { "type": "pass" }],
          [{ "type": "waitms", "wait": 7000 }, { "type": "seterror", "error": "AC is still present!"}]
        ]
      },
      { "type": "matecommand", "command": "USE"},
      { "type": "matecommand", "command": "AUX OFF"},
      { "type": "log", "message": "Finished sequence!"}
    ]
  }
}
```

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

