package me.retrodaredevil.solarthing.config.options;

import com.lexicalscope.jewel.cli.Option;

public interface MasterOption {
	@Option(shortName = "h", longName = "help", helpRequest = true)
	boolean isHelp();
}
