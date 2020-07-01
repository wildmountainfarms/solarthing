package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.modbus.*;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.io.serial.SerialConfigBuilder;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.analytics.AnalyticsManager;
import me.retrodaredevil.solarthing.config.options.*;
import me.retrodaredevil.solarthing.config.request.DataRequester;
import me.retrodaredevil.solarthing.config.request.RaspberryPiCpuTemperatureDataRequester;
import me.retrodaredevil.solarthing.program.modbus.ModbusCacheSlave;
import me.retrodaredevil.solarthing.solar.renogy.rover.DummyRoverReadWrite;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverWriteTable;
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
import java.util.List;

public class RoverMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoverMain.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	public static final SerialConfig ROVER_CONFIG = new SerialConfigBuilder(9600)
			.setDataBits(8)
			.setParity(SerialConfig.Parity.NONE)
			.setStopBits(SerialConfig.StopBits.ONE)
			.build();

	private static int doRover(RoverProgramOptions options, AnalyticsManager analyticsManager, List<DataRequester> dataRequesterList){
		return doRoverProgram(options, (read, write, reloadCache) -> {
			List<DataRequester> list = new ArrayList<>(dataRequesterList);
			list.add((o) -> new RoverPacketListUpdater(read, write, reloadCache, options.isSendErrorPackets()));
			return RequestMain.startRequestProgram(options, analyticsManager, list, options.getPeriod(), options.getMinimumWait());
		}, options.isBulkRequest() ? modbusCacheSlave -> {
//			modbusCacheSlave.cacheRangeInclusive(0x000A, 0x001A);
//			modbusCacheSlave.cacheRangeInclusive(0x0100, 0x0122);
//			modbusCacheSlave.cacheRangeInclusive(0xE002, 0xE02D);

			modbusCacheSlave.cacheRangeInclusive(0x000A, 0x001A);

			modbusCacheSlave.cacheRangeInclusive(0x0100, 0x0109); // skip 0x010A
			modbusCacheSlave.cacheRangeInclusive(0x010B, 0x0122);

			modbusCacheSlave.cacheRangeInclusive(0xE002, 0xE014);
			// break here just because they're for different things
			modbusCacheSlave.cacheRangeInclusive(0xE015, 0xE021);
//			modbusCacheSlave.cacheRangeInclusive(0xE022, 0xE02D); these do not work when querying in bulk for some reason
		} : null);
	}

	public static int connectRover(RoverProgramOptions options, File dataDirectory) {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Beginning rover program");
		AnalyticsManager analyticsManager = new AnalyticsManager(options.isAnalyticsEnabled(), dataDirectory);
		analyticsManager.sendStartUp(ProgramType.ROVER);
		boolean rpiCpuTemperature = options.getExtraOptionFlags().contains(ExtraOptionFlag.RPI_LOG_CPU_TEMPERATURE);

		List<DataRequester> dataRequesterList = new ArrayList<>();
		if(rpiCpuTemperature){
			dataRequesterList.add(new RaspberryPiCpuTemperatureDataRequester());
		}
		dataRequesterList.addAll(options.getDataRequesterList());
		return doRover(options, analyticsManager, dataRequesterList);
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
				ModbusSlaveBus modbus = new IOModbusSlaveBus(ioBundle, new RtuDataEncoder(2000, 20, 4));
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
				return runner.doProgram(read, write, reloadCache);
			} catch (Exception e) {
				LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Got exception!", e);
				return 1;
			}
		}
	}
	@FunctionalInterface
	private interface RoverProgramRunner {
		int doProgram(RoverReadTable read, RoverWriteTable write, Runnable reloadCache);
	}
	@FunctionalInterface
	private interface RegisterCacheHandler {
		void cacheRegisters(ModbusCacheSlave modbusCacheSlave);
	}
}
