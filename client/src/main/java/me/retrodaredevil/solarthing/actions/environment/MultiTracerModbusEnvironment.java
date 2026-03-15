package me.retrodaredevil.solarthing.actions.environment;


import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@NullMarked
public final class MultiTracerModbusEnvironment {
	private final Map<Integer, TracerModbusEnvironment> map;

	public MultiTracerModbusEnvironment(Map<Integer, TracerModbusEnvironment> map) {
		this.map = Map.copyOf(map);
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
