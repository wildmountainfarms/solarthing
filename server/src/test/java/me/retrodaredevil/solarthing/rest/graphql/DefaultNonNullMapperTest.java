package me.retrodaredevil.solarthing.rest.graphql;

import graphql.GraphQL;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLTypeUtil;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLInputField;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.generator.mapping.common.NonNullMapper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
class DefaultNonNullMapperTest {

	private static GraphQLSchema generateSchema() {
		return new GraphQLSchemaGenerator()
				.withOperationsFromSingleton(new Queries())
				.withTypeMappers((config, defaults) -> defaults.replace(NonNullMapper.class, ignored -> new DefaultNonNullMapper()))
				.withSchemaTransformers((config, defaults) -> defaults.replace(NonNullMapper.class, ignored -> new DefaultNonNullMapper()))
				.generate();
	}

	@Test
	void mapsNullabilityAtEachTypePosition() {
		GraphQLSchema schema = generateSchema();
		assertEquals("String!", GraphQLTypeUtil.simplePrint(schema.getQueryType().getFieldDefinition("required").getType()));
		assertEquals("String", GraphQLTypeUtil.simplePrint(schema.getQueryType().getFieldDefinition("nullable").getType()));
		assertEquals("[String!]!", GraphQLTypeUtil.simplePrint(schema.getQueryType().getFieldDefinition("list").getType()));
		assertEquals("[String]!", GraphQLTypeUtil.simplePrint(schema.getQueryType().getFieldDefinition("nullableElements").getType()));
		assertEquals("[String!]", GraphQLTypeUtil.simplePrint(schema.getQueryType().getFieldDefinition("nullableList").getType()));
		assertEquals("[[String]!]!", GraphQLTypeUtil.simplePrint(schema.getQueryType().getFieldDefinition("nestedList").getType()));

		var field = schema.getQueryType().getFieldDefinition("arguments");
		assertEquals("String!", GraphQLTypeUtil.simplePrint(field.getArgument("required").getType()));
		assertEquals("String", GraphQLTypeUtil.simplePrint(field.getArgument("nullable").getType()));
		assertEquals("String!", GraphQLTypeUtil.simplePrint(field.getArgument("defaulted").getType()));
		assertEquals("String", GraphQLTypeUtil.simplePrint(field.getArgument("nullableDefaulted").getType()));
		var input = (GraphQLInputObjectType) GraphQLTypeUtil.unwrapAll(field.getArgument("input").getType());
		assertEquals("String!", GraphQLTypeUtil.simplePrint(input.getFieldDefinition("required").getType()));
		assertEquals("String", GraphQLTypeUtil.simplePrint(input.getFieldDefinition("nullable").getType()));
		assertEquals("[String!]!", GraphQLTypeUtil.simplePrint(input.getFieldDefinition("list").getType()));
		assertEquals("[String]!", GraphQLTypeUtil.simplePrint(input.getFieldDefinition("nullableElements").getType()));
		assertEquals("String!", GraphQLTypeUtil.simplePrint(input.getFieldDefinition("defaulted").getType()));
	}

	@Test
	void defaultsAllowOmissionButRequireNonNullWhenProvided() {
		GraphQL graphQL = GraphQL.newGraphQL(generateSchema()).build();
		var omitted = graphQL.execute("{ arguments(required: \"required\") }");
		assertTrue(omitted.getErrors().isEmpty(), omitted.getErrors().toString());
		assertEquals(Map.of("arguments", "default"), omitted.getData());
		assertFalse(graphQL.execute("{ arguments(required: \"required\", defaulted: null) }").getErrors().isEmpty());
		assertTrue(graphQL.execute("{ arguments(required: \"required\", nullable: null, nullableDefaulted: null) }").getErrors().isEmpty());
		assertTrue(graphQL.execute("{ arguments(required: \"required\", input: {required: \"value\", list: [], nullableElements: [null]}) }").getErrors().isEmpty());
		assertFalse(graphQL.execute("{ arguments(required: \"required\", input: {required: \"value\", list: [], nullableElements: [], defaulted: null}) }").getErrors().isEmpty());
	}

	public static class Queries {
		@GraphQLQuery
		public String required() { return "value"; }
		@GraphQLQuery
		public @Nullable String nullable() { return null; }
		@GraphQLQuery
		public List<String> list() { return List.of("value"); }
		@GraphQLQuery
		public List<@Nullable String> nullableElements() { return List.of("value"); }
		@GraphQLQuery
		public @Nullable List<String> nullableList() { return null; }
		@GraphQLQuery
		public List<List<@Nullable String>> nestedList() { return List.of(List.of("value")); }
		@GraphQLQuery
		public String arguments(
				@GraphQLArgument(name = "required") String required,
				@GraphQLArgument(name = "nullable") @Nullable String nullable,
				@GraphQLArgument(name = "defaulted", defaultValue = "default") String defaulted,
				@GraphQLArgument(name = "nullableDefaulted", defaultValue = "default") @Nullable String nullableDefaulted,
				@GraphQLArgument(name = "input") @Nullable Input input
		) {
			return defaulted;
		}
	}

	public static class Input {
		public String required = "value";
		public @Nullable String nullable;
		public List<String> list = List.of();
		public List<@Nullable String> nullableElements = List.of();
		@GraphQLInputField(defaultValue = "default")
		public String defaulted = "default";
	}
}
