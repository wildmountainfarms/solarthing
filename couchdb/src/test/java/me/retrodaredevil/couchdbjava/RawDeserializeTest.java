package me.retrodaredevil.couchdbjava;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.couchdbjava.json.jackson.JacksonJsonData;
import me.retrodaredevil.couchdbjava.json.jackson.RawDeserializer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RawDeserializeTest {
	@Test
	void testRaw() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String nestedJson = "{\"a\":5}";
		String json = "{\"value\": " + nestedJson + "}";
		SimpleObject object = mapper.readValue(json, SimpleObject.class);
		assertEquals(nestedJson, object.rawValue);
	}
	@Test
	void testRawArray() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String nestedJson = "{\"a\":5}";
		String json = "{\"value\":[" + nestedJson + "]}";
		SimpleArrayObject object = mapper.readValue(json, SimpleArrayObject.class);
		assertEquals(1, object.rawValues.size());
		assertEquals(nestedJson, object.rawValues.get(0).getJson());

	}
	private static class SimpleObject {
		@JsonRawValue
		@JsonDeserialize(using = RawDeserializer.class)
		@JsonProperty("value")
		private String rawValue;
	}
	private static class SimpleArrayObject {
		@JsonProperty("value")
		private List<JacksonJsonData> rawValues;
	}
}
