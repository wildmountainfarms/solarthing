package me.retrodaredevil.solarthing.program.check;

import com.lexicalscope.jewel.cli.Option;

public interface CheckOptions {
	@Option(shortName = "h", longName = "help", helpRequest = true)
	boolean isHelp();

	@Option(longName = "port", defaultToNull = true, description = "The path to the serial port")
	String getPort();

	@Option(longName = "type", defaultToNull = true, description = "The type of device to look for [mate|rover|tracer]")
	String getType();
}
