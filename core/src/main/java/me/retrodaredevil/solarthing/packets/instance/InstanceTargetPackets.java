package me.retrodaredevil.solarthing.packets.instance;

@Deprecated
public final class InstanceTargetPackets {
	private InstanceTargetPackets(){ throw new UnsupportedOperationException(); }
	public static InstanceTargetPacket create(String targetId){
		return new ImmutableInstanceTargetPacket(targetId);
	}
}
