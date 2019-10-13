package me.retrodaredevil.solarthing.config.options;

import com.lexicalscope.jewel.cli.Option;

import java.io.File;
import java.util.List;

public interface PacketHandlingOption {
	
	@Option(longName = {"database", "databases"}, defaultValue = {}, description = "A list of configuration files representing database configurations")
	List<File> getDatabaseConfigurationFiles();
	
	@Option(longName = "source", defaultValue = { "default" }, description = "The source ID representing a way to group packets together")
	String getSourceId();
	@Option(longName = "fragment", defaultToNull = true, minimum = 1, description = "The fragment ID representing a single program instance to differentiate packets with the same source ID")
	Integer getFragmentId();
	
	@Option(longName = {"unique", "unique-ids-in-one-hour"}, defaultToNull = true, description = "By default all packets have a unique ID. When specified limits the number of unique IDs used in one hour")
	Integer getUniqueIdsInOneHour();
}
