## Useful ssh stuff:

Copying your SSH key to your remote machine:
```shell
ssh-copy-id pi@192.168.X.X
```

Once you do this, copying jar files from machine to machine becomes very simple:
```shell
./copy_jar.sh 192.168.1.200
```
