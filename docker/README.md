# docker
This directory is used to contain `Dockerfile`s to build `solarthing` and `solarthing-server` docker images.
Note that this directory is designed to be separate from the state of a codebase at any given commit.
Even while building docker images for older SolarThing versions,
the latest files in this folder should be used to build those images.

So, the only Dockerfiles that should be used to build solarthing should be on the master branch of this repository.

```shell
# run in solarthing root
docker build -f docker/solarthing-server/Dockerfile --build-arg BOOT_JAR=server/build/libs/server-0.0.1-SNAPSHOT.jar --tag wildmountainfarms/solarthing-server:latest .
```
