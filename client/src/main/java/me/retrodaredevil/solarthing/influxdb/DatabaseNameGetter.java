package me.retrodaredevil.solarthing.influxdb;

import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;

public interface DatabaseNameGetter {
	String getDatabaseName(InstancePacketGroup instancePacketGroup);
}
