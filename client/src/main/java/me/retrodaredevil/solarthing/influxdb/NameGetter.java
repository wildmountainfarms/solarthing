package me.retrodaredevil.solarthing.influxdb;

import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;

public interface NameGetter {
	String getDatabaseName(InstancePacketGroup instancePacketGroup);
}
