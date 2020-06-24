package me.retrodaredevil.solarthing.influxdb;

import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import org.influxdb.dto.Point;

public class ConstantMeasurementPacketPointCreator implements PacketPointCreator {
	private final String measurement;

	public ConstantMeasurementPacketPointCreator(String measurement) {
		this.measurement = measurement;
	}

	@Override
	public Point.Builder createBuilder(Packet packet) {
		Point.Builder r = Point.measurement(measurement);
		if(packet instanceof Identifiable){
			Identifier identifier = ((Identifiable) packet).getIdentifier();
			r.tag("identifier", identifier.getRepresentation());
			if(identifier instanceof SupplementaryIdentifier){
				SupplementaryIdentifier supplementaryIdentifier = (SupplementaryIdentifier) identifier;
				r.tag("identifier_supplementaryTo", supplementaryIdentifier.getSupplementaryTo().getRepresentation());
			}
		}
		if(packet instanceof DocumentedPacket){
			DocumentedPacket documentedPacket = (DocumentedPacket) packet;
			DocumentedPacketType type = documentedPacket.getPacketType();
			r.tag("packetType", type.toString());
		}
		return r;
	}
}
