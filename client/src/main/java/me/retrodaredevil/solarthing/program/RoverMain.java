package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.io.modbus.IOModbusSlaveBus;
import me.retrodaredevil.io.modbus.ModbusSlaveBus;
import me.retrodaredevil.io.modbus.RtuDataEncoder;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.config.io.IOConfig;
import me.retrodaredevil.solarthing.config.options.RoverOption;
import me.retrodaredevil.solarthing.config.options.RoverSetupProgramOptions;
import me.retrodaredevil.solarthing.io.ReloadableIOBundle;
import me.retrodaredevil.solarthing.program.modbus.MutableAddressModbusSlave;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverWriteTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.RoverModbusSlaveRead;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.RoverModbusSlaveWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoverMain {
	private RoverMain() { throw new UnsupportedOperationException(); }
	private static final Logger LOGGER = LoggerFactory.getLogger(RoverMain.class);


	public static int connectRoverSetup(RoverSetupProgramOptions options) {
		return doRoverProgram(options, RoverSetupProgram::startRoverSetup);
	}
	private static int doRoverProgram(RoverOption options, RoverProgramRunner runner) {
		IOConfig ioConfig = ConfigUtil.parseIOConfig(options.getIOBundleFile(), RoverReadTable.SERIAL_CONFIG);
		try(ReloadableIOBundle ioBundle = new ReloadableIOBundle(ioConfig::createIOBundle)) {
			ModbusSlaveBus modbus = new IOModbusSlaveBus(ioBundle, new RtuDataEncoder(2000, 20, 4));
			MutableAddressModbusSlave slave = new MutableAddressModbusSlave(options.getModbusAddress(), modbus);
			RoverReadTable read = new RoverModbusSlaveRead(slave);
			RoverWriteTable write = new RoverModbusSlaveWrite(slave);
			return runner.doProgram(slave, read, write, () -> {}, ioBundle::reload);
		} catch (Exception e) {
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Got exception!", e);
			return SolarThingConstants.EXIT_CODE_CRASH;
		}
	}
	@FunctionalInterface
	private interface RoverProgramRunner {
		int doProgram(@Nullable MutableAddressModbusSlave modbusSlave, RoverReadTable read, RoverWriteTable write, Runnable reloadCache, Runnable reloadIO);
	}
}
