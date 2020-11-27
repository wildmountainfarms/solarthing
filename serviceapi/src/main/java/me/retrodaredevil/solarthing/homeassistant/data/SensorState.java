package me.retrodaredevil.solarthing.homeassistant.data;

import java.util.Map;

import static java.util.Objects.requireNonNull;

public class SensorState extends SensorStateBase {
	private final String state;

	public SensorState(String state, Map<String, String> attributes) {
		super(attributes);
		requireNonNull(this.state = state);
	}

	@Override
	public String getState() {
		return state;
	}
}
