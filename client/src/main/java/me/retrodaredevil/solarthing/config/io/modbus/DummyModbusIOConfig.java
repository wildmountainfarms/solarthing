package me.retrodaredevil.solarthing.config.io.modbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.modbus.RtuDataEncoder;
import me.retrodaredevil.solarthing.config.io.IOConfig;

import java.util.HashMap;
import java.util.Map;

@JsonTypeName("dummy-modbus")
public class DummyModbusIOConfig implements IOConfig {
	private final Map<Integer, DummyModbusSlave> addressToSlaveMap;

	@JsonCreator
	public DummyModbusIOConfig(@JsonProperty("devices") Map<Integer, DummyModbusSlave> addressToSlaveMap) {
		this.addressToSlaveMap = addressToSlaveMap;
	}

	@Override
	public IOBundle createIOBundle() {
		Map<Integer, ModbusSlave> map = new HashMap<>(addressToSlaveMap.size());
		for (Map.Entry<Integer, DummyModbusSlave> entry : addressToSlaveMap.entrySet()) {
			int address = entry.getKey();
			DummyModbusSlave slave = entry.getValue();
			map.put(address, slave.createModbusSlave());
		}
		return new DummyModbusIO(map, new RtuDataEncoder());
	}
}
