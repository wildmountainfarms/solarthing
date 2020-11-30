package me.retrodaredevil.solarthing.graphql;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import me.retrodaredevil.couchdb.CouchPropertiesBuilder;
import me.retrodaredevil.solarthing.graphql.solcast.SolcastConfig;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import java.util.Collections;


class GraphQLProviderTest {

	@Test
	void testGeneration() {
		GraphQLSchema schema = GraphQLProvider.createGraphQLSchemaGenerator(JacksonUtil.defaultMapper(), new CouchPropertiesBuilder("http", "localhost", 5984, null, null).build(), DefaultInstanceOptions.DEFAULT_DEFAULT_INSTANCE_OPTIONS, new SolcastConfig(Collections.emptyMap())).generate();
		GraphQL.newGraphQL(schema).build();
	}
}
