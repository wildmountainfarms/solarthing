package me.retrodaredevil.solarthing.config.databases;

import java.beans.ConstructorProperties;

public class SimpleDatabaseType implements DatabaseType {
	private final String name;

	@ConstructorProperties({"name"})
	public SimpleDatabaseType(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
