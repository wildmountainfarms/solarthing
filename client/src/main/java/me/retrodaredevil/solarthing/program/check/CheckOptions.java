package me.retrodaredevil.solarthing.program.check;

import com.lexicalscope.jewel.cli.Option;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface CheckOptions {
	@Option(shortName = "h", longName = "help", helpRequest = true)
	boolean isHelp();

	@Option(longName = "port", defaultToNull = true, description = "The path to the serial port")
	@Nullable String getPort();

	@Option(longName = "type", defaultToNull = true, description = "The type of device to look for [mate|rover|tracer]", pattern = "mate|rover|tracer")
	@Nullable String getType();

	@Option(longName = "modbus", defaultToNull = true, description = "The modbus address if using the rover or tracer type. Defaults to 1 if not set")
	@Nullable Integer getModbusAddress();

	@Option(longName = "scan", description = "Set this flag if you want to scan multiple modbus addresses. Starts at the value set from --modbus")
	boolean isScan();

	@Option(longName = "debug", description = "Set this flag if you want to debug all possible data.")
	boolean isDebugTable();
}
