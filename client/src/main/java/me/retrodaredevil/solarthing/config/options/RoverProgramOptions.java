package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.Nullable;

import java.io.File;
import java.util.List;

@JsonTypeName("rover")
public class RoverProgramOptions extends RequestProgramOptionsBase implements RoverOption, CommandOption {
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

	@JsonProperty("commands")
	private List<CommandConfig> commandConfigs;

	@Override
	public @Nullable List<CommandConfig> getDeclaredCommandsNullable() {
		return commandConfigs;
	}

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
