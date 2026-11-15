package me.retrodaredevil.solarthing.solar.renogy;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import org.jspecify.annotations.NullMarked;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = Version.StringOnlySerializer.class)
@NullMarked
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
		if(number >= 100){
			throw new IllegalArgumentException("number cannot be greater than 100! it was: " + number);
		}
		if(number < 0){
			throw new IllegalArgumentException("number cannot be less than 0! it was: " + number);
		}
		String r = "" + number;
		return switch (r.length()) {
			case 2 -> r;
			case 1 -> "0" + r;
			default -> throw new AssertionError();
		};
	}

	@GraphQLInclude("representation")
	@Override
	public String toString() {
		return "V" + twoPlaces(high) + "." + twoPlaces(middle) + "." +twoPlaces(low);
	}

	public static class StringOnlySerializer extends ValueSerializer<Version> {

		@Override
		public void serialize(Version value, JsonGenerator gen, SerializationContext serializers) {
			gen.writeString(value.toString());
		}
	}
	public static class CompleteVersionSerializer extends ValueSerializer<Version> {

		@Override
		public void serialize(Version value, JsonGenerator gen, SerializationContext serializers) {
			gen.writeStartObject();
			gen.writeNumberProperty("Version", value.raw);
			gen.writeStringProperty("VersionString", value.toString());
			gen.writeEndObject();
		}
	}
}
