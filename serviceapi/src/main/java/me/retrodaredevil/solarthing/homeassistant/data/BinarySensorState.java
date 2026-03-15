package me.retrodaredevil.solarthing.homeassistant.data;

import org.jspecify.annotations.NullMarked;

import java.util.Map;


@NullMarked
public class BinarySensorState extends SensorStateBase {
	private final boolean on;

	public BinarySensorState(boolean on, Map<String, String> attributes) {
		super(attributes);
		this.on = on;
	}

	@Override
	public String getState() {
		return on ? "on" : "off";
	}
}
