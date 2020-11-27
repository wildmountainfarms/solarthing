package me.retrodaredevil.solarthing.homeassistant.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AttributeBuilder {
	private final Map<String, String> map = new HashMap<>();

	public AttributeBuilder friendlyName(String name) {
		return custom("friendly_name", name);
	}

	public AttributeBuilder custom(String key, String value) {
		map.put(key, value);
		return this;
	}

	public Map<String, String> build() {
		if (!validateAttributes(map)) {
			throw new IllegalStateException("You have to give this a friendly name!");
		}
		return Collections.unmodifiableMap(new HashMap<>(map));
	}

	/**
	 * @param map A map of attributes
	 * @return true if {@code map} is a valid attributes map
	 */
	public static boolean validateAttributes(Map<String, String> map) {
		return map.containsKey("friendly_name");
	}
}
