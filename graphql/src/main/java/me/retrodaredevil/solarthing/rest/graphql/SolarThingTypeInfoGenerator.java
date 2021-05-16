package me.retrodaredevil.solarthing.rest.graphql;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import io.leangen.graphql.annotations.types.GraphQLInterface;
import io.leangen.graphql.annotations.types.GraphQLType;
import io.leangen.graphql.annotations.types.GraphQLUnion;
import io.leangen.graphql.metadata.messages.MessageBundle;
import io.leangen.graphql.metadata.strategy.type.DefaultTypeInfoGenerator;
import io.leangen.graphql.util.Utils;

import java.lang.reflect.AnnotatedType;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

public class SolarThingTypeInfoGenerator extends DefaultTypeInfoGenerator {
	@Override
	@SuppressWarnings("unchecked")
	public String generateTypeDescription(AnnotatedType type, MessageBundle messageBundle) {
		Optional<String>[] descriptions = new Optional[]{
				Optional.ofNullable(type.getAnnotation(JsonClassDescription.class))
						.map(JsonClassDescription::value),

				Optional.ofNullable(type.getAnnotation(GraphQLUnion.class))
						.map(GraphQLUnion::description),
				Optional.ofNullable(type.getAnnotation(GraphQLInterface.class))
						.map(GraphQLInterface::description),
				Optional.ofNullable(type.getAnnotation(GraphQLType.class))
						.map(GraphQLType::description)
		};
		return messageBundle.interpolate(getFirstNonEmptyOrDefault(descriptions, () -> ""));

	}
	private String getFirstNonEmptyOrDefault(Optional<String>[] optionals, Supplier<String> defaultValue) {
		return Arrays.stream(optionals)
				.map(opt -> opt.filter(Utils::isNotEmpty))
				.reduce(Utils::or)
				.map(opt -> opt.orElse(defaultValue.get()))
				.get();
	}
}
