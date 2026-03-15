package me.retrodaredevil.solarthing.program.subprogram.run;

import com.lexicalscope.jewel.cli.Option;
import com.lexicalscope.jewel.cli.Unparsed;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface CommandOptions {
	@Option(shortName = "h", longName = "help", helpRequest = true)
	boolean isHelp();
	@Option(longName = "base", defaultToNull = true, description = "Use alone to define the base configuration file")
	@Nullable String getBaseConfigFile();

	@Option(longName = "couchdb-setup", defaultToNull = true, description = "Use alone to define the CouchDB configuration file and launch the CouchDB setup program")
	@Nullable String getCouchDbSetupFile();

	@Option(longName = "from", defaultToNull = true, description = "Use with --base and a pvoutput-upload program type")
	@Nullable String getPVOutputFromDate();
	@Option(longName = "to", defaultToNull = true, description = "Use with --base and a pvoutput-upload program type")
	@Nullable String getPVOutputToDate();

	@Option(longName = "validate", description = "When present, the program does not run but only validates the configuration.")
	boolean isValidate();

	@Unparsed(name = "LEGACY ARGUMENTS")
	List<String> getLegacyOptionsRaw();

	// Note that we cannot use default methods here as jewelcli does not like them
}
