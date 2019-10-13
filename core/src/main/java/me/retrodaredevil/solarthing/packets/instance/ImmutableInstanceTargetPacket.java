package me.retrodaredevil.solarthing.packets.instance;

final class ImmutableInstanceTargetPacket implements InstanceTargetPacket {
	private final InstancePacketType packetType = InstancePacketType.TARGET;
	
	private final String targetId;
	
	ImmutableInstanceTargetPacket(String targetId) {
		this.targetId = targetId;
	}
	
	@Override
	public String getTargetId() {
		return targetId;
	}
	
	@Override
	public InstancePacketType getPacketType() {
		return packetType;
	}
}
