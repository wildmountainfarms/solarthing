package me.retrodaredevil.solarthing.actions.environment;


import me.retrodaredevil.solarthing.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MultiTracerModbusEnvironment {
	private final Map<Integer, TracerModbusEnvironment> map;

	public MultiTracerModbusEnvironment(Map<Integer, TracerModbusEnvironment> map) {
		this.map = Collections.unmodifiableMap(new HashMap<>(map));
	}
	public MultiTracerModbusEnvironment() {
		this.map = Collections.emptyMap();
	}

	public @Nullable TracerModbusEnvironment getOrNull(int number) {
		return map.get(number);
	}

	public MultiTracerModbusEnvironment plus(MultiTracerModbusEnvironment environment) {
		Map<Integer, TracerModbusEnvironment> newMap = new HashMap<>(map);
		newMap.putAll(environment.map);
		return new MultiTracerModbusEnvironment(newMap);
	}
}
