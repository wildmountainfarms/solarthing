package me.retrodaredevil.solarthing.config.databases.implementations;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.SimpleDatabaseType;

import java.io.File;

@JsonTypeName("latest")
public final class LatestFileDatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new SimpleDatabaseType("latest");
	
	private final File file;
	
	public LatestFileDatabaseSettings(File file) {
		this.file = file;
	}
	public File getFile(){
		return file;
	}

	@Override
	public DatabaseType getDatabaseType() {
		return TYPE;
	}
}
