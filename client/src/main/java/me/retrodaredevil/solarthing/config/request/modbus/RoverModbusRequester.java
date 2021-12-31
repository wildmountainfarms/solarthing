package me.retrodaredevil.solarthing.config.request.modbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.environment.RoverModbusEnvironment;
import me.retrodaredevil.solarthing.config.netcat.NetCatConfig;
import me.retrodaredevil.solarthing.config.request.DataRequesterResult;
import me.retrodaredevil.solarthing.config.request.RequestObject;
import me.retrodaredevil.solarthing.netcat.ConnectionHandler;
import me.retrodaredevil.solarthing.netcat.NetCatServerHandler;
import me.retrodaredevil.solarthing.packets.identification.NumberedIdentifier;
import me.retrodaredevil.solarthing.program.receiver.ModbusListUpdaterWrapper;
import me.retrodaredevil.solarthing.program.receiver.RoverPacketListUpdater;
import me.retrodaredevil.solarthing.program.modbus.ModbusCacheSlave;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverWriteTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.RoverModbusSlaveRead;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.RoverModbusSlaveWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@JsonTypeName("rover")
public class RoverModbusRequester implements ModbusRequester {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoverModbusRequester.class);
	private final boolean sendErrorPackets;
	private final boolean bulkRequest;
	private final List<String> attachToCommands;
	private final int number;
	private final NetCatConfig configurationServerConfig;

	@JsonCreator
	public RoverModbusRequester(
			@JsonProperty("error_packets") Boolean sendErrorPackets,
			@JsonProperty("bulk_request") Boolean bulkRequest,
			@JsonProperty("commands") List<String> attachToCommands,
			@JsonProperty("number") Integer number,
			@JsonProperty("server") NetCatConfig configurationServerConfig) {
		this.sendErrorPackets = Boolean.TRUE.equals(sendErrorPackets); // default false
		this.bulkRequest = !Boolean.FALSE.equals(bulkRequest); // default true
		this.attachToCommands = attachToCommands == null ? Collections.emptyList() : attachToCommands;
		this.number = number == null ? NumberedIdentifier.DEFAULT_NUMBER : number;
		this.configurationServerConfig = configurationServerConfig;
		if (number != null) {
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Hey! We noticed you are defining 'number' on this rover modbus requester! Please don't do that unless you actually need to!!");
		}
	}


	@Override
	public SerialConfig getDefaultSerialConfig() {
		return RoverReadTable.SERIAL_CONFIG;
	}

	private static void cacheRegisters(ModbusCacheSlave modbusCacheSlave) {
		modbusCacheSlave.cacheHoldingRangeInclusive(0x000A, 0x001A);

		modbusCacheSlave.cacheHoldingRangeInclusive(0x0100, 0x0109); // skip 0x010A
		modbusCacheSlave.cacheHoldingRangeInclusive(0x010B, 0x0122);

		modbusCacheSlave.cacheHoldingRangeInclusive(0xE002, 0xE014);
		// break here just because they're for different things
		modbusCacheSlave.cacheHoldingRangeInclusive(0xE015, 0xE021);
//			modbusCacheSlave.cacheRangeInclusive(0xE022, 0xE02D); these do not work when querying in bulk for some reason
	}

	@Override
	public DataRequesterResult create(RequestObject requestObject, SuccessReporter successReporter, ModbusSlave modbus) {
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
		final NetCatServerHandler netCatServerHandler;
		if (configurationServerConfig == null) {
			netCatServerHandler = null;
		} else {
			try {
				netCatServerHandler = new NetCatServerHandler(configurationServerConfig.getBindAddress(), configurationServerConfig.getPort());
			} catch (IOException e) {
				throw new RuntimeException("Could not create NetCatServerHandler", e);
			}
		}
		return new DataRequesterResult(
				new ModbusListUpdaterWrapper(new RoverPacketListUpdater(number, read, write, netCatServerHandler == null ? null : new ConnectionHandler(netCatServerHandler)), reloadCache, successReporter, sendErrorPackets, "rover.error." + number),
				new AttachToCommandEnvironmentUpdater(Collections.singletonList(roverModbusEnvironment), attachToCommands::contains)
		);
	}
}
