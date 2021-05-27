package me.retrodaredevil.solarthing.config.request.modbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.InjectEnvironment;
import me.retrodaredevil.solarthing.actions.environment.RoverModbusEnvironment;
import me.retrodaredevil.solarthing.config.options.CommandConfig;
import me.retrodaredevil.solarthing.config.request.RequestObject;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.program.ModbusListUpdaterWrapper;
import me.retrodaredevil.solarthing.program.RoverPacketListUpdater;
import me.retrodaredevil.solarthing.program.modbus.ModbusCacheSlave;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverWriteTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.RoverModbusSlaveRead;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.RoverModbusSlaveWrite;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

	private static void cacheRegisters(ModbusCacheSlave modbusCacheSlave) {
		modbusCacheSlave.cacheRangeInclusive(0x000A, 0x001A);

		modbusCacheSlave.cacheRangeInclusive(0x0100, 0x0109); // skip 0x010A
		modbusCacheSlave.cacheRangeInclusive(0x010B, 0x0122);

		modbusCacheSlave.cacheRangeInclusive(0xE002, 0xE014);
		// break here just because they're for different things
		modbusCacheSlave.cacheRangeInclusive(0xE015, 0xE021);
//			modbusCacheSlave.cacheRangeInclusive(0xE022, 0xE02D); these do not work when querying in bulk for some reason
	}

	@Override
	public PacketListReceiver createPacketListReceiver(RequestObject requestObject, ModbusSlave modbus) {
		final RoverReadTable read;
		final Runnable reloadCache;
		if (bulkRequest) {
			ModbusCacheSlave modbusCacheSlave = new ModbusCacheSlave(modbus);
			read = new RoverModbusSlaveRead(modbusCacheSlave);
			reloadCache = () -> cacheRegisters(modbusCacheSlave);
		} else {
			read = new RoverModbusSlaveRead(modbus);
			reloadCache = () -> {};
		}
		RoverWriteTable write = new RoverModbusSlaveWrite(modbus);
		Map<String, ActionNode> commandActionNodeMap = requestObject.getCommandActionNodeMap();
		for (String commandName : commandActionNodeMap.keySet()) {
			ActionNode actionNode = requestObject.getCommandActionNodeMap().get(commandName);
			ActionNode newActionNode = actionEnvironment -> {
				InjectEnvironment injectEnvironment = actionEnvironment.getInjectEnvironment().newBuilder().add(new RoverModbusEnvironment(read, write)).build();
				return actionNode.createAction(new ActionEnvironment(actionEnvironment.getGlobalEnvironment(), actionEnvironment.getLocalEnvironment(), injectEnvironment));
			};
			requestObject.getCommandActionNodeMap().put(commandName, newActionNode);
		}
		return new ModbusListUpdaterWrapper(new RoverPacketListUpdater(read, write), reloadCache, reloadIO, sendErrorPackets));
	}
}
