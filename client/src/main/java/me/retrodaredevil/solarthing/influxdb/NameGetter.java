package me.retrodaredevil.solarthing.influxdb;

import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface NameGetter {
	String getName(InstancePacketGroup instancePacketGroup);
}
