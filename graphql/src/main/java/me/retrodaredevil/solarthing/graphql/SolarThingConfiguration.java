package me.retrodaredevil.solarthing.graphql;

import com.nfl.glitr.Glitr;
import com.nfl.glitr.GlitrBuilder;
import graphql.GraphQL;
import graphql.schema.GraphQLType;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolarThingConfiguration {
	@Bean
	public Glitr glitr() {
		return GlitrBuilder.newGlitr()
				.withQueryRoot(new Root())
				.withMutationRoot(new Mutation())
				.build();
	}
	@Bean
	public GraphQL graphQL(Glitr glitr) {
		return GraphQL.newGraphQL(glitr.getSchema()).build();
	}
}
