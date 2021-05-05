package me.retrodaredevil.couchdbjava.json;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;

import static java.util.Objects.requireNonNull;

public class StringJsonData implements JsonData {
	private final String json;

	public StringJsonData(String json) {
		requireNonNull(this.json = json);
	}

	@JsonValue
	@JsonRawValue
	@Override
	public String getJson() {
		return json;
	}
}
