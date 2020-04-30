package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.modbus.*;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.io.serial.SerialConfigBuilder;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.config.options.ExtraOptionFlag;
import me.retrodaredevil.solarthing.config.options.RoverOption;
import me.retrodaredevil.solarthing.config.options.RoverProgramOptions;
import me.retrodaredevil.solarthing.config.options.RoverSetupProgramOptions;
import me.retrodaredevil.solarthing.misc.device.RaspberryPiCpuTemperatureListUpdater;
import me.retrodaredevil.solarthing.misc.error.ImmutableExceptionErrorPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.handling.PacketHandlerMultiplexer;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiverMultiplexer;
import me.retrodaredevil.solarthing.program.modbus.ModbusCacheSlave;
import me.retrodaredevil.solarthing.solar.renogy.rover.*;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.RoverModbusSlaveRead;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.RoverModbusSlaveWrite;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoverMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoverMain.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();
	private static final String MODBUS_RUNTIME_EXCEPTION_CATCH_LOCATION_IDENTIFIER = "rover.read.modbus";
	private static final String MODBUS_RUNTIME_INSTANCE_IDENTIFIER = "instance.1";

	private static final SerialConfig ROVER_CONFIG = new SerialConfigBuilder(9600)
			.setDataBits(8)
			.setParity(SerialConfig.Parity.NONE)
			.setStopBits(SerialConfig.StopBits.ONE)
			.build();

	private static int doRover(RoverProgramOptions options, PacketListReceiver packetListReceiver){
		return doRoverProgram(options, (read, write, reloadCache) -> {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					final long startTime = System.currentTimeMillis();
					final RoverStatusPacket packet;
					try {
						reloadCache.run();
						packet = RoverStatusPackets.createFromReadTable(read);
					} catch(ModbusRuntimeException e){
						LOGGER.error("Modbus exception", e);

						if (options.isSendErrorPackets()) {
							List<Packet> packets = new ArrayList<>();
							LOGGER.debug("Sending error packets");
							packets.add(new ImmutableExceptionErrorPacket(
									e.getClass().getName(),
									e.getMessage(),
									MODBUS_RUNTIME_EXCEPTION_CATCH_LOCATION_IDENTIFIER,
									MODBUS_RUNTIME_INSTANCE_IDENTIFIER
							));
							packetListReceiver.receive(packets, true);
						}

						//noinspection BusyWait
						Thread.sleep(5000);
						continue;
					}
					LOGGER.debug("Debugging special power control values: (Will debug all packets later)\n" +
							packet.getSpecialPowerControlE021().getFormattedInfo().replaceAll("\n", "\n\t") + "\n" +
							packet.getSpecialPowerControlE02D().getFormattedInfo().replaceAll("\n", "\n\t")
					);
					List<Packet> packets = new ArrayList<>();
					packets.add(packet);
					final long readDuration = System.currentTimeMillis() - startTime;
					LOGGER.debug("took " + readDuration + "ms to read from Rover");
					final long saveStartTime = System.currentTimeMillis();
					packetListReceiver.receive(packets, true);
					final long saveDuration = System.currentTimeMillis() - saveStartTime;
					LOGGER.debug("took " + saveDuration + "ms to handle packets");
					//noinspection BusyWait
					Thread.sleep(Math.max(1000, 6000 - readDuration)); // sleep between 1 and 6 seconds
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}, modbusCacheSlave -> {
			modbusCacheSlave.cacheRangeInclusive(0x000A, 0x001A);
			modbusCacheSlave.cacheRangeInclusive(0x0100, 0x0121);
			modbusCacheSlave.cacheRangeInclusive(0xE002, 0xE02D);
		});
	}

	public static int connectRover(RoverProgramOptions options) {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Beginning rover program");
		PacketHandlerBundle packetHandlerBundle = PacketHandlerInit.getPacketHandlerBundle(SolarMain.getDatabaseConfigs(options), SolarThingConstants.SOLAR_STATUS_UNIQUE_NAME, SolarThingConstants.SOLAR_EVENT_UNIQUE_NAME);
		boolean rpiCpuTemperature = options.getExtraOptionFlags().contains(ExtraOptionFlag.RPI_LOG_CPU_TEMPERATURE);
		// TODO packetHandlerBundle.getEventPacketHandlers()
		PacketListReceiver sourceAndFragmentUpdater = SolarMain.getSourceAndFragmentUpdater(options);

		PacketCollectionIdGenerator idGenerator = SolarMain.createIdGenerator(options.getUniqueIdsInOneHour());
		PacketListReceiverHandler statusPacketListReceiverHandler = new PacketListReceiverHandler(
				new PacketListReceiverMultiplexer(
						sourceAndFragmentUpdater,
						(packets, wasInstant) -> {
							LOGGER.debug("Debugging all packets");
							try {
								LOGGER.debug(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(packets));
							} catch (JsonProcessingException e) {
								LOGGER.debug("Never mind about that...", e);
							}
						}
				),
				new PacketHandlerMultiplexer(packetHandlerBundle.getStatusPacketHandlers()),
				idGenerator
		);
		List<PacketListReceiver> packetListReceiverList = new ArrayList<>();
		if(rpiCpuTemperature){
			packetListReceiverList.add(new RaspberryPiCpuTemperatureListUpdater());
		}
		packetListReceiverList.addAll(Arrays.asList(
				statusPacketListReceiverHandler.getPacketListReceiverAccepter(),
				statusPacketListReceiverHandler.getPacketListReceiverPacker(),
//				eventPacketListReceiverHandler.getPacketListReceiverPacker(),
				statusPacketListReceiverHandler.getPacketListReceiverHandler()
//				eventPacketListReceiverHandler.getPacketListReceiverHandler()
		));
		return doRover(options, new PacketListReceiverMultiplexer(packetListReceiverList));
	}
	public static int connectRoverSetup(RoverSetupProgramOptions options) {
		return doRoverProgram(options, RoverSetupProgram::startRoverSetup, null);
	}
	private static int doRoverProgram(RoverOption options, RoverProgramRunner runner, RegisterCacheHandler registerCacheHandler) {
		File dummyFile = options.getDummyFile();
		if(dummyFile != null){
			final FileInputStream fileInputStream;
			try {
				fileInputStream = new FileInputStream(dummyFile);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("The dummy file was not found!", e);
			}
			final RoverStatusPacket roverStatusPacket;
			try {
				roverStatusPacket = MAPPER.readValue(fileInputStream, RoverStatusPacket.class);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			DummyRoverReadWrite readWrite = new DummyRoverReadWrite(
					roverStatusPacket,
					(fieldName, previousValue, newValue) -> System.out.println(fieldName + " changed from " + previousValue + " to " + newValue)
			);
			runner.doProgram(readWrite, readWrite, () -> {});
			return 0;
		} else {
			try(IOBundle ioBundle = SolarMain.createIOBundle(options.getIOBundleFile(), ROVER_CONFIG)) {
				ModbusSlaveBus modbus = new IOModbusSlaveBus(ioBundle, new RTUDataEncoder(2000, 20, 4));
				ModbusSlave slave = new ImmutableAddressModbusSlave(options.getModbusAddress(), modbus);
				final RoverReadTable read;
				final Runnable reloadCache;
				if (registerCacheHandler != null) {
					ModbusCacheSlave modbusCacheSlave = new ModbusCacheSlave(slave);
					read = new RoverModbusSlaveRead(modbusCacheSlave);
					reloadCache = () -> registerCacheHandler.cacheRegisters(modbusCacheSlave);
				} else {
					read = new RoverModbusSlaveRead(slave);
					reloadCache = () -> {};
				}
				RoverWriteTable write = new RoverModbusSlaveWrite(slave);
				runner.doProgram(read, write, reloadCache);
				return 0;
			} catch (Exception e) {
				LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Got exception!", e);
				return 1;
			}
		}
	}
	@FunctionalInterface
	private interface RoverProgramRunner {
		void doProgram(RoverReadTable read, RoverWriteTable write, Runnable reloadCache);
	}
	@FunctionalInterface
	private interface RegisterCacheHandler {
		void cacheRegisters(ModbusCacheSlave modbusCacheSlave);
	}
}
