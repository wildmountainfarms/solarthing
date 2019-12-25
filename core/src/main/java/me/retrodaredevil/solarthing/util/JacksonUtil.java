package me.retrodaredevil.solarthing.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class JacksonUtil {
	private JacksonUtil(){ throw new UnsupportedOperationException(); }

	public static ObjectMapper defaultMapper(ObjectMapper mapper){
//		mapper.activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build(), ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS);
		return mapper;
	}
	public static ObjectMapper defaultMapper(){
		return defaultMapper(new ObjectMapper());
	}
}
