package me.retrodaredevil.solarthing.homeassistant.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class SensorStateBase implements HomeAssistantSensorState {
	private final Map<String, String> attributes;

	protected SensorStateBase(Map<String, String> attributes) {
		this.attributes = Collections.unmodifiableMap(new HashMap<>(attributes));
	}

	@Override
	public Map<String, String> getAttributes() {
		return attributes;
	}
}
