package me.retrodaredevil.solarthing.program.check;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import me.retrodaredevil.io.modbus.IOModbusSlaveBus;
import me.retrodaredevil.io.modbus.ModbusRuntimeException;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.modbus.ModbusSlaveBus;
import me.retrodaredevil.io.modbus.ModbusTimeoutException;
import me.retrodaredevil.io.modbus.RtuDataEncoder;
import me.retrodaredevil.io.serial.JSerialIOBundle;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.io.serial.SerialPortException;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.handling.implementations.TimedPacketReceiver;
import me.retrodaredevil.solarthing.program.SolarReader;
import me.retrodaredevil.solarthing.program.modbus.MutableAddressModbusSlave;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;
import me.retrodaredevil.solarthing.solar.outback.MatePacketCreator49;
import me.retrodaredevil.solarthing.solar.outback.OutbackConstants;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.RoverModbusSlaveRead;
import me.retrodaredevil.solarthing.solar.tracer.TracerReadTable;
import me.retrodaredevil.solarthing.solar.tracer.modbus.TracerModbusSlaveRead;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;

import java.io.EOFException;
import java.io.IOException;
import java.time.Duration;
import java.util.function.Function;

/**
 * Contains code to execute the "check" subprogram. This program is designed to take very few arguments and
 * tell the user if a serial port is giving them good data.
 */
@UtilityClass
public class CheckMain {
	private CheckMain() { throw new UnsupportedOperationException(); }

	/*
	Useful command when testing this:
	socat -d -d pty,raw,echo=0 pty,raw,echo=0
	 */

	private static boolean scanForMate(String port) throws SerialPortException {

		System.out.println("Going to open serial port using MATE's default serial configuration...");
		try(JSerialIOBundle ioBundle = JSerialIOBundle.createPort(port, OutbackConstants.MATE_CONFIG)) {
			System.out.println("Successfully opened serial port...");

			boolean[] gotAnyData = new boolean[] { false };
			boolean[] parsedData = new boolean[] { false };

			PacketListReceiver receiver = (packets) -> {
				System.out.println("Got " + packets.size() + " packets");
				parsedData[0] = true;
			};
			// Note that lots of this code logs. Users don't see much of the debug logs because they run the solarthing command, which does INFO logging by default
			SolarReader solarReader = new SolarReader(
					ioBundle.getInputStream(),
					new MatePacketCreator49(IgnoreCheckSum.IGNORE_AND_USE_CALCULATED), // we are testing a lot, so we don't care if the checksum is wrong
					new TimedPacketReceiver(Duration.ofMillis(250), receiver, (firstData, stale) -> gotAnyData[0] = true)
			);
			try {
				for (int i = 0; i < 40; i++) { // do this for about 4 seconds
					try {
						solarReader.update();
					} catch (EOFException e) {
						// This should never happen
						System.err.println("The serial port gave us an EOF. This is unexpected");
						return false;
					} catch (IOException e) {
						System.err.println("Error reading from serial port");
						return false;
					}
					Thread.sleep(100);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
				return false;
			}
			if (parsedData[0]) {
				System.out.println("Got data from mate!");
			} else if (gotAnyData[0]) {
				System.out.println("Got some data, but was unable to parse it.");
			} else {
				System.out.println("Got no data from mate!");
			}
			return parsedData[0];
		}
	}
	private static boolean doModbus(@NotNull String port, int startingAddress, boolean scan, SerialConfig serialConfig, Function<ModbusSlave, BatteryVoltage> slaveToReadTable) throws SerialPortException {
		System.out.println("Going to open serial port using default serial configuration...");
		try(JSerialIOBundle ioBundle = JSerialIOBundle.createPort(port, serialConfig)) {
			System.out.println("Successfully opened serial port...");
			ModbusSlaveBus bus = new IOModbusSlaveBus(ioBundle, new RtuDataEncoder());
			MutableAddressModbusSlave modbusSlave = new MutableAddressModbusSlave(startingAddress, bus);
			BatteryVoltage readTable = slaveToReadTable.apply(modbusSlave);
			int maxAddress = scan ? 247 : startingAddress;
			for (int currentAddress = startingAddress; currentAddress <= maxAddress; currentAddress++) {
				modbusSlave.setAddress(currentAddress);
				System.out.println("Checking on address: " + currentAddress);
				try {
					float batteryVoltage = readTable.getBatteryVoltage();
					System.out.println("Success! Battery Voltage: " + batteryVoltage);
					return true;
				} catch (ModbusTimeoutException e) {
					System.err.println("Got timeout. This means that the modbus address is incorrect or that the cable is not functioning properly.");
				} catch (ModbusRuntimeException e) {
					e.printStackTrace();
					System.err.println("Got some sort of modbus error. Info logged above");
				}
			}
			System.err.println("Did not find a device");
			return false;
		}
	}
	private static boolean scanForRover(@NotNull String port, int modbusAddress, boolean scan) throws SerialPortException {
		return doModbus(port, modbusAddress, scan, RoverReadTable.SERIAL_CONFIG, RoverModbusSlaveRead::new);
	}
	private static boolean scanForTracer(@NotNull String port, int modbusAddress, boolean scan) throws SerialPortException {
		return doModbus(port, modbusAddress, scan, TracerReadTable.SERIAL_CONFIG, TracerModbusSlaveRead::new);
	}

	private static int doForOptions(@NotNull String port, @Nullable String type, @Nullable Integer modbusAddress, boolean scan) throws SerialPortException {
		if (type == null) {
			if (scan) {
				System.err.println("Scan is not supported when searching through all possible options");
				return SolarThingConstants.EXIT_CODE_INVALID_OPTIONS;
			}
			if (scanForMate(port)) {
				return 0;
			}
			int address = modbusAddress == null ? 1 : modbusAddress;
			if (scanForRover(port, address, false)) {
				return 0;
			}
			if (scanForTracer(port, address, false)) {
				return 0;
			}
			System.err.println("Did not detect any devices.");
			return SolarThingConstants.EXIT_CODE_FAIL;
		} else {
			if (type.equals("mate")) {
				if (modbusAddress != null) {
					System.err.println("Modbus address is not supported for mate");
					return SolarThingConstants.EXIT_CODE_INVALID_OPTIONS;
				}
				if (scan) {
					System.err.println("Scan is not supported for mate");
					return SolarThingConstants.EXIT_CODE_INVALID_OPTIONS;
				}
				if (scanForMate(port)) {
					return 0;
				}
				return SolarThingConstants.EXIT_CODE_FAIL;
			} else {
				int address = modbusAddress == null ? 1 : modbusAddress;
				if (type.equals("rover")) {
					if (scanForRover(port, address, scan)) {
						return 0;
					}
					return SolarThingConstants.EXIT_CODE_FAIL;
				} else if (type.equals("tracer")) {
					if (scanForTracer(port, address, scan)) {
						return 0;
					}
					return SolarThingConstants.EXIT_CODE_FAIL;

				} else {
					System.err.println("Unknown option: " + type);
					return SolarThingConstants.EXIT_CODE_INVALID_OPTIONS;
				}
			}
		}
	}

	public static int doCheck(String[] args) {
		Cli<CheckOptions> cli = CliFactory.createCli(CheckOptions.class);
		final CheckOptions options;
		try {
			options = cli.parseArguments(args);
		} catch (ArgumentValidationException ex) {
			if (ex instanceof HelpRequestedException) {
				System.out.println(cli.getHelpMessage());
				return 0;
			}
			System.err.println(cli.getHelpMessage());
			return SolarThingConstants.EXIT_CODE_INVALID_OPTIONS;
		}
		String port = options.getPort();
		if (port == null) {
			System.err.println("Must specify a port!");
			return SolarThingConstants.EXIT_CODE_INVALID_OPTIONS;
		}
		String type = options.getType();
		try {
			return doForOptions(port, type, options.getModbusAddress(), options.isScan());
		} catch (SerialPortException e) {
			System.err.println("Could not open serial port! Is it connected? Is '" + port + "' correct?");
			System.err.println("Message: " + e.getMessage());
			return SolarThingConstants.EXIT_CODE_FAIL;
		}
	}
}
