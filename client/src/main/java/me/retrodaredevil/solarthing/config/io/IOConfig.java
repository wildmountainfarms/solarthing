package me.retrodaredevil.solarthing.config.io;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.solarthing.config.io.modbus.DummyModbusIOConfig;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(SerialIOConfig.class),
		@JsonSubTypes.Type(StandardIOConfig.class),
		@JsonSubTypes.Type(DummyModbusIOConfig.class),
})
public interface IOConfig {
	IOBundle createIOBundle() throws Exception;
}
