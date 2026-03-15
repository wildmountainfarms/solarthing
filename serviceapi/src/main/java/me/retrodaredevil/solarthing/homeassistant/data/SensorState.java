package me.retrodaredevil.solarthing.homeassistant.data;

import org.jspecify.annotations.NullMarked;

import java.util.Map;

import static java.util.Objects.requireNonNull;

@NullMarked
public class SensorState extends SensorStateBase {
	private final String state;

	public SensorState(String state, Map<String, String> attributes) {
		super(attributes);
		this.state = requireNonNull(state);
	}

	@Override
	public String getState() {
		return state;
	}
}
