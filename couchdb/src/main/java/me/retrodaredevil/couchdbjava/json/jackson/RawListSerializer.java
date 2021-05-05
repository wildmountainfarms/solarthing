package me.retrodaredevil.couchdbjava.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.retrodaredevil.couchdbjava.json.JsonData;

import java.io.IOException;
import java.util.List;

@Deprecated
public class RawListSerializer extends JsonSerializer<List<? extends JsonData>> {
	@Override
	public void serialize(List<? extends JsonData> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartArray();
		for (JsonData jsonData : value) {
			gen.writeObject(jsonData);
		}
		gen.writeEndArray();
	}
}
