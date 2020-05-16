package me.retrodaredevil.solarthing.config.options;

import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;

import java.io.File;
import java.util.TimeZone;

/**
 * Represents an option that makes requests to a database
 */
public interface DatabaseOption extends ProgramOptions {
	File getDatabase();

	TimeZone getTimeZone();

	String getSourceId();

	String getDefaultSourceId();

	DefaultInstanceOptions getDefaultInstanceOptions();
}
