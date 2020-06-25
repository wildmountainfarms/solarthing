package me.retrodaredevil.solarthing.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JacksonUtil {
	private JacksonUtil(){ throw new UnsupportedOperationException(); }

	public static ObjectMapper defaultMapper(ObjectMapper mapper){
		mapper.setConfig(
				mapper.getDeserializationConfig()
						.with(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
						.with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
						.with(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
		);
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
