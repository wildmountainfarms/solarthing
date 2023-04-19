# Server 
This module contains a Spring Boot REST API that uses GraphQL to fetch SolarThing data from CouchDB.

For usage with Grafana, look in [this folder](../other/grafana). **This readme file is for technical details,
not to walk you through using this**.

### How this works
This uses [GraphQL-SPQR](https://github.com/leangen/GraphQL-SPQR) to convert the SolarThing API to
a GraphQL Schema.

### What this is used for
Exposes the SolarThing API through GraphQL by fetching data from CouchDB. This allows for
querying from Grafana using the [graphql-datasource](https://github.com/fifemon/graphql-datasource).

#### Useful Links
This is useful for querying: https://github.com/prisma-labs/graphql-playground

For grafana: https://github.com/fifemon/graphql-datasource

For testing your skills/syntax: https://graphql.org/swapi-graphql or https://nhzvc.csb.app/

### Compiling
Use `./gradlew server:bootJar` or [./graphql_compile_and_move.sh](../graphql_compile_and_move.sh) in the root directory.

Or `./gradlew server:bootRun` to run this

#### Building docker image locally
Pushing to GitHub may automatically build a docker image depending on what branch you pushed to,
but sometimes iterating is faster by creating an image locally.

```shell
./gradlew server:bootJar
docker build -f docker/solarthing-server/Dockerfile --build-arg JAR_LOCATION=server/build/libs/server-0.0.1-SNAPSHOT.jar --tag solarthing-server .
```
