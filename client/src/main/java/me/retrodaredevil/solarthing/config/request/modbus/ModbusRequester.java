package me.retrodaredevil.solarthing.config.request.modbus;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.config.request.DataRequesterResult;
import me.retrodaredevil.solarthing.config.request.RequestObject;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(RoverModbusRequester.class),
})
public interface ModbusRequester {
	SerialConfig getDefaultSerialConfig();
	DataRequesterResult create(RequestObject requestObject, ModbusSlave modbus);

	default ActionNode alterCommandActionNode(ActionNode actionNode) {
		return actionNode;
	}
}
