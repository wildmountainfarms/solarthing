package me.retrodaredevil.solarthing.influxdb;

import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;

public enum SourceIdNameGetter implements NameGetter {
	INSTANCE;
	@Override
	public String getName(InstancePacketGroup instancePacketGroup) {
		return instancePacketGroup.getSourceId();
	}
}
