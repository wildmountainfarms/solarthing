package me.retrodaredevil.solarthing.config.databases.implementations;

import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;

import java.io.File;

public final class LatestFileDatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new DatabaseType() { };
	
	private final File file;
	
	public LatestFileDatabaseSettings(File file) {
		this.file = file;
	}
	public File getFile(){
		return file;
	}
}
