package me.retrodaredevil.solarthing.pvoutput;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Objects;

/**
 * Represents a date with a year, month, and day. Both month and day are 1 based.
 */
@JsonDeserialize(using = SimpleDate.Deserializer.class)
public final class SimpleDate implements Comparable<SimpleDate>, PVOutputString {
	private final int year;
	private final int month;
	private final int day;

	public SimpleDate(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
		if(month < 1 || month > 12){
			throw new IllegalArgumentException("Month is out of range! month=" + month);
		}
		if(day < 1 || day > 31){
			throw new IllegalArgumentException("Day is out of range! day=" + day);
		}
	}
	@Deprecated
	public static SimpleDate fromCalendar(Calendar calendar){
		return new SimpleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
	}
	public static SimpleDate fromTemporal(Temporal instant){
		return new SimpleDate(instant.get(ChronoField.YEAR), instant.get(ChronoField.MONTH_OF_YEAR), instant.get(ChronoField.DAY_OF_MONTH));
	}
	public static SimpleDate fromDateMillis(long dateMillis, ZoneId zoneId) {
		LocalDateTime localDateTime = Instant.ofEpochMilli(dateMillis).atZone(zoneId).toLocalDateTime();
		return fromTemporal(localDateTime);
	}
	public LocalDate toLocalDate() {
		return LocalDate.of(year, month, day);
	}

	public long getDayStartDateMillis(ZoneId zoneId) {
		return toLocalDate().atStartOfDay(zoneId).toInstant().toEpochMilli();
	}
	public SimpleDate tomorrow() {
		return fromTemporal(toLocalDate().plusDays(1));
	}
	public SimpleDate yesterday() {
		return fromTemporal(toLocalDate().minusDays(1));
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SimpleDate that = (SimpleDate) o;
		return year == that.year &&
				month == that.month &&
				day == that.day;
	}

	@Override
	public int hashCode() {
		return Objects.hash(year, month, day);
	}

	@Override
	public int compareTo(SimpleDate simpleDate) {
		int a = Integer.compare(year, simpleDate.year);
		if (a != 0) {
			return a;
		}
		a = Integer.compare(month, simpleDate.month);
		if (a != 0) {
			return a;
		}
		return Integer.compare(day, simpleDate.day);
	}

	@Override
	public String toPVOutputString(){
		if(year < 1000){
			// Sorry to time travelers that decide to live in the past, I know this isn't very convenient for you
			throw new IllegalStateException("Year is < 1000! year=" + year);
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
	@Override
	public String toString() {
		return toPVOutputString();
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
