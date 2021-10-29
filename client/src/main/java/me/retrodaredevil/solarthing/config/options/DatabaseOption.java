package me.retrodaredevil.solarthing.config.options;

import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;

import java.io.File;

/**
 * Represents an option that makes requests to a database
 */
public interface DatabaseOption extends TimeZoneOption {
	File getDatabase();

	String getSourceId();

	String getDefaultSourceId();

	DefaultInstanceOptions getDefaultInstanceOptions();
}
