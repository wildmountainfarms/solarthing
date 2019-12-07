package me.retrodaredevil.solarthing.config.options;

import java.io.File;

import static java.util.Objects.requireNonNull;

public class RoverProgramOptions extends PacketHandlingOptionBase implements RoverOption {
	private int modbusAddress = 1;
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
