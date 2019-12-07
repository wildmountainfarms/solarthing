package me.retrodaredevil.solarthing.influxdb;

import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import org.influxdb.dto.Point;

public class ConstantMeasurementPacketPointCreator implements PacketPointCreator {
	private final String measurement;

	public ConstantMeasurementPacketPointCreator(String measurement) {
		this.measurement = measurement;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Point.Builder createBuilder(Packet packet) {
		Point.Builder r = Point.measurement(measurement);
		if(packet instanceof DocumentedPacket){
			DocumentedPacket<? extends DocumentedPacketType> documentedPacket = (DocumentedPacket<? extends DocumentedPacketType>) packet;
			DocumentedPacketType type = documentedPacket.getPacketType();
			r.tag("packetType", type.toString());
			if(packet instanceof Identifiable){
				Identifier identifier = ((Identifiable) packet).getIdentifier();
				return r.tag("identifier", identifier.getRepresentation());
			}
			return r;
		}
		return r;
	}
}
