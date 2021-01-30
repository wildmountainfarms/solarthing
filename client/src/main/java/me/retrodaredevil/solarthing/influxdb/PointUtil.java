package me.retrodaredevil.solarthing.influxdb;

import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;

import java.util.HashMap;
import java.util.Map;

public final class PointUtil {
	private PointUtil() { throw new UnsupportedOperationException(); }

	public static Map<String, String> getTags(Packet packet) {
		Map<String, String> r = new HashMap<>();
		if(packet instanceof Identifiable){
			Identifier identifier = ((Identifiable) packet).getIdentifier();
			r.put("identifier", identifier.getRepresentation());
			if(identifier instanceof SupplementaryIdentifier){
				SupplementaryIdentifier supplementaryIdentifier = (SupplementaryIdentifier) identifier;
				r.put("identifier_supplementaryTo", supplementaryIdentifier.getSupplementaryTo().getRepresentation());
			}
		}
		if(packet instanceof DocumentedPacket){
			DocumentedPacket documentedPacket = (DocumentedPacket) packet;
			DocumentedPacketType type = documentedPacket.getPacketType();
			r.put("packetType", type.toString());
		}
		return r;
	}
}
