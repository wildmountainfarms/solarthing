package me.retrodaredevil.solarthing.config.request.modbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.environment.MultiTracerModbusEnvironment;
import me.retrodaredevil.solarthing.actions.environment.TracerModbusEnvironment;
import me.retrodaredevil.solarthing.config.netcat.NetCatConfig;
import me.retrodaredevil.solarthing.config.request.DataRequesterResult;
import me.retrodaredevil.solarthing.config.request.RequestObject;
import me.retrodaredevil.solarthing.config.request.TracerClockOptions;
import me.retrodaredevil.solarthing.netcat.ConnectionHandler;
import me.retrodaredevil.solarthing.netcat.NetCatServerHandler;
import me.retrodaredevil.solarthing.packets.identification.NumberedIdentifier;
import me.retrodaredevil.solarthing.program.modbus.ModbusCacheSlave;
import me.retrodaredevil.solarthing.program.receiver.ModbusListUpdaterWrapper;
import me.retrodaredevil.solarthing.program.receiver.TracerPacketListUpdater;
import me.retrodaredevil.solarthing.solar.tracer.TracerReadTable;
import me.retrodaredevil.solarthing.solar.tracer.TracerWriteTable;
import me.retrodaredevil.solarthing.solar.tracer.modbus.TracerModbusSlaveRead;
import me.retrodaredevil.solarthing.solar.tracer.modbus.TracerModbusSlaveWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@JsonTypeName("tracer")
public class TracerModbusRequester implements ModbusRequester {
	private static final Logger LOGGER = LoggerFactory.getLogger(TracerModbusRequester.class);

	private final boolean sendErrorPackets;
	private final boolean bulkRequest;
	private final int number;
	private final TracerClockOptions tracerClockOptions;
	private final NetCatConfig configurationServerConfig;
	private final boolean connectionHandlerHasFlushLogic;

	@JsonCreator
	public TracerModbusRequester(
			@JsonProperty("error_packets") Boolean sendErrorPackets,
			@JsonProperty("bulk_request") Boolean bulkRequest,
			@JsonProperty("number") Integer number,
			@JsonProperty("clock") TracerClockOptions tracerClockOptions,
			@JsonProperty("server") NetCatConfig configurationServerConfig,
			@JsonProperty("connectionHandlerHasFlushLogic") Boolean connectionHandlerHasFlushLogic
	) {
		this.sendErrorPackets = Boolean.TRUE.equals(sendErrorPackets); // default false
		this.bulkRequest = !Boolean.FALSE.equals(bulkRequest); // default true
		this.number = number == null ? NumberedIdentifier.DEFAULT_NUMBER : number;
		this.tracerClockOptions = tracerClockOptions;
		this.configurationServerConfig = configurationServerConfig;
		this.connectionHandlerHasFlushLogic = Boolean.TRUE.equals(connectionHandlerHasFlushLogic);
		if (number != null) {
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Hey! We noticed you are defining 'number' on this tracer modbus requester! Please don't do that unless you actually need to!!");
		}
	}

	@Override
	public SerialConfig getDefaultSerialConfig() {
		return TracerReadTable.SERIAL_CONFIG;
	}
	private static void cacheRegisters(ModbusCacheSlave modbusCacheSlave) {
		modbusCacheSlave.cacheInputRangeInclusive(0x3000, 0x3008);
		modbusCacheSlave.cacheInput(0x300E, 1);

		modbusCacheSlave.cacheInputRangeInclusive(0x3100, 0x3107);
		modbusCacheSlave.cacheInputRangeInclusive(0x310C, 0x3112);
		modbusCacheSlave.cacheInputRangeInclusive(0x311A, 0x311B);
		modbusCacheSlave.cacheInput(0x311D, 1);

		modbusCacheSlave.cacheInputRangeInclusive(0x3200, 0x3201);

		modbusCacheSlave.cacheInputRangeInclusive(0x3300, 0x3315);
		modbusCacheSlave.cacheInputRangeInclusive(0x331B, 0x331E);

		modbusCacheSlave.cacheHoldingRangeInclusive(0x9000, 0x900E);
		modbusCacheSlave.cacheHoldingRangeInclusive(0x9013, 0x9021);
		modbusCacheSlave.cacheHoldingRangeInclusive(0x903D, 0x903F);
		modbusCacheSlave.cacheHoldingRangeInclusive(0x9042, 0x904D);
		modbusCacheSlave.cacheHolding(0x9065, 1);
		modbusCacheSlave.cacheHoldingRangeInclusive(0x9069, 0x906E);
		modbusCacheSlave.cacheHolding(0x9070, 1);
	}

	@Override
	public DataRequesterResult create(RequestObject requestObject, SuccessReporter successReporter, ModbusSlave modbus) {
		final TracerReadTable read;
		final Runnable reloadCache;
		if (bulkRequest) {
			ModbusCacheSlave modbusCacheSlave = new ModbusCacheSlave(modbus);
			read = new TracerModbusSlaveRead(modbusCacheSlave);
			reloadCache = () -> cacheRegisters(modbusCacheSlave);
		} else {
			read = new TracerModbusSlaveRead(modbus);
			reloadCache = () -> {};
		}
		TracerWriteTable write = new TracerModbusSlaveWrite(modbus);
		TracerModbusEnvironment tracerModbusEnvironment = new TracerModbusEnvironment(read, write);
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
		final MultiTracerModbusEnvironment multiTracerModbusEnvironmentToAdd;
		{
			Map<Integer, TracerModbusEnvironment> map = new HashMap<>();
			map.put(number, tracerModbusEnvironment);
			multiTracerModbusEnvironmentToAdd = new MultiTracerModbusEnvironment(map);
		}
		return DataRequesterResult.builder()
				.statusPacketListReceiver(new ModbusListUpdaterWrapper(ModbusListUpdaterWrapper.LogType.TRACER, new TracerPacketListUpdater(number, read, write, tracerClockOptions, netCatServerHandler == null ? null : new ConnectionHandler(netCatServerHandler), connectionHandlerHasFlushLogic), reloadCache, successReporter, sendErrorPackets, "tracer.error." + number))
				.environmentUpdater(
						(_executionReason, injectEnvironmentBuilder) ->
								injectEnvironmentBuilder.update(MultiTracerModbusEnvironment.class, multiTracerModbusEnvironment -> multiTracerModbusEnvironment.plus(multiTracerModbusEnvironmentToAdd), MultiTracerModbusEnvironment::new)
				)
				.build();

	}
}
