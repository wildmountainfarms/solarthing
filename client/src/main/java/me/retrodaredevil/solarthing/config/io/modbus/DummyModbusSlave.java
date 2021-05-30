package me.retrodaredevil.solarthing.config.io.modbus;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.io.modbus.ModbusSlave;

@JsonSubTypes({
		@JsonSubTypes.Type(DummyRoverModbusSlave.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface DummyModbusSlave {
	ModbusSlave createModbusSlave();
}
