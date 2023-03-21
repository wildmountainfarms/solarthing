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

### Future `Dockerfile`

https://reflectoring.io/spring-boot-docker/

When we create a `Dockerfile` from the boot jar, we will want to extract its layers.

```shell
java -Djarmode=layertools -jar ../server/build/libs/server-0.0.1-SNAPSHOT.jar extract
# then copy the 4 folders to /app or something
```
