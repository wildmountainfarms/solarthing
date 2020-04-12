package me.retrodaredevil.solarthing.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.leangen.graphql.generator.mapping.common.NonNullMapper;
import io.leangen.graphql.metadata.strategy.query.AnnotatedResolverBuilder;
import io.leangen.graphql.metadata.strategy.query.BeanResolverBuilder;
import io.leangen.graphql.metadata.strategy.query.ResolverBuilder;
import io.leangen.graphql.metadata.strategy.value.jackson.JacksonValueMapperFactory;
import io.leangen.graphql.module.common.jackson.JacksonModule;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Member;
import java.util.Arrays;

@Component
public class GraphQLProvider {
	/*
	NOTE: jetbrains NotNull annotation is not retained at runtime...
	 */

	private GraphQL graphQL;

	@PostConstruct
	public void init() {
		ObjectMapper objectMapper = JacksonUtil.defaultMapper();
		JacksonValueMapperFactory jacksonValueMapperFactory = JacksonValueMapperFactory.builder()
				.withPrototype(objectMapper)
				.build();
		ResolverBuilder resolverBuilder = new AnnotatedResolverBuilder();
		GraphQLSchema schema = new GraphQLSchemaGenerator()
				.withBasePackages("me.retrodaredevil.solarthing")
				.withOperationsFromSingleton(new SolarThingGraphQLService(objectMapper))
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
