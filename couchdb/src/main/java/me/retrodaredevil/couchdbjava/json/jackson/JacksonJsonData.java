package me.retrodaredevil.couchdbjava.json.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.couchdbjava.json.JsonData;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

@JsonDeserialize(using = JacksonJsonData.Deserializer.class)
public class JacksonJsonData implements JsonData {
	private final JsonNode node;

	@JsonCreator
	public JacksonJsonData(JsonNode node) {
		requireNonNull(this.node = node);
	}

	@Override
	public String getJson() {
		return node.toString();
	}
	public JsonNode getNode() {
		return node;
	}

	@Override
	public boolean isKnownToBeValid() {
		return true;
	}

	public static class Deserializer extends JsonDeserializer<JacksonJsonData> {
		@Override
		public JacksonJsonData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			JsonNode node = p.readValueAs(JsonNode.class);
			return new JacksonJsonData(node);
		}
	}
	public static class Serializer extends JsonSerializer<JacksonJsonData> {
		@Override
		public void serialize(JacksonJsonData value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeTree(value.node);
		}
	}
}
