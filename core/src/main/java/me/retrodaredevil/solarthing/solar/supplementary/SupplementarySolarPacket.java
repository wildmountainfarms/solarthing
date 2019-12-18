package me.retrodaredevil.solarthing.solar.supplementary;

import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;

public interface SupplementarySolarPacket extends DocumentedPacket<SupplementarySolarPacketType>, Identifiable {
	@Override
	SupplementaryIdentifier getIdentifier();
}
