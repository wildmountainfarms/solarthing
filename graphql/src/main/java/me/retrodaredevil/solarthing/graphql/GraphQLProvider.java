package me.retrodaredevil.solarthing.graphql;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class GraphQLProvider {

	private GraphQL graphQL;

	@PostConstruct
	public void init() throws IOException {
		GraphQLSchema schema = new GraphQLSchemaGenerator()
				.withBasePackages("me.retrodaredevil.solarthing")
				.withOperationsFromSingleton(new SolarThingGraphQLService()) // TODO
				.generate();

		this.graphQL = GraphQL.newGraphQL(schema).build();
	}


	@Bean
	public GraphQL graphQL() {
		return graphQL;
	}

}
