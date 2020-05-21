## Simulate Serial
Here are some commands to help simulate serial

https://stackoverflow.com/questions/52187/virtual-serial-port-for-linux

https://www.onetransistor.eu/2015/12/wine-serial-port-linux.html

```shell script
sudo socat PTY,link=/dev/ttyS10 PTY,link=/dev/ttyS11
```
Connect one end to `/dev/ttyS10` and the other to `/dev/ttyS11`.

Also use this:
```shell script
sudo ./diagslave -m rtu -a 1 -b 9600 -p none /dev/ttyS10
```

If you're doing stuff with wine:
```shell script
cd ~/.wine/dosdevices/
sudo ln -s /dev/ttyS11 com1
```
If you're running Solar Station Monitor using Wine:
```shell script
wine start 'C:\windows\system32\Solar Station Monitor.exe'
```
