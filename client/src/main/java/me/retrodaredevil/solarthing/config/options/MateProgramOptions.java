package me.retrodaredevil.solarthing.config.options;

import com.google.gson.annotations.SerializedName;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;

import java.io.File;

import static java.util.Objects.requireNonNull;

public class MateProgramOptions extends PacketHandlingOptionBase implements IOBundleOption {

	@SerializedName("allow_commands")
	private boolean allowCommands = false;
	@SerializedName("ignore_check_sum")
	private boolean ignoreCheckSum = false;
	@SerializedName("correct_check_sum")
	private boolean correctCheckSum = false;

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
}
