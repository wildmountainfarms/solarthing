package me.retrodaredevil.solarthing.influxdb;

import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;

public class ConstantDatabaseNameGetter implements DatabaseNameGetter {
	private final String databaseName;

	public ConstantDatabaseNameGetter(String databaseName) {
		this.databaseName = databaseName;
	}

	@Override
	public String getDatabaseName(InstancePacketGroup instancePacketGroup) {
		return databaseName;
	}
}
