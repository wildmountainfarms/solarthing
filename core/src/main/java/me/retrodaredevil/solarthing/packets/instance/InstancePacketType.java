package me.retrodaredevil.solarthing.packets.instance;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;

public enum InstancePacketType implements DocumentedPacketType {
	SOURCE,
	@Deprecated // never used. Maybe used in the future
	TARGET,
	FRAGMENT_INDICATOR
}
