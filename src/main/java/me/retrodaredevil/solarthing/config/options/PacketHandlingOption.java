package me.retrodaredevil.solarthing.config.options;

import com.lexicalscope.jewel.cli.Option;

import java.io.File;

public interface PacketHandlingOption {
	@Option(longName = {"couch", "couchdb"}, defaultToNull = true, description = "File path to CouchDB connection configuration")
	File getCouchPropertiesFile();
	
	@Option(longName = {"latest", "latest-save-location"}, defaultToNull = true)
	File getLatestPacketJsonSaveLocation();
	
	@Option(longName = "source", defaultValue = { "default" })
	String getSourceId();
	@Option(longName = "fragment", defaultToNull = true, minimum = 1)
	Integer getFragmentId();
	
	@Option(longName = {"unique", "unique-ids-in-one-hour"}, defaultToNull = true)
	Integer getUniqueIdsInOneHour();
}
