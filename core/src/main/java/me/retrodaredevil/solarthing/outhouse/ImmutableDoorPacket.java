package me.retrodaredevil.solarthing.outhouse;

public class ImmutableDoorPacket implements DoorPacket {
	private final OuthousePacketType packetType = OuthousePacketType.DOOR;
	
	private final boolean isOpen;
	private final Long lastCloseTimeMillis;
	private final Long lastOpenTimeMillis;
	
	public ImmutableDoorPacket(boolean isOpen, Long lastCloseTimeMillis, Long lastOpenTimeMillis) {
		this.isOpen = isOpen;
		this.lastCloseTimeMillis = lastCloseTimeMillis;
		this.lastOpenTimeMillis = lastOpenTimeMillis;
	}
	
	@Override
	public boolean isOpen() {
		return isOpen;
	}
	
	@Override
	public Long getLastCloseTimeMillis() {
		return lastCloseTimeMillis;
	}
	
	@Override
	public Long getLastOpenTimeMillis() {
		return lastOpenTimeMillis;
	}
	
	@Override
	public OuthousePacketType getPacketType() {
		return packetType;
	}
}
