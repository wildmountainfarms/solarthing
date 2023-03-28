package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

@JsonTypeName("rover-setup")
@JsonExplicit
public class RoverSetupProgramOptions implements ProgramOptions, RoverOption {
	@JsonProperty("modbus")
	private int modbusAddress = 1;
	@JsonProperty(value = "io", required = true)
	private Path io;

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
