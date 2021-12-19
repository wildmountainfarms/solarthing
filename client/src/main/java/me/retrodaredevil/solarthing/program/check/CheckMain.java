package me.retrodaredevil.solarthing.program.check;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.serial.JSerialIOBundle;
import me.retrodaredevil.io.serial.SerialPortException;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.handling.implementations.TimedPacketReceiver;
import me.retrodaredevil.solarthing.program.SolarReader;
import me.retrodaredevil.solarthing.solar.outback.MatePacketCreator49;
import me.retrodaredevil.solarthing.solar.outback.OutbackConstants;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;

import java.io.EOFException;
import java.io.IOException;
import java.time.Duration;

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
		try(IOBundle ioBundle = JSerialIOBundle.createPort(port, OutbackConstants.MATE_CONFIG)) {
			System.out.println("Successfully opened serial port...");

			boolean[] gotAnyData = new boolean[] { false };
			boolean[] parsedData = new boolean[] { false };

			PacketListReceiver receiver = (packets, instantType) -> {
				System.out.println("Got " + packets.size() + " packets");
				parsedData[0] = true;
			};
			// TODO I tried to make the check program not log anything using loggers because the goal is to only output to the terminal screen.
			//   However, stuff in some of these classes ends up debug logging. One solution might be to make the solarthing command use a different log4j2.xml.
			//   Just something to think about
			SolarReader solarReader = new SolarReader(
					ioBundle.getInputStream(),
					new MatePacketCreator49(IgnoreCheckSum.IGNORE_AND_USE_CALCULATED), // we are testing a lot, so we don't care if the checksum is wrong
					new TimedPacketReceiver(Duration.ofMillis(250), receiver, (firstData, instantType) -> gotAnyData[0] = true)
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
		} catch (SerialPortException e) {
			throw e;
		} catch (Exception e) {
			throw new AssertionError("JSerialIOBundle's close should not throw checked exceptions!", e);
		}
	}

	private static int doForOptions(@NotNull String port, @Nullable String type) throws SerialPortException {
		if (type == null) {
			// if not type specified, search throw all possible devices
			System.err.println("Not implemented yet!");
			return 0;
		} else {
			if (type.equals("mate")) {
				scanForMate(port);
			} else {

			}
		}
		return 0;
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
			return doForOptions(port, type);
		} catch (SerialPortException e) {
			System.err.println("Could not open serial port! Is it connected? Is '" + port + "' correct?");
			System.err.println("Message: " + e.getMessage());
			return SolarThingConstants.EXIT_CODE_FAIL;
		}
	}
}
