package me.retrodaredevil.solarthing.packets.collection.parsing;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class JacksonPolymorphicTest {

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "packetType")
	public interface A {
	}
	@JsonTypeName("myType")
	static class B implements A {
	}
	@Test
	void test() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper().copy();
		objectMapper.getSubtypeResolver().registerSubtypes(B.class);
		String json = "{ \"packetType\": \"myType\" }";
		objectMapper.readValue(json, A.class);
	}
}
