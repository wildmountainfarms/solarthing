package me.retrodaredevil.solarthing.config.options;

import com.google.gson.annotations.SerializedName;

import java.io.File;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("FieldCanBeLocal")
public class RoverProgramOptions extends PacketHandlingOptionBase implements RoverOption {
	@SerializedName(value = "modbus")
	private int modbusAddress = 1;
	@SerializedName("dummy")
	private File dummyFile = null;
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
