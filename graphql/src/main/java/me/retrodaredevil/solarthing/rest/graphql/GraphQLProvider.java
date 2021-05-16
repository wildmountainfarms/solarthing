package me.retrodaredevil.solarthing.rest.graphql;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.leangen.graphql.generator.mapping.common.NonNullMapper;
import io.leangen.graphql.metadata.strategy.query.AnnotatedResolverBuilder;
import io.leangen.graphql.metadata.strategy.query.ResolverBuilder;
import io.leangen.graphql.metadata.strategy.value.jackson.JacksonValueMapperFactory;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.rest.graphql.service.*;
import me.retrodaredevil.solarthing.rest.graphql.solcast.SolcastConfig;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;

@Component
public class GraphQLProvider {

	private final CouchDbDatabaseSettings couchDbDatabaseSettings;
	private final DefaultInstanceOptions defaultInstanceOptions;

	@Value("${solarthing.config.solcast_file:config/solcast.json}")
	private File solcastFile;

	private GraphQL graphQL;

	public GraphQLProvider(CouchDbDatabaseSettings couchDbDatabaseSettings, DefaultInstanceOptions defaultInstanceOptions) {
		this.couchDbDatabaseSettings = couchDbDatabaseSettings;
		this.defaultInstanceOptions = defaultInstanceOptions;
	}


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

		SolcastConfig solcastConfig = null;
		try {
			solcastConfig = objectMapper.readValue(solcastFile, SolcastConfig.class);
		} catch (JsonParseException | JsonMappingException e) {
			throw new RuntimeException("Bad solcast JSON!", e);
		} catch (IOException e) {
			System.out.println("No solcast config! Not using solcast!");
		}
		if (solcastConfig == null) {
			solcastConfig = new SolcastConfig(Collections.emptyMap());
		}

		GraphQLSchema schema = createGraphQLSchemaGenerator(objectMapper, couchDbDatabaseSettings, defaultInstanceOptions, solcastConfig).generate();

		this.graphQL = GraphQL.newGraphQL(schema).build();
	}

	static GraphQLSchemaGenerator createGraphQLSchemaGenerator(ObjectMapper objectMapper, CouchDbDatabaseSettings couchDbDatabaseSettings, DefaultInstanceOptions defaultInstanceOptions, @NotNull SolcastConfig solcastConfig) {
		JacksonValueMapperFactory jacksonValueMapperFactory = JacksonValueMapperFactory.builder()
				.withPrototype(objectMapper)
				.build();
		ResolverBuilder resolverBuilder = new AnnotatedResolverBuilder();
		SimpleQueryHandler simpleQueryHandler = new SimpleQueryHandler(defaultInstanceOptions, couchDbDatabaseSettings, objectMapper);
		ZoneId zoneId = ZoneId.systemDefault(); // In the future, we could make this customizable, but like, bro just make sure your system time is correct
		System.out.println("Using timezone: " + zoneId);
		return new GraphQLSchemaGenerator()
				.withBasePackages("me.retrodaredevil.solarthing")
				.withOperationsFromSingleton(new SolarThingGraphQLService(simpleQueryHandler))
				.withOperationsFromSingleton(new SolarThingGraphQLDailyService(simpleQueryHandler, zoneId))
				.withOperationsFromSingleton(new SolarThingGraphQLLongTermService(simpleQueryHandler, zoneId))
				.withOperationsFromSingleton(new SolarThingGraphQLMetaService(simpleQueryHandler))
				.withOperationsFromSingleton(new SolarThingGraphQLExtensions())
				.withOperationsFromSingleton(new SolarThingGraphQLFXService(simpleQueryHandler))
				.withOperationsFromSingleton(new SolarThingGraphQLSolcastService(solcastConfig, zoneId))
				.withTypeInfoGenerator(new SolarThingTypeInfoGenerator())
				.withValueMapperFactory(jacksonValueMapperFactory)
				.withResolverBuilders(resolverBuilder)
				.withNestedResolverBuilders(
						resolverBuilder,
						new JacksonResolverBuilder().withObjectMapper(objectMapper),
						new SolarThingResolverBuilder()
				);
	}


	@Bean
	public GraphQL graphQL() {
		return graphQL;
	}

}
