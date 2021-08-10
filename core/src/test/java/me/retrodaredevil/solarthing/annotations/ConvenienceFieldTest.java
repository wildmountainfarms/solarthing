package me.retrodaredevil.solarthing.annotations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConvenienceFieldTest {
	@Test
	void test() throws JsonProcessingException {
		Simple simple = new Simple(5, "The code is 5");
		String expectedIncluded = "{\"code\":5,\"prettyCode\":\"The code is 5\"}";
		String expectedRaw = "{\"code\":5}";
		ObjectMapper rawMapper = JacksonUtil.defaultMapper();
		ObjectMapper includeMapper = JacksonUtil.includeConvenienceFields(JacksonUtil.defaultMapper());

		assertEquals(expectedRaw, rawMapper.writeValueAsString(simple));
		assertEquals(expectedIncluded, includeMapper.writeValueAsString(simple));
	}

	static class Simple {
		@JsonProperty("code")
		private final int code;

		@ConvenienceField
		@JsonProperty("prettyCode")
		private final String prettyCode;

		Simple(int code, String prettyCode) {
			this.code = code;
			this.prettyCode = prettyCode;
		}
	}
}
