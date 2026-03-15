package me.retrodaredevil.solarthing.config.io.modbus;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.io.modbus.ModbusSlave;
import org.jspecify.annotations.NullMarked;

@JsonSubTypes({
		@JsonSubTypes.Type(DummyRoverModbusSlave.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@NullMarked
public interface DummyModbusSlave {
	ModbusSlave createModbusSlave();
}
