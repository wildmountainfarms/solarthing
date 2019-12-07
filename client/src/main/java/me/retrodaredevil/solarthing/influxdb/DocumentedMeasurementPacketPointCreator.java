package me.retrodaredevil.solarthing.influxdb;

import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import org.influxdb.dto.Point;

public enum DocumentedMeasurementPacketPointCreator implements PacketPointCreator {
	INSTANCE;
	@SuppressWarnings("unchecked")
	@Override
	public Point.Builder createBuilder(Packet packet) {
		if(packet instanceof DocumentedPacket){
			DocumentedPacket<? extends DocumentedPacketType> documentedPacket = (DocumentedPacket<? extends DocumentedPacketType>) packet;
			DocumentedPacketType type = documentedPacket.getPacketType();
			if(packet instanceof Identifiable){
				Identifier identifier = ((Identifiable) packet).getIdentifier();
				return Point.measurement(type.toString()).tag("identifier", identifier.getRepresentation());
			}
			return Point.measurement(type.toString());
		}
		return Point.measurement(packet.getClass().getSimpleName());
	}
}
