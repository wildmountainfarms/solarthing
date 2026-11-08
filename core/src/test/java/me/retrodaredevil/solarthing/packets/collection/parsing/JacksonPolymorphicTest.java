package me.retrodaredevil.solarthing.packets.collection.parsing;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

@NullMarked
public class JacksonPolymorphicTest {

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "packetType")
	public interface A {
	}
	@JsonTypeName("myType")
	static class B implements A {
	}
	@Test
	void test() {
		JsonMapper objectMapper = JsonMapper.builder().registerSubtypes(B.class).build();
		String json = "{ \"packetType\": \"myType\" }";
		objectMapper.readValue(json, A.class);
	}
}
