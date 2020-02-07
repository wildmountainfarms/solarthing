package me.retrodaredevil.solarthing.pvoutput;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.IOException;

@JsonSerialize(using = SimpleTime.Serializer.class)
@JsonDeserialize(using = SimpleTime.Deserializer.class)
public final class SimpleTime implements PVOutputString {
	private final int hour;
	private final int minute;

	public SimpleTime(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	@Override
	public String toPVOutputString() {
		String hourString = "" + hour;
		if(hourString.length() < 2){
			hourString = "0" + hourString;
		}
		String minuteString = "" + minute;
		if(minuteString.length() < 2){
			minuteString = "0" + minuteString;
		}
		return hourString + ":" + minuteString;
	}
	static class Serializer extends JsonSerializer<SimpleTime> {
		@Override
		public void serialize(SimpleTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeString(value.toPVOutputString());
		}
	}
	static class Deserializer extends JsonDeserializer<SimpleTime> {

		@Override
		public SimpleTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			JsonNode node = p.getCodec().readTree(p);
			if(!node.isTextual()){
				throw new JsonMappingException(p, "This isn't textual!");
			}
			String value = node.textValue();
			String[] split = value.split(":");
			if(split.length != 2){
				throw MismatchedInputException.from(p, SimpleTime.class, "split.length isn't 2! value=" + value);
			}
			final int hour, minute;
			try {
				hour = Integer.parseInt(split[0]);
				minute = Integer.parseInt(split[1]);
			} catch (NumberFormatException ex){
				throw new JsonMappingException(p, "Couldn't parse either hour or minute! value=" + value, ex);
			}
			return new SimpleTime(hour, minute);
		}
	}
}
