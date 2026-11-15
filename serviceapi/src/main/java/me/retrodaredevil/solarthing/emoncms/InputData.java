package me.retrodaredevil.solarthing.emoncms;

import org.jspecify.annotations.NullMarked;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@NullMarked
public class InputData {
	private static final ObjectMapper MAPPER = JsonMapper.builder().build();

	private final Map<String, Number> map;

	public InputData(Map<String, ? extends Number> map) {
		this.map = new LinkedHashMap<>(map);
	}

	@Override
	public String toString() {
		// if we decide not to use toString() in the future: https://stackoverflow.com/questions/28510056/serialize-query-parameter-in-retrofit/42459356#42459356
		try {
			return MAPPER.writeValueAsString(map);
		} catch (JacksonException e) {
			throw new RuntimeException(e);
		}
	}
	public static class Builder {
		private final Map<String, Number> map = new HashMap<>();
		public Builder add(String key, Number value){
			map.put(key, value);
			return this;
		}
		public InputData build(){
			return new InputData(map);
		}
	}
}
