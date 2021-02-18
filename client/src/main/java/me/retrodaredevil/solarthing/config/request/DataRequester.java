package me.retrodaredevil.solarthing.config.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.solar.pzem.modbus.PzemShuntModbusSlaveRead;

@JsonSubTypes({
		@JsonSubTypes.Type(RaspberryPiCpuTemperatureDataRequester.class),
		@JsonSubTypes.Type(W1TemperatureDataRequester.class),
		@JsonSubTypes.Type(BatteryVoltageIODataRequester.class),
		@JsonSubTypes.Type(PzemShuntModbusSlaveRead.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface DataRequester {

	PacketListReceiver createPacketListReceiver(PacketListReceiver eventPacketReceiver);

}
