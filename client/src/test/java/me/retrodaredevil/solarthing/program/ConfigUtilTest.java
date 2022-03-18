package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigUtilTest {

	@Test
	void testJacksonExceptions() {
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		for (String badJson : new String[] { "", "{" }) {
			try {
				mapper.readValue(badJson, Map.class);
				fail("Reading did not cause exception for: " + badJson);
			} catch (JsonProcessingException e) {
				assertTrue(e.getClass() == MismatchedInputException.class || e.getClass() == JsonEOFException.class, "Failed for: " + badJson);
				assertTrue(e.getMessage().contains("end-of-input"), "Failed for: " + badJson);
			}
		}
	}
}
