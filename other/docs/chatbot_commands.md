NOTE: This documentation is outdated. It currently has no corresponding page on https://solarthing.readthedocs.io

### Chatbot Commands
WIP for the possibility of how a chatbot could work

Commands:
* command generator off
  * Sends "generator off" command
* command generator on  
  * Sends "generator on" command
* battery voltage
  * Responds with battery voltage
* 


### Slack Setup:
```json
{
    "_metadata": {
        "major_version": 1,
        "minor_version": 1
    },
    "display_information": {
        "name": "SolarThing"
    },
    "features": {
        "bot_user": {
            "display_name": "SolarThing",
            "always_online": true
        }
    },
    "oauth_config": {
        "scopes": {
            "bot": [
                "chat:write",
                "chat:write.public",
                "channels:history"
            ]
        }
    },
    "settings": {
        "event_subscriptions": {
            "bot_events": [
                "message.channels"
            ]
        },
        "interactivity": {
            "is_enabled": true
        },
        "org_deploy_enabled": false,
        "socket_mode_enabled": true,
        "token_rotation_enabled": false
    }
}
```
