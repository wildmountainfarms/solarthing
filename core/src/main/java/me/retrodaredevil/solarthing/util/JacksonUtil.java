package me.retrodaredevil.solarthing.util;

import me.retrodaredevil.solarthing.annotations.ConvenienceField;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import org.jspecify.annotations.NullMarked;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import tools.jackson.databind.json.JsonMapper;

@UtilityClass
@NullMarked
public final class JacksonUtil {
	private JacksonUtil(){ throw new UnsupportedOperationException(); }

	public static <M extends ObjectMapper, B extends MapperBuilder<M, B>> B defaultMapper(MapperBuilder<M, B> builder) {
		return builder
				.enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
				.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
				.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES) // reject explicit null for primitives instead of coercing it to zero
				// TODO consider enabling FAIL_ON_MISSING_CREATOR_PROPERTIES to reject missing constructor/factory arguments
				.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.disable(EnumFeature.READ_ENUMS_USING_TO_STRING)
				.disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
				.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS) // for things we want to be represented numerically, we usually use numeric types, otherwise use ISO-8601
				.disable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS) // durations should be written with ISO-8601
				;
	}
	public static JsonMapper.Builder defaultMapperBuilder() {
		return defaultMapper(JsonMapper.builder());
	}
	public static JsonMapper defaultMapper() {
		return defaultMapperBuilder().build();
	}
	public static JsonMapper.Builder configurationMapper(JsonMapper.Builder builder) {
		// When parsing configuration files, we should allow comments
		return builder.enable(JsonReadFeature.ALLOW_JAVA_COMMENTS);
	}

	/** Configures the builder to accept unknown properties, such as fields from newer packets. */
	public static <M extends ObjectMapper, B extends MapperBuilder<M, B>> B lenientMapper(MapperBuilder<M, B> builder) {
		return builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
	public static <M extends ObjectMapper, B extends MapperBuilder<M, B>> B lenientSubTypeMapper(MapperBuilder<M, B> builder) {
		return builder.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
	}
	public static JsonMapper.Builder jsonLikeTolerantMapper(JsonMapper.Builder builder) {
		return builder.enable(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES);
	}
	public static <M extends ObjectMapper, B extends MapperBuilder<M, B>> B includeConvenienceFields(MapperBuilder<M, B> builder) {
		return builder.annotationIntrospector(new JacksonAnnotationIntrospector() {
			@Override
			public boolean hasIgnoreMarker(MapperConfig<?> config, AnnotatedMember m) {
				if (m.hasAnnotation(ConvenienceField.class)) {
					return false;
				}
				return super.hasIgnoreMarker(config, m);
			}
		});
	}
}
