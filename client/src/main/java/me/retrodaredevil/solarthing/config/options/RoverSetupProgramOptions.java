package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
@JsonTypeName("rover-setup")
@JsonExplicit
@NullMarked
public class RoverSetupProgramOptions implements ProgramOptions, RoverOption {
	@JsonProperty("modbus")
	private int modbusAddress = 1;
	@JsonProperty(value = "io", required = true)
	private @Nullable Path io;

	@Override
	public int getModbusAddress() {
		return modbusAddress;
	}

	@Override
	public Path getIOBundleFilePath() {
		return requireNonNull(io);
	}

	@Override
	public ProgramType getProgramType() {
		return ProgramType.ROVER_SETUP;
	}
}
