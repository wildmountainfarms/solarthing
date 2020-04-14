# GraphQL
This module contains a Spring Boot REST API that uses GraphQL to fetch SolarThing data from CouchDB.

### How this works
This uses [GraphQL-SPQR](https://github.com/leangen/GraphQL-SPQR) to convert the SolarThing API to
a GraphQL Schema.

### What this is used for
Exposes the SolarThing API through GraphQL by fetching data from CouchDB.

#### Useful Links
This is useful for querying: https://github.com/prisma-labs/graphql-playground

Good example here: https://nhzvc.csb.app/

* For grafana: https://github.com/fifemon/graphql-datasource
* Online type converter: https://walmartlabs.github.io/json-to-simple-graphql-schema/
* https://github.com/graphql-java/graphql-java-spring and [tutorial](https://www.graphql-java.com/tutorials/getting-started-with-spring-boot/)
* More useful stuff: https://github.com/graphql-java/awesome-graphql-java and https://github.com/graphql-java-generator/awesome-graphql-java

For testing your skills/syntax: https://graphql.org/swapi-graphql or https://nhzvc.csb.app/

### Compiling
Use `./gradlew graphql:bootJar`

Or `./gradlew graphql:bootRun` to run this

