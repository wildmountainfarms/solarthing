package me.retrodaredevil.solarthing.packets.instance;

import me.retrodaredevil.solarthing.annotations.UtilityClass;

import java.util.Collection;

@UtilityClass
public final class InstanceTargetPackets {
	private InstanceTargetPackets(){ throw new UnsupportedOperationException(); }

	public static InstanceTargetPacket create(Collection<Integer> targetFragmentIds){
		return new ImmutableInstanceTargetPacket(targetFragmentIds);
	}
}
