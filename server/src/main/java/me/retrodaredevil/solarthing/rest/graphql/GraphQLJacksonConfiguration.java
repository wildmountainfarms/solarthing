package me.retrodaredevil.solarthing.rest.graphql;

import io.leangen.graphql.GraphQLSchemaGenerator;
import io.leangen.graphql.util.ClassUtils;
import java.util.Collections;
import me.retrodaredevil.solarthing.rest.graphql.jackson.JacksonValueMapper;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.TreeNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/** Explicit Jackson 3 wiring shared by the HTTP transport and SPQR schemas. */
@Configuration(proxyBeanMethods = false)
@NullMarked
public class GraphQLJacksonConfiguration {
	@Bean
	public JsonMapper jsonMapper() {
		return JacksonUtil.defaultMapper();
	}

	public static GraphQLSchemaGenerator schemaGenerator(ObjectMapper mapper) {
		var resolverBuilder = new SolarThingAnnotatedResolverBuilder(mapper);
		return new GraphQLSchemaGenerator()
				// Do not install JSON integrations based on unrelated libraries on the classpath.
				.withModules((config, defaults) -> Collections.emptyList())
				.withValueMapperFactory((subtypes, environment) -> new JacksonValueMapper(mapper))
				.withScalarDeserializationStrategy(type -> TreeNode.class.isAssignableFrom(ClassUtils.getRawType(type.getType())))
				.withResolverBuilders(resolverBuilder)
				.withNestedResolverBuilders(resolverBuilder, new JacksonResolverBuilder(mapper), new SolarThingResolverBuilder(mapper));
	}
}
