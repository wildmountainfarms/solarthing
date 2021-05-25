package me.retrodaredevil.solarthing.config.request.modbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.solarthing.config.options.CommandConfig;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;

import java.util.Collections;
import java.util.List;

@JsonTypeName("rover")
public class RoverModbusRequester implements ModbusRequester {
	private final boolean sendErrorPackets;
	private final boolean bulkRequest;
	private final List<CommandConfig> commandConfigs;

	@JsonCreator
	public RoverModbusRequester(
			@JsonProperty("error_packets") Boolean sendErrorPackets,
			@JsonProperty("bulk_request") Boolean bulkRequest,
			@JsonProperty("commands") List<CommandConfig> commandConfigs) {
		this.sendErrorPackets = Boolean.TRUE.equals(sendErrorPackets); // default false
		this.bulkRequest = !Boolean.FALSE.equals(sendErrorPackets); // default true
		this.commandConfigs = commandConfigs == null ? Collections.emptyList() : commandConfigs;
	}


	@Override
	public SerialConfig getDefaultSerialConfig() {
		return RoverReadTable.SERIAL_CONFIG;
	}

	@Override
	public PacketListReceiver createPacketListReceiver(PacketListReceiver eventPacketReceiver, ModbusSlave modbus) {
		return null;
	}
}
