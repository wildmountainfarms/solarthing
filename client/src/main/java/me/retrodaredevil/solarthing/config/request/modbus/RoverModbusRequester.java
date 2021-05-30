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
import me.retrodaredevil.solarthing.config.request.DataRequesterResult;
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
	private final List<String> attachToCommands;

	@JsonCreator
	public RoverModbusRequester(
			@JsonProperty("error_packets") Boolean sendErrorPackets,
			@JsonProperty("bulk_request") Boolean bulkRequest,
			@JsonProperty("commands") List<String> attachToCommands) {
		this.sendErrorPackets = Boolean.TRUE.equals(sendErrorPackets); // default false
		this.bulkRequest = !Boolean.FALSE.equals(bulkRequest); // default true
		this.attachToCommands = attachToCommands == null ? Collections.emptyList() : attachToCommands;
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
	public DataRequesterResult create(RequestObject requestObject, ModbusSlave modbus) {
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
		RoverModbusEnvironment roverModbusEnvironment = new RoverModbusEnvironment(read, write);
		// TODO make reload IO like it used to be before refactor
		Runnable reloadIO = () -> {};
		return new DataRequesterResult(
				new ModbusListUpdaterWrapper(new RoverPacketListUpdater(read, write), reloadCache, reloadIO, sendErrorPackets),
				(dataSource, injectEnvironmentBuilder) -> {
					String commandName = dataSource.getData();
					if (attachToCommands.contains(commandName)) {
						injectEnvironmentBuilder.add(roverModbusEnvironment);
					}
				}
		);
	}
}
