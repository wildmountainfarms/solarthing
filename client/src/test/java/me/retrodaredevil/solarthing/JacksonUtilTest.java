package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@NullMarked
public class JacksonUtilTest {
	@Test
	void testDefaultMapper() {
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		mapper.readValue("\"PT5M\"", Duration.class);
	}

}
