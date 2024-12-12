# docker
This directory is used to contain `Dockerfile`s to build `solarthing` and `solarthing-server` docker images.

NOTE: If you are looking for documentation, go here: https://solarthing.readthedocs.io/en/latest/quickstart/install/docker.html



## Technical details about this directory

Note that this directory is designed to be separate from the state of a codebase at any given commit.
Even while building docker images for older SolarThing versions,
the latest files in this folder should be used to build those images.

So, the only Dockerfiles that should be used to build solarthing should be on the master branch of this repository.

```shell
# run in solarthing root
docker build -f docker/solarthing-server/Dockerfile --build-arg JAR_LOCATION=server/build/libs/server-0.0.1-SNAPSHOT.jar --tag solarthing-server:latest .

docker build -f docker/solarthing/Dockerfile --build-arg JAR_LOCATION=client/build/libs/client-0.0.1-SNAPSHOT-all.jar --tag solarthing:latest .
```
