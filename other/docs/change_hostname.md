## Change Hostname

Changing your hostname is very easy on a Raspberry Pi.

First run this:
```shell
hostnamectl set-hostname YOUR_NAME
```
Then edit `/etc/hosts` and add `127.0.0.1 YOUR_NAME` to it,
or just run the script in `/other/linux/set_hostname.sh`
