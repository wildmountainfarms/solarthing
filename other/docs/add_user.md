## Adding a new user
It's very easy to add another user besides `pi` to your system.

```shell
# Create a user with a home directory, with some groups, with the primary group as solarthing
# Note that the -g solarthing part is optional and makes dealing with file permission easier
sudo useradd --create-home --groups dialout,tty,video,sudo -g solarthing NAME 
```
