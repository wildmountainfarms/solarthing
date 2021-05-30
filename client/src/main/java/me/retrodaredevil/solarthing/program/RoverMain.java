package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.io.modbus.IOModbusSlaveBus;
import me.retrodaredevil.io.modbus.ModbusSlaveBus;
import me.retrodaredevil.io.modbus.RtuDataEncoder;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.analytics.AnalyticsManager;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.commands.CommandInfo;
import me.retrodaredevil.solarthing.config.io.IOConfig;
import me.retrodaredevil.solarthing.config.options.ProgramType;
import me.retrodaredevil.solarthing.config.options.RoverOption;
import me.retrodaredevil.solarthing.config.options.RoverProgramOptions;
import me.retrodaredevil.solarthing.config.options.RoverSetupProgramOptions;
import me.retrodaredevil.solarthing.config.request.DataRequester;
import me.retrodaredevil.solarthing.config.request.modbus.ModbusDataRequester;
import me.retrodaredevil.solarthing.config.request.modbus.ModbusRequester;
import me.retrodaredevil.solarthing.config.request.modbus.RoverModbusRequester;
import me.retrodaredevil.solarthing.io.ReloadableIOBundle;
import me.retrodaredevil.solarthing.program.modbus.ModbusCacheSlave;
import me.retrodaredevil.solarthing.program.modbus.MutableAddressModbusSlave;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverWriteTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.RoverModbusSlaveRead;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.RoverModbusSlaveWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoverMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoverMain.class);


	private static int doRover(RoverProgramOptions options, AnalyticsManager analyticsManager, List<DataRequester> dataRequesterList) throws Exception {
		RoverModbusRequester roverModbusRequester = new RoverModbusRequester(
				options.isSendErrorPackets(), options.isBulkRequest(),
				options.getCommandInfoList().stream().map(CommandInfo::getName).collect(Collectors.toList()), // attach the given rover modbus environment to all commands
                null);
		Map<Integer, ModbusRequester> deviceMap = new HashMap<>();
		deviceMap.put(options.getModbusAddress(), roverModbusRequester);
		ModbusDataRequester dataRequester = new ModbusDataRequester(options.getIOBundleFile(), deviceMap);

		List<DataRequester> list = new ArrayList<>(dataRequesterList);
		list.add(dataRequester);

		return RequestMain.startRequestProgram(options, analyticsManager, list, options.getPeriod(), options.getMinimumWait());
	}

	public static int connectRover(RoverProgramOptions options, File dataDirectory) throws Exception {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Beginning rover program");
		AnalyticsManager analyticsManager = new AnalyticsManager(options.isAnalyticsEnabled(), dataDirectory);
		analyticsManager.sendStartUp(ProgramType.ROVER);

		List<DataRequester> dataRequesterList = new ArrayList<>(options.getDataRequesterList());
		return doRover(options, analyticsManager, dataRequesterList);
	}
	public static int connectRoverSetup(RoverSetupProgramOptions options) {
		return doRoverProgram(options, RoverSetupProgram::startRoverSetup, null);
	}
	private static int doRoverProgram(RoverOption options, RoverProgramRunner runner, @Nullable RegisterCacheHandler registerCacheHandler) {
		IOConfig ioConfig = ConfigUtil.parseIOConfig(options.getIOBundleFile(), RoverReadTable.SERIAL_CONFIG);
		try(ReloadableIOBundle ioBundle = new ReloadableIOBundle(ioConfig::createIOBundle)) {
			ModbusSlaveBus modbus = new IOModbusSlaveBus(ioBundle, new RtuDataEncoder(2000, 20, 4));
			MutableAddressModbusSlave slave = new MutableAddressModbusSlave(options.getModbusAddress(), modbus);
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
			return runner.doProgram(slave, read, write, reloadCache, ioBundle::reload);
		} catch (Exception e) {
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Got exception!", e);
			return 1;
		}
	}
	@FunctionalInterface
	private interface RoverProgramRunner {
		int doProgram(@Nullable MutableAddressModbusSlave modbusSlave, RoverReadTable read, RoverWriteTable write, Runnable reloadCache, Runnable reloadIO);
	}
	@FunctionalInterface
	private interface RegisterCacheHandler {
		void cacheRegisters(ModbusCacheSlave modbusCacheSlave);
	}
}
