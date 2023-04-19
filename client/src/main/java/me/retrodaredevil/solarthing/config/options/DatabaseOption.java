package me.retrodaredevil.solarthing.config.options;

import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;

import java.nio.file.Path;

/**
 * Represents an option that makes requests to a database
 */
public interface DatabaseOption extends TimeZoneOption {
	Path getDatabaseFilePath();
	// TODO allow for DatabaseConfig to be specified in this file

	String getSourceId();

	String getDefaultSourceId();

	DefaultInstanceOptions getDefaultInstanceOptions();
}
