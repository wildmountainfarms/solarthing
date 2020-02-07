package me.retrodaredevil.solarthing.pvoutput;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.IOException;

@JsonSerialize(using = SimpleDate.Serializer.class)
@JsonDeserialize(using = SimpleDate.Deserializer.class)
public final class SimpleDate implements PVOutputString {
	private final int year;
	private final int month;
	private final int day;

	public SimpleDate(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
		if(month < 0 || month > 12){
			throw new IllegalArgumentException("Month is out of range! month=" + month);
		}
		if(day < 0 || day > 31){
			throw new IllegalArgumentException("Day is out of range! day=" + day);
		}
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}
	@Override
	public String toPVOutputString(){
		if(year < 0){
			throw new IllegalStateException("Year is < 0! year=" + year);
		}
		if(year > 9999){
			// Sorry future programmers (are you still called programmers?), I know at the beginning of year 9999, you'll all have to be fixing stuff like this.
			// obviously SolarThing will be top priority when the year 9999 hits so I do apologize.
			throw new IllegalStateException("Year is > 9999! year=" + year);
		}
		StringBuilder yearString = new StringBuilder("" + year);
		while(yearString.length() < 4){
			yearString.insert(0, "0");
		}
		String monthString = "" + month;
		if(monthString.length() < 2){
			monthString = "0" + monthString;
		}
		String dayString = "" + day;
		if(dayString.length() < 2){
			dayString = "0" + dayString;
		}

		return yearString + monthString + dayString;
	}
	static class Serializer extends JsonSerializer<SimpleDate> {
		@Override
		public void serialize(SimpleDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeString(value.toPVOutputString());
		}
	}
	static class Deserializer extends JsonDeserializer<SimpleDate> {

		@Override
		public SimpleDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			JsonNode node = p.getCodec().readTree(p);
			if(!node.isTextual()){
				throw new JsonMappingException(p, "This isn't textual!");
			}
			String value = node.textValue();
			if(value.length() != 8){
				throw MismatchedInputException.from(p, SimpleDate.class, "value.length() isn't 8! value=" + value);
			}
			String yearString = value.substring(0, 4);
			String monthString = value.substring(4, 6);
			String dayString = value.substring(6, 8);
			final int year, month, day;
			try {
				year = Integer.parseInt(yearString);
				month = Integer.parseInt(monthString);
				day = Integer.parseInt(dayString);
			} catch(NumberFormatException ex){
				throw new JsonMappingException(p, "Couldn't parse either year, month, or day! value=" + value, ex);
			}
			return new SimpleDate(year, month, day);
		}
	}
}
