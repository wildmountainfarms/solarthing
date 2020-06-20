package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.File;

@JsonTypeName("rover")
public class RoverProgramOptions extends RequestProgramOptionsBase implements RoverOption {
	@JsonProperty("modbus")
	private int modbusAddress = 1;
	@JsonProperty("dummy")
	private File dummyFile = null;
	@JsonProperty(value = "io")
	private File io;

	@JsonProperty("error_packets")
	private boolean sendErrorPackets = false;

	@JsonProperty("bulk_request")
	private boolean bulkRequest = true;

	public boolean isSendErrorPackets() {
		return sendErrorPackets;
	}

	public boolean isBulkRequest() {
		return bulkRequest;
	}

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
		File io = this.io;
		if(io == null){
			if(dummyFile == null){
				throw new IllegalStateException("(Configuration error) Both 'io' and 'dummy' are null or unspecified. You must define one!");
			} else {
				throw new IllegalStateException("(Program error) 'io' is null! 'dummy' is not null! You should use 'dummy'!");
			}
		}
		return io;
	}

	@Override
	public ProgramType getProgramType() {
		return ProgramType.ROVER;
	}

}
