package me.retrodaredevil.solarthing.packets.instance;

public final class InstanceFragmentIndicatorPackets {
	private InstanceFragmentIndicatorPackets(){ throw new UnsupportedOperationException(); }
	
	public static InstanceFragmentIndicatorPacket create(int fragmentId){
		return new ImmutableInstanceFragmentIndicatorPacket(fragmentId);
	}
}
