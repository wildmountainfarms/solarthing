package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import java.io.File;

@JsonTypeName("rover-setup")
@JsonExplicit
public class RoverSetupProgramOptions implements ProgramOptions, RoverOption {
	@JsonProperty("modbus")
	private int modbusAddress = 1;
	@JsonProperty("dummy")
	private File dummyFile = null;
	@JsonProperty("io")
	private File io;

	@Override
	public int getModbusAddress() {
		return modbusAddress;
	}

	@Override
	public File getDummyFile() {
		return dummyFile;
	}

	@Override
	public File getIOBundleFile() {
		return io;
	}

	@Override
	public ProgramType getProgramType() {
		return ProgramType.ROVER_SETUP;
	}
}
