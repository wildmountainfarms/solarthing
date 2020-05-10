package me.retrodaredevil.solarthing.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.leangen.graphql.generator.mapping.common.NonNullMapper;
import io.leangen.graphql.metadata.strategy.query.AnnotatedResolverBuilder;
import io.leangen.graphql.metadata.strategy.query.ResolverBuilder;
import io.leangen.graphql.metadata.strategy.value.jackson.JacksonValueMapperFactory;
import me.retrodaredevil.solarthing.annotations.NotNull;
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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@Component
public class GraphQLProvider {

	@Value("${solarthing.config.database}")
	private File databaseFile;
	@Value("${solarthing.config.default_source:default}")
	private String defaultSourceId;
	@Value("${solarthing.config.default_fragment:#{null}}")
	private Integer defaultFragmentId;

	private GraphQL graphQL;

	private void updateNonNull() throws NoSuchFieldException, IllegalAccessException {
		// more info here: https://github.com/leangen/graphql-spqr/issues/334
		Field field = NonNullMapper.class.getDeclaredField("COMMON_NON_NULL_ANNOTATIONS");
		field.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		String[] nonNullAnnotations = (String[]) field.get(null);
		String[] newAnnotations = Arrays.copyOf(nonNullAnnotations, nonNullAnnotations.length + 1);
		newAnnotations[newAnnotations.length - 1] = NotNull.class.getName();
		field.set(null, newAnnotations);
	}

	@PostConstruct
	public void init() {
		try {
			updateNonNull();
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
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
