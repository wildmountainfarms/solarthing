package me.retrodaredevil.solarthing.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public final class JacksonUtil {
	private JacksonUtil(){ throw new UnsupportedOperationException(); }

	public static ObjectMapper defaultMapper(ObjectMapper mapper){
		mapper.setConfig(
				mapper.getDeserializationConfig()
						.with(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
//						.with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES) this really means fail on undefined, not null
						.with(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
		);
		mapper.setConfig(
				mapper.getSerializationConfig()
						.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // for things we want to be represented numerically, we usually use numeric types, otherwise use ISO-8601
						.without(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS) // durations should be written with ISO-8601
		);
		mapper.findAndRegisterModules(); // this is just plain useful. In serviceapi we depend on the JavaTimeModule, so this will auto add it if it is there.
		return mapper;
	}
	public static ObjectMapper defaultMapper(){
		return defaultMapper(new ObjectMapper());
	}

	/**
	 * This edits {@code mapper}'s deserialization config to make deserializing more lenient. This is useful for
	 * deserialization when you want a mapper that <b>will not fail</b> if it tries to parse a packet with newer fields.
	 * @param mapper The {@link ObjectMapper} to configure
	 * @return {@code mapper}.
	 */
	public static ObjectMapper lenientMapper(ObjectMapper mapper) {
		mapper.setConfig(mapper.getDeserializationConfig().without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
		return mapper;
	}
	public static ObjectMapper lenientSubTypeMapper(ObjectMapper mapper) {
		mapper.setConfig(mapper.getDeserializationConfig().without(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE));
		return mapper;
	}
}
