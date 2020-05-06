# Updating
To update, run these commands:
```shell script
# NOTE: These will need sudo
git pull
program/download_solarthing.sh # if there's an update, this will download it
```
If you are running the systemd service, do this to restart:
```shell script
sudo systemctl restart solarthing-<program type>
```
