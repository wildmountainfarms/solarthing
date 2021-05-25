package me.retrodaredevil.solarthing.config.request.modbus;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(RoverModbusRequester.class),
})
public interface ModbusRequester {
	SerialConfig getDefaultSerialConfig();
	PacketListReceiver createPacketListReceiver(PacketListReceiver eventPacketReceiver, ModbusSlave modbus);
}
