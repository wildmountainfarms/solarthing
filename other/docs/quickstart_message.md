# Quick start with Message Sender program
If you haven't already, [click here](quickstart.md) to view how to clone this repo and install the service.

Once everything is installed, you're ready to edit the configs. You will cd to the `program/message` directory.
```
cd /opt/solarthing/program/message
```

You should already have CouchDB set up. You can re-use its configuration file. It is recommended that CouchDB's
configuration file is put in [program/config](../../program/config), so other programs can use it.

You can find an example `base.json` [here](../../config_templates/base/message_sender_template.json).

The `base.json` of the message sender program references different message senders. Currently `mattermost`, `slack` 
and `log` are supported

## Slack
An example slack configuration can be found [here](../../config_templates/message/slack_template.json).

## Mattermost
An example mattermost configuration can be found [here](../../config_templates/message/mattermost_template.json).
