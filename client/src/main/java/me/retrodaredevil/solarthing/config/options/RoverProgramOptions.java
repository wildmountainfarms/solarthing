package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.File;

import static java.util.Objects.requireNonNull;

@JsonTypeName("rover")
public class RoverProgramOptions extends PacketHandlingOptionBase implements RoverOption {
	@JsonProperty("modbus")
	private int modbusAddress = 1;
	@JsonProperty("dummy")
	private File dummyFile = null;
	@JsonProperty(value = "io", required = true)
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
		return requireNonNull(io, "io is required!");
	}

}
