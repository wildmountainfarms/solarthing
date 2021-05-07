package me.retrodaredevil.solarthing.graphql;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import me.retrodaredevil.couchdb.CouchPropertiesBuilder;
import me.retrodaredevil.okhttp3.OkHttpPropertiesBuilder;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.graphql.solcast.SolcastConfig;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import java.util.Collections;


class GraphQLProviderTest {

	@Test
	void testGeneration() {
		CouchDbDatabaseSettings couchDbDatabaseSettings = new CouchDbDatabaseSettings(
				new CouchPropertiesBuilder("http", "localhost", 5984, null, null).build(),
				new OkHttpPropertiesBuilder().build()
		);
		GraphQLSchema schema = GraphQLProvider.createGraphQLSchemaGenerator(JacksonUtil.defaultMapper(), couchDbDatabaseSettings, DefaultInstanceOptions.DEFAULT_DEFAULT_INSTANCE_OPTIONS, new SolcastConfig(Collections.emptyMap())).generate();
		GraphQL.newGraphQL(schema).build();
	}
}
