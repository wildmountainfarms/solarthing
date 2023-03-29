package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MultiRoverModbusEnvironment {
	private final Map<Integer, RoverModbusEnvironment> map;

	public MultiRoverModbusEnvironment(Map<Integer, RoverModbusEnvironment> map) {
		this.map = Map.copyOf(map);
	}
	public MultiRoverModbusEnvironment() {
		this.map = Collections.emptyMap();
	}

	public @Nullable RoverModbusEnvironment getOrNull(int number) {
		return map.get(number);
	}

	public MultiRoverModbusEnvironment plus(MultiRoverModbusEnvironment environment) {
		Map<Integer, RoverModbusEnvironment> newMap = new HashMap<>(map);
		newMap.putAll(environment.map);
		return new MultiRoverModbusEnvironment(newMap);
	}
}
