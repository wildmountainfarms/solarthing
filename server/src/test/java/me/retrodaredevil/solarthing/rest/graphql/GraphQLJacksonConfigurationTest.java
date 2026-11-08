package me.retrodaredevil.solarthing.rest.graphql;

import graphql.GraphQL;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLTypeUtil;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLInputField;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.metadata.DefaultValue;
import io.leangen.graphql.metadata.strategy.value.DefaultValueProvider;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.util.Map;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.module.SimpleModule;

import tools.jackson.databind.json.JsonMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
class GraphQLJacksonConfigurationTest {
	private static GraphQL graphQL(int offset) {
		SimpleModule module = new SimpleModule();
		// Only the schema's mapper knows this behavior: a separately created mapper will fail these assertions.
		module.addDeserializer(Object.class, new ValueDeserializer<>() {
			@Override
			public Object deserialize(JsonParser parser, DeserializationContext context) {
				return parser.getIntValue() + offset;
			}
		});
		JsonMapper mapper = JacksonUtil.defaultMapperBuilder()
				.addModule(module)
				.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
				.build();
		return GraphQL.newGraphQL(GraphQLJacksonConfiguration.schemaGenerator(mapper)
				.withOperationsFromSingleton(new Queries()).generate()).build();
	}

	@Test
	void usesSchemaMapperForArgumentAndNestedDefaults() {
		GraphQL first = graphQL(100);
		var firstResult = first.execute("{ argument nested { value } }");
		assertTrue(firstResult.getErrors().isEmpty(), firstResult.getErrors().toString());
		assertEquals(Map.of("argument", 101, "nested", Map.of("value", 101)), firstResult.getData());
		var secondResult = graphQL(200).execute("{ argument nested { value } }");
		assertTrue(secondResult.getErrors().isEmpty(), secondResult.getErrors().toString());
		assertEquals(Map.of("argument", 201, "nested", Map.of("value", 201)), secondResult.getData());
	}

	@Test
	void usesSchemaMapperForInputNamesValuesAndDefaults() {
		GraphQL graphQL = graphQL(100);
		var inputArgument = graphQL.getGraphQLSchema().getQueryType().getFieldDefinition("input").getArgument("input");
		var inputType = (GraphQLInputObjectType) GraphQLTypeUtil.unwrapAll(inputArgument.getType());
		assertNotNull(inputType.getFieldDefinition("sample_value"));
		assertNotNull(inputType.getFieldDefinition("default_value"));
		var result = graphQL.execute("{ input(input: {sample_value: 7}) }");
		assertTrue(result.getErrors().isEmpty(), result.getErrors().toString());
		assertEquals(Map.of("input", 108), result.getData());
	}

	@Test
	void preservesExplicitDefaultProviders() {
		var result = graphQL(100).execute("{ custom input(input: {sample_value: 0}) }");
		assertTrue(result.getErrors().isEmpty(), result.getErrors().toString());
		assertEquals(Map.of("custom", 333, "input", 101), result.getData());
		var customInput = graphQL(100).execute("{ customInput(input: {}) }");
		assertTrue(customInput.getErrors().isEmpty(), customInput.getErrors().toString());
		assertEquals(Map.of("customInput", 333), customInput.getData());
	}

	public static class Queries {
		@GraphQLQuery
		public int argument(@GraphQLArgument(name = "value", defaultValue = "1") int value) { return value; }
		@GraphQLQuery
		public Nested nested() { return new Nested(); }
		@GraphQLQuery
		public int input(@GraphQLArgument(name = "input") Input input) { return input.sampleValue + input.defaultValue; }
		@GraphQLQuery
		public int custom(@GraphQLArgument(name = "value", defaultValue = "1", defaultValueProvider = CustomDefaultProvider.class) int value) { return value; }
		@GraphQLQuery
		public int customInput(@GraphQLArgument(name = "input") CustomInput input) { return input.value; }
	}

	public static class Nested {
		@GraphQLInclude("value")
		public int value(@GraphQLArgument(name = "value", defaultValue = "1") int value) { return value; }
	}

	public static class Input {
		public int sampleValue;
		@GraphQLInputField(defaultValue = "1")
		public int defaultValue;
	}

	public static class CustomInput {
		@GraphQLInputField(defaultValue = "1", defaultValueProvider = CustomDefaultProvider.class)
		public int value;
	}

	public static class CustomDefaultProvider implements DefaultValueProvider {
		@Override
		public DefaultValue getDefaultValue(AnnotatedElement targetElement, AnnotatedType type, DefaultValue initialValue) {
			return new DefaultValue(333);
		}
	}
}
