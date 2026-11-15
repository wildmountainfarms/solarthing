package me.retrodaredevil.solarthing.config.options;

import org.jspecify.annotations.NullMarked;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = ProgramType.Deserializer.class)
@JsonSerialize(using = ProgramType.Serializer.class)
@NullMarked
public enum ProgramType {
	MATE("mate"),
//	ROVER("rover"),
	ROVER_SETUP("rover-setup"),
	PVOUTPUT_UPLOAD("pvoutput-upload"),
	REQUEST("request"),
	AUTOMATION("automation"),
	;
	private final String name;

	ProgramType(String name) {
		this.name = name;
	}
	public String getName(){
		return name;
	}

	static class Deserializer extends ValueDeserializer<ProgramType> {

		@Override
		public ProgramType deserialize(JsonParser p, DeserializationContext ctxt) {
			JsonNode node = p.objectReadContext().readTree(p);
			String text = node.asString();
			for(ProgramType type : values()){
				if(type.name.equals(text)){
					return type;
				}
			}
			throw DatabindException.from(p, text + " is an unknown program type!");
		}
	}
	static class Serializer extends ValueSerializer<ProgramType> {
		@Override
		public void serialize(ProgramType value, JsonGenerator gen, SerializationContext serializers) {
			gen.writeString(value.name);
		}
	}
}
