# Quick start with Automation program
If you haven't already, [click here](quickstart.md) to view how to clone this repo and install the service.

Once everything is installed, you're ready to edit the configs. You will cd to the `program/automation` directory.
```
cd /opt/solarthing/program/automation
```

You should already have CouchDB set up. You can re-use its configuration file. It is recommended that CouchDB's
configuration file is put in [program/config](../../program/config), so other programs can use it.

You can find an example `base.json` [here](../../config_templates/base/automation_generic_template.json).

For the automation program, you will get to define your own action. Currently, there is little documentation on how
to do this, so if you were directed to this page from another, you can go back to configure your specific action.
