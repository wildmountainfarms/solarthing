package me.retrodaredevil.solarthing.rest.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;
import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.couchdb.CouchPropertiesBuilder;
import me.retrodaredevil.okhttp3.OkHttpPropertiesBuilder;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.rest.cache.CacheController;
import me.retrodaredevil.solarthing.rest.cache.CacheHandler;
import me.retrodaredevil.solarthing.rest.graphql.solcast.SolcastConfig;
import me.retrodaredevil.solarthing.util.JacksonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class OutputSchemaMain {
	private OutputSchemaMain() { throw new UnsupportedOperationException(); }
	public static void main(String[] args) {
		Path outputFile = Path.of(args[0]);

		CouchDbDatabaseSettings couchDbDatabaseSettings = new CouchDbDatabaseSettings(
				new CouchPropertiesBuilder("http", "localhost", 5984, null, null).build(),
				new OkHttpPropertiesBuilder().build()
		);
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		// Use DefaultInstanceOptions.REQUIRE_NO_DEFAULTS while testing because first, we don't actually query any data,
		//   and second, we're trying to get rid of usages of DefaultInstanceOptions.DEFAULT_DEFAULT_INSTANCE_OPTIONS
		CacheController cacheController = new CacheController(new CacheHandler(mapper, DefaultInstanceOptions.REQUIRE_NO_DEFAULTS, CouchDbUtil.createInstance(couchDbDatabaseSettings.getCouchProperties(), couchDbDatabaseSettings.getOkHttpProperties())));
		GraphQLSchema schema = GraphQLProvider.createGraphQLSchemaGenerator(JacksonUtil.defaultMapper(), couchDbDatabaseSettings, DefaultInstanceOptions.REQUIRE_NO_DEFAULTS, new SolcastConfig(Collections.emptyMap()), cacheController).generate();
		String schemaString = new SchemaPrinter(
				SchemaPrinter.Options.defaultOptions()
						.includeIntrospectionTypes(true)
						.includeScalarTypes(true)
						.includeSchemaDefinition(true)
						.includeDirectiveDefinitions(true)
						.useAstDefinitions(true)
		).print(schema);

		try {
			Files.writeString(outputFile, schemaString);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
