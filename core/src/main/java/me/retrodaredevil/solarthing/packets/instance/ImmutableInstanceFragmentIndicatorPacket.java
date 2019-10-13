package me.retrodaredevil.solarthing.packets.instance;

final class ImmutableInstanceFragmentIndicatorPacket implements InstanceFragmentIndicatorPacket {
	
	private final InstancePacketType packetType = InstancePacketType.FRAGMENT_INDICATOR;
	
	private final int fragmentId;
	
	public ImmutableInstanceFragmentIndicatorPacket(int fragmentId) {
		this.fragmentId = fragmentId;
	}
	
	@Override
	public int getFragmentId() {
		return fragmentId;
	}
	
	@Override
	public InstancePacketType getPacketType() {
		return packetType;
	}
}
