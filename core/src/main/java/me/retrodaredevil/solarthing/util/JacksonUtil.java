package me.retrodaredevil.solarthing.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

public final class JacksonUtil {
	private JacksonUtil(){ throw new UnsupportedOperationException(); }

	public static ObjectMapper defaultMapper(ObjectMapper mapper){
		mapper.activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build(), ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS);
		return mapper;
	}
}
