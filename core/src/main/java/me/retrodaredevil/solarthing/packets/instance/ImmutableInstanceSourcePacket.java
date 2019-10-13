package me.retrodaredevil.solarthing.packets.instance;

final class ImmutableInstanceSourcePacket implements InstanceSourcePacket {
	private final InstancePacketType packetType = InstancePacketType.SOURCE;
	
	private final String sourceId;
	
	ImmutableInstanceSourcePacket(String sourceId) {
		this.sourceId = sourceId;
	}
	
	@Override
	public String getSourceId() {
		return sourceId;
	}
	
	@Override
	public InstancePacketType getPacketType() {
		return packetType;
	}
}
