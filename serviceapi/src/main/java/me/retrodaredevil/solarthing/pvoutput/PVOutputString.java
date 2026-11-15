package me.retrodaredevil.solarthing.pvoutput;

import org.jspecify.annotations.NullMarked;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = PVOutputString.Serializer.class)
@NullMarked
public interface PVOutputString {
	String toPVOutputString();

	class Serializer extends ValueSerializer<PVOutputString> {
		@Override
		public void serialize(PVOutputString value, JsonGenerator gen, SerializationContext serializers) {
			gen.writeString(value.toPVOutputString());
		}
	}
}
