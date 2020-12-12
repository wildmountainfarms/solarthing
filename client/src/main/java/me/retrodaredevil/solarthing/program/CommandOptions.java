package me.retrodaredevil.solarthing.program;

import com.lexicalscope.jewel.cli.Option;
import com.lexicalscope.jewel.cli.Unparsed;

import java.io.File;
import java.util.List;

public interface CommandOptions {
	@Option(shortName = "h", longName = "help", helpRequest = true)
	boolean isHelp();
	@Option(longName = "base", defaultToNull = true, description = "Use alone to define the base configuration file")
	File getBaseConfigFile();

	@Option(longName = "couchdb-setup", defaultToNull = true, description = "Use alone to define the CouchDB configuration file and launch the CouchDB setup program")
	File getCouchDbSetupFile();

	@Option(longName = "from", defaultToNull = true, description = "Use with --base and a pvoutput-upload program type")
	String getPVOutputFromDate();
	@Option(longName = "to", defaultToNull = true, description = "Use with --base and a pvoutput-upload program type")
	String getPVOutputToDate();

	@Unparsed(name = "LEGACY ARGUMENTS")
	List<String> getLegacyOptions();
}
