package me.retrodaredevil.solarthing.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.leangen.graphql.metadata.strategy.query.AnnotatedResolverBuilder;
import io.leangen.graphql.metadata.strategy.query.ResolverBuilder;
import io.leangen.graphql.metadata.strategy.value.jackson.JacksonValueMapperFactory;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;
import me.retrodaredevil.solarthing.program.DatabaseConfig;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Component
public class GraphQLProvider {

	@Value("${solarthing.config.database}")
	private File databaseFile;
	@Value("${solarthing.config.default_source}")
	private String defaultSourceId = InstanceSourcePacket.DEFAULT_SOURCE_ID;
	@Value("${solarthing.config.default_fragment}")
	private Integer defaultFragmentId = null;

	private GraphQL graphQL;

	@PostConstruct
	public void init() {
		ObjectMapper objectMapper = JacksonUtil.defaultMapper();
		objectMapper.getSubtypeResolver().registerSubtypes(
				DatabaseSettings.class,
				CouchDbDatabaseSettings.class
		);
		System.out.println("file: " + databaseFile + " absolute: " + databaseFile.getAbsolutePath());
		final FileInputStream reader;
		try {
			reader = new FileInputStream(databaseFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		final DatabaseConfig databaseConfig;
		try {
			databaseConfig = objectMapper.readValue(reader, DatabaseConfig.class);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't parse data!", e);
		}
		DatabaseSettings databaseSettings = databaseConfig.getSettings();
		if(!(databaseSettings instanceof CouchDbDatabaseSettings)) {
			throw new UnsupportedOperationException("Only CouchDB is supported right now!");
		}
		CouchDbDatabaseSettings couchDbDatabaseSettings = (CouchDbDatabaseSettings) databaseSettings;

		JacksonValueMapperFactory jacksonValueMapperFactory = JacksonValueMapperFactory.builder()
				.withPrototype(objectMapper)
				.build();
		ResolverBuilder resolverBuilder = new AnnotatedResolverBuilder();
		DefaultInstanceOptions defaultInstanceOptions = new DefaultInstanceOptions(defaultSourceId, defaultFragmentId);
		System.out.println("Using defaultInstanceOptions=" + defaultInstanceOptions);
		GraphQLSchema schema = new GraphQLSchemaGenerator()
				.withBasePackages("me.retrodaredevil.solarthing")
				.withOperationsFromSingleton(new SolarThingGraphQLService(
						defaultInstanceOptions,
						objectMapper,
						couchDbDatabaseSettings.getCouchProperties()
				))
				.withOperationsFromSingleton(new SolarThingGraphQLExtensions())
				.withValueMapperFactory(jacksonValueMapperFactory)
				.withResolverBuilders(resolverBuilder)
				.withNestedResolverBuilders(
						resolverBuilder,
						new JacksonResolverBuilder().withObjectMapper(objectMapper),
						new SolarThingResolverBuilder()
				)
				.generate();

		this.graphQL = GraphQL.newGraphQL(schema).build();
	}


	@Bean
	public GraphQL graphQL() {
		return graphQL;
	}

}
