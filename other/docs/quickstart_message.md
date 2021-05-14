# Quick start with Message Sender action (part of automation program)
If you haven't already, [click here](quickstart.md) to view how to clone this repo and install the service.

Also make sure to follow the instructions on how to set up the automation program [here](./quickstart_automation.md).

You will now define the message-sender action in a separate file. The recommended placement is in [program/automation/config/actions](../../program/automation/config/actions).

You can find an example action [here](../../config_templates/actions/message_sender.json)

This action file references different message senders. Currently `slack` and `log` are supported.
These are defined in separate files and referenced in the `senders` node as you can see in the example action file.

## Slack
An example slack configuration can be found [here](../../config_templates/message/slack_template.json).
