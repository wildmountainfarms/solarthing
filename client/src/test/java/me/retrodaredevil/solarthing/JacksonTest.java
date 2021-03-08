package me.retrodaredevil.solarthing;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class JacksonTest {
	@Test
	void testOptionalInject() throws JsonProcessingException {
		// In the future, I hope that this test fails, meaning the code below doesn't fail. https://github.com/FasterXML/jackson-databind/issues/3072
		ObjectMapper mapper = new ObjectMapper();
		assertThrows(InvalidDefinitionException.class, () -> mapper.readValue("{}", SimpleTest.class));
	}

	private static class SimpleTest {
		@JacksonInject("myValue")
		private String myValue = null;
	}
}
