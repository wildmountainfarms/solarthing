package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

@JsonDeserialize(using = ProgramType.Deserializer.class)
@JsonSerialize(using = ProgramType.Serializer.class)
public enum ProgramType {
	MATE("mate"),
	ROVER("rover"),
	ROVER_SETUP("rover-setup"),
	@Deprecated
	OUTHOUSE("outhouse"),
	PVOUTPUT_UPLOAD("pvoutput-upload"),
	MESSAGE_SENDER("message-sender"),
	REQUEST("request")
	;
	private final String name;

	ProgramType(String name) {
		this.name = name;
	}
	public String getName(){
		return name;
	}

	static class Deserializer extends JsonDeserializer<ProgramType> {

		@Override
		public ProgramType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			JsonNode node = p.getCodec().readTree(p);
			String text = node.asText();
			for(ProgramType type : values()){
				if(type.name.equals(text)){
					return type;
				}
			}
			throw new JsonMappingException(p, text + " is an unknown program type!");
		}
	}
	static class Serializer extends JsonSerializer<ProgramType> {
		@Override
		public void serialize(ProgramType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeString(value.name);
		}
	}
}
