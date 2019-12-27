package me.retrodaredevil.solarthing.solar.renogy;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

@JsonSerialize(using = Version.StringOnlySerializer.class)
public final class Version {
	private final int raw;
	private final int high, middle, low;
	
	public Version(int versionCodeRaw) {
		this.raw = versionCodeRaw;
		int versionCode = versionCodeRaw & 0x00FFFFFF;
		low = versionCode & 0xFF;
		middle = (versionCode & 0xFF00) >>> 8;
		high = (versionCode & 0xFF0000) >>> 16;
	}
	public int getMajor(){
		return high;
	}
	public int getMinor(){
		return middle;
	}
	public int getPatch(){
		return low;
	}
	
	
	private static String twoPlaces(int number){
		if(number > 100){
			throw new IllegalArgumentException("number cannot be greater than 100! it was: " + number);
		}
		if(number < 0){
			throw new IllegalArgumentException("number cannot be less than 0! it was: " + number);
		}
		String r = "" + number;
		switch(r.length()){
			case 2: return r;
			case 1: return "0" + r;
			default: throw new AssertionError();
		}
	}
	
	@Override
	public String toString() {
		return "V" + twoPlaces(high) + "." + twoPlaces(middle) + "." +twoPlaces(low);
	}

	public static class StringOnlySerializer extends JsonSerializer<Version> {

		@Override
		public void serialize(Version value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeString(value.toString());
		}
	}
	public static class CompleteVersionSerializer extends JsonSerializer<Version> {

		@Override
		public void serialize(Version value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeStartObject();
			gen.writeNumberField("Version", value.raw);
			gen.writeStringField("VersionString", value.toString());
			gen.writeEndObject();
		}
	}
}
