package me.retrodaredevil.solarthing.packets.security;

public final class ImmutableIntegrityPacket implements IntegrityPacket {
	private final SecurityPacketType packetType = SecurityPacketType.INTEGRITY_PACKET;
	
	private final String sender;
	private final String encryptedData;
	
	public ImmutableIntegrityPacket(String sender, String encryptedData) {
		this.sender = sender;
		this.encryptedData = encryptedData;
	}
	
	@Override
	public String getSender() {
		return sender;
	}
	
	@Override
	public String getEncryptedData() {
		return encryptedData;
	}
	
	@Override
	public SecurityPacketType getPacketType() {
		return packetType;
	}
}
