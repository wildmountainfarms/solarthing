package me.retrodaredevil.solarthing.packets.instance;

import java.util.Collection;

public final class InstanceTargetPackets {
	private InstanceTargetPackets(){ throw new UnsupportedOperationException(); }

	public static InstanceTargetPacket create(Collection<Integer> targetFragmentIds){
		return new ImmutableInstanceTargetPacket(targetFragmentIds);
	}
}
