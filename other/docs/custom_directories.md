# Custom directories
Sometimes you should create custom directories so that you can more accurately name
the folder for the SolarThing instance you are running.

And sometimes, you may need an additional "request" directory.

The only reason you should be reading this is if you already have a SolarThing instance running, and you want
to create another instance of SolarThing to run.

It's good to note that technically you don't need to create another directory because the run.sh file is the same in
all the directories except for graphql, but I do recommend creating a custom directory if you need to.

```shell
cd /opt/solarthing/program
./create_custom.sh custom_coolname
```
But replace `coolname` with the name that you want. Note that you MUST prefix the name with `custom`. If you don't
there may be conflicts when updating SolarThing, which you don't want.

You can also install that program using `sudo /opt/solarthing/other/systemd/install.sh custom_coolname`.

That's it! You're done!
