package me.retrodaredevil.solarthing.packets.instance;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum InstancePacketType implements DocumentedPacketType {
	SOURCE,
	TARGET,
	FRAGMENT_INDICATOR
}
