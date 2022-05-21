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
Use `./gradlew graphql:bootJar` or [./graphql_compile_and_move.sh](../graphql_compile_and_move.sh) in the root directory.

Or `./gradlew graphql:bootRun` to run this
