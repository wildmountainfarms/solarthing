package me.retrodaredevil.solarthing.packets.instance;

import me.retrodaredevil.solarthing.annotations.UtilityClass;

@UtilityClass
public final class InstanceFragmentIndicatorPackets {
	private InstanceFragmentIndicatorPackets(){ throw new UnsupportedOperationException(); }

	public static InstanceFragmentIndicatorPacket create(int fragmentId){
		return new ImmutableInstanceFragmentIndicatorPacket(fragmentId);
	}
}
