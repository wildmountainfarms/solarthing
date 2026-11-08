package me.retrodaredevil.solarthing.annotations;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public class ConvenienceFieldTest {
	@Test
	void test() {
		Simple simple = new Simple(5, "The code is 5");
		String expectedIncluded = "{\"code\":5,\"prettyCode\":\"The code is 5\"}";
		String expectedRaw = "{\"code\":5}";
		ObjectMapper rawMapper = JacksonUtil.defaultMapper();
		ObjectMapper includeMapper = JacksonUtil.includeConvenienceFields(JacksonUtil.defaultMapperBuilder()).build();

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
