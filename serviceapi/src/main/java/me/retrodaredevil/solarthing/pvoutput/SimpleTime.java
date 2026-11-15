package me.retrodaredevil.solarthing.pvoutput;

import org.jspecify.annotations.NullMarked;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.MismatchedInputException;

import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Calendar;

@JsonDeserialize(using = SimpleTime.Deserializer.class)
@NullMarked
public final class SimpleTime implements PVOutputString {
	private final int hour;
	private final int minute;

	public SimpleTime(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
	}
	public static SimpleTime fromCalendar(Calendar calendar){
		return new SimpleTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
	}
	public static SimpleTime fromTemporal(Temporal temporal){
		return new SimpleTime(temporal.get(ChronoField.HOUR_OF_DAY), temporal.get(ChronoField.MINUTE_OF_HOUR));
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
	static class Deserializer extends ValueDeserializer<SimpleTime> {

		@Override
		public SimpleTime deserialize(JsonParser p, DeserializationContext ctxt) {
			JsonNode node = p.objectReadContext().readTree(p);
			if(!node.isString()){
				throw DatabindException.from(p, "This isn't textual!");
			}
			String value = node.stringValue();
			String[] split = value.split(":");
			if(split.length != 2){
				throw MismatchedInputException.from(p, SimpleTime.class, "split.length isn't 2! value=" + value);
			}
			final int hour, minute;
			try {
				hour = Integer.parseInt(split[0]);
				minute = Integer.parseInt(split[1]);
			} catch (NumberFormatException ex){
				throw DatabindException.from(p, "Couldn't parse either hour or minute! value=" + value, ex);
			}
			return new SimpleTime(hour, minute);
		}
	}
}
