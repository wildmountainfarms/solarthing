package me.retrodaredevil.solarthing.packets.instance;

import me.retrodaredevil.solarthing.annotations.UtilityClass;

@UtilityClass
public final class InstanceSourcePackets {
	private InstanceSourcePackets(){ throw new UnsupportedOperationException(); }
	public static InstanceSourcePacket create(String sourceId){
		return new ImmutableInstanceSourcePacket(sourceId);
	}
}
