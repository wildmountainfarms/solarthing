package me.retrodaredevil.solarthing.config.databases.implementations;

import me.retrodaredevil.influxdb.InfluxProperties;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;

public final class InfluxDbDatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new DatabaseType() { };

	private final InfluxProperties influxProperties;

	public InfluxDbDatabaseSettings(InfluxProperties influxProperties) {
		this.influxProperties = influxProperties;
	}

	public InfluxProperties getInfluxProperties(){ return influxProperties; }
}
