# Mattermost Setup


### Installing
If you use a docker-compose, that is a great way to set up Mattermost
https://docs.mattermost.com/install/prod-docker.html?src=dl

### Bot info
* https://docs.mattermost.com/developer/bot-accounts.html
  * https://docs.mattermost.com/developer/personal-access-tokens.html

### Enabling full push notifications
https://docs.mattermost.com/administration/config-settings.html#push-notification-contents
* Set "PushNotificationContents" to "full" in config.json
  * I believe there's also a place in the settings to change this
* Note that this uses an outside server, so you need real Internet for this to work

### Other random links
* API https://api.mattermost.com/#tag/posts/paths/%7E1posts/post
