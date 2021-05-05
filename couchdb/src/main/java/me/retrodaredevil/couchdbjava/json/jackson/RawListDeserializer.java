package me.retrodaredevil.couchdbjava.json.jackson;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class RawListDeserializer extends JsonDeserializer<List<JacksonJsonData>> {
	@Override
	public List<JacksonJsonData> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		if (p.getCurrentToken() != JsonToken.START_ARRAY) {
			throw new JsonParseException(p, "Expected a start array!", p.getCurrentLocation());
		}
		List<JacksonJsonData> r = new ArrayList<>();
		p.nextToken();
		while (p.getCurrentToken() != JsonToken.END_ARRAY) {
			JsonNode node = p.readValueAs(JsonNode.class);
			r.add(new JacksonJsonData(node));

			p.nextToken();
		}
		return r;
	}
}
