package me.retrodaredevil.solarthing.influxdb;

import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ConstantNameGetter implements NameGetter {
	private final String databaseName;

	public ConstantNameGetter(String databaseName) {
		this.databaseName = databaseName;
	}

	@Override
	public String getName(InstancePacketGroup instancePacketGroup) {
		return databaseName;
	}
}
