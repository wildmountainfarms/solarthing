package me.retrodaredevil.solarthing.packets.instance;

public final class InstanceSourcePackets {
	private InstanceSourcePackets(){ throw new UnsupportedOperationException(); }
	public static InstanceSourcePacket create(String sourceId){
		return new ImmutableInstanceSourcePacket(sourceId);
	}
}
