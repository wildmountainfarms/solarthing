package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;

import java.io.File;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("FieldCanBeLocal")
@JsonTypeName("mate")
public class MateProgramOptions extends PacketHandlingOptionBase implements IOBundleOption, ProgramOptions {

	@JsonProperty("allow_commands")
	private boolean allowCommands = false;
	@JsonProperty("ignore_check_sum")
	private boolean ignoreCheckSum = false;
	@JsonProperty("correct_check_sum")
	private boolean correctCheckSum = false;

	@JsonProperty("io")
	private File io;
	
	public boolean isAllowCommands() {
		return allowCommands;
	}
	
	public boolean isIgnoreCheckSum() {
		return ignoreCheckSum;
	}
	public boolean isCorrectCheckSum() {
		return correctCheckSum;
	}

	@Override
	public File getIOBundleFile() {
		return requireNonNull(io, "io is required!");
	}

	public static IgnoreCheckSum getIgnoreCheckSum(MateProgramOptions options) {
		if(options.isCorrectCheckSum()){
			return IgnoreCheckSum.IGNORE_AND_USE_CALCULATED;
		} else if(options.isIgnoreCheckSum()){
			return IgnoreCheckSum.IGNORE;
		}
		return IgnoreCheckSum.DISABLED;
	}

	@Override
	public ProgramType getProgramType() {
		return ProgramType.MATE;
	}
}
