package me.retrodaredevil.solarthing;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class JacksonTestUtil {
	@Test
	void testDefaultMapper() throws JsonProcessingException {
		// Yeah this test seems stupid and passes every time, but it's here cuz there's a bug in 2.12.0 that this test can't detect for some reason. So here it stays for now.
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		TestObject object = mapper.readValue("{ \"duration\": 5.4 }", TestObject.class);
		System.out.println(mapper.writeValueAsString(object));
	}


	private static class TestObject {
		@JsonFormat(pattern = "MINUTES")
		private final float timeMinutes;
		private TestObject(@JsonProperty("duration") @JsonFormat(pattern = "MINUTES") float timeMinutes) {
			this.timeMinutes = timeMinutes;
			System.out.println(timeMinutes);
		}
	}
}
