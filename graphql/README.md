# GraphQL
This module contains a Spring Boot REST API that uses GraphQL to fetch SolarThing data from CouchDB.

This is work in progress

This is useful for querying: https://github.com/prisma-labs/graphql-playground

Good example here: https://nhzvc.csb.app/

* For grafana: https://github.com/fifemon/graphql-datasource
* NPM converter: https://github.com/lifeomic/json-schema-to-graphql-types
* Online type converter: https://walmartlabs.github.io/json-to-simple-graphql-schema/
* https://github.com/graphql-java/graphql-java-spring and [tutorial](https://www.graphql-java.com/tutorials/getting-started-with-spring-boot/)

For testing your skills/syntax: https://graphql.org/swapi-graphql or https://nhzvc.csb.app/

## Goal
The goal of this module would be to be able to export a Java type system heavily annotated with Jackson annotations
to a GraphQL schema. This would have to be done automatically without hassle, otherwise this would become
too difficult to maintain as the SolarThing API is updated.
