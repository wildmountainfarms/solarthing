package me.retrodaredevil.solarthing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class JacksonUtilTest {
	@Test
	void testDefaultMapper() throws JsonProcessingException {
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		mapper.readValue("\"PT5M\"", Duration.class);
	}

}
