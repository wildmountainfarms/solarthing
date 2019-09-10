package me.retrodaredevil.solarthing.config.options;

import com.lexicalscope.jewel.cli.Option;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;

public interface MateProgramOptions extends PacketHandlingOption, IOBundleOption, MasterOption {
	
	@Option(longName = "allow-commands")
	boolean isAllowCommands();
	
	@Option(longName = "ignore-check-sum", description = "If toggled, the program will not check the checksum and will use an incorrect checksum if the checksum is incorrect. Almost never recommended.")
	boolean isIgnoreCheckSum();
	@Option(longName = {"cc", "correct-checksum"}, description = "If toggled, the program will not check the checksum and will use a calculated checksum that is correct. Recommended for testing mock output.")
	boolean isCorrectCheckSum();
	
	
	default IgnoreCheckSum getIgnoreCheckSum() {
		if(isCorrectCheckSum()){
			return IgnoreCheckSum.IGNORE_AND_USE_CALCULATED;
		} else if(isIgnoreCheckSum()){
			return IgnoreCheckSum.IGNORE;
		}
		return IgnoreCheckSum.DISABLED;
	}
}
