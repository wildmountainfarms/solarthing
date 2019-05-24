package me.retrodaredevil.iot.outhouse;

public class ImmutableOccupancyPacket implements OccupancyPacket {
	private final OuthousePacketType packetType = OuthousePacketType.OCCUPANCY;
	
	private final int occupancy;
	
	public ImmutableOccupancyPacket(int occupancy) {
		this.occupancy = occupancy;
	}
	
	@Override
	public int getOccupancy() {
		return occupancy;
	}
	
	@Override
	public OuthousePacketType getPacketType() {
		return packetType;
	}
}
