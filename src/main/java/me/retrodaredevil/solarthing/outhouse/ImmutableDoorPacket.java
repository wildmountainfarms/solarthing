package me.retrodaredevil.solarthing.outhouse;

public class ImmutableDoorPacket implements DoorPacket {
	private final OuthousePacketType packetType = OuthousePacketType.DOOR;
	
	private final boolean isOpen;
	private final Long lastClose;
	private final Long lastOpen;
	
	public ImmutableDoorPacket(boolean isOpen, Long lastClose, Long lastOpen) {
		this.isOpen = isOpen;
		this.lastClose = lastClose;
		this.lastOpen = lastOpen;
//		System.out.println("Created " + isOpen + " " + lastClose + " " + lastOpen);
	}
	
	@Override
	public boolean isOpen() {
		return isOpen;
	}
	
	@Override
	public Long getLastCloseTimeMillis() {
		return lastClose;
	}
	
	@Override
	public Long getLastOpenTimeMillis() {
		return lastOpen;
	}
	
	@Override
	public OuthousePacketType getPacketType() {
		return packetType;
	}
}
