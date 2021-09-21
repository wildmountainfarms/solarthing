## Installing Java 11 on RPi1 or RPi0
If you have an RPi1 or RPi0, they both have ARMv6 processors, so you need to install Java 11 differently.

NOTE: You don't have to use Java 11. Using Java 8 with SolarThing is perfectly fine. **Go [here](./installing_java.md)
for normal (simple) installation.**

### Installing with sdkman
Note: I currently cannot get this method to work on my Raspberry Pi Zero.

First, install sdkman:
```shell
# https://sdkman.io/install
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
```
Now, go [here](https://www.azul.com/downloads/zulu-community/?version=java-11-lts&os=linux&architecture=arm-32-bit-hf&package=jdk)
and check what the latest Java 11 version is (It should be something like 11.0.10+9). Now, for 11.0.10+9, you'd run this command:
```shell
# https://sdkman.io/jdks
sdk list java # you can install one of these packages if you'd like
# This will take a while to "repackage"
sdk install java 11.0.10-zulu
```
Now java should be installed! The reason we use the zulu version is that they support ARMv6.

--- 

--- 

--- 

### Installing Manually
https://pi4j.com/documentation/java-installation/
```shell
mkdir -p /home/pi/Downloads
cd /home/pi/Downloads
# Note: Go here to get a newer verison to download: https://www.azul.com/downloads/zulu-community/?version=java-11-lts&os=linux&architecture=arm-32-bit-hf&package=jdk
wget https://cdn.azul.com/zulu-embedded/bin/zulu11.45.27-ca-jdk11.0.10-linux_aarch32hf.tar.gz

cd /usr/lib/jvm
sudo tar -xf ~/Downloads/zulu11.45.27-ca-jdk11.0.10-linux_aarch32hf.tar.gz
sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/zulu11.45.27-ca-jdk11.0.10-linux_aarch32hf/bin/java 100
sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/zulu11.45.27-ca-jdk11.0.10-linux_aarch32hf/bin/javac 100

sudo update-alternatives --config java
sudo update-alternatives --config javac
```
Note that using since we didn't use `update-java-alternatives` we only have `java` and `javac` changed, which is enough
for most people's use cases.

