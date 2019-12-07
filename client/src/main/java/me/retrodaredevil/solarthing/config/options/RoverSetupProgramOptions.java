package me.retrodaredevil.solarthing.config.options;

import com.google.gson.annotations.SerializedName;

import java.io.File;

@SuppressWarnings("FieldCanBeLocal")
public class RoverSetupProgramOptions implements RoverOption {
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
		return io;
	}

}
