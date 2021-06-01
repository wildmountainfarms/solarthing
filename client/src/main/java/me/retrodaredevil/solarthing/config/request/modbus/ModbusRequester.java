package me.retrodaredevil.solarthing.config.request.modbus;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.solarthing.config.request.DataRequesterResult;
import me.retrodaredevil.solarthing.config.request.RequestObject;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(RoverModbusRequester.class),
		@JsonSubTypes.Type(TracerModbusRequester.class),
})
public interface ModbusRequester {
	SerialConfig getDefaultSerialConfig();
	DataRequesterResult create(RequestObject requestObject, SuccessReporter successReporter, ModbusSlave modbus);
}
