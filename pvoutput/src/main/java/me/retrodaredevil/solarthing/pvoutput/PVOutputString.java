package me.retrodaredevil.solarthing.pvoutput;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

@JsonSerialize(using = PVOutputString.Serializer.class)
public interface PVOutputString {
	String toPVOutputString();

	class Serializer extends JsonSerializer<PVOutputString> {
		@Override
		public void serialize(PVOutputString value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeString(value.toPVOutputString());
		}
	}
}
