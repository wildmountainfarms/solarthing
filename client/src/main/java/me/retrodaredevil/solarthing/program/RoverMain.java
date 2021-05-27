package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.io.modbus.*;
import me.retrodaredevil.solarthing.DataSource;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.InjectEnvironment;
import me.retrodaredevil.solarthing.actions.environment.RoverModbusEnvironment;
import me.retrodaredevil.solarthing.analytics.AnalyticsManager;
import me.retrodaredevil.solarthing.analytics.RoverAnalyticsHandler;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.config.io.IOConfig;
import me.retrodaredevil.solarthing.config.options.*;
import me.retrodaredevil.solarthing.config.request.DataRequester;
import me.retrodaredevil.solarthing.config.request.modbus.ModbusDataRequester;
import me.retrodaredevil.solarthing.config.request.modbus.ModbusRequester;
import me.retrodaredevil.solarthing.config.request.modbus.RoverModbusRequester;
import me.retrodaredevil.solarthing.io.ReloadableIOBundle;
import me.retrodaredevil.solarthing.packets.handling.LatestPacketHandler;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.packets.handling.PacketHandlerMultiplexer;
import me.retrodaredevil.solarthing.program.modbus.ModbusCacheSlave;
import me.retrodaredevil.solarthing.program.modbus.MutableAddressModbusSlave;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoverMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoverMain.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();


	private static int doRover(RoverProgramOptions options, AnalyticsManager analyticsManager, List<DataRequester> dataRequesterList){
		RoverModbusRequester roverModbusRequester = new RoverModbusRequester(options.isSendErrorPackets(), options.isBulkRequest(), options.getDeclaredCommandsNullable());
		Map<Integer, ModbusRequester> deviceMap = new HashMap<>();
		deviceMap.put(options.getModbusAddress(), roverModbusRequester);
		ModbusDataRequester dataRequester = new ModbusDataRequester(options.getIOBundleFile(), deviceMap);

		List<DataRequester> list = new ArrayList<>(dataRequesterList);
		list.add(dataRequester);

		return RequestMain.startRequestProgram(options, analyticsManager, list, options.getPeriod(), options.getMinimumWait());
	}

	public static int connectRover(RoverProgramOptions options, File dataDirectory) {
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
