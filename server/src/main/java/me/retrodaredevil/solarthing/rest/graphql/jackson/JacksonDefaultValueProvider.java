package me.retrodaredevil.solarthing.rest.graphql.jackson;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLInputField;
import io.leangen.graphql.execution.GlobalEnvironment;
import io.leangen.graphql.metadata.DefaultValue;
import io.leangen.graphql.metadata.strategy.query.AnnotatedArgumentBuilder;
import io.leangen.graphql.metadata.strategy.value.DefaultValueProvider;
import io.leangen.graphql.metadata.strategy.value.InputFieldInfoGenerator;
import io.leangen.graphql.metadata.strategy.value.JsonDefaultValueProvider;
import io.leangen.graphql.util.ClassUtils;
import io.leangen.graphql.util.ReservedStrings;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Parameter;
import java.util.List;

/** Parses GraphQL annotation defaults with the mapper configured for this schema. */
@NullMarked
public final class JacksonDefaultValueProvider implements DefaultValueProvider {
	private final ObjectMapper mapper;

	public JacksonDefaultValueProvider(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public DefaultValue getDefaultValue(AnnotatedElement targetElement, AnnotatedType type, DefaultValue initialValue) {
		if (!(initialValue.getValue() instanceof String) || type.getType() == String.class) {
			return initialValue;
		}
		return initialValue.map(value -> mapper.readValue((String) value, Object.class));
	}

	private static DefaultValueProvider provider(Class<? extends DefaultValueProvider> type, ObjectMapper mapper) {
		if (type == JsonDefaultValueProvider.class || type == JacksonDefaultValueProvider.class) {
			return new JacksonDefaultValueProvider(mapper);
		}
		// Keep explicitly configured providers responsible for their own parsing.
		return ClassUtils.instance(type);
	}

	/** Uses the schema mapper for argument defaults, preserving explicitly supplied providers. */
	public static final class ArgumentBuilder extends AnnotatedArgumentBuilder {
		private final ObjectMapper mapper;

		public ArgumentBuilder(ObjectMapper mapper) {
			this.mapper = mapper;
		}

		@Override
		protected DefaultValue defaultValue(Parameter parameter, AnnotatedType parameterType, GlobalEnvironment environment) {
			var annotation = parameter.getAnnotation(GraphQLArgument.class);
			if (annotation == null) {
				return DefaultValue.EMPTY;
			}
			DefaultValue initial = ReservedStrings.decodeDefault(environment.messageBundle.interpolate(annotation.defaultValue()));
			return provider(annotation.defaultValueProvider(), mapper)
					.getDefaultValue(parameter, environment.getMappableInputType(parameterType), initial);
		}
	}

	/** Uses the same provider selection for defaults on input-object properties. */
	public static final class InputFieldInfo extends InputFieldInfoGenerator {
		private final ObjectMapper mapper;

		public InputFieldInfo(ObjectMapper mapper) {
			this.mapper = mapper;
		}

		@Override
		public DefaultValue defaultValue(List<? extends AnnotatedElement> candidates, AnnotatedType type, GlobalEnvironment environment) {
			for (AnnotatedElement candidate : candidates) {
				GraphQLInputField annotation = candidate.getAnnotation(GraphQLInputField.class);
				if (annotation != null) {
					DefaultValue initial = ReservedStrings.decodeDefault(environment.messageBundle.interpolate(annotation.defaultValue()));
					return provider(annotation.defaultValueProvider(), mapper).getDefaultValue(candidate, type, initial);
				}
			}
			return DefaultValue.EMPTY;
		}
	}
}
