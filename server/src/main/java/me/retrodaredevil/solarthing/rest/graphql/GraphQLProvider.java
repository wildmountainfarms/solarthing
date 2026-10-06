package me.retrodaredevil.solarthing.rest.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.leangen.graphql.generator.mapping.common.NonNullMapper;
import io.leangen.graphql.metadata.strategy.query.ResolverBuilder;
import io.leangen.graphql.metadata.strategy.value.jackson.JacksonValueMapperFactory;
import jakarta.annotation.PostConstruct;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.rest.cache.CacheController;
import me.retrodaredevil.solarthing.rest.graphql.service.SolarThingGraphQLAlterService;
import me.retrodaredevil.solarthing.rest.graphql.service.SolarThingGraphQLBatteryRecordService;
import me.retrodaredevil.solarthing.rest.graphql.service.SolarThingGraphQLDailyService;
import me.retrodaredevil.solarthing.rest.graphql.service.SolarThingGraphQLFXService;
import me.retrodaredevil.solarthing.rest.graphql.service.SolarThingGraphQLLongTermService;
import me.retrodaredevil.solarthing.rest.graphql.service.SolarThingGraphQLService;
import me.retrodaredevil.solarthing.rest.graphql.service.SolarThingGraphQLSolcastService;
import me.retrodaredevil.solarthing.rest.graphql.service.web.DefaultDatabaseProvider;
import me.retrodaredevil.solarthing.rest.graphql.service.web.SolarThingAdminService;
import me.retrodaredevil.solarthing.rest.graphql.solcast.SolcastConfig;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.Collections;

@Component
@NullMarked
public class GraphQLProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLProvider.class);

	private final CouchDbDatabaseSettings couchDbDatabaseSettings;
	private final DefaultInstanceOptions defaultInstanceOptions;
	private final CacheController cacheController;

	@Value("${solarthing.config.solcast_file:config/solcast.json}")
	private Path solcastFile;

	private GraphQL graphQL;

	public GraphQLProvider(CouchDbDatabaseSettings couchDbDatabaseSettings, DefaultInstanceOptions defaultInstanceOptions, CacheController cacheController) {
		this.couchDbDatabaseSettings = couchDbDatabaseSettings;
		this.defaultInstanceOptions = defaultInstanceOptions;
		this.cacheController = cacheController;
	}


	@PostConstruct
	public void init() {
		ObjectMapper objectMapper = JacksonUtil.defaultMapper();

		SolcastConfig solcastConfig = null;
		try (InputStream inputStream = Files.newInputStream(solcastFile)) {
			// TODO [Interpolate] Allow interpolated values
			solcastConfig = objectMapper.readValue(inputStream, SolcastConfig.class);
		} catch (IOException e) {
			LOGGER.debug("No solcast config! Not using solcast!");
		}
		if (solcastConfig == null) {
			solcastConfig = new SolcastConfig(Collections.emptyMap());
		}

		GraphQLSchema schema = createGraphQLSchemaGenerator(objectMapper, couchDbDatabaseSettings, defaultInstanceOptions, solcastConfig, cacheController).generate();

		this.graphQL = GraphQL.newGraphQL(schema)
				.defaultDataFetcherExceptionHandler(new SolarThingExceptionHandler())
				.build();
	}

	static GraphQLSchemaGenerator createGraphQLSchemaGenerator(ObjectMapper objectMapper, CouchDbDatabaseSettings couchDbDatabaseSettings, DefaultInstanceOptions defaultInstanceOptions, SolcastConfig solcastConfig, CacheController cacheController) {
		JacksonValueMapperFactory jacksonValueMapperFactory = JacksonValueMapperFactory.builder()
				.withPrototype(objectMapper)
				.build();
		ResolverBuilder resolverBuilder = new SolarThingAnnotatedResolverBuilder();
		SimpleQueryHandler simpleQueryHandler = new SimpleQueryHandler(defaultInstanceOptions, couchDbDatabaseSettings, objectMapper);
		ZoneId zoneId = ZoneId.systemDefault(); // In the future, we could make this customizable, but like, bro just make sure your system time is correct
		LOGGER.debug("Using timezone: " + zoneId);
		return new GraphQLSchemaGenerator()
				.withBasePackages("me.retrodaredevil.solarthing")
				.withOperationsFromSingleton(new SolarThingGraphQLService(simpleQueryHandler))
				.withOperationsFromSingleton(new SolarThingGraphQLDailyService(simpleQueryHandler, zoneId, cacheController))
				.withOperationsFromSingleton(new SolarThingGraphQLBatteryRecordService(simpleQueryHandler, cacheController))
				.withOperationsFromSingleton(new SolarThingGraphQLLongTermService(cacheController, zoneId))
				.withOperationsFromSingleton(new SolarThingGraphQLMetaService(simpleQueryHandler))
				.withOperationsFromSingleton(new SolarThingGraphQLExtensions())
				.withOperationsFromSingleton(new SolarThingGraphQLFXService(simpleQueryHandler))
				.withOperationsFromSingleton(new SolarThingGraphQLSolcastService(solcastConfig, zoneId, cacheController))
				.withOperationsFromSingleton(new SolarThingGraphQLAlterService(simpleQueryHandler))
				.withOperationsFromSingleton(new SolarThingAdminService(new DefaultDatabaseProvider(couchDbDatabaseSettings, objectMapper)))
				.withTypeMappers((config, defaults) -> defaults.replace(NonNullMapper.class, ignored -> new DefaultNonNullMapper()))
				.withSchemaTransformers((config, defaults) -> defaults.replace(NonNullMapper.class, ignored -> new DefaultNonNullMapper()))
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
	public GraphQlSource graphQlSource() {
		return new SimpleGraphQlSource(graphQL, graphQL.getGraphQLSchema());
	}
	private record SimpleGraphQlSource(GraphQL graphQl, GraphQLSchema schema) implements GraphQlSource{
	}
}
